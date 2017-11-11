package ru.mail.park.aroundyou.requests;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class MeetRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public abstract void setItems(List<MeetRequestItem> items);
}
