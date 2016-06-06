package edu.uci.collabevent;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Prateek on 04/06/16.
 */
public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {
    private List<Invitation> mInvitations;
    private static Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.invitation_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final InvitationAdapter.ViewHolder viewHolder, final int position) {
        Invitation invitation = mInvitations.get(position);

        TextView nameView = viewHolder.inviteName;
        nameView.setText(invitation.getName());

        TextView venueView = viewHolder.inviteVenue;
        venueView.setText(invitation.getVenue());

        TextView dateView = viewHolder.inviteDate;
        dateView.setText(Event.displayDateFormat.format(invitation.getDate()));

        viewHolder.inviteId = invitation.getId();

        viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptTask acceptTask = new AcceptTask(position);
                acceptTask.execute(Integer.toString(viewHolder.inviteId));

            }
        });

        viewHolder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RejectTask rejectTask = new RejectTask(position);
                rejectTask.execute(Integer.toString(viewHolder.inviteId));
            }
        });


    }


    @Override
    public int getItemCount() {
        return mInvitations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView inviteName;
        public TextView inviteVenue;
        public TextView inviteDate;
        public ImageButton acceptButton;
        public ImageButton rejectButton;
        private Integer inviteId;

        public ViewHolder(View itemView) {
            super(itemView);
            inviteName = (TextView) itemView.findViewById(R.id.invite_name);
            inviteVenue = (TextView) itemView.findViewById(R.id.invite_venue);
            inviteDate = (TextView) itemView.findViewById(R.id.invite_date);
            acceptButton = (ImageButton) itemView.findViewById(R.id.btn_accept);
            rejectButton = (ImageButton) itemView.findViewById(R.id.btn_reject);
        }
    }

    public InvitationAdapter(List<Invitation> mInvitations) {
        this.mInvitations = mInvitations;
    }

    private class AcceptTask extends AsyncTask<String, Void, String> {
        private int position;

        AcceptTask(int p) {
            this.position = position;
        }


        @Override
        protected String doInBackground(String... ids) {
            StringBuffer response = new StringBuffer();
            String eventId = ids[0];
            Log.d("DEBUG_INVITE_ACCEPT", eventId);
            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("event_id", eventId);
                String query = builder.build().getEncodedQuery();

                URL url = new URL(context.getString(R.string.server_ip) + context.getString(R.string.invitation_accept_url) + "?" + query);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                Log.d("DEBUG", "\nSending 'GET' request to URL : " + url);
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
        protected void onPostExecute(String result) {
            if (!result.isEmpty() && result.equals("Success")) {
                Toast.makeText(context, "Invitation accepted!", Toast.LENGTH_SHORT).show();
                mInvitations.remove(position);
                InvitationAdapter.this.notifyItemRemoved(position);
            }
        }
    }

    private class RejectTask extends AsyncTask<String, Void, String> {
        private int position;

        RejectTask(int p) {
            this.position = position;
        }


        @Override
        protected String doInBackground(String... ids) {
            StringBuffer response = new StringBuffer();
            String eventId = ids[0];
            Log.d("DEBUG_INVITE_REJECT", eventId);
            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("event_id", eventId);
                String query = builder.build().getEncodedQuery();

                URL url = new URL(context.getString(R.string.server_ip) + context.getString(R.string.invitation_reject_url) + "?" + query);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                Log.d("DEBUG", "\nSending 'GET' request to URL : " + url);
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
        protected void onPostExecute(String result) {
            if (!result.isEmpty() && result.equals("Success")) {
                Toast.makeText(context, "Invitation rejected.", Toast.LENGTH_SHORT).show();
                mInvitations.remove(position);
                InvitationAdapter.this.notifyItemRemoved(position);
            }
        }
    }
}
