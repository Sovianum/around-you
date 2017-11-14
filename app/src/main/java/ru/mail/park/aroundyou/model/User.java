package ru.mail.park.aroundyou.model;

import java.io.Serializable;


public class User implements Serializable {
    private String login;
    private String password;
    private String sex;
    private String about;
    private int age;
    private int id;

    public User() {}

    public User(String login, String sex, String about, int age, int id) {
        this.login = login;
        this.sex = sex;
        this.about = about;
        this.age = age;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
