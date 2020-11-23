package neu.madcourse.walkwithme;

public class User {
    private String username;
    private String password;
    private double weight;
    private double height;
    private long time;
    private double BMI;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBMI() {
        return BMI;
    }

    public User(final String username, final String password, final long timeStamp, final double weight, final double height) {
        this.username = username;
        this.password = password;
        this.time = timeStamp;
        this.weight = weight;
        this.height = height;
        this.BMI = 30;
    }
}
