package edu.uci.collabevent;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Parcelable {
    public static SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    public static SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");
    public static SimpleDateFormat onlyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat onlyTimeFormat = new SimpleDateFormat("HH:mm");

    private String name;
    private Date date;
    private String venue;
    private String description;
    private URL imgURL;
    private Integer membersCount;
    private Integer invitedCount;
    private Integer eventId;
    private String eventCreator;

    public Event(String name) {
        this.name = name;
    }

    public Event(String name, String date, String venue, String imgURL, Integer membersCount, Integer invitedCount, Integer eventId, String description, String createdBy) {
        this.name = name;
        this.venue = venue;
        this.eventId = eventId;
        try {
            this.date = parseDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Log.d("DEBUG_IMG", imgURL);
            this.imgURL = new URL(SplashActivity.getContext().getString(R.string.server_ip) + imgURL);
            Log.d("DEBUG_NEW_IMG", imgURL);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.description = description;
        this.membersCount = membersCount;
        this.invitedCount = invitedCount;
        this.eventCreator = createdBy;


    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public Date getDate() {
        return date;
    }

    public URL getImgURL() {
        return imgURL;
    }

    public String getDescription() {
        return description;
    }

    public String getEventCreator() {
        return eventCreator;
    }

    public static ArrayList<Event> createEventsFromJSON(String EventsJSON) {
        ArrayList<Event> eventsList = new ArrayList<Event>();
        Log.d("DEBUG-JSON", EventsJSON);
        try {
            JSONArray reader = new JSONArray(EventsJSON);

            for (int i = 0; i < reader.length(); i++) {
                JSONObject jsonEvent = reader.getJSONObject(i);
                String eventName = jsonEvent.getString("name");
                String eventDate = jsonEvent.getString("time");
                String eventVenue = jsonEvent.getString("venue");
                String eventDesc = jsonEvent.getString("desc");
                Integer eventMembersCount = jsonEvent.getInt("members_count");
                Integer eventInvitedCount = jsonEvent.getInt("invited_count");
                Integer eventId = jsonEvent.getInt("id");
                String eventImgUrl = jsonEvent.getString("picture");
                String eventCreator = jsonEvent.getString("created_by");

                Event event = new Event(eventName, eventDate, eventVenue, eventImgUrl, eventMembersCount, eventInvitedCount, eventId, eventDesc, eventCreator);
                eventsList.add(event);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return eventsList;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public Integer getInvitedCount() {
        return invitedCount;
    }

    public Integer getEventId() {
        return eventId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.venue);
        dest.writeString(this.description);
        dest.writeSerializable(this.imgURL);
        dest.writeValue(this.membersCount);
        dest.writeValue(this.invitedCount);
        dest.writeValue(this.eventId);
        dest.writeString(this.eventCreator);
    }

    protected Event(Parcel in) {
        this.name = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.venue = in.readString();
        this.description = in.readString();
        this.imgURL = (URL) in.readSerializable();
        this.membersCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.invitedCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.eventId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.eventCreator = in.readString();
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}

