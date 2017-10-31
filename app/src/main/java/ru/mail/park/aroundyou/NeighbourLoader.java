package ru.mail.park.aroundyou;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by sergey on 28.10.17.
 */

public class NeighbourLoader {
    private final OkHttpClient client = new OkHttpClient();
    //private final Executor executor  = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static final String jwtStub =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTc4MjYyMzMsImlkIjoyLCJsb2dpbiI6InRlc3QxIn0.mATPV2kp7Yx_cPEuVuJblbNYAweL2NY6pTbwsXgKj0U";


    public void loadNeighbours(final List<NeighbourItem> neighbourList) {
        neighbourList.size();
        //executor.execute(new Runnable() {
            //@Override
            //public void run() {
                Request request = new Request.Builder()
                        .url(ServerInfo.URL_NEIGHBOUR)
                        .addHeader(ServerInfo.AUTHORIZATION, jwtStub)
                        .build();

                ResponseBody body = null;
                try {
                    final Response response = client.newCall(request).execute();
                    body = response.body();
                    if (body != null) {
                        final List<NeighbourItem> recievedNeighbourList = new ArrayList<>();
                        String bodyString = body.string();
                        JSONObject bodyJSON = new JSONObject(bodyString);
                        JSONArray array = bodyJSON.getJSONArray(ServerInfo.NEIGHBOURS_ARRAY_NAME);
                        for (int i = 0; i < array.length(); i++) {
                            recievedNeighbourList.add(new NeighbourItem(array.getJSONObject(i)));
                        };
                        neighbourList.addAll(recievedNeighbourList);
                        //setNeighboursList(neighbourList, recievedNeighbourList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (body != null) {
                        body.close();
                    }
                }
            //}
        //});
    }

    private void setNeighboursList(final List<NeighbourItem> neighbourList,
                              final List<NeighbourItem> recievedNeighbourList) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                neighbourList.addAll(recievedNeighbourList);
            }
        });
    }
}

