package edu.uci.collabevent;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private Integer id;

    public Invitation(String name) {
        this.name = name;
    }

    public Invitation(String name, String date, String venue, Integer membersCount, Integer invitedCount, Integer id) {
        this.name = name;
        this.venue = venue;
        try {
            this.date = parseDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.membersCount = membersCount;
        this.invitedCount = invitedCount;
        this.id = id;

    }

    public Invitation(String name, String date, String venue, Integer id) {
        this.name = name;
        try {
            this.date = parseDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.venue = venue;
        this.id = id;
    }

    public Invitation(String name, String date, Integer id) {
        this.name = name;
        this.id = id;
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
                JSONObject jsonInvite = reader.getJSONObject(i);
                String inviteName = jsonInvite.getString("name");
                String inviteDate = jsonInvite.getString("time");
                String inviteVenue = jsonInvite.getString("venue");
                Integer inviteMembersCount = jsonInvite.getInt("members_count");
                Integer inviteInvitedCount = jsonInvite.getInt("invited_count");
                Integer inviteId = jsonInvite.getInt("id");

                Invitation invitation = new Invitation(inviteName, inviteDate, inviteVenue, inviteMembersCount, inviteInvitedCount, inviteId);
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

    public Integer getId() {
        return id;
    }
}
