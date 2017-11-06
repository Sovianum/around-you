package ru.mail.park.aroundyou.requests;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class MeetRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<MeetRequestItem> items;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
//    public static int CARD_ID = R.layout.item_neighbour_card;
//    private List<NeighbourItem> items;
//
//    public class CardViewHolder extends RecyclerView.ViewHolder {
//        TextView loginView;
//        TextView aboutView;
//        ImageView requestBtn;
//
//        public CardViewHolder(final View itemView) {
//            super(itemView);
//            loginView = itemView.findViewById(R.id.login_txt);
//            aboutView = itemView.findViewById(R.id.about_txt);
//            requestBtn = itemView.findViewById(R.id.request_btn);
//        }
//    }
//
//    public NeighbourAdapter(List<NeighbourItem> items) {
//        this.items = items;
//        notifyDataSetChanged();
//    }
//
//    public void setItems(List<NeighbourItem> items) {
//        this.items = items;
//        notifyDataSetChanged();
//    }
//
//    public List<NeighbourItem> getItems() {
//        return this.items;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
//        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
//        return new NeighbourAdapter.CardViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        NeighbourItem item = items.get(position);
//
//        NeighbourAdapter.CardViewHolder cardHolder = (NeighbourAdapter.CardViewHolder) holder;
//        cardHolder.loginView.setText(item.getLogin());
//        cardHolder.aboutView.setText(item.getAbout());
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
}
