package com.example.actuallyanagenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateTask extends AppCompatActivity {
    Database db;
    EditText name, description, duration, dueDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task);
        db = new Database(this);
        Button createTask = findViewById(R.id.addtask);
        Button backToMainActivity = findViewById(R.id.backtomainactivity);
        backToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name = (EditText)findViewById(R.id.name);
        description = (EditText)findViewById(R.id.description);
        duration = (EditText)findViewById(R.id.duration);
        dueDate = (EditText)findViewById(R.id.duedate);
        TextWatcher tw = new TextWatcher() { //credit to https://stackoverflow.com/questions/16889502/how-to-mask-an-edittext-to-show-the-dd-mm-yyyy-date-format/16889503#16889503
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    dueDate.setText(current);
                    dueDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            public void beforeTextChanged (CharSequence s,int start, int count, int after){}
            @Override
            public void afterTextChanged (Editable s){}
        };
        dueDate.addTextChangedListener(tw);
    }
    public void addTask (View view) {
        String nameEntry = name.getText().toString();
        String descriptionEntry = description.getText().toString();
        String durationEntry = duration.getText().toString();
        String dueDateEntry = dueDate.getText().toString();
        db.insertTask(nameEntry, descriptionEntry, durationEntry, dueDateEntry);
        finish();
    }
}