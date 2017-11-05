package ru.mail.park.aroundyou;

public class ReceivedData {
    private String data;
    private String error;

    public void setData(String data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
