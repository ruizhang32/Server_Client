import java.util.Date;

public class Record {

    private int id;
    private String ip_address;
    private String access_time;

    public Record(int id, String ip_address, String access_time, String parameters) {
        this.id = id;
        this.ip_address = ip_address;
        this.access_time = access_time;
        this.parameters = parameters;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getAccess_time() {
        return access_time;
    }

    public void setAccess_time(String access_time) {
        this.access_time = access_time;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    private String parameters;
}
