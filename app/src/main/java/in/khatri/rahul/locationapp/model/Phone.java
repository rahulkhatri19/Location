package in.khatri.rahul.locationapp.model;

public class Phone {
   private String position, name;
   private long password;

    public Phone() {
    }

    public Phone(String position, String name, long password) {
        this.position = position;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public long getPassword() {
        return password;
    }

    public void setPassword(long password) {
        this.password = password;
    }
}
