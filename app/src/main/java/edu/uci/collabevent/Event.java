package edu.uci.collabevent;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 15-05-2016.
 */
public class Event {
    private String name;
    private Date date;
    private String venue;
    private String imgURL;

    public Event() {
    }

    public Event(String name) {
        this.name = name;
    }

    public Event(String name, Date date, String venue) {
        this.name = name;
        this.date = date;
        this.venue = venue;
    }

    public Event(String name, String venue) {
        this.name = name;
        this.venue = venue;
    }

    public String getName() {
        return name;
    }

    public static ArrayList<Event> createEvents(int num) {
        ArrayList<Event> events = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            events.add(new Event("Event" + i + 1));
        }
        return events;
    }

    public String getVenue() {
        return venue;
    }

    public Date getDate() {
        return date;
    }
}

