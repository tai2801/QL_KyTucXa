package dorm;
public class Building {
    private String id, name, gender;
    private int floors;
    public Building(String id,String name,int floors,String gender){
        if(id==null||id.isBlank()) throw new IllegalArgumentException("Ma toa rong");
        if(floors<=0) throw new IllegalArgumentException("So tang phai >0");
        this.id=id.trim(); this.name=name; this.floors=floors; this.gender=gender==null?"":gender;
    }
    public String getId(){return id;} public String getName(){return name;}
    public int getFloors(){return floors;} public String getGender(){return gender;}
    public String toLine(){return String.join("|",id,name,String.valueOf(floors),gender);}
    public static Building fromLine(String l){String[] p=l.split("\\|",-1); return new Building(p[0],p[1],Integer.parseInt(p[2]),p[3]);}
    @Override public String toString(){return id+" - "+name+" ("+floors+" tang, "+gender+")";}
}
