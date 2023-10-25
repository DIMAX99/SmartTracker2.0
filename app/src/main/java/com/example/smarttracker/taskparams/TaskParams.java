package com.example.smarttracker.taskparams;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

    public class TaskParams extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "toDoListDatabase";
    //TASK TABLE
    public static final String TASK_TABLE_NAME = "todo";
    public static final String TASK_COLUMN_ID = "id";
    public static final String TASK_COLUMN_NAME = "task";
    public static final String TASK_COLUMN_Date = "Date";
    public static final String TASK_COLUMN_Time = "Time";
    public static final String TASK_COLUMN_STATUS = "status";
    public static final String COLUMN_TASK_CATEGORY_ID = "category_id";

    // CATEGORY TABLE
    public static final String CATEGORY_TABLE_NAME = "categories";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";

    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TaskParams.TASK_TABLE_NAME + " (" +
            TaskParams.TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TaskParams.TASK_COLUMN_NAME + " TEXT, " +
            TaskParams.TASK_COLUMN_Date + " TEXT, " +
            TaskParams.TASK_COLUMN_Time + " TEXT, " +
            TaskParams.TASK_COLUMN_STATUS + " INTEGER, " +
            TaskParams.COLUMN_TASK_CATEGORY_ID + " INTEGER, " +
            "FOREIGN KEY(" + TaskParams.COLUMN_TASK_CATEGORY_ID + ") REFERENCES " + TaskParams.CATEGORY_TABLE_NAME + "(" + TaskParams.COLUMN_CATEGORY_ID + "));";
    private static final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TaskParams.CATEGORY_TABLE_NAME + " (" +
            TaskParams.COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TaskParams.COLUMN_CATEGORY_NAME + " TEXT);";
    public TaskParams(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        onCreate(db);
    }
}
