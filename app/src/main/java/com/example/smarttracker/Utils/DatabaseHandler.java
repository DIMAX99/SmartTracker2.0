package com.example.smarttracker.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smarttracker.Model.CategoryModel;
import com.example.smarttracker.Model.TaskModel;
import com.example.smarttracker.TaskReminderBroadcastReceiver;
import com.example.smarttracker.taskparams.TaskParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler{
    private SQLiteDatabase database;
    private TaskParams dbHelper;
    private Context context;


    public DatabaseHandler(@Nullable Context context) {
        this.context=context;
        dbHelper = new TaskParams(context);

    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }


    // Insert a new task
    public long addTask(TaskModel Task, String category){
        ContentValues cv = new ContentValues();
        cv.put(TaskParams.TASK_COLUMN_NAME, Task.getName());
        cv.put(TaskParams.TASK_COLUMN_Date, Task.getDate());
        cv.put(TaskParams.TASK_COLUMN_Time, Task.getTime());
        if (!TextUtils.isEmpty(category)) {
            Cursor cursor = database.query(TaskParams.CATEGORY_TABLE_NAME, new String[]{TaskParams.COLUMN_CATEGORY_ID}, TaskParams.COLUMN_CATEGORY_NAME + "=?", new String[]{category}, null, null, null);
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int categoryId = cursor.getInt(cursor.getColumnIndex(TaskParams.COLUMN_CATEGORY_ID));
                cv.put(TaskParams.COLUMN_TASK_CATEGORY_ID, categoryId);
            } else {
                ContentValues categoryValues = new ContentValues();
                categoryValues.put(TaskParams.COLUMN_CATEGORY_NAME, category);
                long newCategoryId = database.insert(TaskParams.CATEGORY_TABLE_NAME, null, categoryValues);
                cv.put(TaskParams.COLUMN_TASK_CATEGORY_ID, newCategoryId);
            }
            cursor.close();
        }
        cv.put(TaskParams.TASK_COLUMN_STATUS, 0);
        long taskId = database.insert(TaskParams.TASK_TABLE_NAME, null, cv);
        if(taskId!=-1){
            scheduleTaskReminder(taskId, Task.getName(), Task.getDate(), Task.getTime(), 30);
        }
        return taskId;
    }
    // Schedule a reminder for a specific task
    private void scheduleTaskReminder(long taskId, String taskName, String date, String time, int minutesBeforeReminder) {
        SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH);

        try {
            Date dateTimeObj = dateTimeSdf.parse(date + " " + time);

            Calendar taskDateTime = Calendar.getInstance();
            taskDateTime.setTime(dateTimeObj);

            Calendar currentTime = Calendar.getInstance();
            long reminderTime;

            // Calculate the reminder time 30 minutes before the task
            Calendar reminderTimeBefore = Calendar.getInstance();
            reminderTimeBefore.setTime(taskDateTime.getTime());
            reminderTimeBefore.add(Calendar.MINUTE, -minutesBeforeReminder);

            if (taskDateTime.before(currentTime)) {
                // Task date and time is in the past, schedule the reminder immediately
                reminderTime = currentTime.getTimeInMillis();
            } else if (reminderTimeBefore.before(currentTime)) {
                // Task date and time is within the next 30 minutes, schedule the reminder at the exact task date and time
                reminderTime = taskDateTime.getTimeInMillis();
            } else {
                // Task date and time is more than 30 minutes in the future, schedule the reminder 30 minutes before
                reminderTime = reminderTimeBefore.getTimeInMillis();
            }

            // Create a PendingIntent for the reminder
            Intent intent = new Intent(context, TaskReminderBroadcastReceiver.class);
            intent.putExtra("task_name", taskName);

            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12 and above
                pendingIntent = PendingIntent.getBroadcast(context, (int) taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                // Android 11 and lower
                pendingIntent = PendingIntent.getBroadcast(context, (int) taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Log.d("set alarm", " done");

            // Schedule the reminder
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //retrieve task from the database
    public List<TaskModel> getAllTasks() {
        String[] projection = {
                TaskParams.TASK_COLUMN_ID,
                TaskParams.TASK_COLUMN_NAME,
                TaskParams.TASK_COLUMN_Date,
                TaskParams.TASK_COLUMN_Time,
                TaskParams.TASK_COLUMN_STATUS,
                TaskParams.COLUMN_TASK_CATEGORY_ID
        };
        return getAllTasks(projection);
    }
    @SuppressLint("Range")
    public List<TaskModel> getAllTasks(String[] projection){
        List<TaskModel> taskList = new ArrayList<>();
        Cursor cursor = null;
        database.beginTransaction();

        try {
            cursor = database.query(TaskParams.TASK_TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        TaskModel task = new TaskModel();
                        task.setId(cursor.getLong(cursor.getColumnIndex(TaskParams.TASK_COLUMN_ID)));
                        task.setName(cursor.getString(cursor.getColumnIndex(TaskParams.TASK_COLUMN_NAME)));
                        task.setDate(cursor.getString(cursor.getColumnIndex(TaskParams.TASK_COLUMN_Date)));
                        task.setTime(cursor.getString(cursor.getColumnIndex(TaskParams.TASK_COLUMN_Time)));
                        task.setCategoryId(cursor.getLong(cursor.getColumnIndex(TaskParams.COLUMN_TASK_CATEGORY_ID)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(TaskParams.TASK_COLUMN_STATUS)));
                        taskList.add(task);
                    }
                    while (cursor.moveToNext());
                }
            }
        } finally {
            database.endTransaction();
            assert cursor != null;
            cursor.close();

        }
        return taskList;
    }

    //creating a category
    public long addCategory(CategoryModel Category){
        ContentValues values = new ContentValues();
        values.put(TaskParams.COLUMN_CATEGORY_NAME,Category.getName());
        return database.insert(TaskParams.CATEGORY_TABLE_NAME, null, values);
    }
    // Retrieve categories from the database
    public List<CategoryModel> getAllCategories() {
        String[] projection = {
                TaskParams.COLUMN_CATEGORY_ID,
                TaskParams.COLUMN_CATEGORY_NAME
        };
        return getAllCategories(projection);
    }
    @SuppressLint("Range")
    public List<CategoryModel> getAllCategories(String[] projection) {
        List<CategoryModel> CategoryList = new ArrayList<>();
        Cursor cursor = null;
        database.beginTransaction();

        try {
            cursor = database.query(TaskParams.CATEGORY_TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        CategoryModel Category = new CategoryModel();
                        Category.setId(cursor.getLong(cursor.getColumnIndex(TaskParams.COLUMN_CATEGORY_ID)));
                        Category.setName(cursor.getString(cursor.getColumnIndex(TaskParams.COLUMN_CATEGORY_NAME)));
                        CategoryList.add(Category);
                    }
                    while (cursor.moveToNext());
                }
            }
        } finally {
            database.endTransaction();
            assert cursor != null;
            cursor.close();

        }
        return CategoryList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(TaskParams.TASK_COLUMN_STATUS, status);
        database.update(TaskParams.TASK_TABLE_NAME, cv, TaskParams.TASK_COLUMN_ID + "= ?",
                new String[] {String.valueOf(id)});
    }
    public int updateTask(long taskId, String name, String date, String time, String categoryName, int status) {
        // Retrieve the category ID based on the category name
        ContentValues values = new ContentValues();
        values.put(TaskParams.TASK_COLUMN_NAME, name);
        values.put(TaskParams.TASK_COLUMN_Date, date);
        values.put(TaskParams.TASK_COLUMN_Time, time);
        if (categoryName != null && !categoryName.isEmpty()) {
            long categoryId = getCategoryId(categoryName);
            values.put(TaskParams.COLUMN_TASK_CATEGORY_ID, categoryId);
        } else {
            values.putNull(TaskParams.COLUMN_TASK_CATEGORY_ID);
        }
        values.put(TaskParams.TASK_COLUMN_STATUS, status);

        String whereClause = TaskParams.TASK_COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(taskId)};

        return database.update(TaskParams.TASK_TABLE_NAME, values, whereClause, whereArgs);
    }

    public int updateCategory(long categoryId, String newName) {
        ContentValues values = new ContentValues();
        values.put(TaskParams.COLUMN_CATEGORY_NAME, newName);

        String whereClause = TaskParams.COLUMN_CATEGORY_ID + "=?";
        String[] whereArgs = {String.valueOf(categoryId)};

        return database.update(TaskParams.CATEGORY_TABLE_NAME, values, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    private long getCategoryId(String categoryName) {
        long categoryId = -1;
        Cursor cursor = database.query(
                TaskParams.CATEGORY_TABLE_NAME,
                new String[]{TaskParams.COLUMN_CATEGORY_ID},
                TaskParams.COLUMN_CATEGORY_NAME + "=?",
                new String[]{categoryName},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                categoryId = cursor.getLong(cursor.getColumnIndex(TaskParams.COLUMN_CATEGORY_ID));
            }
            cursor.close();
        }

        return categoryId;
    }
    public boolean deleteTask(long taskId) {
        database.beginTransaction();

        try {
            // Step 3: Check if there are any tasks left in the category associated with the deleted task
            long categoryId = getTaskCategoryId(taskId);
            // Step 2: Delete the task with the given ID from the task table
            String whereClause = TaskParams.TASK_COLUMN_ID + "=?";
            String[] whereArgs = {String.valueOf(taskId)};
            database.delete(TaskParams.TASK_TABLE_NAME, whereClause, whereArgs);
            boolean isCategoryEmpty = isCategoryEmpty(categoryId);

            // Step 4: If there are no tasks left in the category, delete the category
                if(isCategoryEmpty) {
                    whereClause = TaskParams.COLUMN_CATEGORY_ID + "=?";
                    whereArgs = new String[]{String.valueOf(categoryId)};
                    database.delete(TaskParams.CATEGORY_TABLE_NAME, whereClause, whereArgs);
                }

            // Step 5: End the transaction on the database
            database.setTransactionSuccessful();

            // Step 6: Return the flag indicating whether the category was deleted or not
            return isCategoryEmpty;
        } finally {
            database.endTransaction();
        }
    }

    @SuppressLint("Range")
    private long getTaskCategoryId(long taskId) {
        long categoryId = -1;
        Cursor cursor = database.query(
                TaskParams.TASK_TABLE_NAME,
                new String[]{TaskParams.COLUMN_TASK_CATEGORY_ID},
                TaskParams.TASK_COLUMN_ID + "=?",
                new String[]{String.valueOf(taskId)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                categoryId = cursor.getLong(cursor.getColumnIndex(TaskParams.COLUMN_TASK_CATEGORY_ID));
            }
            cursor.close();
        }

        return categoryId;
    }

    private boolean isCategoryEmpty(long categoryId) {
        String[] whereArgs = {String.valueOf(categoryId)};
        Cursor cursor = database.query(
                TaskParams.TASK_TABLE_NAME,
                new String[]{TaskParams.TASK_COLUMN_ID},
                TaskParams.COLUMN_TASK_CATEGORY_ID + "=?",
                whereArgs,
                null,
                null,
                null
        );

        boolean isEmpty = true;
        if (cursor != null) {
            isEmpty = cursor.getCount() == 0;
            cursor.close();
        }

        return isEmpty;
    }


}

