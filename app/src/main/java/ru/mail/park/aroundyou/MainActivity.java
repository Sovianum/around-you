package ru.mail.park.aroundyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NeighbourAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<NeighbourItem> items = buildItemList();
        adapter = new NeighbourAdapter(items);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private List<NeighbourItem> buildItemList() {
        final int cardCnt = 100;
        List<NeighbourItem> items = new ArrayList<>();

        for (int i = 0; i != cardCnt; i++) {
            items.add(NeighbourMock.getRandomNeighbourItem());
        }
        return items;
    }
}
