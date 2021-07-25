package com.example.actuallyanagenda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ListView;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = findViewById(R.id.calendarView);
        ListView listView = findViewById(R.id.list);
//        listView.
    }

    /**
     * Adds an entry to the calendar using
     * @param entry
     */
//    protected void addEntry(CalendarEntry entry) {
//
//    }
}