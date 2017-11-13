package ru.mail.park.aroundyou.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class Position {
    private int id;
    private int userId;
    private Point point;
    private Timestamp time;

    public Position() {
        point = new Point();
    }

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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
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

        public Point() {}

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Point(JSONObject jsonObject) throws JSONException {
            x = jsonObject.getDouble("x");
            y = jsonObject.getDouble("y");
        }

        public double getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
