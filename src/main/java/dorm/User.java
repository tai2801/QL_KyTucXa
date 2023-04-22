package dorm;
public class User {
    public enum Role { ADMIN, STAFF }
    private String username, password, fullName;
    private Role role;
    public User(String username,String password,String fullName,Role role){
        if(username==null||username.isBlank()) throw new IllegalArgumentException("username rong");
        if(password==null||password.isBlank()) throw new IllegalArgumentException("password rong");
        this.username=username.trim(); this.password=password; this.fullName=fullName==null?"":fullName.trim(); this.role=role==null?Role.STAFF:role;
    }
    public String getUsername(){return username;} public String getPassword(){return password;}
    public String getFullName(){return fullName;} public Role getRole(){return role;}
    public boolean isAdmin(){return role==Role.ADMIN;}
    public boolean checkPassword(String p){return password.equals(p);}
    public void setFullName(String v){fullName=v;} public void setRole(Role r){role=r;}
    public String toLine(){return String.join("|",username,password,fullName,role.name());}
    public static User fromLine(String line){String[] p=line.split("\\|",-1); return new User(p[0],p[1],p[2],Role.valueOf(p[3]));}
    @Override public String toString(){return username+" ("+role+") - "+fullName;}
}
