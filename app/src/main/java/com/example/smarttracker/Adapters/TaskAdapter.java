package com.example.smarttracker.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttracker.AddNewTask;
import com.example.smarttracker.MainActivity;
import com.example.smarttracker.Model.TaskModel;
import com.example.smarttracker.R;
import com.example.smarttracker.Utils.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<TaskModel> tasklist;
    private DatabaseHandler db;
    private Context context;
    private CategoryAdapter category;
    private MainActivity activity;


    public TaskAdapter(DatabaseHandler db, MainActivity activity,CategoryAdapter category) {
        this.db = db;
        this.activity = activity;
        this.category=category;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.open();
        final TaskModel item = tasklist.get(position);
        holder.task.setText(item.getName());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.tasktime.setText(item.getTime());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try{
            Date date = inputFormat.parse(item.getDate());
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);

            String dayAbbreviation = dayFormat.format(date);
            String monthAbbreviation = monthFormat.format(date);
            String dateText = dateFormat.format(date);
            holder.taskday.setText(dayAbbreviation);
            holder.taskmonth.setText(monthAbbreviation);
            holder.taskdate.setText(dateText);
        }catch (ParseException e){
            e.printStackTrace();
        }
        int taskcompletedornot = item.getStatus();
        if(taskcompletedornot==1){
            holder.task.setChecked(true);
            holder.taskstatus.setText("Completed");
        }else{
            holder.task.setChecked(false);
            holder.taskstatus.setText("Upcoming");
        }
        holder.taskcategory.setText(db.getCategoryNameByTaskId(item.getId()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus((int) item.getId(), 1);
                    holder.taskstatus.setText("Completed");
                    Toast.makeText(activity, "Task Completed", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateStatus((int) item.getId(), 0);
                    holder.taskstatus.setText("Upcoming");
                }
            }
        });

    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return tasklist.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<TaskModel> tasklist) {
        this.tasklist = tasklist;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        TaskModel item = tasklist.get(position);
        boolean shouldDeleteCategory = db.deleteTask(item.getId());
        tasklist.remove(position);
        notifyItemRemoved(position);
        if (shouldDeleteCategory) {
            // Update the CategoryAdapter by calling its method to refresh the category list
            category.updateCategories();
        }
    }

    public void editItem(int position) {
        TaskModel item = tasklist.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", (int) item.getId());
        bundle.putString("task", item.getName());
        bundle.putString("Date", item.getDate());
        bundle.putString("Time", item.getTime());
        bundle.putLong("categoryId", item.getCategoryId());
        bundle.putString("category", db.getCategoryNameByTaskId(item.getId()));
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView tasktime;
        TextView taskday;
        TextView taskdate;
        TextView taskmonth;
        TextView taskstatus;
        TextView taskcategory;


        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            tasktime = view.findViewById(R.id.tasktime);
            taskdate = view.findViewById(R.id.taskdate);
            taskday = view.findViewById(R.id.taskday);
            taskmonth = view.findViewById(R.id.taskmonth);
            taskstatus = view.findViewById(R.id.taskstatus);
            taskcategory = view.findViewById(R.id.taskcategory);
        }
    }
}
