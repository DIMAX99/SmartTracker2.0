package com.example.smarttracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttracker.Model.TaskModel;
import com.example.smarttracker.R;
import com.example.smarttracker.Utils.DatabaseHandler;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<TaskModel> tasklist;
    private DatabaseHandler db;
    private Context context;

    public TaskAdapter(DatabaseHandler db, Context context){
        this.db = db;
        this.context = context;
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
        return context;
    }

    public void setTasks(List<TaskModel> tasklist){
        this.tasklist=tasklist;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;

        ViewHolder(View view){
            super(view);
            task=view.findViewById(R.id.todoCheckBox);
        }
    }
}
