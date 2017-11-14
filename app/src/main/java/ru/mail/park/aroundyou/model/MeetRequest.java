package ru.mail.park.aroundyou.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class MeetRequest {
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_DECLINED = "DECLINED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_INTERRUPTED = "INTERRUPTED";

    private int id;
    @SerializedName("requester_id")
    private int requesterId;
    @SerializedName("requested_id")
    private int requestedId;
    @SerializedName("requester_login")
    private String requesterLogin;
    @SerializedName("requested_login")
    private String requestedLogin;
    @SerializedName("requester_about")
    private String requesterAbout;
    @SerializedName("requested_about")
    private String requestedAbout;
    private Timestamp time;
    private String status;

    public MeetRequest(int id, int requesterId, int requestedId, String requesterLogin,
                       String requestedLogin, String requesterAbout, String requestedAbout, Timestamp time, String status) {
        this.id = id;
        this.requestedId = requestedId;
        this.requesterId = requesterId;
        this.requesterLogin = requesterLogin;
        this.requestedLogin = requestedLogin;
        this.requesterAbout = requesterAbout;
        this.requestedAbout = requestedAbout;
        this.time = time;
        this.status = status;

    }

    public MeetRequest(int requestedId) {
        this.requestedId = requestedId;
    }

    public String getRequesterLogin() {
        return requesterLogin;
    }

    public String getRequestedLogin() {
        return requestedLogin;
    }

    public String getRequesterAbout() {
        return requesterAbout;
    }

    public String getRequestedAbout() {
        return requestedAbout;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public int getRequestedId() {
        return requestedId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

}
