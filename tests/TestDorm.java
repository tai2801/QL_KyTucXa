package dorm;
import java.nio.file.*; import java.time.LocalDate; import java.util.*;
public class TestDorm {
    static int passed=0, failed=0;
    static void check(String name, boolean cond){ if(cond){passed++; System.out.println("PASS: "+name);} else {failed++; System.out.println("FAIL: "+name);} }
    static void expectThrow(String name, Runnable r){ try{r.run(); check(name,false);}catch(Exception e){check(name+" ("+e.getMessage()+")",true);} }
    public static void main(String[] args) throws Exception {
        Path tmp=Files.createTempDirectory("dorm-test");
        DormService svc=new DormService(new DataStore(tmp)); svc.load(); // seeds if empty
        check("Seed >=3 toa", svc.getStore().buildings.size()>=3);
        check("Seed >=12 phong", svc.getStore().rooms.size()>=12);
        check("Seed >=15 SV", svc.getStore().students.size()>=15);
        check("Login admin ok", svc.login("admin","admin123").isPresent() && svc.login("admin","admin123").get().isAdmin());
        check("Login staff ok, khong phai admin", svc.login("staff","staff123").isPresent() && !svc.login("staff","staff123").get().isAdmin());
        check("Login sai mat khau", svc.login("admin","sai").isEmpty());
        check("Login trong", svc.login("","").isEmpty());
        User admin=svc.login("admin","admin123").get(); User staff=svc.login("staff","staff123").get();
        expectThrow("Staff xoa SV bi chan", ()->svc.deleteStudent(staff,"SV001"));
        check("SV001 van con sau khi staff xoa that bai", svc.findStudent("SV001").isPresent());
        expectThrow("Trung ma SV bi chan", ()->svc.addStudent(new Student("SV001","Trung","Nam","K13","","","")));
        expectThrow("SV rong bi chan", ()->new Student("","Ten","Nam","K13","","",""));
        // Room full: A101 capacity 4, seed has 3 occupants (SV001-003). Add 1 -> full, next fails
        svc.addStudent(new Student("SV100","Test Full","Nam","K13","090","HN",""));
        svc.registerRoom("SV100","A101",LocalDate.of(2022,9,1),LocalDate.of(2023,5,31));
        check("A101 day sau dang ky thu 4", svc.isFull(svc.findRoom("A101").get()));
        svc.addStudent(new Student("SV101","Test Full2","Nam","K13","090","HN",""));
        expectThrow("Dang ky vao phong day bi chan", ()->svc.registerRoom("SV101","A101",LocalDate.now(),LocalDate.now().plusMonths(9)));
        // Gender mismatch: Nu vao phong Nam
        svc.addStudent(new Student("SV102","Test Nu","Nu","K13","090","HN",""));
        expectThrow("Nu vao phong Nam bi chan", ()->svc.registerRoom("SV102","A102",LocalDate.now(),LocalDate.now().plusMonths(9)));
        // Transfer
        Contract nc=svc.transferRoom("SV100","A201");
        check("Chuyen phong thanh cong", svc.findStudent("SV100").get().getRoomId().equals("A201") && nc.getRoomId().equals("A201"));
        check("A101 khong con day sau chuyen", !svc.isFull(svc.findRoom("A101").get()));
        // Bill calc: (160-100)*3500=210000, (28-20)*15000=120000, tong 330000
        UtilityBill seed=svc.getStore().bills.stream().filter(b->b.getId().equals("BILL001")).findFirst().orElse(null);
        check("Bill seed tinh dung", seed!=null && seed.elecCost()==210000 && seed.waterCost()==120000 && seed.total()==330000);
        UtilityBill b=svc.createBill("BILL100","A201","2023-04",0,50,3500,0,5,15000);
        check("Bill moi tinh dung", b.total()==50*3500+5*15000);
        expectThrow("Chi so moi < cu bi chan", ()->svc.createBill("BILL101","A201","2023-04",100,50,3500,0,5,15000));
        expectThrow("Trung ma bill bi chan", ()->svc.createBill("BILL100","A201","2023-05",0,1,3500,0,1,15000));
        // Monthly charge: A201 price 600000 or  (cap by index) + share
        long charge=svc.monthlyChargeForStudent("SV100","2023-04");
        check("Tien thang = tien phong + chia dien nuoc (> gia phong)", charge>svc.findRoom("A201").get().getPricePerMonth());
        // Search
        check("Tim theo ten", !svc.searchStudents("An").isEmpty());
        check("Tim theo ma", svc.searchStudents("SV001").size()==1);
        check("Tim khong thay -> rong", svc.searchStudents("ZZZZZ").isEmpty());
        // Stats
        var st=svc.stats(); check("Thong ke tong phong dung", st.totalRooms()==svc.getStore().rooms.size());
        check("Thong ke giuong", st.totalBeds()>=48);
        // Persistence
        svc.save();
        DormService svc2=new DormService(new DataStore(tmp)); svc2.load();
        check("Luu/doc file: so SV khop", svc2.getStore().students.size()==svc.getStore().students.size());
        check("Luu/doc file: SV100 o A201", svc2.findStudent("SV100").get().getRoomId().equals("A201"));
        // Admin delete works
        svc.deleteStudent(admin,"SV101"); check("Admin xoa SV duoc", svc.findStudent("SV101").isEmpty());
        expectThrow("Staff xoa phong bi chan", ()->svc.deleteRoom(staff,"C202"));
        System.out.println("\nTong: "+passed+" PASS, "+failed+" FAIL");
        if(failed>0) System.exit(1);
    }
}
