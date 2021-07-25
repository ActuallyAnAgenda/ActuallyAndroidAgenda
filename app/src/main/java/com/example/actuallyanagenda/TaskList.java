package com.example.actuallyanagenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TaskList extends AppCompatActivity {
    Database db;
    EditText name, description, duration, dueDate;
    ListView listView;
    ArrayList<String[]> taskList = db.getTasks();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);
        Button backToMainActivity = findViewById(R.id.backtomainactivity);
        backToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView)findViewById(R.id.tasklist);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int x=0; x<=taskList.size(); x++) {
            HashMap<String,String> cur = new HashMap<>();
            cur.put("name", taskList.get(x)[1]);
            cur.put("duration", taskList.get(x)[3]);
            list.add(cur);
        }
        String[] from={"name"};//string array
        int[] to={R.id.textView, R.id.textView};//int array of views id's
        SimpleAdapter simpleAdapter=new SimpleAdapter(this, list, R.layout.list_view_items,from,to);//Create object and set the parameters for simpleAdapter
        listView.setAdapter(simpleAdapter);//sets the adapter for listView

        //perform listView item click event
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),fruitsNames[i],Toast.LENGTH_LONG).show();//show the selected image in toast according to position
//            }
//        });
    }
}