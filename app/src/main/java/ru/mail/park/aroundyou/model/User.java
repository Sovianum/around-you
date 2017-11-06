package ru.mail.park.aroundyou.model;

public class User {
    private String login;
    private String password;
    private String about;
    private Integer age;
    private String sex;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
