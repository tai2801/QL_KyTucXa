package dorm;
import javax.swing.*; import javax.swing.table.DefaultTableModel; import java.awt.*; import java.nio.file.Path; import java.time.LocalDate;
/** Java Swing GUI - chay: java -cp out dorm.DormApp */
public class DormApp extends JFrame {
    private final DormService svc; private User current;
    private DefaultTableModel studentModel, roomModel, billModel;
    private JLabel statsLabel;
    public DormApp(DormService svc, User current){
        super("Quan ly Ky tuc xa - "+current.getFullName()+" ["+current.getRole()+"]");
        this.svc=svc; this.current=current;
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(960,620); setLocationRelativeTo(null);
        JTabbedPane tabs=new JTabbedPane();
        tabs.add("Sinh vien", studentPanel());
        tabs.add("Phong & Toa", roomPanel());
        tabs.add("Hoa don dien nuoc", billPanel());
        tabs.add("Thong ke", statsPanel());
        add(tabs);
        JButton save=new JButton("Luu du lieu"); save.addActionListener(e->{try{svc.save(); JOptionPane.showMessageDialog(this,"Da luu");}catch(Exception ex){err(ex);}});
        add(save, BorderLayout.SOUTH);
        refreshAll();
    }
    private JPanel studentPanel(){
        studentModel=new DefaultTableModel(new String[]{"Ma SV","Ho ten","Gioi tinh","Lop","SDT","Phong"},0);
        JTable t=new JTable(studentModel);
        JButton add=new JButton("Them SV"); add.addActionListener(e->{
            String id=JOptionPane.showInputDialog(this,"Ma SV:"); if(id==null)return;
            String name=JOptionPane.showInputDialog(this,"Ho ten:"); if(name==null)return;
            String gender=JOptionPane.showInputDialog(this,"Gioi tinh (Nam/Nu):","Nam");
            String cls=JOptionPane.showInputDialog(this,"Lop:","K13-CNTT");
            try{svc.addStudent(new Student(id,name,gender,cls,"","","")); svc.save(); refreshAll();}catch(Exception ex){err(ex);}
        });
        JButton del=new JButton("Xoa SV (ADMIN)"); del.addActionListener(e->{int r=t.getSelectedRow(); if(r<0)return; try{svc.deleteStudent(current,(String)studentModel.getValueAt(r,0)); svc.save(); refreshAll();}catch(Exception ex){err(ex);}});
        JButton reg=new JButton("Dang ky phong"); reg.addActionListener(e->{int r=t.getSelectedRow(); if(r<0)return; String room=JOptionPane.showInputDialog(this,"Ma phong (vd A201):"); if(room==null)return; try{svc.registerRoom((String)studentModel.getValueAt(r,0),room,LocalDate.now(),LocalDate.now().plusMonths(9)); svc.save(); refreshAll();}catch(Exception ex){err(ex);}});
        JButton trans=new JButton("Chuyen phong"); trans.addActionListener(e->{int r=t.getSelectedRow(); if(r<0)return; String room=JOptionPane.showInputDialog(this,"Phong moi:"); if(room==null)return; try{svc.transferRoom((String)studentModel.getValueAt(r,0),room); svc.save(); refreshAll();}catch(Exception ex){err(ex);}});
        JButton search=new JButton("Tra cuu"); search.addActionListener(e->{String kw=JOptionPane.showInputDialog(this,"Ma / ten SV:"); refreshStudents(kw);});
        JPanel p=new JPanel(new BorderLayout()); p.add(new JScrollPane(t)); JPanel b=new JPanel(); b.add(add); b.add(del); b.add(reg); b.add(trans); b.add(search); p.add(b,BorderLayout.SOUTH); return p;
    }
    private JPanel roomPanel(){
        roomModel=new DefaultTableModel(new String[]{"Ma phong","Toa","So phong","Giuong","Dang o","Con trong","Gia/thang","Trang thai"},0);
        JTable t=new JTable(roomModel); JPanel p=new JPanel(new BorderLayout()); p.add(new JScrollPane(t)); return p;
    }
    private JPanel billPanel(){
        billModel=new DefaultTableModel(new String[]{"Ma","Phong","Thang","Dien (kWh)","Tien dien","Nuoc (m3)","Tien nuoc","Tong","Da tra"},0);
        JTable t=new JTable(billModel);
        JButton add=new JButton("Tao hoa don"); add.addActionListener(e->{
            try{ String id=JOptionPane.showInputDialog(this,"Ma bill:"); String room=JOptionPane.showInputDialog(this,"Phong:"); String month=JOptionPane.showInputDialog(this,"Thang yyyy-MM:","2023-04");
            double eo=Double.parseDouble(JOptionPane.showInputDialog(this,"Dien cu:")); double en=Double.parseDouble(JOptionPane.showInputDialog(this,"Dien moi:"));
            double wo=Double.parseDouble(JOptionPane.showInputDialog(this,"Nuoc cu:")); double wn=Double.parseDouble(JOptionPane.showInputDialog(this,"Nuoc moi:"));
            svc.createBill(id,room,month,eo,en,3500,wo,wn,15000); svc.save(); refreshAll(); }catch(Exception ex){err(ex);}
        });
        JPanel p=new JPanel(new BorderLayout()); p.add(new JScrollPane(t)); JPanel b=new JPanel(); b.add(add); p.add(b,BorderLayout.SOUTH); return p;
    }
    private JPanel statsPanel(){ JPanel p=new JPanel(new BorderLayout()); statsLabel=new JLabel("",SwingConstants.CENTER); statsLabel.setFont(new Font("SansSerif",Font.BOLD,16)); p.add(statsLabel); return p; }
    private void refreshStudents(String kw){ studentModel.setRowCount(0); svc.searchStudents(kw==null?"":kw).forEach(s->studentModel.addRow(new Object[]{s.getId(),s.getFullName(),s.getGender(),s.getClassName(),s.getPhone(),s.getRoomId().isEmpty()?"-":s.getRoomId()})); }
    private void refreshAll(){
        if(studentModel==null) return; refreshStudents("");
        roomModel.setRowCount(0); svc.getStore().rooms.forEach(r->{int o=svc.occupancy(r.getId()); roomModel.addRow(new Object[]{r.getId(),r.getBuildingId(),r.getRoomNumber(),r.getCapacity(),o,r.freeBeds(o),r.getPricePerMonth(),o==0?"Trong":(svc.isFull(r)?"Day":"Con cho")});});
        billModel.setRowCount(0); svc.getStore().bills.forEach(b->billModel.addRow(new Object[]{b.getId(),b.getRoomId(),b.getMonth(),b.elecConsumption(),b.elecCost(),b.waterConsumption(),b.waterCost(),b.total(),b.isPaid()}));
        DormService.Stats st=svc.stats(); statsLabel.setText("<html>Tong phong: "+st.totalRooms()+" | Trong: "+st.emptyRooms()+" | Day: "+st.fullRooms()+" | Con cho: "+st.partialRooms()+"<br>Tong giuong: "+st.totalBeds()+" | Da o: "+st.occupiedBeds()+" | SV: "+st.totalStudents()+" | SV chua co phong: "+st.studentsWithoutRoom()+"</html>");
    }
    private void err(Exception e){ JOptionPane.showMessageDialog(this,"Loi: "+e.getMessage(),"Loi",JOptionPane.ERROR_MESSAGE); }
    public static void main(String[] args) throws Exception {
        DormService svc=new DormService(new DataStore(Path.of("data"))); svc.load();
        SwingUtilities.invokeLater(()->{
            String u=JOptionPane.showInputDialog(null,"Tai khoan (admin/staff):","admin");
            String p=JOptionPane.showInputDialog(null,"Mat khau:","admin123");
            svc.login(u,p).ifPresentOrElse(user->new DormApp(svc,user).setVisible(true),()->{JOptionPane.showMessageDialog(null,"Dang nhap that bai"); System.exit(1);});
        });
    }
}
