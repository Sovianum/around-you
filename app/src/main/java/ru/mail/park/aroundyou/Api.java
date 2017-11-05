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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

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
                .baseUrl("https://around-you-backend.herokuapp.com")
                .build();
        service = retrofit.create(LoaderService.class);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static Api getInstance() {
        return INSTANCE;
    }

    public ListenerHandler<OnSmthGetListener<List<NeighbourItem>>>
    getNeighbours(final OnSmthGetListener<List<NeighbourItem>> listener) {
        final ListenerHandler<OnSmthGetListener<List<NeighbourItem>>> handler = new ListenerHandler<>(listener);
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

    public ListenerHandler<OnSmthGetListener<ReceivedData>>
    loginUser(final OnSmthGetListener<ReceivedData> listener, final LoginUser user) {
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
    registerUser(final OnSmthGetListener<ReceivedData> listener, final RegisterUser user) {
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

    private List<NeighbourItem> parseNeighbours(final String body) throws IOException {
        try {
            //TODO: поправить это
            //JsonObject jsonArray = GSON.fromJson(body, JsonObject.class);
            //Type collectionType = new TypeToken<List<NeighbourItem>>(){}.getType();
            //List<NeighbourItem> users = GSON.fromJson(jsonArray.getAsJsonArray("data"), collectionType);
            //return users;
            final List<NeighbourItem> receivedNeighbourList = new ArrayList<>();
            JSONObject bodyJSON = new JSONObject(body);
            JSONArray array = bodyJSON.getJSONArray(ServerInfo.NEIGHBOURS_ARRAY_NAME);
            for (int i = 0; i < array.length(); i++) {
                receivedNeighbourList.add(new NeighbourItem(array.getJSONObject(i)));
            }
            return receivedNeighbourList;
        } catch (JsonSyntaxException | JSONException e) {
            throw new IOException(e);
        }
    }

    private ReceivedData parseLoginData(final String body) {
        return GSON.fromJson(body, ReceivedData.class);
    }

    private <T> void invokeSuccess(final ListenerHandler<OnSmthGetListener<T>> handler,
                               final T items) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSmthGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeSucces");
                    listener.onGettingSuccess(items);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private <T> void invokeError(final ListenerHandler<OnSmthGetListener<T>> handler,
                                 final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSmthGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeError");
                    listener.onGettingError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    public interface OnSmthGetListener<T> {
        void onGettingSuccess(final T items);

        void onGettingError(final Exception error);
    }
}
