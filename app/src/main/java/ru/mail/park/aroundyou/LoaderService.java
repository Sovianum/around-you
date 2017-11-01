package ru.mail.park.aroundyou;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by sergey on 01.11.17.
 */
public interface LoaderService {

    @POST("api/v1/auth/register")
    Call<ResponseBody> registerUser();

    @POST("api/v1/auth/login")
    Call<ResponseBody> loginUser();

    @Headers("Authorization: " + ServerInfo.jwtStub)
    @POST("api/v1/user/position/save")
    Call<ResponseBody> savePosition();

    @Headers("Authorization: " + ServerInfo.jwtStub)
    @GET("api/v1/user/position/neighbours")
    Call<ResponseBody> getNeighbours();

    @Headers("Authorization: " + ServerInfo.jwtStub)
    @POST("api/v1/user/request/create")
    Call<ResponseBody> createRequest();

    @Headers("Authorization: " + ServerInfo.jwtStub)
    @GET("api/v1/user/request")
    Call<ResponseBody> getAllRequest();

    @Headers("Authorization: " + ServerInfo.jwtStub)
    @POST("api/v1/user/request/update")
    Call<ResponseBody> updateRequest();

    @Headers("Authorization: " + ServerInfo.jwtStub)
    @GET("api/v1/user/request/new")
    Call<ResponseBody> getNewRequest();


}
