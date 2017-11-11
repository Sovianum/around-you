package ru.mail.park.aroundyou.requests.outcome;

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

public class OutcomeMeetRequestAdapter extends MeetRequestAdapter {
    public static final int CARD_ID = R.layout.item_outcome_request_card;
    private List<MeetRequestItem> items;

    OutcomeMeetRequestAdapter(List<MeetRequestItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new OutcomeMeetRequestAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MeetRequestItem item = items.get(position);

        OutcomeMeetRequestAdapter.CardViewHolder cardHolder = (OutcomeMeetRequestAdapter.CardViewHolder) holder;
        cardHolder.requestedLoginView.setText(item.getRequestedLogin());
        cardHolder.requestedAboutView.setText("Выгрузить информацию о пользователе из кеша");
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
        TextView requestedLoginView;
        TextView requestedAboutView;

        CardViewHolder(View itemView) {
            super(itemView);
            requestedLoginView = itemView.findViewById(R.id.requested_login_txt);
            requestedAboutView = itemView.findViewById(R.id.requested_about_txt);
        }
    }

    public List<MeetRequestItem> getItems() {
        return items;
    }
}
