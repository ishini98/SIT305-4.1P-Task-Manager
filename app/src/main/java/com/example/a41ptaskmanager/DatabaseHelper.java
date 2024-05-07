package com.example.a41ptaskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlinx.coroutines.scheduling.Task;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String CUSTOMER_TABLE = "CUSTOMER_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_DUE_DATE = "DUE_DATE";
    public static final String COLUMN_IS_COMPLETE = "IS_COMPLETE";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "tasks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + CUSTOMER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_DUE_DATE + " TEXT, " + COLUMN_IS_COMPLETE + " BOOL)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<TaskModel> getAll(boolean showCompleted) {
        // Set up data structure for task list to return
        List<TaskModel> returnList = new ArrayList<>();

        // Get all records from DB
        String queryString = "SELECT * FROM " + CUSTOMER_TABLE;
        if (!showCompleted) {
            queryString += " WHERE " + COLUMN_IS_COMPLETE + " = 0";
        }
        queryString += " ORDER BY date(" + COLUMN_DUE_DATE + ") ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        TaskModel taskModel;

        // If there are any records, add them to our list
        if (cursor.moveToFirst()) {
            do {
                // Get all the columns of the task
                int taskId = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskDescription = cursor.getString(2);
                String taskDueDateString = cursor.getString(3);
//                Date taskDueDate = new Date(2024, 04, 18);
                boolean taskIsComplete = cursor.getInt(4) == 1;

                // Create a task object out of them
                taskModel = new TaskModel(taskId, taskName, taskDescription, taskDueDateString, taskIsComplete);

                // And append it to our list of tasks.
                returnList.add(taskModel);

            } while (cursor.moveToNext());
        }

        // Clean up our DB
        cursor.close();
        db.close();

        // Return the list
        return returnList;
    }

    public boolean addTask(TaskModel taskModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, taskModel.getName());
        cv.put(COLUMN_DESCRIPTION, taskModel.getDescription());
        cv.put(COLUMN_DUE_DATE, taskModel.getDueDate());
        cv.put(COLUMN_IS_COMPLETE, taskModel.isComplete());

        long insert = db.insert(CUSTOMER_TABLE, null, cv);
        return insert != -1;
    }

    public TaskModel getTaskById(int id) {
        String queryString = "SELECT * FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        TaskModel taskModel;

        // If a record was returned, create a task object out of it
        if (cursor.moveToFirst()) {
                // Get all the columns of the task
                int taskId = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskDescription = cursor.getString(2);
                String taskDueDateString = cursor.getString(3);
//                Date taskDueDate = new Date(2024, 04, 18);
                boolean taskIsComplete = cursor.getInt(4) == 1;

                // Create a task object out of them
                taskModel=  new TaskModel(taskId, taskName, taskDescription, taskDueDateString, taskIsComplete);

        } else {
            taskModel = new TaskModel(-1, "Error", "", "", false);
        }

        // Clean up our DB
        cursor.close();
        db.close();

        // Return the task
        return taskModel;
    }

    public boolean deleteTaskById(int id) {
        String queryString = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        return cursor.moveToFirst(); // returns true if a record was deleted and returned from db.rawQuery; false if nothing was returned.
    }

    public boolean updateTask(TaskModel taskModel) {

        SQLiteDatabase db = this.getReadableDatabase();

        // First, check if the record to be updated exists
        String queryString = "SELECT * FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + " = " + taskModel.getId();
        Cursor cursor = db.rawQuery(queryString, null);
        // If no record was found to update, return false.
        if (!cursor.moveToFirst()) {
            // Clean up our DB first
            cursor.close();
            db.close();

            return false;
        }

        // If the record does exist, update it
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, taskModel.getName());
        cv.put(COLUMN_DESCRIPTION, taskModel.getDescription());
        cv.put(COLUMN_DUE_DATE, taskModel.getDueDate());
        cv.put(COLUMN_IS_COMPLETE, taskModel.isComplete());
        db.update(CUSTOMER_TABLE, cv, "id = ?", new String[]{Integer.toString(taskModel.getId())} );

        // Clean up our DB
        cursor.close();
        db.close();

        return true;
    }
}
