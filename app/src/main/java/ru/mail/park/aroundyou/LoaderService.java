package ru.mail.park.aroundyou;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoaderService extends Service {
    private final Executor executor  = Executors.newSingleThreadExecutor();
    public static final String ACTION_LOAD_NEIGHBOURS = "load_neighbours";
    public static final String DATA_NEIGHBOURS_NAME = "neighbours";

    public LoaderService() {
    }

    private List<NeighbourItem> neighbours;
    private NeighbourLoader neighbourLoader;
    @Override
    public void onCreate() {
        super.onCreate();
        neighbours = new ArrayList<>();
        neighbourLoader = new NeighbourLoader();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case ACTION_LOAD_NEIGHBOURS:
                loadNeighbours();
                break;
        }
        return START_STICKY;
    }

    private void loadNeighbours() {
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                neighbourLoader.loadNeighbours(neighbours);
                Intent loadedNeighboursIntent = new Intent(ACTION_LOAD_NEIGHBOURS);
                loadedNeighboursIntent.setAction(ACTION_LOAD_NEIGHBOURS);
                loadedNeighboursIntent.putExtra(DATA_NEIGHBOURS_NAME, (Serializable) neighbours);
                broadcastManager.sendBroadcast(loadedNeighboursIntent);
            }
        });
        //NeighbourItem itemStub = new NeighbourItem("login", "sex", "about", 18);
        //neighbours.add(itemStub);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
