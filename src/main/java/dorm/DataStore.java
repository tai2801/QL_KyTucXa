package dorm;
import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*; import java.util.*;
/**
 * Repository layer (DataStore) - file persistence.
 * Kien truc tach lop: DormService chi lam viec qua DataStore.
 * De chuyen sang JDBC SQLite/MySQL: tao class JdbcDataStore implements cac phuong thuc
 * load/save tuong duong bang JDBC (xem docs/DB.md). Khong can sua DormService/Main/GUI
 * neu giu nguyen chu ky phuong thuc.
 */
public class DataStore {
    private final Path dir;
    public List<User> users=new ArrayList<>();
    public List<Building> buildings=new ArrayList<>();
    public List<Room> rooms=new ArrayList<>();
    public List<Student> students=new ArrayList<>();
    public List<Contract> contracts=new ArrayList<>();
    public List<UtilityBill> bills=new ArrayList<>();
    public DataStore(Path dir){this.dir=dir;}
    public Path getDir(){return dir;}

    private List<String> read(String name) throws IOException {
        Path p=dir.resolve(name); if(!Files.exists(p)) return List.of();
        List<String> out=new ArrayList<>();
        for(String l: Files.readAllLines(p,StandardCharsets.UTF_8)){ if(!l.isBlank()&&!l.startsWith("#")) out.add(l.trim()); }
        return out;
    }
    private void write(String name, List<String> lines) throws IOException {
        Files.createDirectories(dir);
        Files.write(dir.resolve(name), lines, StandardCharsets.UTF_8);
    }
    public void load() throws IOException {
        users.clear(); buildings.clear(); rooms.clear(); students.clear(); contracts.clear(); bills.clear();
        for(String l: read("users.txt")) users.add(User.fromLine(l));
        for(String l: read("buildings.txt")) buildings.add(Building.fromLine(l));
        for(String l: read("rooms.txt")) rooms.add(Room.fromLine(l));
        for(String l: read("students.txt")) students.add(Student.fromLine(l));
        for(String l: read("contracts.txt")) contracts.add(Contract.fromLine(l));
        for(String l: read("bills.txt")) bills.add(UtilityBill.fromLine(l));
        if(users.isEmpty()&&buildings.isEmpty()){ seed(); save(); }
    }
    public void save() throws IOException {
        write("users.txt", users.stream().map(User::toLine).toList());
        write("buildings.txt", buildings.stream().map(Building::toLine).toList());
        write("rooms.txt", rooms.stream().map(Room::toLine).toList());
        write("students.txt", students.stream().map(Student::toLine).toList());
        write("contracts.txt", contracts.stream().map(Contract::toLine).toList());
        write("bills.txt", bills.stream().map(UtilityBill::toLine).toList());
    }
    public void seed(){
        users.add(new User("admin","admin123","Quan tri vien",User.Role.ADMIN));
        users.add(new User("staff","staff123","Nhan vien KTX",User.Role.STAFF));
        buildings.add(new Building("A","Toa A - Nam",5,"Nam"));
        buildings.add(new Building("B","Toa B - Nu",5,"Nu"));
        buildings.add(new Building("C","Toa C - Nam",4,"Nam"));
        String[][] r={{"A101","A","101"},{"A102","A","102"},{"A201","A","201"},{"A202","A","202"},
                      {"B101","B","101"},{"B102","B","102"},{"B201","B","201"},{"B202","B","202"},
                      {"C101","C","101"},{"C102","C","102"},{"C201","C","201"},{"C202","C","202"}};
        for(int i=0;i<r.length;i++){
            String gender=r[i][1].equals("B")?"Nu":"Nam";
            int cap=(i%3==0)?4:6;
            rooms.add(new Room(r[i][0],r[i][1],r[i][2],cap,600000,gender));
        }
        String[][] s={{"SV001","Nguyen Van An","Nam","K13-CNTT","0901000001","Ha Noi","A101"},
            {"SV002","Tran Van Binh","Nam","K13-CNTT","0901000002","Hai Phong","A101"},
            {"SV003","Le Van Cuong","Nam","K13-KT","0901000003","Nghe An","A101"},
            {"SV004","Pham Van Dung","Nam","K13-CNTT","0901000004","Thanh Hoa","A102"},
            {"SV005","Hoang Van Em","Nam","K14-CNTT","0901000005","Ha Nam","A102"},
            {"SV006","Vu Thi Phuong","Nu","K13-KT","0901000006","Bac Ninh","B101"},
            {"SV007","Do Thi Giang","Nu","K13-CNTT","0901000007","Hung Yen","B101"},
            {"SV008","Bui Thi Hoa","Nu","K14-KT","0901000008","Nam Dinh","B101"},
            {"SV009","Dang Thi Lan","Nu","K13-CNTT","0901000009","Thai Binh","B102"},
            {"SV010","Ngo Thi Mai","Nu","K14-CNTT","0901000010","Hai Duong","B102"},
            {"SV011","Duong Van Nam","Nam","K13-DT","0901000011","Vinh Phuc","C101"},
            {"SV012","Ly Van Oanh","Nam","K14-DT","0901000012","Phu Tho","C101"},
            {"SV013","Trinh Thi Quyen","Nu","K13-KT","0901000013","Quang Ninh",""},
            {"SV014","Phan Van Son","Nam","K13-CNTT","0901000014","Bac Giang","C102"},
            {"SV015","Vo Thi Trang","Nu","K14-CNTT","0901000015","Ha Tinh",""}};
        for(String[] x:s) students.add(new Student(x[0],x[1],x[2],x[3],x[4],x[5],x[6]));
        contracts.add(new Contract("HD001","SV001","A101",java.time.LocalDate.of(2022,9,1),java.time.LocalDate.of(2023,5,31),600000,600000,Contract.Status.ACTIVE));
        contracts.add(new Contract("HD002","SV006","B101",java.time.LocalDate.of(2022,9,1),java.time.LocalDate.of(2023,5,31),600000,600000,Contract.Status.ACTIVE));
        bills.add(new UtilityBill("BILL001","A101","2023-03",100,160,3500,20,28,15000,false));
        bills.add(new UtilityBill("BILL002","B101","2023-03",200,245,3500,30,36,15000,true));
    }
}
