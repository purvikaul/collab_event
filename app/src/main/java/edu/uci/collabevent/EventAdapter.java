package edu.uci.collabevent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 21-05-2016.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> mEvents;
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
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

        ImageView imageView = viewHolder.eventImage;
        String imgUrl = event.getImgURL().toString();
        if (!imgUrl.endsWith("null")) {
            DownloadImageTask downloadImageTask = new DownloadImageTask(imageView);
            Log.d("DEBUG-IMG", imgUrl);
            downloadImageTask.execute(imgUrl);
        }

        viewHolder.event = event;

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView eventName;
        public TextView eventVenue;
        public TextView eventDate;
        public ImageView eventImage;
        public Event event;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventVenue = (TextView) itemView.findViewById(R.id.event_venue);
            eventDate = (TextView) itemView.findViewById(R.id.event_date);
            eventImage = (ImageView) itemView.findViewById(R.id.event_image);

            // Add onClick listener to the event card
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Pass event id to the Detail activity
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    Bundle informationBundle = new Bundle();
                    informationBundle.putParcelable("event", event);
                    intent.putExtras(informationBundle);
                    context.startActivity(intent);
                }
            });
        }

    }

    public EventAdapter(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

}
