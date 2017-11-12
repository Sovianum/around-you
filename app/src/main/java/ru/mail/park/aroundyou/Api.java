package ru.mail.park.aroundyou;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mail.park.aroundyou.model.MeetRequestUpdate;
import ru.mail.park.aroundyou.model.User;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.model.Position;

import static java.net.HttpURLConnection.HTTP_OK;

public class Api {
    private static final Api INSTANCE = new Api();
    private static final Gson GSON = new GsonBuilder().create();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final LoaderService service;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String token;

    private Api() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerInfo.BACKEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(LoaderService.class);
    }

    public void
    setToken(String token) {
        this.token = token;
    }

    public static Api
    getInstance() {
        return INSTANCE;
    }

    public ListenerHandler<OnSmthGetListener<Integer>>
    savePosition(final Position position, final OnSmthGetListener<Integer> listener) {
        final ListenerHandler<OnSmthGetListener<Integer>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service
                            .savePosition(token, position)
                            .execute();
                    invokeSuccess(handler, response.code());
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<List<User>>>
    getNeighbours(final OnSmthGetListener<List<User>> listener) {
        final ListenerHandler<OnSmthGetListener<List<User>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service.getNeighbours(token).execute();
                    if (response.code() != HTTP_OK) {
                        throw new IOException("HTTP code " + response.code());
                    }
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseNeighbours(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<Position>>
    getNeighbourPosition(final int neighbourId, final OnSmthGetListener<Position> listener) {
        final ListenerHandler<OnSmthGetListener<Position>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service.getNeighbourPosition(neighbourId, token).execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parsePosition(body));
                    }

                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<Integer>>
    createMeetRequest(final Integer requestedId, final OnSmthGetListener<Integer> listener) {
        final ListenerHandler<OnSmthGetListener<Integer>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service
                            .createRequest(new MeetRequest(requestedId), token)
                            .execute();
                    invokeSuccess(handler, response.code());
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<MeetRequest>>
    updateMeetRequest(final MeetRequestUpdate update, final OnSmthGetListener<MeetRequest> listener) {
        final ListenerHandler<OnSmthGetListener<MeetRequest>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service
                            .updateRequest(update, token)
                            .execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        JSONObject jsonBody = new JSONObject(body);
                        try {
                            JSONObject jsonData = jsonBody.getJSONObject("data");
                            invokeSuccess(handler, new MeetRequest(jsonData));
                        } catch (JSONException e) {
                            throw new IOException(jsonBody.getString("err_msg"));
                        }
                    }
                } catch (IOException | JSONException | ParseException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<List<MeetRequest>>>
    getOutcomePendingRequests(final OnSmthGetListener<List<MeetRequest>> listener) {
        final ListenerHandler<OnSmthGetListener<List<MeetRequest>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service.getOutcomePendingRequests(token).execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseMeetRequests(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<List<MeetRequest>>>
    getIncomePendingRequests(final OnSmthGetListener<List<MeetRequest>> listener) {
        final ListenerHandler<OnSmthGetListener<List<MeetRequest>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service.getIncomePendingRequests(token).execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseMeetRequests(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<ReceivedData>>
    loginUser(final OnSmthGetListener<ReceivedData> listener, final User user) {
        final ListenerHandler<OnSmthGetListener<ReceivedData>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String strRequestBody = GSON.toJson(user);
                    RequestBody requestBody =
                            RequestBody.create(MediaType.parse("text/plain"), strRequestBody);
                    final Response<ResponseBody> response = service.loginUser(requestBody).execute();
                    if (response.code() != HTTP_OK) {
                        throw new IOException("HTTP code " + response.code());
                    }
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseLoginData(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<ReceivedData>>
    registerUser(final OnSmthGetListener<ReceivedData> listener, final User user) {
        final ListenerHandler<OnSmthGetListener<ReceivedData>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String strRequestBody = GSON.toJson(user);
                    RequestBody requestBody =
                            RequestBody.create(MediaType.parse("text/plain"), strRequestBody);
                    final Response<ResponseBody> response = service.registerUser(requestBody).execute();
                    if (response.code() != HTTP_OK) {
                        throw new IOException("HTTP code " + response.code());
                    }
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        //invokeSuccess(handler, parseNeighbours(body));
                        invokeSuccess(handler, parseLoginData(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private List<User>
    parseNeighbours(final String body) throws IOException {
        try {
            //TODO: поправить это
            //JsonObject jsonArray = GSON.fromJson(body, JsonObject.class);
            //Type collectionType = new TypeToken<List<User>>(){}.getType();
            //List<User> users = GSON.fromJson(jsonArray.getAsJsonArray("data"), collectionType);
            //return users;
            final List<User> receivedNeighbourList = new ArrayList<>();
            JSONObject bodyJSON = new JSONObject(body);
            JSONArray array = bodyJSON.getJSONArray(ServerInfo.NEIGHBOURS_ARRAY_NAME);
            for (int i = 0; i < array.length(); i++) {
                receivedNeighbourList.add(new User(array.getJSONObject(i)));
            }
            return receivedNeighbourList;
        } catch (JsonSyntaxException | JSONException e) {
            throw new IOException(e);
        }
    }

    private Position
    parsePosition(final String body) throws IOException {
        try {
            JSONObject bodyJSON = new JSONObject(body);
            return new Position(bodyJSON.getJSONObject(ServerInfo.NEIGHBOURS_ARRAY_NAME));
        } catch (JsonSyntaxException | JSONException | ParseException e) {
            throw new IOException(e);
        }
    }

    private List<MeetRequest>
    parseMeetRequests(final String body) throws IOException {
        try {
            final List<MeetRequest> receivedRequestList = new ArrayList<>();
            JSONObject bodyJSON = new JSONObject(body);
            JSONArray array = bodyJSON.getJSONArray(ServerInfo.NEIGHBOURS_ARRAY_NAME);
            for (int i = 0; i < array.length(); i++) {
                receivedRequestList.add(new MeetRequest(array.getJSONObject(i)));
            }
            return receivedRequestList;
        } catch (JsonSyntaxException | JSONException | ParseException e) {
            throw new IOException(e);
        }
    }

    private ReceivedData
    parseLoginData(final String body) {
        return GSON.fromJson(body, ReceivedData.class);
    }

    private <T> void
    invokeSuccess(final ListenerHandler<OnSmthGetListener<T>> handler, final T payload) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSmthGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeSucces");
                    listener.onSuccess(payload);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private <T> void
    invokeError(final ListenerHandler<OnSmthGetListener<T>> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSmthGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeError");
                    listener.onError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    public interface OnSmthGetListener<T> {
        void onSuccess(final T items);

        void onError(final Exception error);
    }
}
