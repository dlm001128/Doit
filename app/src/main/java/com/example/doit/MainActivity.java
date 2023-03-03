package com.example.doit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    EditText etn;
    String name;
    String deadline;
    String project;
    boolean finish;
    boolean hide;
    String index;
    TextView mInfoTextView;
    TextView mInfoDeadline;
    TextView tvDelete;
    Switch fSwitch, hSwitch;
    int year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_;
    String[] day = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private ExpandableListView elvProject;
    private TaskAdapter adapter;
    Button addBtn, confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自定义上方工具栏
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
        //modify task & delete task
        elvProject.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPos = elvProject.getExpandableListPosition(position);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                if(childPosition != -1){
                    Task curTask = taskList.get(projectList[groupPosition]).get(childPosition);
                    name = curTask.getName();
                    deadline = curTask.getDeadline();
                    project = curTask.getProject();
                    finish = curTask.getFinish();
                    hide = curTask.getHide();
                    index = curTask.getIndex();
                    year_ = curTask.getYear();
                    month_ = curTask.getMonth();
                    dayOfMonth_ = curTask.getDayOfMonth();
                    dayOfWeek_ = curTask.getDayOfWeek();
                    hourOfDay_ = curTask.getHour();
                    minute_ = curTask.getMinute();

                    BottomSheetDialog sheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetStyle);
                    View sheetView = LayoutInflater.from(getApplicationContext())
                            .inflate(R.layout.activity_bottom_menu, (LinearLayout)findViewById(R.id.bottom_layout));
                    //Task name
                    etn = (EditText) sheetView.findViewById(R.id.editTaskName);
                    etn.setText(curTask.getName());
                    mInfoDeadline = sheetView.findViewById(R.id.textView_deadline);
                    mInfoDeadline.setText(curTask.getDeadline());
                    mInfoDeadline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar calendar=Calendar.getInstance();
                            new TimePickerDialog( MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    hourOfDay_ = hourOfDay;
                                    minute_ = minute;
                                    String tempMinute = "" + minute_;
                                    if(minute_ >= 0 && minute_ <= 9) tempMinute = "0" + minute_;
                                    deadline = hourOfDay_ + ":" + tempMinute + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                    String text="你选择了"+hourOfDay+"时"+minute+"分";
                                    mInfoDeadline.setText(deadline);
                                    System.out.println(text);
                                    Toast.makeText( MainActivity.this, text, Toast.LENGTH_SHORT ).show();
                                }
                            }
                                    ,calendar.get(Calendar.HOUR_OF_DAY)
                                    ,calendar.get(Calendar.MINUTE),true).show();
                            //Choose date(year,month,day)
                            new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    year_ = year;
                                    month_ = month + 1;
                                    dayOfMonth_ = dayOfMonth;
                                    dayOfWeek_ = calendar.get(Calendar.DAY_OF_WEEK);
                                    deadline = hourOfDay_ + ":" + minute_ + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                    String text = "你选择了：" + year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                                    mInfoDeadline.setText(deadline);
                                    System.out.println(text);
                                    Toast.makeText( MainActivity.this, text, Toast.LENGTH_SHORT ).show();
                                }
                            }
                                    ,calendar.get(Calendar.YEAR)
                                    ,calendar.get(Calendar.MONTH)
                                    ,calendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });

                    //Task project
                    Spinner spinnerProject = sheetView.findViewById(R.id.spinner_project);
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_expandable_list_item_1, projectList);
                    spinnerProject.setAdapter(spinner_adapter);
                    spinnerProject.setSelection(spinner_adapter.getPosition(curTask.getProject()));
                    spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            project= spinnerProject.getItemAtPosition(i).toString();
                            Toast.makeText(MainActivity.this,project,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                    //finish
                    fSwitch = (Switch) sheetView.findViewById(R.id.finish);
                    fSwitch.setChecked(curTask.getFinish());
                    fSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked) finish = true;
                            else finish = false;
                        }
                    });

                    //hide
                    hSwitch = (Switch) sheetView.findViewById(R.id.hide);
                    hSwitch.setChecked(curTask.getHide());
                    hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked) hide = true;
                            else hide = false;
                        }
                    });

                    tvDelete = (TextView) sheetView.findViewById(R.id.textview_delete);
                    tvDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(groupPosition == 0) study.remove(childPosition);
                            else if(groupPosition == 1) life.remove(childPosition);
                            else if(groupPosition == 2) work.remove(childPosition);
                            else if(groupPosition == 3) others.remove(childPosition);
                            updateList();
                            adapter.notifyDataSetChanged();
                            sheetDialog.cancel();
                        }
                    });

                    confirmBtn = sheetView.findViewById(R.id.button_confirm);
                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String prevProject = curTask.getProject();
                            name = etn.getText().toString();
                            curTask.setName(name);
                            curTask.setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                            curTask.setProject(project);
                            curTask.setFinish(finish);
                            curTask.setHide(hide);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
                            String createTime = sdf.format(System.currentTimeMillis());
                            curTask.setIndex(createTime); //用时间戳做index，保证唯一性
                            //modify project
                            if(!prevProject.equals(project)){
                                taskList.get(prevProject).remove(childPosition);
                                if(groupPosition == 0) study.remove(childPosition);
                                else if(groupPosition == 1) life.remove(childPosition);
                                else if(groupPosition == 2) work.remove(childPosition);
                                else if(groupPosition == 3) others.remove(childPosition);

                                if(project.equals("Study")) study.add(curTask);
                                else if(project.equals("Life")) life.add(curTask);
                                else if(project.equals("Work")) work.add(curTask);
                                else if(project.equals("Others")) others.add(curTask);
                            }
                            updateList();
                            curTask.display();
                            adapter.notifyDataSetChanged();
                            sheetDialog.cancel();
                        }
                    });
                    sheetDialog.setContentView(sheetView);
                    sheetDialog.show();
                }
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

        addBtn = (Button) findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog sheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetStyle);
                View sheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.activity_bottom_menu, (LinearLayout)findViewById(R.id.bottom_layout));
                //Task name
                etn = (EditText) sheetView.findViewById(R.id.editTaskName);

                mInfoDeadline = sheetView.findViewById(R.id.textView_deadline);
                mInfoDeadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar=Calendar.getInstance();
                        //Choose exact time(hour, minute)
                        new TimePickerDialog( MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hourOfDay_ = hourOfDay;
                                minute_ = minute;
                                String tempMinute = "" + minute_;
                                if(minute_ >= 0 && minute_ <= 9) tempMinute = "0" + minute_;
                                deadline = hourOfDay_ + ":" + tempMinute + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                String text="你选择了"+hourOfDay+"时"+minute+"分";
                                mInfoDeadline.setText(deadline);
                                System.out.println(text);
                                Toast.makeText( MainActivity.this, text, Toast.LENGTH_SHORT ).show();
                            }
                        }
                        ,calendar.get(Calendar.HOUR_OF_DAY)
                        ,calendar.get(Calendar.MINUTE),true).show();
                        //Choose date(year,month,day)
                        new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                year_ = year;
                                month_ = month + 1;
                                dayOfMonth_ = dayOfMonth;
                                dayOfWeek_ = calendar.get(Calendar.DAY_OF_WEEK);
                                deadline = hourOfDay_ + ":" + minute_ + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                String text = "你选择了：" + year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                                mInfoDeadline.setText(deadline);
                                System.out.println(text);
                                Toast.makeText( MainActivity.this, text, Toast.LENGTH_SHORT ).show();
                            }
                        }
                        ,calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)).show();

                        //Display deadline in TextView
                        mInfoDeadline.setText(deadline);
                    }
                });

                //Task project
                Spinner spinnerProject = sheetView.findViewById(R.id.spinner_project);
                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_expandable_list_item_1, projectList);
                spinnerProject.setAdapter(spinner_adapter);
                spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        project= spinnerProject.getItemAtPosition(i).toString();
                        Toast.makeText(MainActivity.this,project,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                //finish
                fSwitch = (Switch) sheetView.findViewById(R.id.finish);
                fSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) finish = true;
                        else finish = false;
                    }
                });

                //hide
                hSwitch = (Switch) sheetView.findViewById(R.id.hide);
                hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) hide = true;
                        else hide = false;
                    }
                });

                confirmBtn = sheetView.findViewById(R.id.button_confirm);
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Task curTask = new Task();
                        name = etn.getText().toString();
                        curTask.setName(name);
                        curTask.setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                        curTask.setProject(project);
                        curTask.setFinish(finish);
                        curTask.setHide(hide);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
                        String createTime = sdf.format(System.currentTimeMillis());
                        curTask.setIndex(createTime); //用时间戳做index，保证唯一性
                        curTask.display();

                        if(curTask.getProject().equals("Study")) study.add(curTask);
                        else if(curTask.getProject().equals("Life")) life.add(curTask);
                        else if(curTask.getProject().equals("Work")) work.add(curTask);
                        else if(curTask.getProject().equals("Others")) others.add(curTask);
                        updateList();
                        adapter.notifyDataSetChanged();
                        sheetDialog.cancel();
                    }
                });

                sheetDialog.setContentView(sheetView);
                sheetDialog.show();
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