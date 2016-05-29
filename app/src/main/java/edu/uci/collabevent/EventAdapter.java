package edu.uci.collabevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 21-05-2016.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> mEvents;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.event_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder viewHolder, int position) {
        Event event = mEvents.get(position);

        TextView nameView = viewHolder.eventName;
        nameView.setText(event.getName());

        TextView venueView = viewHolder.eventVenue;
        venueView.setText(event.getVenue());

        TextView dateView = viewHolder.eventDate;
        dateView.setText(Event.displayDateFormat.format(event.getDate()));
    }


    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView eventName;
        public TextView eventVenue;
        public TextView eventDate;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventVenue = (TextView) itemView.findViewById(R.id.event_venue);
            eventDate = (TextView) itemView.findViewById(R.id.event_date);
        }

    }

    public EventAdapter(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

}
