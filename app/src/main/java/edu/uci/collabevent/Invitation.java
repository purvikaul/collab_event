package edu.uci.collabevent;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Prateek on 04/06/16.
 */
public class Invitation {
    public static SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    public static SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");

    private String name;
    private Date date;
    private String venue;
    private Integer membersCount;
    private Integer invitedCount;


    public Invitation(String name) {
        this.name = name;
    }

    public Invitation(String name, String date, String venue, Integer membersCount, Integer invitedCount) {
        this.name = name;
        this.venue = venue;
        try {
            this.date = parseDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.membersCount = membersCount;
        this.invitedCount = invitedCount;

    }


    public Invitation(String name, String date, String venue) {
        this.name = name;
        try {
            this.date = parseDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.venue = venue;
    }

    public Invitation(String name, String date) {
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

    public static ArrayList<Invitation> createInvitationsFromJSON(String InvitationsJSON) {
        ArrayList<Invitation> invitations = new ArrayList<Invitation>();
        Log.d("DEBUG-JSON", InvitationsJSON);
        try {
            JSONArray reader = new JSONArray(InvitationsJSON);

            for (int i = 0; i < reader.length(); i++) {
                JSONObject jsonEvent = reader.getJSONObject(i);
                String eventName = jsonEvent.getString("name");
                String eventDate = jsonEvent.getString("time");
                String eventVenue = jsonEvent.getString("venue");
                Integer eventMembersCount = jsonEvent.getInt("members_count");
                Integer eventInvitedCount = jsonEvent.getInt("invited_count");

                Invitation invitation = new Invitation(eventName, eventDate, eventVenue, eventMembersCount, eventInvitedCount);
                invitations.add(invitation);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return invitations;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public Integer getInvitedCount() {
        return invitedCount;
    }
}
