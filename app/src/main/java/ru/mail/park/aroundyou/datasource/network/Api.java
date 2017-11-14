package ru.mail.park.aroundyou.datasource.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.common.ServerInfo;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.model.MeetRequestUpdate;
import ru.mail.park.aroundyou.model.Position;
import ru.mail.park.aroundyou.model.ServerResponse;
import ru.mail.park.aroundyou.model.User;

import static java.net.HttpURLConnection.HTTP_OK;

public class Api {
    private static final Api INSTANCE = new Api();
    private static final Gson GSON = new GsonBuilder().create();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final LoaderService service;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String token;

    private Api() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerInfo.BACKEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
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

    public ListenerHandler<OnSmthGetListener<User>>
    getSelfInfo(final OnSmthGetListener<User> listener) {
        final ListenerHandler<OnSmthGetListener<User>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ServerResponse<User>> response = service.getSelfInfo(token).execute();
                    handleDefaultSuccess(response, handler, HTTP_OK);
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
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
                final Response<ServerResponse<List<User>>> response = service.getNeighbours(token).execute();
                handleDefaultSuccess(response, handler, HTTP_OK);
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
                final Response<ServerResponse<Position>> response = service.getNeighbourPosition(neighbourId, token).execute();
                handleDefaultSuccess(response, handler, HTTP_OK);
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
                final Response<ServerResponse<MeetRequest>> response =  service
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
                    final Response<ServerResponse<MeetRequest>> response = service
                            .updateRequest(update, token)
                            .execute();
                    handleDefaultSuccess(response, handler, HTTP_OK);
                } catch (IOException e) {
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
                    final Response<ServerResponse<List<MeetRequest>>>
                            response = service.getOutcomePendingRequests(token).execute();
                    handleDefaultSuccess(response, handler, HTTP_OK);
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
                    final Response<ServerResponse<List<MeetRequest>>>
                            response = service.getIncomePendingRequests(token).execute();
                    handleDefaultSuccess(response, handler, HTTP_OK);
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<List<MeetRequest>>>
    getNewRequests(final OnSmthGetListener<List<MeetRequest>> listener) {
        final ListenerHandler<OnSmthGetListener<List<MeetRequest>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ServerResponse<List<MeetRequest>>>
                            response = service.getNewRequests(token).execute();
                    handleDefaultSuccess(response, handler, HTTP_OK);
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<ServerResponse<String>>>
    loginUser(final OnSmthGetListener<ServerResponse<String>> listener, final User user) {
        final ListenerHandler<OnSmthGetListener<ServerResponse<String>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String strRequestBody = GSON.toJson(user);
                    RequestBody requestBody =
                            RequestBody.create(MediaType.parse("text/plain"), strRequestBody);
                    final Response<ServerResponse<String>> response = service.loginUser(requestBody).execute();

                    final ServerResponse<String> body = response.body();
                    if (body == null) {
                        throw new NetworkError(response.code());
                    }
                    if (body.getData() == null || response.code() != HTTP_OK) {
                        throw new NetworkError(body.getErrMsg(), response.code());
                    }
                    invokeSuccess(handler, body);
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnSmthGetListener<ServerResponse<String>>>
    registerUser(final OnSmthGetListener<ServerResponse<String>> listener, final User user) {
        final ListenerHandler<OnSmthGetListener<ServerResponse<String>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String strRequestBody = GSON.toJson(user);
                    RequestBody requestBody =
                            RequestBody.create(MediaType.parse("text/plain"), strRequestBody);
                    final Response<ServerResponse<String>> response = service.registerUser(requestBody).execute();
                    if (response.code() != HTTP_OK) {
                        throw new IOException("HTTP code " + response.code());
                    }

                    final ServerResponse<String> responseBody = response.body();
                    if (responseBody == null) {
                        throw new IOException("Cannot get body");
                    }
                    if (responseBody.getData() == null) {
                        throw new IOException(responseBody.getErrMsg());
                    }
                    invokeSuccess(handler, responseBody);
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private <T> void handleDefaultSuccess(
            Response<ServerResponse<T>> response,
            ListenerHandler<OnSmthGetListener<T>> handler,
            int expectedCode
    ) throws NetworkError {
        final ServerResponse<T> body = response.body();
        if (body == null) {
            throw new NetworkError(response.code());
        }
        if (body.getData() == null || response.code() != expectedCode) {
            throw new NetworkError(body.getErrMsg(), response.code());
        }
        invokeSuccess(handler, body.getData());
    }

    private <T> void
    invokeSuccess(final ListenerHandler<OnSmthGetListener<T>> handler, final T payload) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSmthGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeSuccess");
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
