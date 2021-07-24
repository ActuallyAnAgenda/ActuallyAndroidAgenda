package com.example.actuallyanagenda;

import android.content.*; //is this going to take too long...?
import android.database.Cursor;
import android.database.sqlite.*;
import android.provider.BaseColumns;

import java.util.*;

public class Database {
    dbHelper help;
    public Database(Context context) {
        help = new dbHelper(context);
    }

    /**
     * inserts a task into the Task table
     * @param name name of task
     * @param description description of task (enter "" if blank)
     * @param durationInMins estimated time in minutes
     * @param dueDate due date, in DD/MM/YYYY format...?
     * @return the id of the inserted task in the table
     */
    public long insertTask(String name, String description, int durationInMins, String dueDate) {
        //preferably the columns aren't hard coded :/
        SQLiteDatabase db = help.getWritableDatabase();
        ContentValues insert = new ContentValues();
        insert.put("Name", name);
        insert.put("Description", description);
        insert.put("durationInMins", durationInMins);
        insert.put("dueDate", dueDate);
        return db.insert("Tasks", null , insert);
    }
    /**
     * gets the Task table
     * @return an ArrayList of the task table, with each task represented by a String array.
     *          [0] is the ID of the task, [1] is the Name, [2] is the description, [3] is the duration in minutes, etc.
     */
    public ArrayList<String[]> getTasks() {
        SQLiteDatabase db = help.getWritableDatabase();
        String[] columns = {"Name", "Description", "DurationInMins", "Due Date", "Scheduled Start Time", "Scheduled End Time", "Priority"};
        Cursor cursor =db.query("Tasks", columns,null,null,null,null,null);

        ArrayList<String[]> output = new ArrayList<String[]>();
        while (cursor.moveToNext())
        {
            String [] currentTask = new String [columns.length+1]; //probably should've made every entry in the table a string...
            currentTask[0] = cursor.getInt(cursor.getColumnIndex("_id"))+"";
            currentTask[1] = cursor.getString(cursor.getColumnIndex("Name"));
            currentTask[2] = cursor.getString(cursor.getColumnIndex("Description"));
            currentTask[3] = cursor.getString(cursor.getColumnIndex("DurationInMins"));
            currentTask[4] = cursor.getString(cursor.getColumnIndex("Due Date"));
            currentTask[5] = cursor.getString(cursor.getColumnIndex("Scheduled Start Time"));
            currentTask[6] = cursor.getString(cursor.getColumnIndex("Scheduled End Time"));
            currentTask[7] = cursor.getString(cursor.getColumnIndex("Priority"));
            output.add(currentTask);
        }
        return output;
    }

    static class dbHelper extends SQLiteOpenHelper { //create/update database
        public static final int DATABASE_VERSION = 1; //increment when you change database schema
        public static final String DATABASE_NAME = "ActuallyAnAgenda.db";

        public dbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public static class TableInfo implements BaseColumns {
            public String tableName = ""; //string
            public String [] columnNames = new String [1]; //columNames and columnTypes should have the same length
            public String [] columnTypes = new String [1];
            public TableInfo(String tableName, String [] columnNames, String [] columnTypes) {
                tableName = tableName;
                columnNames = columnNames;
                columnTypes = columnTypes;
            }
        }
        public static TableInfo tasksTable = new TableInfo("Tasks",
                new String [] {"Name", "Description", "DurationInMins", "Due Date", "Scheduled Start Time", "Scheduled End Time", "Priority"},
                new String [] {"TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT"});

        public String createTableSQL(TableInfo table) { //creates the sql statement that creates the table
            String output = "CREATE TABLE " + table.tableName + " (" + tasksTable._ID + " INTEGER PRIMARY KEY, ";
            int length = table.columnNames.length;
            for (int x=0; x<length-1; x++) {
                output+=table.columnNames[x]+" "+table.columnTypes[x]+", ";
            }
            output+=table.columnNames[length-1]+" "+table.columnTypes[length-1]+")";
            return output;
        }
        public String deleteTableSQL(TableInfo table) { //creates the sql statement that deletes the table
            return "DROP TABLE IF EXISTS " + table.tableName;
        }

        public void onCreate(SQLiteDatabase db) { //create database if it doesn't exist
            db.execSQL(createTableSQL(tasksTable));
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //when the version number changes
            //delete table and create new one when you update
            db.execSQL(deleteTableSQL(tasksTable));
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
