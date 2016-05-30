package edu.uci.collabevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class TaskListFragment extends Fragment {

    private ArrayList<Task> tasks;
    private ProgressDialog progressDialog = null;
    private TasksListTask listFetchTask;
    private RecyclerView taskCard;

    public TaskListFragment() {
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
        View view = inflater.inflate(R.layout.activity_tasks, container, false);
        taskCard = (RecyclerView) view.findViewById(R.id.taskCard);
        assert taskCard != null;
        fetchTasks();
        return view;
    }

    private void fetchTasks() {
        Context context = getActivity();
        listFetchTask = new TasksListTask();
        listFetchTask.execute();

        // Show loading dialog box.
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

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
                URL eventsListURL = new URL(mContext.getString(R.string.server_ip) + mContext.getString(R.string.tasks_list_url));
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

            TaskAdapter adapter = new TaskAdapter(tasks);
            taskCard.setAdapter(adapter);
            taskCard.setLayoutManager(new LinearLayoutManager(mContext));

        }
    }

}