package dorm;
public class UtilityBill {
    private String id, roomId, month;
    private double elecOld, elecNew, waterOld, waterNew;
    private long elecPrice, waterPrice;
    private boolean paid;
    public UtilityBill(String id,String roomId,String month,double elecOld,double elecNew,long elecPrice,double waterOld,double waterNew,long waterPrice,boolean paid){
        if(elecNew<elecOld) throw new IllegalArgumentException("Chi so dien moi < cu");
        if(waterNew<waterOld) throw new IllegalArgumentException("Chi so nuoc moi < cu");
        this.id=id; this.roomId=roomId; this.month=month; this.elecOld=elecOld; this.elecNew=elecNew;
        this.elecPrice=elecPrice; this.waterOld=waterOld; this.waterNew=waterNew; this.waterPrice=waterPrice; this.paid=paid;
    }
    public String getId(){return id;} public String getRoomId(){return roomId;} public String getMonth(){return month;}
    public double elecConsumption(){return elecNew-elecOld;}
    public double waterConsumption(){return waterNew-waterOld;}
    public long elecCost(){return Math.round(elecConsumption()*elecPrice);}
    public long waterCost(){return Math.round(waterConsumption()*waterPrice);}
    public long total(){return elecCost()+waterCost();}
    public long sharePerStudent(int occupants){return occupants<=0? total() : total()/occupants;}
    public boolean isPaid(){return paid;} public void setPaid(boolean p){paid=p;}
    public double getElecOld(){return elecOld;} public double getElecNew(){return elecNew;}
    public double getWaterOld(){return waterOld;} public double getWaterNew(){return waterNew;}
    public long getElecPrice(){return elecPrice;} public long getWaterPrice(){return waterPrice;}
    public String toLine(){return String.join("|",id,roomId,month,String.valueOf(elecOld),String.valueOf(elecNew),String.valueOf(elecPrice),String.valueOf(waterOld),String.valueOf(waterNew),String.valueOf(waterPrice),String.valueOf(paid));}
    public static UtilityBill fromLine(String l){String[] p=l.split("\\|",-1); return new UtilityBill(p[0],p[1],p[2],Double.parseDouble(p[3]),Double.parseDouble(p[4]),Long.parseLong(p[5]),Double.parseDouble(p[6]),Double.parseDouble(p[7]),Long.parseLong(p[8]),Boolean.parseBoolean(p[9]));}
}
