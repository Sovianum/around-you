package ru.mail.park.aroundyou.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Common {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static Timestamp extractTimestamp(String key, JSONObject jsonObject) throws JSONException, ParseException {
        return new Timestamp(
                (new SimpleDateFormat(DATE_FORMAT)).parse(jsonObject.getString(key)).getTime()
        );
    }
}
