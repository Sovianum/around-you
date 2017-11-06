package ru.mail.park.aroundyou.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;

import ru.mail.park.aroundyou.model.Common;

public class MeetRequestItem {
    private int id;
    private int requesterId;
    private int requestedId;
    private Timestamp time;
    private String status;

    public MeetRequestItem(JSONObject jsonObject) throws JSONException, ParseException {
        id = jsonObject.getInt("id");
        requestedId = jsonObject.getInt("requested_id");
        requesterId = jsonObject.getInt("requester_id");
        status = jsonObject.getString("status");
        time = Common.extractTimestamp("time", jsonObject);
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
