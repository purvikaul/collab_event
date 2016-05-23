package edu.uci.collabevent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by user on 21-05-2016.
 */
public class EventListActivity extends AppCompatActivity {

    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        RecyclerView eventCard = (RecyclerView) findViewById(R.id.eventCard);
        assert eventCard != null;
        eventCard.setHasFixedSize(true);

        events = Event.createEvents(10);

        EventAdapter adapter = new EventAdapter(events);

        eventCard.setAdapter(adapter);

        eventCard.setLayoutManager(new LinearLayoutManager(this));
    }
}
