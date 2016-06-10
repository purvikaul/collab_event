package edu.uci.collabevent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 30-05-2016.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> mTasks;
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View taskView = inflater.inflate(R.layout.task_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder viewHolder, int position) {
        Task task = mTasks.get(position);
        viewHolder.task = task;

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView taskTitle;
        public TextView eventName;
        public TextView dueDate;
        public ImageView statusImage;
        public Task task;

        public ViewHolder(View itemView) {
            super(itemView);
            taskTitle = (TextView) itemView.findViewById(R.id.task_name);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            dueDate = (TextView) itemView.findViewById(R.id.due_date);
            statusImage = (ImageView) itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Pass event id to the Detail activity
                    Intent intent = new Intent(context, HomeActivity.class);
                    Bundle informationBundle = new Bundle();
                    informationBundle.putParcelable("task", task);
                    intent.putExtras(informationBundle);
                    context.startActivity(intent);
                }
            });
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle("Select The Action");
                    menu.add(0, task.getTaskId(), 0, "Edit");//groupId, itemId, order, title
                    menu.add(0, task.getTaskId(), 0, "SMS");

                }
            });

        }
        }

    public TaskAdapter(List<Task> mTasks) {
        this.mTasks = mTasks;
    }

}
