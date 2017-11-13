package ru.mail.park.aroundyou.datasource;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.mail.park.aroundyou.common.ServerInfo;
import ru.mail.park.aroundyou.model.MeetRequestUpdate;
import ru.mail.park.aroundyou.model.Position;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.model.ServerResponse;
import ru.mail.park.aroundyou.model.User;

public interface LoaderService {

    @POST("api/v1/auth/register")
    Call<ServerResponse<String>> registerUser(@Body RequestBody user);

    @POST("api/v1/auth/login")
    Call<ServerResponse<String>> loginUser(@Body RequestBody user);

    @GET("api/user/self")
    Call<ServerResponse<User>> getSelfInfo(@Header(ServerInfo.AUTH_HEADER) String token);

    @POST("api/v1/user/position/save")
    Call<ResponseBody> savePosition(@Header(ServerInfo.AUTH_HEADER) String token, @Body Position position);

    @GET("api/v1/user/position/neighbours")
    Call<ServerResponse<List<User>>> getNeighbours(@Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/position/neighbour/{id}")
    Call<ServerResponse<Position>> getNeighbourPosition(@Path("id") int id, @Header(ServerInfo.AUTH_HEADER) String token);

    @POST("api/v1/user/request/create")
    Call<ServerResponse<MeetRequest>> createRequest(@Body MeetRequest requestItem, @Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/all")
    Call<ServerResponse<List<MeetRequest>>> getAllRequests(@Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/income/pending")
    Call<ServerResponse<List<MeetRequest>>> getIncomePendingRequests(@Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/outcome/pending")
    Call<ServerResponse<List<MeetRequest>>> getOutcomePendingRequests(@Header(ServerInfo.AUTH_HEADER) String token);

    @POST("api/v1/user/request/update")
    Call<ServerResponse<MeetRequest>> updateRequest(@Body MeetRequestUpdate update, @Header(ServerInfo.AUTH_HEADER) String token);

    @GET("api/v1/user/request/new")
    Call<ServerResponse<List<MeetRequest>>> getNewRequests(@Header(ServerInfo.AUTH_HEADER) String token);


}
