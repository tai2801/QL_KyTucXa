package dorm;
public class Student {
    private String id, fullName, gender, className, phone, hometown, roomId;
    public Student(String id,String fullName,String gender,String className,String phone,String hometown,String roomId){
        if(id==null||id.isBlank()) throw new IllegalArgumentException("Ma SV rong");
        if(fullName==null||fullName.isBlank()) throw new IllegalArgumentException("Ho ten rong");
        this.id=id.trim(); this.fullName=fullName.trim(); this.gender=gender==null?"":gender;
        this.className=className==null?"":className; this.phone=phone==null?"":phone;
        this.hometown=hometown==null?"":hometown; this.roomId=(roomId==null||roomId.isBlank())?"":roomId.trim();
    }
    public String getId(){return id;} public String getFullName(){return fullName;}
    public String getGender(){return gender;} public String getClassName(){return className;}
    public String getPhone(){return phone;} public String getHometown(){return hometown;}
    public String getRoomId(){return roomId;} public void setRoomId(String r){roomId=r==null?"":r;}
    public void setFullName(String v){fullName=v;} public void setPhone(String v){phone=v;}
    public void setClassName(String v){className=v;}
    public String toLine(){return String.join("|",id,fullName,gender,className,phone,hometown,roomId);}
    public static Student fromLine(String l){String[] p=l.split("\\|",-1); while(p.length<7){String[] n=new String[p.length+1]; System.arraycopy(p,0,n,0,p.length); n[p.length]=""; p=n;} return new Student(p[0],p[1],p[2],p[3],p[4],p[5],p[6]);}
    @Override public String toString(){return id+" - "+fullName+" ("+className+") phong: "+(roomId.isEmpty()?"chua co":roomId);}
}
