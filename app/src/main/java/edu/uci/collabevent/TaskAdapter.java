package edu.uci.collabevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View taskView = inflater.inflate(R.layout.task_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder viewHolder, int position) {
        Task task = mTasks.get(position);

        TextView titleView = viewHolder.taskTitle;
        titleView.setText(task.getTitle());

        TextView eventNameView = viewHolder.eventName;
        eventNameView.setText(task.getEventName());

        TextView dateView = viewHolder.dueDate;
        if (task.getDueDate() != null) {
            dateView.setText(Task.displayDateFormat.format(task.getDueDate()));
        }
        ImageView imageView = viewHolder.statusImage;
//        DownloadImageTask downloadImageTask = new DownloadImageTask(imageView);
//        Log.d("DEBUG-IMG", task.getImgURL().toString());
//        downloadImageTask.execute(task.getImgURL().toString());

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
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            dueDate = (TextView) itemView.findViewById(R.id.due_date);
            statusImage = (ImageView) itemView.findViewById(R.id.status);
        }

    }

    public TaskAdapter(List<Task> mTasks) {
        this.mTasks = mTasks;
    }

}
