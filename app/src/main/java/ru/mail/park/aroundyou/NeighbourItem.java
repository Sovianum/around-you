package ru.mail.park.aroundyou;

public class NeighbourItem {
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
