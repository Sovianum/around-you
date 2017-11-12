package ru.mail.park.aroundyou.requests;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import ru.mail.park.aroundyou.model.MeetRequest;

public abstract class MeetRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public abstract void setItems(List<MeetRequest> items);
}
