package ru.mail.park.aroundyou;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NeighbourAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static int CARD_ID = R.layout.item_neighbour_card;
    private List<NeighbourItem> items;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView loginView;
        TextView aboutView;
        ImageView requestBtn;

        public CardViewHolder(final View itemView) {
            super(itemView);
            loginView = itemView.findViewById(R.id.login_txt);
            aboutView = itemView.findViewById(R.id.about_txt);
            requestBtn = itemView.findViewById(R.id.request_btn);
        }
    }

    public NeighbourAdapter(List<NeighbourItem> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NeighbourItem item = items.get(position);

        CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.loginView.setText(item.getLogin());
        cardHolder.aboutView.setText(item.getAbout());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
