package com.example.smarttracker.fragments;




import android.content.Context;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.smarttracker.Adapters.CategoryAdapter;
import com.example.smarttracker.Adapters.TaskAdapter;
import com.example.smarttracker.AddNewTask;
import com.example.smarttracker.DialogCloseListener;
import com.example.smarttracker.Model.CategoryModel;
import com.example.smarttracker.Model.TaskModel;
import com.example.smarttracker.R;
import com.example.smarttracker.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment implements DialogCloseListener {
    private RecyclerView tasksRecyclerView;
    private RecyclerView categoryRecyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fab;
    private DatabaseHandler dbHandler;
    private List<TaskModel> tasklist;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categorylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View HomeView = inflater.inflate(R.layout.fragment_home, container, false);
        Context context = requireContext();

        dbHandler = new DatabaseHandler(context);
        dbHandler.open();
        tasklist = new ArrayList<>();
        tasksRecyclerView = HomeView.findViewById(R.id.tasksRecyclerView);
        categoryRecyclerView=HomeView.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        categoryAdapter = new CategoryAdapter(dbHandler,context);
        tasksRecyclerView.setLayoutManager((new LinearLayoutManager(context)));
        taskAdapter = new TaskAdapter(dbHandler,context);
        categoryRecyclerView.setAdapter(categoryAdapter);
        tasksRecyclerView.setAdapter(taskAdapter);
        fab = HomeView.findViewById(R.id.fab);
        categorylist = dbHandler.getAllCategories(null);
        categoryAdapter.setcategory(categorylist);
        tasklist = dbHandler.getAllTasks(null);
        taskAdapter.setTasks(tasklist);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(requireActivity().getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
        Log.d("HomeFragment", "onCreateView: Fragment created");
        return HomeView;

    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        tasklist = dbHandler.getAllTasks(null);
        Collections.reverse(tasklist);
        taskAdapter.setTasks(tasklist);
        taskAdapter.notifyDataSetChanged();
        Log.d("HomeFragment", "handleDialogClose: Dialog closed, data updated");
    }
}