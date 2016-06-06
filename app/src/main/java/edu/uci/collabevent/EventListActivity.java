package edu.uci.collabevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 21-05-2016.
 */
public class EventListActivity extends AppCompatActivity {

    private ArrayList<Event> events;
    private ProgressDialog progressDialog = null;
    private EventsListTask listFetchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Load events list for the user asynchronously.
        fetchEvents();
    }

    private void fetchEvents() {
        Context context = getApplicationContext();
        listFetchTask = new EventsListTask();
        listFetchTask.execute();

        // Show loading dialog box.
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

    }

    protected class EventsListTask extends AsyncTask<Void, Void, String> {
        Context mContext;

        EventsListTask() {
            this.mContext = getApplicationContext();
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
            RecyclerView eventCard = (RecyclerView) findViewById(R.id.eventCard);
            assert eventCard != null;
            eventCard.setHasFixedSize(true);

            EventAdapter adapter = new EventAdapter(events);
            eventCard.setAdapter(adapter);
            eventCard.setLayoutManager(new LinearLayoutManager(mContext));

        }
    }
}
