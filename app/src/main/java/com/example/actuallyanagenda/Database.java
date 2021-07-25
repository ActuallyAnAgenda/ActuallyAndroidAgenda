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

    public static class TableInfo {
        public String tableName = ""; //string
        public String [] columnNames = new String [1]; //columnNames and columnTypes should have the same length
        public String [] columnTypes = new String [1];
        public TableInfo(String tableName, String [] columnNames, String [] columnTypes) {
            this.tableName = tableName;
            this.columnNames = columnNames;
            this.columnTypes = columnTypes;
        }
    }
    //list of tables
    public static TableInfo tasksTable = new TableInfo("Tasks",
            new String [] {"Name", "Description", "DurationInMins", "Due_Date", "Due_Time", "Scheduled_Start_Time", "Scheduled_End_Time", "Variable"},
            new String [] {"TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT"});

    /**
     * inserts a task into the Task table
     * @param name name of task
     * @param description description of task (enter "" if blank)
     * @param durationInMins estimated time in minutes
     * @param dueDate due date, in DD/MM/YYYY format...?
     * @return the id of the inserted task in the table
     */
    public void insertTask(String name, String description, String durationInMins, String dueDate) {
        //preferably the columns aren't hard coded :/
        SQLiteDatabase db = help.getWritableDatabase();
        db.execSQL("INSERT INTO Tasks(Name,Description,durationInMins,Due_Date) VALUES ('"+name+"', '"+description+"', '"+durationInMins+"', '"+dueDate+"')");
    }
    /**
     * gets the Task table
     * @return an ArrayList of the task table, with each task represented by a String array. The first entry of the ArrayList are the column names
     *          [0] is the ID of the task, [1] is the Name, [2] is the description, [3] is the duration in minutes, etc.
     */
    public ArrayList<String[]> getTasks() {
        SQLiteDatabase db = help.getWritableDatabase();
        Cursor cursor = db.query(tasksTable.tableName, tasksTable.columnNames,null,null,null,null,null);
        ArrayList<String[]> output = new ArrayList<>();
        String [] columnTitles = new String [tasksTable.columnNames.length+1];
        columnTitles[0] = "id";
        for (int x=1; x<=tasksTable.columnNames.length; x++) {
            columnTitles[x] = tasksTable.columnNames[x-1];
        }
        output.add(columnTitles);
        while (cursor.moveToNext()) {
            String [] currentTask = new String [tasksTable.columnNames.length+1];
            currentTask[0] = cursor.getInt(Math.max(cursor.getColumnIndex(columnTitles[0]), 0))+""; //bc apparently ID is at column -1 and doesn't exist
            for (int x=1; x<=tasksTable.columnNames.length; x++) {
                currentTask[x] = cursor.getString(cursor.getColumnIndex(tasksTable.columnNames[x-1]));
            }
            output.add(currentTask);
        }
        cursor.close();
        return output;
    }
    public void deleteTasksTable() {
        SQLiteDatabase db = help.getWritableDatabase();
        help.deleteTasksTable(db);
    }
    public void deleteTasksTableSchedule(String nameOfDeletedTask) {
        SQLiteDatabase db = help.getWritableDatabase();
        help.clearTaskTableEntry(db, nameOfDeletedTask);
    }

    static class dbHelper extends SQLiteOpenHelper { //create/update database
        public static final int DATABASE_VERSION = 8; //increment when you change database schema
        public static final String DATABASE_NAME = "ActuallyAnAgenda.db";

        public dbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public String createTableSQL(TableInfo table) { //creates the sql statement that creates the table
            String output = "CREATE TABLE " + table.tableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "; //for some inexplicable reason the id refuses to work properly
            int length = table.columnNames.length;
            for (int x=0; x<length-1; x++) {
                output+=table.columnNames[x]+" "+table.columnTypes[x]+", ";
            }
            output+=table.columnNames[length-1]+" "+table.columnTypes[length-1]+");";
            return output;
        }
        public String deleteTableSQL(TableInfo table) { //creates the sql statement that deletes the table
            return "DROP TABLE IF EXISTS " + table.tableName;
        }
        public void deleteTasksTable(SQLiteDatabase db) { //function that deletes the tasks table
            db.execSQL(deleteTableSQL(tasksTable));
        }
        public void clearTaskTableEntry(SQLiteDatabase db, String deleteName) { //creates the sql statement that completely empties the table
            db.execSQL(clearTaskTableEntrySQL(tasksTable, deleteName));
        }
        public String clearTableSQL(TableInfo table) { //creates the sql statement that completely empties the table
            return "DELETE FROM " + table.tableName;
        }
        public String clearTaskTableEntrySQL(TableInfo table, String deleteName) { //creates the sql statement that completely empties the table
            return "DELETE FROM " + table.tableName + "WHERE ";
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
