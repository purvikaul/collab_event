package edu.uci.collabevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 29-05-2016.
 */
public class EventListFragment extends Fragment {

    private ArrayList<Event> events;
    private ProgressDialog progressDialog = null;
    private EventsListTask listFetchTask;
    private RecyclerView eventCard;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.activity_events, container, false);
        eventCard = (RecyclerView) view.findViewById(R.id.eventCard);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        assert eventCard != null;
        //   fetchEvents(true);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchEvents(false);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getActivity(), CreateEvent.class);
                startActivity(I);
            }
        });
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public void onResume() {
        super.onResume();
        fetchEvents(true);
    }

    private void fetchEvents(Boolean showProgess) {
        Context context = getActivity();
        listFetchTask = new EventsListTask();
        listFetchTask.execute();

        // Show loading dialog box.

        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading....");
        if (showProgess) {
            progressDialog.show();
        }

    }

    protected class EventsListTask extends AsyncTask<Void, Void, String> {
        Context mContext;

        EventsListTask() {
            this.mContext = getActivity();
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer response = new StringBuffer("");
            try {
                URL eventsListURL = new URL(mContext.getString(R.string.server_ip) + mContext.getString(R.string.events_list_url));
                HttpURLConnection connection = (HttpURLConnection) eventsListURL.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                Log.d("DEBUG", "\nSending 'GET' request to URL : " + eventsListURL);
                Log.d("DEBUG", "Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                Log.d("DEBUG", response.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            events = Event.createEventsFromJSON(s);
            progressDialog.dismiss();
            eventCard.setHasFixedSize(true);

            EventAdapter adapter = new EventAdapter(events);
            eventCard.setAdapter(adapter);
            eventCard.setLayoutManager(new LinearLayoutManager(mContext));
            swipeContainer.setRefreshing(false);

        }
    }

}
