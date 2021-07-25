package com.example.actuallyanagenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Database(this);
        Button createTask = findViewById(R.id.button);
        Button showTasks = findViewById(R.id.button2);
    }

    public void showTasks (View view) {
        ArrayList<String[]> output = db.getTasks();
        for (int x=0; x<output.size(); x++) {
            for (int y=0; y<output.get(x).length; y++) {
                System.out.print(output.get(0)[y]+": "+output.get(x)[y]+" ");
            }
            System.out.println();
        }
    }

    public void deleteTasksTable(View view) {
        db.deleteTasksTable();
    }
    public void switchToCreateTask(View view) {
        Intent switchScreens = new Intent(this, CreateTask.class); //"Intent" passes important stuff to another screen (called "Activity"), like the create task screen
        startActivity(switchScreens);
    }
}