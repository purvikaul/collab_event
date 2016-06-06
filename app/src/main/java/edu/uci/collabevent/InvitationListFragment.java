package edu.uci.collabevent;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
 * Created by Prateek on 04/06/16.
 */
public class InvitationListFragment extends Fragment {
    private ArrayList<Invitation> invitations;
    private ProgressDialog progressDialog = null;
    private InvitationsListTask listFetchTask;
    private RecyclerView invitationCard;
    private SwipeRefreshLayout swipeContainer;
    private InvitationAdapter adapter;

    public InvitationListFragment() {
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
        View view = inflater.inflate(R.layout.activity_invites, container, false);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        invitationCard = (RecyclerView) view.findViewById(R.id.invitationCard);
        assert invitationCard != null;

        fetchInvitations(true);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchInvitations(false);
            }
        });
        return view;
    }

    private void fetchInvitations(Boolean showProgress) {
        Context context = getActivity();
        listFetchTask = new InvitationsListTask();
        listFetchTask.execute();

        // Show loading dialog box.
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading....");
        if (showProgress) {
            progressDialog.show();
        }

    }

    protected class InvitationsListTask extends AsyncTask<Void, Void, String> {
        Context mContext;

        InvitationsListTask() {

            this.mContext = getActivity();
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer response = new StringBuffer("");
            try {
                URL invitationsListURL = new URL(mContext.getString(R.string.server_ip) + mContext.getString(R.string.invitations_list_url));
                HttpURLConnection connection = (HttpURLConnection) invitationsListURL.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                Log.d("DEBUG", "\nSending 'GET' request to URL : " + invitationsListURL);
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
            invitations = Invitation.createInvitationsFromJSON(s);
            progressDialog.dismiss();
            invitationCard.setHasFixedSize(true);

            adapter = new InvitationAdapter(invitations);
            invitationCard.setAdapter(adapter);
            invitationCard.setLayoutManager(new LinearLayoutManager(mContext));
            swipeContainer.setRefreshing(false);
        }
    }
}
