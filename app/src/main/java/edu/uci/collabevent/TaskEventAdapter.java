package edu.uci.collabevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prateek on 07/06/16.
 */
public class TaskEventAdapter extends RecyclerView.Adapter<TaskEventAdapter.ViewHolder> {

    private List<Task> mTasks;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View taskView = inflater.inflate(R.layout.task_event_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskEventAdapter.ViewHolder viewHolder, int position) {
        Task task = mTasks.get(position);

        TextView titleView = viewHolder.taskTitle;
        titleView.setText(task.getTitle());

        TextView eventNameView = viewHolder.eventName;
        eventNameView.setText(task.getEventName());

        TextView dateView = viewHolder.dueDate;
        if (task.getDueDate() != null) {
            dateView.setText(Html.fromHtml("<b>Due: </b>") + Task.displayDateFormat.format(task.getDueDate()));
        }
        ImageView imageView = viewHolder.statusImage;

        // Put a check mark in the imageView if the task has been completed.
        if (task.getTaskStatus() == TaskStatus.COMPLETED) {
            imageView.setImageResource(R.drawable.ic_done_black_18dp);
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView taskTitle;
        public TextView eventName;
        public TextView dueDate;
        public ImageView statusImage;

        public ViewHolder(View itemView) {
            super(itemView);
            taskTitle = (TextView) itemView.findViewById(R.id.task_name);
            eventName = (TextView) itemView.findViewById(R.id.task_assignment);
            dueDate = (TextView) itemView.findViewById(R.id.due_date);
            statusImage = (ImageView) itemView.findViewById(R.id.status);
        }

    }

    public TaskEventAdapter(List<Task> mTasks) {
        this.mTasks = mTasks;
    }

}