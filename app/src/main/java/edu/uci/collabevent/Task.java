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
 * Created by user on 30-05-2016.
 */
public class Task {

    public static SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    public static SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, d MMM");

    private String title;
    private String eventName;
    private TaskStatus taskStatus;
    private Date dueDate;
    private Integer taskId;
    private String description;
    private String assignedUser;

    public Integer getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public Task(String title, String eventName, String taskStatus, String dueDate, Integer taskId, String desc, String assignedUser) {
        this.title = title;
        this.eventName = eventName;
        this.taskId = taskId;
        this.description = desc;
        switch (taskStatus) {
            case "C":
                this.taskStatus = TaskStatus.COMPLETED;
                break;
            case "A":
                this.taskStatus = TaskStatus.ASSIGNED;
                break;
            case "UA":
                this.taskStatus = TaskStatus.UNASSIGNED;
                break;
            default:
                break;
        }
        try {
            if (dueDate != null) {
                this.dueDate = parseDateFormat.parse(dueDate);
            } else {
                this.dueDate = null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (assignedUser != null) {
            this.assignedUser = assignedUser;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getEventName() {
        return eventName;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public static ArrayList<Task> createTasksFromJSON(String TasksJSON) {
        ArrayList<Task> tasksList = new ArrayList<>();
        Log.d("DEBUG-JSON", TasksJSON);
        try {
            JSONArray reader = new JSONArray(TasksJSON);
            Log.d("DEBUG_LEN", Integer.toString(reader.length()));

            for (int i = 0; i < reader.length(); i++) {
                JSONObject jsonEvent = reader.getJSONObject(i);
                String taskName = jsonEvent.getString("title");
                String eventName = jsonEvent.getString("event_name");
                String taskStatus = jsonEvent.getString("status");
                String taskDue = jsonEvent.getString("due_date");
                Integer taskId = jsonEvent.getInt("id");
                String desc = jsonEvent.getString("desc");
                String assignedUser = jsonEvent.getString("assigned_to");
                Task task = new Task(taskName, eventName, taskStatus, taskDue, taskId, desc, assignedUser);
                tasksList.add(task);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tasksList;
    }
}
