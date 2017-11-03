package ru.mail.park.aroundyou;

/**
 * Created by sergey on 03.11.17.
 */

public class RegisterUser {
    private String login;
    private String password;
    private String about;
    private Integer age;
    //private enum Sex {
    //    M, F
    //}
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

    /*public void setSex(String sex) {
        if (Sex.M.toString().equals(sex)) {
            this.sex = Sex.M;
        } else {
            this.sex = Sex.F;
        }
    }*/
}
