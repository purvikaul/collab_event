package edu.uci.collabevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prateek on 04/06/16.
 */
public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {
    private List<Invitation> mInvitations;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.invitation_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InvitationAdapter.ViewHolder viewHolder, int position) {
        Invitation invitation = mInvitations.get(position);

        TextView nameView = viewHolder.eventName;
        nameView.setText(invitation.getName());

        TextView venueView = viewHolder.eventVenue;
        venueView.setText(invitation.getVenue());

        TextView dateView = viewHolder.eventDate;
        dateView.setText(Event.displayDateFormat.format(invitation.getDate()));

    }


    @Override
    public int getItemCount() {
        return mInvitations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView eventName;
        public TextView eventVenue;
        public TextView eventDate;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.invite_name);
            eventVenue = (TextView) itemView.findViewById(R.id.invite_venue);
            eventDate = (TextView) itemView.findViewById(R.id.invite_date);
        }

    }

    public InvitationAdapter(List<Invitation> mInvitations) {
        this.mInvitations = mInvitations;
    }
}
