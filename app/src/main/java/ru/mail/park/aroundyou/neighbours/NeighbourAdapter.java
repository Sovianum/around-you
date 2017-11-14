package ru.mail.park.aroundyou.neighbours;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.model.User;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;

public class NeighbourAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static int CARD_ID = R.layout.item_neighbour_card;
    private List<User> items;
    private NeighbourFragment fragment;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView loginView;
        TextView aboutView;
        ImageView requestBtn;
        TextView expanderView;
        int defaultMaxLines;

        public CardViewHolder(final View itemView) {
            super(itemView);
            loginView = itemView.findViewById(R.id.login_txt);
            aboutView = itemView.findViewById(R.id.about_txt);
            requestBtn = itemView.findViewById(R.id.request_btn);
            expanderView = itemView.findViewById(R.id.expander_view);
            defaultMaxLines = aboutView.getMaxLines();
        }
    }

    public NeighbourAdapter(NeighbourFragment fragment, List<User> items) {
        this.fragment = fragment;
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItems(List<User> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final User user = items.get(position);

        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.loginView.setText(user.getLogin());
        cardHolder.aboutView.setText(user.getAbout());
        cardHolder.requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.createRequest(user.getId());
            }
        });

        cardHolder.expanderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardHolder.aboutView.getMaxLines() == cardHolder.defaultMaxLines) {
                    cardHolder.aboutView.setMaxLines(Integer.MAX_VALUE);
                    cardHolder.expanderView.setText(R.string.collapse_str);
                } else {
                    cardHolder.aboutView.setMaxLines(cardHolder.defaultMaxLines);
                    cardHolder.expanderView.setText(R.string.expand_str);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
