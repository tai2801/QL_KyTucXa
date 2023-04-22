package dorm;
import java.nio.file.Path; import java.time.LocalDate; import java.util.*;
public class Main {
    public static void main(String[] args) throws Exception {
        DormService svc=new DormService(new DataStore(Path.of("data")));
        svc.load();
        if(args.length>0 && args[0].equals("--demo")){
            System.out.println("=== DEMO KTX ===");
            svc.getStore().buildings.forEach(System.out::println);
            svc.getStore().rooms.forEach(r->System.out.println(r+" | dang o: "+svc.occupancy(r.getId())+"/"+r.getCapacity()));
            svc.getStore().students.forEach(System.out::println);
            System.out.println("Thong ke: "+svc.stats());
            System.out.println("Dang nhap admin/admin123: "+svc.login("admin","admin123").isPresent());
            return;
        }
        Scanner sc=new Scanner(System.in);
        System.out.println("=== QUAN LY KY TUC XA ===");
        System.out.print("Tai khoan: "); String u=sc.nextLine();
        System.out.print("Mat khau: "); String p=sc.nextLine();
        Optional<User> user=svc.login(u,p);
        if(user.isEmpty()){ System.out.println("Dang nhap that bai."); return; }
        System.out.println("Xin chao "+user.get().getFullName()+" ["+user.get().getRole()+"]");
        while(true){
            System.out.println("\n1.DS sinh vien  2.Them SV  3.Tim SV  4.DS phong & thong ke  5.Dang ky phong  6.Chuyen phong  7.Tao hoa don dien nuoc  8.DS hoa don  9.Tinh tien thang cua SV  10.Xoa SV (ADMIN)  0.Luu & Thoat");
            System.out.print("Chon: "); String ch=sc.nextLine().trim();
            try{
                switch(ch){
                    case "1" -> svc.getStore().students.forEach(System.out::println);
                    case "2" -> { System.out.print("Ma SV: "); String id=sc.nextLine(); System.out.print("Ho ten: "); String n=sc.nextLine(); System.out.print("Gioi tinh (Nam/Nu): "); String g=sc.nextLine(); System.out.print("Lop: "); String c=sc.nextLine(); svc.addStudent(new Student(id,n,g,c,"","","")); System.out.println("Da them."); }
                    case "3" -> { System.out.print("Tu khoa: "); svc.searchStudents(sc.nextLine()).forEach(System.out::println); }
                    case "4" -> { svc.getStore().rooms.forEach(r->System.out.println(r+" dang o "+svc.occupancy(r.getId())+"/"+r.getCapacity()+(svc.isFull(r)?" DAY":""))); System.out.println(svc.stats()); }
                    case "5" -> { System.out.print("Ma SV: "); String sid=sc.nextLine(); System.out.print("Ma phong: "); String rid=sc.nextLine(); System.out.println("Hop dong: "+svc.registerRoom(sid,rid,LocalDate.now(),LocalDate.now().plusMonths(9)).getId()); }
                    case "6" -> { System.out.print("Ma SV: "); String sid=sc.nextLine(); System.out.print("Phong moi: "); String rid=sc.nextLine(); System.out.println("Da chuyen, HD moi: "+svc.transferRoom(sid,rid).getId()); }
                    case "7" -> { System.out.print("Ma HD bill (BILLxxx): "); String id=sc.nextLine(); System.out.print("Phong: "); String rid=sc.nextLine(); System.out.print("Thang yyyy-MM: "); String m=sc.nextLine(); System.out.print("Dien cu/moi/gia: "); double eo=sc.nextDouble(),en=sc.nextDouble(); long ep=sc.nextLong(); System.out.print("Nuoc cu/moi/gia: "); double wo=sc.nextDouble(),wn=sc.nextDouble(); long wp=sc.nextLong(); sc.nextLine(); UtilityBill b=svc.createBill(id,rid,m,eo,en,ep,wo,wn,wp); System.out.println("Tong bill: "+b.total()); }
                    case "8" -> svc.getStore().bills.forEach(b->System.out.println(b.getId()+" "+b.getRoomId()+" "+b.getMonth()+" dien:"+b.elecConsumption()+"kWh="+b.elecCost()+" nuoc:"+b.waterConsumption()+"m3="+b.waterCost()+" tong:"+b.total()));
                    case "9" -> { System.out.print("Ma SV: "); String sid=sc.nextLine(); System.out.print("Thang yyyy-MM: "); String m=sc.nextLine(); System.out.println("Tien thang: "+svc.monthlyChargeForStudent(sid,m)+" VND"); }
                    case "10" -> { System.out.print("Ma SV can xoa: "); svc.deleteStudent(user.get(), sc.nextLine()); System.out.println("Da xoa."); }
                    case "0" -> { svc.save(); System.out.println("Da luu. Tam biet!"); return; }
                    default -> System.out.println("Lua chon khong hop le");
                }
            }catch(Exception e){ System.out.println("Loi: "+e.getMessage()); }
        }
    }
}
