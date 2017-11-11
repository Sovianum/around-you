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

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.ListenerHandler;
import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.requests.MeetRequestItem;

public class NeighbourAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static int CARD_ID = R.layout.item_neighbour_card;
    private List<NeighbourItem> items;
    private Context context;
    private ListenerHandler<Api.OnSmthGetListener<Integer>> requestHandler;

    private Api.OnSmthGetListener<Integer> requestListener = new Api.OnSmthGetListener<Integer>() {
        @Override
        public void onSuccess(Integer code) {
            Toast.makeText(NeighbourAdapter.this.context, String.format("Response code is %d", code), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(NeighbourAdapter.this.context, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

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

    public NeighbourAdapter(Context context, List<NeighbourItem> items) {
        this.context = context;
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItems(List<NeighbourItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<NeighbourItem> getItems() {
        return this.items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NeighbourItem item = items.get(position);

        CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.loginView.setText(item.getLogin());
        cardHolder.aboutView.setText(item.getAbout());
        cardHolder.requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestHandler = Api.getInstance().createMeetRequest(item.getId(), requestListener);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
