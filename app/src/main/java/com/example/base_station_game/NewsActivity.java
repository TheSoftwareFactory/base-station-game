package com.example.base_station_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements NewsFeedAdapter.ItemClickListener {
    NewsFeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //TODO: get the news from somewhere and put them in this arraylist.
        ArrayList<String> news = new ArrayList<>();
        news.add("Stations 3 has been conquered by your team.");
        for (int i = 0; i < 100; i++) {
            news.add("A new station has appeared.");
        }

        RecyclerView recyclerView = findViewById(R.id.rvNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsFeedAdapter(this, news);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Clicked on position " + position + "which had " + adapter.getItem(position)  + " in it.", Toast.LENGTH_SHORT).show();
    }
}