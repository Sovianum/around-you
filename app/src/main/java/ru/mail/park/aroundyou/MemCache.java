package ru.mail.park.aroundyou;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.model.MeetRequest;

public class MemCache {
    private static final MemCache INSTANCE = new MemCache();

    private MemCache() {

    }

    public static MemCache getInstance() {
        return INSTANCE;
    }

    private static List<MeetRequest> requests = new ArrayList<>();


    public static synchronized void addRequests(List<MeetRequest> requests) {
        MemCache.requests.addAll(requests);
    }

    public static synchronized void clearRequests() {
        requests.clear();
    }

    public synchronized List<MeetRequest> getRequests() {
        return requests;
    }
}
