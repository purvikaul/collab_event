package edu.uci.collabevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Prateek on 06/06/16.
 */
public class TasksEventFragment extends Fragment {

    private ArrayList<Task> tasks;
    private ProgressDialog progressDialog = null;
    private TasksListTask listFetchTask;
    private RecyclerView taskCard;
    private SwipeRefreshLayout swipeContainer;
    private Event event;

    public TasksEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.event_tasks, container, false);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        taskCard = (RecyclerView) view.findViewById(R.id.taskCard);
        registerForContextMenu(taskCard);
        assert taskCard != null;
        EventDetailActivity parentActivity = (EventDetailActivity) getActivity();
        event = parentActivity.getEvent();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTasks(false);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getActivity(), CreateTaskActivity.class);
                startActivity(I);
            }
        });
        return view;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "SMS");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Call") {
            Toast.makeText(getContext(), "calling code", Toast.LENGTH_LONG).show();
        } else if (item.getTitle() == "SMS") {
            Toast.makeText(getContext(), "sending sms code", Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTasks(true);
    }

    private void fetchTasks(Boolean showProgress) {
        Context context = getActivity();
        listFetchTask = new TasksListTask();
        listFetchTask.execute();

        // Show loading dialog box.
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading....");
        if (showProgress) {
            progressDialog.show();
        }

    }

    protected class TasksListTask extends AsyncTask<Void, Void, String> {
        Context mContext;

        TasksListTask() {

            this.mContext = getActivity();
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer response = new StringBuffer("");
            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("event_id", Integer.toString(event.getEventId()));
                String query = builder.build().getEncodedQuery();

                URL eventsListURL = new URL(mContext.getString(R.string.server_ip) + mContext.getString(R.string.tasks_event_url) + "?" + query);
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
            tasks = Task.createTasksFromJSON(s);
            progressDialog.dismiss();
            taskCard.setHasFixedSize(true);

            TaskEventAdapter adapter = new TaskEventAdapter(tasks);
            taskCard.setAdapter(adapter);
            taskCard.setLayoutManager(new LinearLayoutManager(mContext));
            swipeContainer.setRefreshing(false);

        }
    }

}


