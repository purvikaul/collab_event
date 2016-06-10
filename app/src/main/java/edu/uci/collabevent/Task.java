package edu.uci.collabevent;

import android.os.Parcel;
import android.os.Parcelable;
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
public class Task implements Parcelable {

    public static SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.dueDate != null ? this.dueDate.getTime() : -1);
        dest.writeString(this.eventName);
        dest.writeString(this.taskStatus.toString());
        dest.writeValue(this.taskId);
        dest.writeString(this.description);
        dest.writeString(this.assignedUser);
    }

    protected Task(Parcel in) {
        this.title = in.readString();
        long tmpDate = in.readLong();
        this.dueDate = tmpDate == -1 ? null : new Date(tmpDate);
        this.eventName = in.readString();
        String status = in.readString();
        switch (status) {
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
        this.taskId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.description = in.readString();
        this.assignedUser = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
    public static ArrayList<Task> createTasksFromJSON(String TasksJSON) {
        ArrayList<Task> tasksList = new ArrayList<>();
        Log.d("DEBUG-JSON", TasksJSON);
        try {
            JSONArray reader = new JSONArray(TasksJSON);
            Log.d("DEBUG_LEN", Integer.toString(reader.length()));

            for (int i = 0; i < reader.length(); i++) {
                JSONObject jsonEvent = reader.getJSONObject(i);
                Task task = createTaskFromJSON(jsonEvent);
                tasksList.add(task);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tasksList;
    }

    public static Task createTaskFromJSON(JSONObject jsonEvent) throws JSONException {
        String taskName = jsonEvent.getString("title");
        String eventName = jsonEvent.getString("event_name");
        String taskStatus = jsonEvent.getString("status");
        String taskDue = jsonEvent.getString("due_date");
        Integer taskId = jsonEvent.getInt("id");
        String desc = jsonEvent.getString("desc");
        String assignedUser = jsonEvent.getString("assigned_to");
        Task task = new Task(taskName, eventName, taskStatus, taskDue, taskId, desc, assignedUser);
        return task;

    }
}
