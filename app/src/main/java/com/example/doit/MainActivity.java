package com.example.doit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String[] projectList = {"Study", "Life", "Work", "Others"};
    HashMap<String, ArrayList<Task>> taskList;
    ArrayList<Task> record = null;
    ArrayList<Task> study = new ArrayList<>();
    ArrayList<Task> life = new ArrayList<>();
    ArrayList<Task> work = new ArrayList<>();
    ArrayList<Task> others = new ArrayList<>();
    Task curTask;
    private ExpandableListView elvProject;
    private TaskAdapter adapter;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);

        loadPreferences();
        for(int i = 0; i < record.size(); i++){
            if(record.get(i).getProject().equals("Study")) study.add(record.get(i));
            else if(record.get(i).getProject().equals("Life")) life.add(record.get(i));
            else if(record.get(i).getProject().equals("Work")) work.add(record.get(i));
            else if(record.get(i).getProject().equals("Others")) others.add(record.get(i));
        }
        taskList= new HashMap<>();
        updateList();
        //通过资源标识获得空间实例
        elvProject = findViewById(R.id.expandableListView);
        //创建适配器
        adapter = new TaskAdapter(this, taskList, projectList);
        //给列表空间设置适配器
        elvProject.setAdapter(adapter);
        elvProject.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(MainActivity.this, "父级条目：" + groupPosition + "     子条目：" + childPosition + "    id:" + id, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        elvProject.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(MainActivity.this, "父级条目：" + groupPosition + "    id:" + id, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
    private void updateList(){
        taskList.put("Study", study);
        taskList.put("Life", life);
        taskList.put("Work", work);
        taskList.put("Others", others);
        savePreferences(taskList);
    }
    public void startEditTask(View v){
        Intent intent = new Intent(MainActivity.this, bottomMenu.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                curTask = (Task) data.getSerializableExtra("curTask");

                curTask.display();

                if(curTask.getProject().equals("Study")) study.add(curTask);
                else if(curTask.getProject().equals("Life")) life.add(curTask);
                else if(curTask.getProject().equals("Work")) work.add(curTask);
                else if(curTask.getProject().equals("Others")) others.add(curTask);
                updateList();
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void savePreferences(HashMap<String, ArrayList<Task>> taskList){
        ArrayList<Task> record = null;
        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        record = new ArrayList<>();
        for(int i = 0; i < study.size(); i++) record.add(study.get(i));
        for(int i = 0; i < life.size(); i++) record.add(life.get(i));
        for(int i = 0; i < work.size(); i++) record.add(work.get(i));
        for(int i = 0; i < others.size(); i++) record.add(others.get(i));
        String json = gson.toJson(record);
        editor.putString("record", json);
        editor.apply();
    }
    private void loadPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("record", "");
        Type type = new TypeToken<ArrayList<Task>>(){}.getType();
        record = gson.fromJson(json, type);
        if(record == null) record = new ArrayList<>();
    }
}