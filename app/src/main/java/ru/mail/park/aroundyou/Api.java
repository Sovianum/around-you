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

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by sergey on 01.11.17.
 */

public class Api {
    private static final Api INSTANCE = new Api();

    private static final Gson GSON = new GsonBuilder().create();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final LoaderService service;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Api() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://around-you-backend.herokuapp.com")
                .build();
        service = retrofit.create(LoaderService.class);
    }

    public static Api getInstance() {
        return INSTANCE;
    }

    //public ListenerHandler<OnNeighboursGetListener>
    //getNeighbours(final OnNeighboursGetListener listener) {
        //final ListenerHandler<OnNeighboursGetListener> handler = new ListenerHandler<>(listener);
    public ListenerHandler<OnSmthGetListener<List<NeighbourItem>>>
    getNeighbours(final OnSmthGetListener<List<NeighbourItem>> listener) {
        final ListenerHandler<OnSmthGetListener<List<NeighbourItem>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service.getNeighbours().execute();
                    if (response.code() != 200) {
                        throw new IOException("HTTP code " + response.code());
                    }
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        //invokeSuccess(handler, parseNeighbours(body));
                        invokeSuccess(handler, parseNeighbours(body));
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
            final List<NeighbourItem> recievedNeighbourList = new ArrayList<>();
            JSONObject bodyJSON = new JSONObject(body);
            JSONArray array = bodyJSON.getJSONArray(ServerInfo.NEIGHBOURS_ARRAY_NAME);
            for (int i = 0; i < array.length(); i++) {
                recievedNeighbourList.add(new NeighbourItem(array.getJSONObject(i)));
            };
            return recievedNeighbourList;
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        } catch (JSONException e) {
            throw new IOException(e);
        }
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



    /*private void invokeSuccess(final ListenerHandler<OnNeighboursGetListener> handler,
                               final List<NeighbourItem> neighbourItems) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnNeighboursGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeSucces");
                    listener.onGettingSuccess(neighbourItems);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeError(final ListenerHandler<OnNeighboursGetListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnNeighboursGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeError");
                    listener.onGettingError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }*/


    public interface OnSmthGetListener<T> {
        void onGettingSuccess(final T items);

        void onGettingError(final Exception error);
    }

    public interface OnNeighboursGetListener {
        void onGettingSuccess(final List<NeighbourItem> neighbourItems);

        void onGettingError(final Exception error);
    }
}
