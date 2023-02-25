package com.example.doit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class bottomMenu extends AppCompatActivity{
    //ArrayList<Task>[][] taskList = new ArrayList[4][100];
    ArrayList<List<Task>> taskList = new ArrayList<List<Task>>();
    List<Task> study = new ArrayList<>();
    List<Task> life = new ArrayList<>();
    List<Task> work = new ArrayList<>();
    List<Task> others = new ArrayList<>();
    EditText etn;
    String name;
    String deadline;
    String[] projectList = {"Study", "Life", "Work", "Others"};
    String project;
    boolean finish;
    boolean hide;
    TextView mInfoTextView;
    Switch fSwitch, hSwitch;
    int count = 0;
    int year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_;
    String[] day = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_menu);
        //Task name
        etn = (EditText) findViewById(R.id.editTaskName);

        //Task project
        Spinner spinnerProject = findViewById(R.id.spinner_project);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, projectList);
        spinnerProject.setAdapter(adapter);
        spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                project= spinnerProject.getItemAtPosition(i).toString();
                Toast.makeText(bottomMenu.this,project,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //finish
        fSwitch = (Switch) findViewById(R.id.finish);
        fSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) finish = true;
                else finish = false;
            }
        });

        //hide
        hSwitch = (Switch) findViewById(R.id.hide);
        hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) hide = true;
                else hide = false;
            }
        });
    }
    //Deadline
    public void onclickDate(View v){
        Calendar calendar=Calendar.getInstance();
        hourOfDay_ = calendar.get(Calendar.HOUR_OF_DAY);
        minute_ = calendar.get(Calendar.MINUTE);
        year_ = calendar.get(Calendar.YEAR);
        month_ = calendar.get(Calendar.MONTH);
        dayOfMonth_ = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfWeek_ = calendar.get(Calendar.DAY_OF_WEEK);
        //Choose exact time(hour, minute)
        new TimePickerDialog( this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String text="你选择了"+hourOfDay+"时"+minute+"分";
                Toast.makeText( bottomMenu.this, text, Toast.LENGTH_SHORT ).show();
            }
        }
        ,calendar.get(Calendar.HOUR_OF_DAY)
        ,calendar.get(Calendar.MINUTE),true).show();
        //Choose date(year,month,day)
        new DatePickerDialog( this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String text = "你选择了：" + year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                Toast.makeText( bottomMenu.this, text, Toast.LENGTH_SHORT ).show();
            }
        }
        ,calendar.get(Calendar.YEAR)
        ,calendar.get(Calendar.MONTH)
        ,calendar.get(Calendar.DAY_OF_MONTH)).show();

        //Display deadline in TextView
        mInfoTextView = (TextView) findViewById(R.id.textView_deadline);
        deadline = hourOfDay_ + ":" + minute_ + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
        mInfoTextView.setText(deadline);
    }

    //Add button
    public void onClickAdd(View v){
        //loadPreferences();
        Task curTask = new Task();
        name = etn.getText().toString();
        curTask.setName(name);
        curTask.setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
        curTask.setProject(project);
        curTask.setFinish(finish);
        curTask.setHide(hide);
        curTask.setIndex(count);
        count++;

        if(curTask.equals("Study")) study.add(curTask);
        else if(curTask.equals("Life")) life.add(curTask);
        else if(curTask.equals("Work")) work.add(curTask);
        else if(curTask.equals("Others")) others.add(curTask);
        //curTask.display();

        //savePreferences();
        Intent data = new Intent();
        data.putExtra("curTask", curTask);
        setResult(MainActivity.RESULT_OK, data);
        finish();

        System.out.println("taskList over");
    }
    private void savePreferences(){
        SharedPreferences tPrefs = getSharedPreferences("shared preference", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = tPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        prefsEditor.putString("Task list", json);
        prefsEditor.apply();
    }
    private void loadPreferences(){
        SharedPreferences tPrefs = getSharedPreferences("shared preference", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = tPrefs.getString("Task list", "null");
        Type type = new TypeToken<ArrayList<Task>>(){}.getType();
        taskList = gson.fromJson(json, type);

        if(taskList == null){
            taskList = new ArrayList<>();
        }
    }
}