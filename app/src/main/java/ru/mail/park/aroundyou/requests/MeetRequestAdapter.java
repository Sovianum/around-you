package ru.mail.park.aroundyou.requests;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.mail.park.aroundyou.R;

public class MeetRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int CARD_ID = R.layout.item_request_card;
    private List<MeetRequestItem> items;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView fromView;
        TextView toView;
        TextView statusView;

        public CardViewHolder(View itemView) {
            super(itemView);
            fromView = itemView.findViewById(R.id.from_txt);
            toView = itemView.findViewById(R.id.to_txt);
            statusView = itemView.findViewById(R.id.status_txt);
        }
    }

    public MeetRequestAdapter(List<MeetRequestItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<MeetRequestItem> getItems() {
        return items;
    }

    public void setItems(List<MeetRequestItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new MeetRequestAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MeetRequestItem item = items.get(position);

        MeetRequestAdapter.CardViewHolder cardHolder = (MeetRequestAdapter.CardViewHolder) holder;
        cardHolder.fromView.setText(item.getRequesterLogin());
        cardHolder.toView.setText(item.getRequestedLogin());
        cardHolder.statusView.setText(item.getStatus());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
