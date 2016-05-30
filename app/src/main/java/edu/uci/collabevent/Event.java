package edu.uci.collabevent;

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


public class Event {
    public static SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    public static SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");

    private String name;
    private Date date;
    private String venue;
    private URL imgURL;
    private Integer membersCount;
    private Integer invitedCount;


    public Event(String name) {
        this.name = name;
    }

    public Event(String name, String date, String venue, String imgURL, Integer membersCount, Integer invitedCount) {
        this.name = name;
        this.venue = venue;
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
        this.membersCount = membersCount;
        this.invitedCount = invitedCount;

    }

    public Event(String name, String date, String venue, Integer membersCount, Integer invitedCount) {
        this.name = name;
        this.venue = venue;
        this.membersCount = membersCount;
        this.invitedCount = invitedCount;

    }

    public Event(String name, String date, String venue) {
        this.name = name;
        try {
            this.date = parseDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.venue = venue;
    }

    public Event(String name, String date) {
        this.name = name;
        try {
            this.date = parseDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                Integer eventMembersCount = jsonEvent.getInt("members_count");
                Integer eventInvitedCount = jsonEvent.getInt("invited_count");
                String eventImgUrl = jsonEvent.getString("picture");

                Event event = new Event(eventName, eventDate, eventVenue, eventImgUrl, eventMembersCount, eventInvitedCount);
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
}

