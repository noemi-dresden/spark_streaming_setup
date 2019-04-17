package examplePackage.data;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RumDeserializer.class)
public class Rum {

    private Double timestamp;
    private String user;
    private String keyword;
    private String event;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
