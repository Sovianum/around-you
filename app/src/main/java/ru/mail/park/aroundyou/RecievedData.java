package ru.mail.park.aroundyou;

/**
 * Created by sergey on 03.11.17.
 */

public class RecievedData {
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
