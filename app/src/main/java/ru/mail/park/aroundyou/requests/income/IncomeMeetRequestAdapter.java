package ru.mail.park.aroundyou.requests.income;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestItem;

public class IncomeMeetRequestAdapter extends MeetRequestAdapter {
    public static final int CARD_ID = R.layout.item_income_request_card;
    private List<MeetRequestItem> items;

    public IncomeMeetRequestAdapter(List<MeetRequestItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new IncomeMeetRequestAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MeetRequestItem item = items.get(position);

        IncomeMeetRequestAdapter.CardViewHolder cardHolder = (IncomeMeetRequestAdapter.CardViewHolder) holder;
        cardHolder.requesterLoginView.setText(item.getRequesterLogin());
        cardHolder.requesterAboutView.setText(item.getRequesterAbout());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void setItems(List<MeetRequestItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView requesterLoginView;
        TextView requesterAboutView;
        View submitView;
        View declineView;

        CardViewHolder(View itemView) {
            super(itemView);
            requesterLoginView = itemView.findViewById(R.id.requester_login_txt);
            requesterAboutView = itemView.findViewById(R.id.requester_about_txt);
            submitView = itemView.findViewById(R.id.submit_txt);
            declineView = itemView.findViewById(R.id.decline_txt);
        }
    }
}
