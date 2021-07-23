package com.example.actuallyanagenda;
import android.content.Context;
import android.database.sqlite.*;
import android.provider.BaseColumns;

public class Database {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Database() {}

    static class dbHelper extends SQLiteOpenHelper { //create/update database
        public static final int DATABASE_VERSION = 1; //increment when you change database schema
        public static final String DATABASE_NAME = "ActuallyAnAgenda.db";

        public dbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public class TableInfo implements BaseColumns {
            public String tableName = ""; //string
            public String [] columnNames = new String [1]; //columNames and columnTypes should have the same length
            public String [] columnTypes = new String [1];
            public TableInfo(String tableName, String [] columnNames, String [] columnTypes) {
                tableName = tableName;
                columnNames = columnNames;
                columnTypes = columnTypes;
            }
        }
        TableInfo tasksTable = new TableInfo("Tasks",
                new String [] {"Name", "Description", "DurationInMins", "Due Date", "Scheduled Start Time", "Scheduled End Time", "Priority"},
                new String [] {"TEXT", "TEXT", "INTEGER", "TEXT", "TEXT", "TEXT", "INTEGER"});

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
