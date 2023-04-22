package dorm;
import java.io.IOException; import java.time.LocalDate; import java.util.*;
import java.util.stream.Collectors;
public class DormService {
    private final DataStore store;
    public DormService(DataStore store){this.store=store;}
    public DataStore getStore(){return store;}
    public void load() throws IOException{store.load();}
    public void save() throws IOException{store.save();}

    public Optional<User> login(String u,String p){
        if(u==null||p==null) return Optional.empty();
        return store.users.stream().filter(x->x.getUsername().equals(u.trim())&&x.checkPassword(p)).findFirst();
    }
    private void requireAdmin(User actor){
        if(actor==null||!actor.isAdmin()) throw new SecurityException("Chi ADMIN moi duoc xoa du lieu");
    }
    // Students
    public void addStudent(Student s){
        if(store.students.stream().anyMatch(x->x.getId().equalsIgnoreCase(s.getId()))) throw new IllegalArgumentException("Trung ma SV: "+s.getId());
        if(!s.getRoomId().isEmpty()){ Room r=findRoom(s.getRoomId()).orElseThrow(()->new IllegalArgumentException("Phong khong ton tai")); checkRoomAvailable(r, s.getGender()); }
        store.students.add(s);
    }
    public void deleteStudent(User actor,String id){ requireAdmin(actor); store.students.removeIf(x->x.getId().equals(id)); store.contracts.removeIf(c->c.getStudentId().equals(id)); }
    public Optional<Student> findStudent(String id){return store.students.stream().filter(x->x.getId().equalsIgnoreCase(id)).findFirst();}
    public List<Student> searchStudents(String kw){
        if(kw==null||kw.isBlank()) return new ArrayList<>(store.students);
        String k=kw.toLowerCase();
        return store.students.stream().filter(x->x.getId().toLowerCase().contains(k)||x.getFullName().toLowerCase().contains(k)||x.getClassName().toLowerCase().contains(k)).collect(Collectors.toList());
    }
    // Buildings / Rooms
    public void addBuilding(Building b){ if(store.buildings.stream().anyMatch(x->x.getId().equalsIgnoreCase(b.getId()))) throw new IllegalArgumentException("Trung toa"); store.buildings.add(b); }
    public void deleteBuilding(User actor,String id){ requireAdmin(actor); store.buildings.removeIf(x->x.getId().equals(id)); }
    public void addRoom(Room r){ if(store.rooms.stream().anyMatch(x->x.getId().equalsIgnoreCase(r.getId()))) throw new IllegalArgumentException("Trung phong"); if(store.buildings.stream().noneMatch(b->b.getId().equals(r.getBuildingId()))) throw new IllegalArgumentException("Toa khong ton tai"); store.rooms.add(r); }
    public void deleteRoom(User actor,String id){ requireAdmin(actor); if(occupancy(id)>0) throw new IllegalStateException("Phong dang co SV, khong the xoa"); store.rooms.removeIf(x->x.getId().equals(id)); }
    public Optional<Room> findRoom(String id){return store.rooms.stream().filter(x->x.getId().equalsIgnoreCase(id)).findFirst();}
    public int occupancy(String roomId){ return (int)store.students.stream().filter(s->s.getRoomId().equalsIgnoreCase(roomId)).count(); }
    public boolean isFull(Room r){ return occupancy(r.getId())>=r.getCapacity(); }
    private void checkRoomAvailable(Room r,String gender){
        if(isFull(r)) throw new IllegalStateException("Phong "+r.getId()+" da day ("+r.getCapacity()+" giuong)");
        if(gender!=null&&!gender.isBlank()&&!r.getGender().isBlank()&&!r.getGender().equalsIgnoreCase(gender)) throw new IllegalArgumentException("Phong danh cho "+r.getGender()+", SV gioi tinh "+gender+" khong phu hop");
    }
    // Register & transfer
    public Contract registerRoom(String studentId,String roomId,LocalDate start,LocalDate end){
        Student s=findStudent(studentId).orElseThrow(()->new IllegalArgumentException("SV khong ton tai"));
        Room r=findRoom(roomId).orElseThrow(()->new IllegalArgumentException("Phong khong ton tai"));
        if(!s.getRoomId().isEmpty()) throw new IllegalStateException("SV da co phong, hay dung chuyen phong");
        checkRoomAvailable(r,s.getGender());
        s.setRoomId(roomId);
        String cid="HD"+String.format("%03d", store.contracts.size()+1);
        while(contractExists(cid)) cid=cid+"X";
        Contract c=new Contract(cid,studentId,roomId,start,end,r.getPricePerMonth(),r.getPricePerMonth(),Contract.Status.ACTIVE);
        store.contracts.add(c); return c;
    }
    public Contract transferRoom(String studentId,String newRoomId){
        Student s=findStudent(studentId).orElseThrow(()->new IllegalArgumentException("SV khong ton tai"));
        Room r=findRoom(newRoomId).orElseThrow(()->new IllegalArgumentException("Phong moi khong ton tai"));
        if(newRoomId.equalsIgnoreCase(s.getRoomId())) throw new IllegalArgumentException("Dang o phong nay roi");
        checkRoomAvailable(r,s.getGender());
        // End active contracts for old room, create new one keeping dates if exists
        LocalDate start=LocalDate.now(), end=LocalDate.now().plusMonths(9);
        for(Contract c: store.contracts) if(c.getStudentId().equals(studentId)&&c.getStatus()==Contract.Status.ACTIVE){ start=c.getStartDate(); end=c.getEndDate(); c.setStatus(Contract.Status.ENDED); }
        s.setRoomId(newRoomId);
        String cid="HD"+String.format("%03d", store.contracts.size()+1);
        while(contractExists(cid)) cid=cid+"X";
        Contract nc=new Contract(cid,studentId,newRoomId,start,end,r.getPricePerMonth(),r.getPricePerMonth(),Contract.Status.ACTIVE);
        store.contracts.add(nc); return nc;
    }
    public void endContract(String contractId){ store.contracts.stream().filter(c->c.getId().equals(contractId)).findFirst().ifPresent(c->{c.setStatus(Contract.Status.ENDED); findStudent(c.getStudentId()).ifPresent(s->{if(s.getRoomId().equals(c.getRoomId())) s.setRoomId("");});}); }
    private boolean contractExists(String id){ return store.contracts.stream().anyMatch(c->c.getId().equals(id)); }
    // Bills & fees
    public UtilityBill createBill(String id,String roomId,String month,double eo,double en,long ep,double wo,double wn,long wp){
        findRoom(roomId).orElseThrow(()->new IllegalArgumentException("Phong khong ton tai"));
        if(store.bills.stream().anyMatch(b->b.getId().equals(id))) throw new IllegalArgumentException("Trung ma hoa don");
        UtilityBill b=new UtilityBill(id,roomId,month,eo,en,ep,wo,wn,wp,false); store.bills.add(b); return b;
    }
    /** Tong tien 1 SV trong 1 thang: tien phong + phan dien nuoc chia deu theo so nguoi trong phong */
    public long monthlyChargeForStudent(String studentId,String month){
        Student s=findStudent(studentId).orElseThrow(()->new IllegalArgumentException("SV khong ton tai"));
        if(s.getRoomId().isEmpty()) return 0;
        Room r=findRoom(s.getRoomId()).get();
        long total=r.getPricePerMonth();
        for(UtilityBill b: store.bills) if(b.getRoomId().equals(s.getRoomId())&&b.getMonth().equals(month)) total+=b.sharePerStudent(Math.max(1,occupancy(s.getRoomId())));
        return total;
    }
    // Stats
    public record Stats(int totalRooms,int emptyRooms,int fullRooms,int partialRooms,int totalBeds,int occupiedBeds,int totalStudents,int studentsWithoutRoom){}
    public Stats stats(){
        int empty=0,full=0,partial=0,beds=0,occ=0;
        for(Room r: store.rooms){ beds+=r.getCapacity(); int o=occupancy(r.getId()); occ+=o; if(o==0)empty++; else if(o>=r.getCapacity())full++; else partial++; }
        int without=(int)store.students.stream().filter(s->s.getRoomId().isEmpty()).count();
        return new Stats(store.rooms.size(),empty,full,partial,beds,occ,store.students.size(),without);
    }
    public List<Room> emptyRooms(){ return store.rooms.stream().filter(r->occupancy(r.getId())==0).collect(Collectors.toList()); }
    public List<Room> fullRooms(){ return store.rooms.stream().filter(this::isFull).collect(Collectors.toList()); }
}
