package com.example.smarttracker.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttracker.AddNewTask;
import com.example.smarttracker.MainActivity;
import com.example.smarttracker.Model.TaskModel;
import com.example.smarttracker.R;
import com.example.smarttracker.Utils.DatabaseHandler;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<TaskModel> tasklist;
    private DatabaseHandler db;
    private Context context;
    private MainActivity activity;

    public TaskAdapter(DatabaseHandler db, MainActivity activity){
        this.db = db;
        this.activity = activity;
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
        holder.task.setText(item.getName()  );
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus((int) item.getId(), 1);
                } else {
                    db.updateStatus((int) item.getId(), 0);
                }
            }
        });

    }

    private boolean toBoolean(int n) {
        return n!=0;
    }

    @Override
    public int getItemCount() {
        return tasklist.size();
    }
    public Context getContext() {
        return activity;
    }

    public void setTasks(List<TaskModel> tasklist){
        this.tasklist=tasklist;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        TaskModel item = tasklist.get(position);
        db.deleteTask(item.getId());
        tasklist.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        TaskModel item = tasklist.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", (int) item.getId());
        bundle.putString("task", item.getName());
        bundle.putString("Date", item.getDate());
        bundle.putString("Time",item.getTime());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;

        ViewHolder(View view){
            super(view);
            task=view.findViewById(R.id.todoCheckBox);
        }
    }
}
