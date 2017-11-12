package ru.mail.park.aroundyou;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.mail.park.aroundyou.model.MeetRequestUpdate;
import ru.mail.park.aroundyou.model.Position;
import ru.mail.park.aroundyou.model.MeetRequest;

public interface LoaderService {

    @POST("api/v1/auth/register")
    Call<ResponseBody> registerUser(@Body RequestBody user);

    @POST("api/v1/auth/login")
    Call<ResponseBody> loginUser(@Body RequestBody user);

    @POST("api/v1/user/position/save")
    Call<ResponseBody> savePosition(@Header(ServerInfo.AUTH_HEADER) String token, @Body Position position);

    @GET("api/v1/user/position/neighbours")
    Call<ResponseBody> getNeighbours(@Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/position/neighbour/{id}")
    Call<ResponseBody> getNeighbourPosition(@Path("id") int id, @Header(ServerInfo.AUTH_HEADER) String token);

    @POST("api/v1/user/request/create")
    Call<ResponseBody> createRequest(@Body MeetRequest requestItem, @Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/all")
    Call<ResponseBody> getAllRequests(@Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/income/pending")
    Call<ResponseBody> getIncomePendingRequests(@Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/outcome/pending")
    Call<ResponseBody> getOutcomePendingRequests(@Header(ServerInfo.AUTH_HEADER) String token);

    @POST("api/v1/user/request/update")
    Call<ResponseBody> updateRequest(@Body MeetRequestUpdate update, @Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/new")
    Call<ResponseBody> getNewRequests(@Header(ServerInfo.AUTH_HEADER) String token);


}
