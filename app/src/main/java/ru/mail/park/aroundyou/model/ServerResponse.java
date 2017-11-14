package ru.mail.park.aroundyou.model;


import com.google.gson.annotations.SerializedName;

public class ServerResponse<T> {
    @SerializedName("err_msg")
    private String errMsg;

    @SerializedName("data")
    private T data;

    public String getErrMsg() {
        return errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
