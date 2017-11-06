package ru.mail.park.aroundyou.neighbours;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class NeighbourItem implements Serializable {
    private String login;
    private String sex;
    private String about;
    private int age;

    public NeighbourItem(String login, String sex, String about, int age) {
        this.login = login;
        this.sex = sex;
        this.about = about;
        this.age = age;
    }

    public NeighbourItem(JSONObject neighbourJSON) throws JSONException {
        this.login = neighbourJSON.getString("login");
        this.sex = neighbourJSON.getString("sex");
        this.about = neighbourJSON.getString("about");
        this.age = Integer.parseInt(neighbourJSON.getString("age"));
    }

    public String getLogin() {
        return login;
    }

    public String getSex() {
        return sex;
    }

    public String getAbout() {
        return about;
    }

    public int getAge() {
        return age;
    }
}
