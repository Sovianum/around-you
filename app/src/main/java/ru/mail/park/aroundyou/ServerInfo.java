package ru.mail.park.aroundyou;

/**
 * Created by sergey on 28.10.17.
 */

public class ServerInfo {
    public static final String URL_BACKEND = "https://around-you-backend.herokuapp.com";
    public static final String URL_REGISTER = URL_BACKEND +
            "/api/v1/auth/register";
    public static final String URL_LOGIN = URL_BACKEND +
            "/api/v1/auth/login";
    public static final String URL_NEIGHBOUR = URL_BACKEND +
            "/api/v1/user/position/neighbours";
    public static final String URL_MEETING = URL_BACKEND +
            "/api/v1/user/request/create";
    public static final String URL_FRESH_REQUEST = URL_BACKEND +
            "/api/v1/user/request/";
    public static final String URL_UPDATE_REQUEST = URL_BACKEND +
            "/api/v1/user/request/new";
    public static final String AUTHORIZATION = "Authorization";
    public static final String NEIGHBOURS_ARRAY_NAME = "data";
}
