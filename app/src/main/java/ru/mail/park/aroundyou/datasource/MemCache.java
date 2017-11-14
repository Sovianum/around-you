package ru.mail.park.aroundyou.datasource;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.model.MeetRequest;

public class MemCache {
    private static final MemCache INSTANCE = new MemCache();

    private MemCache() {}

    public static MemCache getInstance() {
        return INSTANCE;
    }

    private static List<MeetRequest> incomeRequests = new ArrayList<>();
    private static List<MeetRequest> outcomeRequests = new ArrayList<>();


    public static synchronized void clearAndAddIncomeRequests(List<MeetRequest> requests) {
        MemCache.incomeRequests.clear();
        MemCache.incomeRequests.addAll(requests);
    }

    public synchronized List<MeetRequest> getIncomeRequests() {
        return incomeRequests;
    }

    public static synchronized void clearAndAddOutcomeRequests(List<MeetRequest> requests) {
        MemCache.outcomeRequests.clear();
        MemCache.outcomeRequests.addAll(requests);
    }

    public synchronized List<MeetRequest> getOutcomeRequests() {
        return outcomeRequests;
    }

    public boolean isEmptyIncome() {
        return isEmpty(incomeRequests);
    }

    public boolean isEmptyOutcome() {
        return isEmpty(outcomeRequests);
    }

    private boolean isEmpty(List<MeetRequest> requests) {
        return requests == null || requests.size() == 0;
    }
}
