package com.example.smarttracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarttracker.Adapters.CategoryAdapter;
import com.example.smarttracker.Adapters.TaskAdapter;
import com.example.smarttracker.Model.TaskModel;
import com.example.smarttracker.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText editTaskName;
    private EditText editCategory;
    private Button SetDate;
    private Button SetTime;
    private Button AddTask;
    private DatabaseHandler dbHandler;
    private TextView showdate;
   private TextView showtime;
    private String finalDate;
    private String finalTime;
    private CategoryAdapter categoryAdapter;
    private TaskAdapter taskAdapter;
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
        dbHandler = new DatabaseHandler(requireContext());
        dbHandler.open();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.taskadd_bottom_sheet, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTaskName = view.findViewById(R.id.newTaskText);
        editCategory = view.findViewById(R.id.Category);
        SetDate = view.findViewById(R.id.Set_Date);
        SetTime = view.findViewById(R.id.Set_Time);
        showdate = view.findViewById(R.id.showdate);
        showtime = view.findViewById(R.id.showtime);
        AddTask = view.findViewById(R.id.newTaskButton);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            long taskid = bundle.getLong("id");
            String taskname = bundle.getString("task");
            String taskdate = bundle.getString("Date");
            String tasktime = bundle.getString("Time");
            String category = bundle.getString("category");
            editTaskName.setText(taskname);
            showdate.setText(taskdate);
            showtime.setText(tasktime);
            editCategory.setText(category);
        }


        SetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        SetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });


        boolean finalIsUpdate = isUpdate;
        AddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TaskName = editTaskName.getText().toString();
                String TaskCategory = editCategory.getText().toString();
                String Date = finalDate.toString();
                String Time = finalTime.toString();

                    if(finalIsUpdate){
                        dbHandler.updateTask(bundle.getInt("id"),TaskName,Date,Time,TaskCategory,0);
                    }
                    else {
                        TaskModel newTask = new TaskModel();

                        newTask.setName(TaskName);
                        newTask.setDate(Date);
                        newTask.setTime(Time);
                        newTask.setStatus(0);


                        dbHandler.addTask(newTask, TaskCategory);
                    }
                Log.d("taskadded","done");
                dismiss();
            }
        });
    }


    private void showTimePicker() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                            if (hourOfDay > 12) {
                                hourOfDay -= 12;
                            }
                        } else {
                            amPm = "AM";
                            if (hourOfDay == 0) {
                                hourOfDay = 12;
                            }
                        }
                        // Handle the selected time
                        finalTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minute, amPm);
                        showtime.setText(finalTime);

                        // You can do something with the selected time here
                    }
                },
                currentHour, // Initial hour
                currentMinute, // Initial minute
                false // 24-hour format
        );

        // Show the time picker dialog
        timePickerDialog.show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        // Handle the selected date here
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        finalDate = sdf.format(calendar.getTime());
                        showdate.setText(finalDate);
                    }
                },
                initialYear,
                initialMonth,
                initialDay
        );
        datePickerDialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        Activity activity = getActivity();
        Fragment homeFragment = getParentFragment();

        Context context = getContext();
        if(homeFragment instanceof DialogCloseListener){
            ((DialogCloseListener)homeFragment).handleDialogClose(dialog);
        }
        if(activity instanceof DialogCloseListener){
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }
}
