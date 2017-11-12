package ru.mail.park.aroundyou.requests;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;

import ru.mail.park.aroundyou.model.Common;

public class MeetRequestItem {
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
    private Timestamp time;
    private String status;

    public MeetRequestItem(JSONObject jsonObject) throws JSONException, ParseException {
        id = jsonObject.getInt("id");
        requestedId = jsonObject.getInt("requested_id");
        requesterId = jsonObject.getInt("requester_id");
        requestedLogin = jsonObject.getString("requested_login");
        requesterLogin = jsonObject.getString("requester_login");
        requesterAbout = jsonObject.getString("requester_about");
        status = jsonObject.getString("status");
        time = Common.extractTimestamp("time", jsonObject);
    }

    public MeetRequestItem(int requestedId) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getRequestedId() {
        return requestedId;
    }

    public void setRequestedId(int requestedId) {
        this.requestedId = requestedId;
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

    public void setStatus(String status) {
        this.status = status;
    }
}
