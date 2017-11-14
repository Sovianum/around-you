package ru.mail.park.aroundyou.model;

import java.sql.Timestamp;

public class Position {
    private int id;
    private int userId;
    private Point point;
    private Timestamp time;

    public Position(double x, double y) {
        point = new Point(x, y);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public Point getPoint() {
        return point;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public class Point {
        private double x;
        private double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }
}
