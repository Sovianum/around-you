package ru.mail.park.aroundyou.model;


public class MeetRequestUpdate {
    private int id;
    private String status;

    public MeetRequestUpdate(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
