package dorm;
import java.time.LocalDate; import java.time.temporal.ChronoUnit;
public class Contract {
    public enum Status{ACTIVE,ENDED}
    private String id, studentId, roomId;
    private LocalDate startDate, endDate;
    private long deposit, monthlyPrice;
    private Status status;
    public Contract(String id,String studentId,String roomId,LocalDate start,LocalDate end,long deposit,long monthlyPrice,Status status){
        if(id==null||id.isBlank()) throw new IllegalArgumentException("Ma HD rong");
        if(end.isBefore(start)) throw new IllegalArgumentException("Ngay ket thuc truoc ngay bat dau");
        this.id=id; this.studentId=studentId; this.roomId=roomId; this.startDate=start; this.endDate=end;
        this.deposit=deposit; this.monthlyPrice=monthlyPrice; this.status=status==null?Status.ACTIVE:status;
    }
    public String getId(){return id;} public String getStudentId(){return studentId;} public String getRoomId(){return roomId;}
    public void setRoomId(String r){roomId=r;}
    public LocalDate getStartDate(){return startDate;} public LocalDate getEndDate(){return endDate;}
    public long getDeposit(){return deposit;} public long getMonthlyPrice(){return monthlyPrice;}
    public Status getStatus(){return status;} public void setStatus(Status s){status=s;}
    public long months(){return Math.max(1, ChronoUnit.MONTHS.between(startDate, endDate));}
    public long totalRoomFee(){return months()*monthlyPrice;}
    public String toLine(){return String.join("|",id,studentId,roomId,startDate.toString(),endDate.toString(),String.valueOf(deposit),String.valueOf(monthlyPrice),status.name());}
    public static Contract fromLine(String l){String[] p=l.split("\\|",-1); return new Contract(p[0],p[1],p[2],LocalDate.parse(p[3]),LocalDate.parse(p[4]),Long.parseLong(p[5]),Long.parseLong(p[6]),Status.valueOf(p[7]));}
}
