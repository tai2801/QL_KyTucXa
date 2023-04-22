package dorm;
public class Room {
    private String id, buildingId, roomNumber, gender;
    private int capacity; // so giuong = suc chua
    private long pricePerMonth;
    public Room(String id,String buildingId,String roomNumber,int capacity,long pricePerMonth,String gender){
        if(id==null||id.isBlank()) throw new IllegalArgumentException("Ma phong rong");
        if(capacity<=0) throw new IllegalArgumentException("Suc chua (so giuong) phai >0");
        if(pricePerMonth<0) throw new IllegalArgumentException("Gia phong khong am");
        this.id=id.trim(); this.buildingId=buildingId; this.roomNumber=roomNumber;
        this.capacity=capacity; this.pricePerMonth=pricePerMonth; this.gender=gender==null?"":gender;
    }
    public String getId(){return id;} public String getBuildingId(){return buildingId;}
    public String getRoomNumber(){return roomNumber;} public int getCapacity(){return capacity;}
    public int getBedCount(){return capacity;} // moi giuong = 1 cho o
    public long getPricePerMonth(){return pricePerMonth;} public String getGender(){return gender;}
    public boolean isFull(int occupancy){return occupancy>=capacity;}
    public int freeBeds(int occupancy){return Math.max(0,capacity-occupancy);}
    public String toLine(){return String.join("|",id,buildingId,roomNumber,String.valueOf(capacity),String.valueOf(pricePerMonth),gender);}
    public static Room fromLine(String l){String[] p=l.split("\\|",-1); return new Room(p[0],p[1],p[2],Integer.parseInt(p[3]),Long.parseLong(p[4]),p[5]);}
    @Override public String toString(){return id+" phong "+roomNumber+" toa "+buildingId+" giuong:"+capacity+" gia:"+pricePerMonth;}
}
