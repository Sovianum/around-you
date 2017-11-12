package ru.mail.park.aroundyou.requests.income;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.ListenerHandler;
import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.model.MeetRequestUpdate;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.model.MeetRequest;

import static java.net.HttpURLConnection.HTTP_OK;

public class IncomeMeetRequestAdapter extends MeetRequestAdapter {
    public static final int HTTP_UNAVAILABLE_FOR_LEGAL_REASONS = 451;
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_DECLINED = "DECLINED";

    public static final int CARD_ID = R.layout.item_income_request_card;
    private List<MeetRequest> items;
    private Fragment fragment;
    private ListenerHandler<Api.OnSmthGetListener<Integer>> acceptHandler;
    private ListenerHandler<Api.OnSmthGetListener<Integer>> declineHandler;

    private Api.OnSmthGetListener<Integer> acceptListener = new Api.OnSmthGetListener<Integer>() {
        @Override
        public void onSuccess(Integer code) {
            String message;
            switch (code) {
                case HTTP_OK:
                    message = fragment.getString(R.string.request_accepted_str);
                    break;
                case HTTP_UNAVAILABLE_FOR_LEGAL_REASONS:
                    message = fragment.getString(R.string.user_busy_str);
                    break;
                default:
                    message = String.format("Response code is %d", code);
                    break;
            }
            Toast.makeText(IncomeMeetRequestAdapter.this.fragment.getActivity(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(IncomeMeetRequestAdapter.this.fragment.getActivity(), error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private Api.OnSmthGetListener<Integer> declineListener = new Api.OnSmthGetListener<Integer>() {
        @Override
        public void onSuccess(Integer code) {
            String message;
            switch (code) {
                case HTTP_OK:
                    message = fragment.getString(R.string.request_declined_str);
                    break;
                default:
                    message = String.format("Response code is %d", code);
                    break;
            }
            Toast.makeText(IncomeMeetRequestAdapter.this.fragment.getActivity(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(IncomeMeetRequestAdapter.this.fragment.getActivity(), error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public IncomeMeetRequestAdapter(Fragment fragment, List<MeetRequest> items) {
        this.items = items;
        this.fragment = fragment;
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
        final MeetRequest item = items.get(position);

        IncomeMeetRequestAdapter.CardViewHolder cardHolder = (IncomeMeetRequestAdapter.CardViewHolder) holder;
        cardHolder.requesterLoginView.setText(item.getRequesterLogin());
        cardHolder.requesterAboutView.setText(item.getRequesterAbout());
        cardHolder.submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptHandler = Api.getInstance().updateMeetRequest(
                        new MeetRequestUpdate(item.getId(), STATUS_ACCEPTED),
                        acceptListener
                );
            }
        });

        cardHolder.declineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineHandler = Api.getInstance().updateMeetRequest(
                        new MeetRequestUpdate(item.getId(), STATUS_DECLINED),
                        declineListener
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void setItems(List<MeetRequest> items) {
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
