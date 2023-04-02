package com.ee5415.doit;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private SortMode state = SortMode.DEADLINE;
    final String[] projectList = {"Study", "Life", "Work", "Others"};
    ArrayList<DateKey> dateList;
    HashMap<String, ArrayList<Task>> taskList;
    HashMap<DateKey, ArrayList<Task>> taskList_w_date;
    HashMap<DateKey, ArrayList<Task>> taskList_w_unfinish_date;
    HashMap<DateKey, ArrayList<Task>> taskList_w_unhide_date;
    HashMap<DateKey, ArrayList<Task>> taskList_w_date_record;
    ArrayList<Task> record = null;
    ArrayList<Task> study = new ArrayList<>(); //store study task
    ArrayList<Task> life = new ArrayList<>(); //store life task
    ArrayList<Task> work = new ArrayList<>(); //store work task
    ArrayList<Task> others = new ArrayList<>(); //store others task

    // 存储勾选了show_hidden后的新数据
    ArrayList<Task> study_new = new ArrayList<>(); //store study task
    ArrayList<Task> life_new = new ArrayList<>(); //store life task
    ArrayList<Task> work_new = new ArrayList<>(); //store work task
    ArrayList<Task> others_new = new ArrayList<>(); //store others task

    ArrayList<Task> study_unfinish = new ArrayList<>(); //store study task
    ArrayList<Task> life_unfinish = new ArrayList<>(); //store life task
    ArrayList<Task> work_unfinish = new ArrayList<>(); //store work task
    ArrayList<Task> others_unfinish = new ArrayList<>(); //store others task
    MenuItem item;

    EditText etn; //用于填写任务名的EditText
    String name; //task name
    String deadline; //task deadline
    String project; //task project
    boolean finish; //if current task is finished
    boolean hide; //if current task is hidden

    // show_hidden & unshow_hidden
    boolean show_hidden_checked = true; // if show_hidden checkbox is selected
    boolean show_finished_checked = true; //if show_finished checkbox is selected

    String index;
    TextView mInfoDeadline;
    TextView tvDelete; //textview_delete
    Switch fSwitch, hSwitch; //fSwitch-finish, hSwitch-hide
    int year_, month_, dayOfMonth_, hourOfDay_, minute_;
    int dayOfWeek_;
    String[] day = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private ExpandableListView elvProject;
    private TaskAdapter adapter;
    private TaskDateAdapter adapter_w_date;
    Button addBtn, confirmBtn; //addBtn-add task, confirmBtn-confirm
    private String CHANNEL_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自定义上方工具栏
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);

//        CHANNEL_ID = getString(R.string.channel_id);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "NotificationCode", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            assert manager != null;
//            manager.createNotificationChannel(channel);
//        }

        //加载之前的信息
        loadPreferences();
        for (int i = 0; i < record.size(); i++) {
            if (record.get(i).getProject().equals("Study"))
                study.add(record.get(i));
            else if (record.get(i).getProject().equals("Life"))
                life.add(record.get(i));
            else if (record.get(i).getProject().equals("Work"))
                work.add(record.get(i));
            else if (record.get(i).getProject().equals("Others"))
                others.add(record.get(i));
        }

        // Initialize the parameter
        taskList = new HashMap<>();
        taskList_w_date = new HashMap<>();
        taskList_w_date_record = new HashMap<>();
        taskList_w_unfinish_date = new HashMap<>();
        taskList_w_unhide_date = new HashMap<>();
        dateList = new ArrayList<>();

        // Insert record to task list
        for (Task t : record) {
            addTask(new Task(t));
        }
        taskList_w_date_record.putAll(taskList_w_date);

        updateList(); //更新taskList方便TaskAdapter展示task情况

        //通过资源标识获得空间实例
        elvProject = findViewById(R.id.expandableListView);
        //创建适配器
        adapter = new TaskAdapter(this, taskList, projectList);
        adapter_w_date = new TaskDateAdapter(this, taskList_w_date_record, dateList);
        //给列表空间设置适配器
        if (state == SortMode.PROJECT)
            elvProject.setAdapter(adapter);
        else if (state == SortMode.DEADLINE)
            elvProject.setAdapter(adapter_w_date);

        //点击子条目
        elvProject.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //尝试链接finish和check
//                CheckBox checkBox = null;
//                if(state == SortMode.DEADLINE ){
//                    checkBox = (CheckBox) v.findViewById(R.id.checkbox);
//                }else if(state == SortMode.PROJECT ){
//
////                    if (project.equals("Study")) checkBox = (CheckBox) v.findViewById(R.id.study_checkbox);
////                    else if (project.equals("Life")) checkBox = (CheckBox) v.findViewById(R.id.life_checkbox);
////                    else if (project.equals("Work")) checkBox = (CheckBox) v.findViewById(R.id.work_checkbox);
////                    else if (project.equals("Others")) checkBox = (CheckBox) v.findViewById(R.id.others_checkbox);
//
//
//                }
//                if(checkBox != null){
//                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                            finish = isChecked;
//                        }
//                    });
//                }
                //Toast.makeText(MainActivity.this, "  子条目：" + childPosition + "  id:" + id + " finish" + finish, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //modify task & delete task
        //长按子条目修改任务信息
        elvProject.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPos = elvProject.getExpandableListPosition(position);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                //如果选择的是父条目childPosition为-1，长按父条目的时候我们并不需要修改什么
                //保证是长按子条目时才对任务信息进行修改
                if (childPosition != -1) {
                    Task curTask = new Task();
                    if (state == SortMode.PROJECT)
                        curTask = taskList.get(projectList[groupPosition]).get(childPosition);
                    else if (state == SortMode.DEADLINE)
                        curTask = taskList_w_date.get(dateList.get(groupPosition)).get(childPosition);
                    //先得到当前任务的信息并展示出来
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
                            .inflate(R.layout.activity_bottom_menu, (LinearLayout) findViewById(R.id.bottom_layout));
                    //Task name
                    etn = (EditText) sheetView.findViewById(R.id.editTaskName);
                    etn.setText(curTask.getName());
                    mInfoDeadline = sheetView.findViewById(R.id.textView_deadline);
                    mInfoDeadline.setText(curTask.getDeadline());
                    mInfoDeadline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar calendar = Calendar.getInstance();
                            new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    //在这里更新用户选择的时间，否则数据不是最新的
                                    hourOfDay_ = hourOfDay;
                                    minute_ = minute;
                                    String tempMinute = "" + minute_;
                                    if (minute_ >= 0 && minute_ <= 9) tempMinute = "0" + minute_;
                                    deadline = hourOfDay_ + ":" + tempMinute + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                    mInfoDeadline.setText(deadline);
                                }
                            }
                                    , calendar.get(Calendar.HOUR_OF_DAY)
                                    , calendar.get(Calendar.MINUTE), true).show();
                            //Choose date(year,month,day)
                            new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    year_ = year;
                                    month_ = month + 1;
                                    dayOfMonth_ = dayOfMonth;
                                    SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                                    Date date = new Date(year, month, dayOfMonth-1);
                                    String dayOfWeek = simpledateformat.format(date);
                                    System.out.println(dayOfWeek);
                                    if(dayOfWeek.equals("Monday")) dayOfWeek_ = 1;
                                    else if(dayOfWeek.equals("Tuesday")) dayOfWeek_ = 2;
                                    else if(dayOfWeek.equals("Wednesday")) dayOfWeek_ = 3;
                                    else if(dayOfWeek.equals("Thursday")) dayOfWeek_ = 4;
                                    else if(dayOfWeek.equals("Friday")) dayOfWeek_ = 5;
                                    else if(dayOfWeek.equals("Saturday")) dayOfWeek_ = 6;
                                    else if(dayOfWeek.equals("Sunday")) dayOfWeek_ = 0;
                                    deadline = hourOfDay_ + ":" + minute_ + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                    mInfoDeadline.setText(deadline);
                                }
                            }
                                    , calendar.get(Calendar.YEAR)
                                    , calendar.get(Calendar.MONTH)
                                    , calendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });

                    //Task project
                    Spinner spinnerProject = sheetView.findViewById(R.id.spinner_project);
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.dropdown_item, projectList){
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View view;
                            view = View.inflate(MainActivity.this, R.layout.dropdown_item, null);
                            TextView textView = (TextView) view.findViewById(R.id.tv_dropdown);
                            textView.setText(projectList[position]);
                            switch (position) {
                                case 0:
                                    textView.setTextColor(getResources().getColor(R.color.purple_200));
                                    break;
                                case 1:
                                    textView.setTextColor(getResources().getColor(R.color.green));
                                    break;
                                case 2:
                                    textView.setTextColor(getResources().getColor(R.color.blue));
                                    break;
                                case 3:
                                    textView.setTextColor(getResources().getColor(R.color.grey));
                                    break;
                            }

                            return view;
                        }
                    };
                    spinnerProject.setAdapter(spinner_adapter);
                    spinnerProject.setSelection(spinner_adapter.getPosition(curTask.getProject()));
                    spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            switch (i) {
                                case 0:
                                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.purple_200));
                                    break;
                                case 1:
                                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.green));
                                    break;
                                case 2:
                                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.blue));
                                    break;
                                case 3:
                                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.grey));
                                    break;
                            }
                            project = spinnerProject.getItemAtPosition(i).toString();
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
                            finish = isChecked;
                        }
                    });

                    //hide
                    hSwitch = (Switch) sheetView.findViewById(R.id.hide);
                    hSwitch.setChecked(curTask.getHide());
                    hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            hide = isChecked;
                        }
                    });

                    //delete task
                    tvDelete = (TextView) sheetView.findViewById(R.id.textview_delete);
                    tvDelete.setOnClickListener(new View.OnClickListener() {
                        Task removeTask_ = new Task();

                        @Override
                        public void onClick(View v) {
                            if (state == SortMode.PROJECT) {
                                if (groupPosition == 0) {
                                    removeTask_ = study.get(childPosition);
                                    study.remove(childPosition);
                                } else if (groupPosition == 1) {
                                    removeTask_ = life.get(childPosition);
                                    life.remove(childPosition);
                                } else if (groupPosition == 2) {
                                    removeTask_ = work.get(childPosition);
                                    work.remove(childPosition);
                                } else if (groupPosition == 3) {
                                    removeTask_ = others.get(childPosition);
                                    others.remove(childPosition);
                                }
                                //updateList();
                                item_hide_isChecked_state(show_hidden_checked);
                                item_finish_isChecked_state(show_finished_checked);
                            } else if (state == SortMode.DEADLINE) {
                                removeTask_ = taskList_w_date.get(dateList.get(groupPosition)).get(childPosition);
//                                deleteTaskByIndex(groupPosition, childPosition);
                            }

                            // Delete the task for other task list
                            if (state == SortMode.PROJECT) {
//                                DateKey key = new DateKey(removeTask_.getYear(), removeTask_.getMonth(), removeTask_.getDayOfMonth(),
//                                        removeTask_.getDayOfWeek(), removeTask_.getHour(), removeTask_.getMinute());
//                                deleteTaskByKey(removeTask_, key);
                            } else if (state == SortMode.DEADLINE) {
                                if (removeTask_.getProject().equals(projectList[0])) {
                                    study.remove(removeTask_);
                                } else if (removeTask_.getProject().equals(projectList[1])) {
                                    life.remove(removeTask_);
                                } else if (removeTask_.getProject().equals(projectList[2])) {
                                    work.remove(removeTask_);
                                } else if (removeTask_.getProject().equals(projectList[3])) {
                                    others.remove(removeTask_);
                                }
                                //updateList();
                                item_hide_isChecked_state(show_hidden_checked);
                                item_finish_isChecked_state(show_finished_checked);
                            }
                            updateTaskListDate();
                            adapter.notifyDataSetChanged();
                            adapter_w_date.notifyDataSetChanged();
                            sheetDialog.cancel();
                        }
                    });

                    //modify task
                    confirmBtn = sheetView.findViewById(R.id.button_confirm);
                    Task curTask_ = curTask;
                    confirmBtn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Task removeTask_ = new Task(curTask_);
                            String prevProject = curTask_.getProject();
                            name = etn.getText().toString();
                            if(name.equals("") || deadline.equals("")) {
                                return;
                            }
                            curTask_.setName(name);
                            curTask_.setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                            curTask_.setProject(project);
                            curTask_.setFinish(finish);
                            curTask_.setHide(hide);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
                            String createTime = sdf.format(System.currentTimeMillis());
                            curTask_.setIndex(createTime); //用时间戳做index，保证唯一
                            //modify project
                            if (state == SortMode.PROJECT) {
                                if (!prevProject.equals(project)) {
                                    if (prevProject.equals("Study")) study.remove(childPosition);
                                    else if (prevProject.equals("Life")) life.remove(childPosition);
                                    else if (prevProject.equals("Work")) work.remove(childPosition);
                                    else if (prevProject.equals("Others")) others.remove(childPosition);

                                    if (project.equals("Study")) study.add(curTask_);
                                    else if (project.equals("Life")) life.add(curTask_);
                                    else if (project.equals("Work")) work.add(curTask_);
                                    else if (project.equals("Others")) others.add(curTask_);
                                } else {
                                    if (project.equals("Study")) {
                                        study.get(childPosition).setName(name);
                                        study.get(childPosition).setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                                        study.get(childPosition).setFinish(finish);
                                        study.get(childPosition).setHide(hide);
                                    } else if(prevProject.equals("Life")) {
                                        life.get(childPosition).setName(name);
                                        life.get(childPosition).setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                                        life.get(childPosition).setFinish(finish);
                                        life.get(childPosition).setHide(hide);
                                    } else if(prevProject.equals("Work")) {
                                        work.get(childPosition).setName(name);
                                        work.get(childPosition).setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                                        work.get(childPosition).setFinish(finish);
                                        work.get(childPosition).setHide(hide);
                                    } else if(prevProject.equals("Others")) {
                                        others.get(childPosition).setName(name);
                                        others.get(childPosition).setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                                        others.get(childPosition).setFinish(finish);
                                        others.get(childPosition).setHide(hide);
                                    }
                                }
                            } else if (state == SortMode.DEADLINE) {
//                                deleteTaskByIndex(groupPosition, childPosition);
//                                addTask(new Task(curTask_));
                            }


                            if (state == SortMode.PROJECT) {
//                                DateKey key = new DateKey(removeTask_.getYear(), removeTask_.getMonth(), removeTask_.getDayOfMonth(),
//                                        removeTask_.getDayOfWeek(), removeTask_.getHour(), removeTask_.getMinute());
//                                Log.i("modify", key.toString());
//                                deleteTaskByKey(removeTask_, key);
//                                addTask(curTask_);
                            } else if (state == SortMode.DEADLINE) {
                                if (!prevProject.equals(project)) {
                                    if (prevProject.equals("Study")) study.remove(removeTask_);
                                    else if (prevProject.equals("Life")) life.remove(removeTask_);
                                    else if (prevProject.equals("Work")) work.remove(removeTask_);
                                    else if (prevProject.equals("Others")) others.remove(removeTask_);

                                    if (project.equals("Study")) study.add(curTask_);
                                    else if (project.equals("Life")) life.add(curTask_);
                                    else if (project.equals("Work")) work.add(curTask_);
                                    else if (project.equals("Others")) others.add(curTask_);
                                } else {
                                    if (project.equals("Study")) {
                                        study.remove(removeTask_);
                                        study.add(curTask_);
                                    } else if(prevProject.equals("Life")) {
                                        life.remove(removeTask_);
                                        life.add(curTask_);
                                    } else if(prevProject.equals("Work")) {
                                        work.remove(removeTask_);
                                        work.add(curTask_);
                                    } else if(prevProject.equals("Others")) {
                                        others.remove(removeTask_);
                                        others.add(curTask_);
                                    }
                                }
                            }
                            updateTaskListDate();
                            item_hide_isChecked_state(show_hidden_checked);
                            item_finish_isChecked_state(show_finished_checked);
                            curTask_.display();
                            adapter.notifyDataSetChanged();
                            adapter_w_date.notifyDataSetChanged();
                            sheetDialog.cancel();
                        }
                    });
                    sheetDialog.setContentView(sheetView);
                    sheetDialog.show();
                }
                return false;
            }
        });

        //点击父条目
        elvProject.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //Toast.makeText(MainActivity.this, "父级条目：" + groupPosition + "  id:" + id, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //点击add task准备添加任务
        addBtn = (Button) findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = "";
                deadline = "";
                project = "";
                finish = false;
                hide = false;
                //底部菜单
                BottomSheetDialog sheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetStyle);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_bottom_menu, (LinearLayout) findViewById(R.id.bottom_layout));
                //Task name
                etn = (EditText) sheetView.findViewById(R.id.editTaskName);

                mInfoDeadline = sheetView.findViewById(R.id.textView_deadline);
                mInfoDeadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        //Choose exact time(hour, minute)
                        //用户选择具体时间（时，分）
                        new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hourOfDay_ = hourOfDay;
                                minute_ = minute;
                                String tempMinute = "" + minute_;
                                if (minute_ >= 0 && minute_ <= 9) tempMinute = "0" + minute_;
                                deadline = hourOfDay_ + ":" + tempMinute + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                mInfoDeadline.setText(deadline);
                            }
                        }
                                , calendar.get(Calendar.HOUR_OF_DAY)
                                , calendar.get(Calendar.MINUTE), true).show();
                        //Choose date(year,month,day)
                        //用户选择日期（年，月，日）
                        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                year_ = year;
                                month_ = month + 1;
                                dayOfMonth_ = dayOfMonth;
                                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                                Date date = new Date(year, month, dayOfMonth-1);
                                String dayOfWeek = simpledateformat.format(date);
                                System.out.println(dayOfWeek);
                                if(dayOfWeek.equals("Monday")) dayOfWeek_ = 1;
                                else if(dayOfWeek.equals("Tuesday")) dayOfWeek_ = 2;
                                else if(dayOfWeek.equals("Wednesday")) dayOfWeek_ = 3;
                                else if(dayOfWeek.equals("Thursday")) dayOfWeek_ = 4;
                                else if(dayOfWeek.equals("Friday")) dayOfWeek_ = 5;
                                else if(dayOfWeek.equals("Saturday")) dayOfWeek_ = 6;
                                else if(dayOfWeek.equals("Sunday")) dayOfWeek_ = 0;
                                deadline = hourOfDay_ + ":" + minute_ + "   " + dayOfMonth_ + "/" + month_ + "/" + year_;
                                mInfoDeadline.setText(deadline);
                            }
                        }
                                , calendar.get(Calendar.YEAR)
                                , calendar.get(Calendar.MONTH)
                                , calendar.get(Calendar.DAY_OF_MONTH)).show();

                        //Display deadline in TextView
                        mInfoDeadline.setText(deadline);
                    }
                });

                //project
                //用户选择任务所属项目并更新
                Spinner spinnerProject = sheetView.findViewById(R.id.spinner_project);
                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.dropdown_item, projectList){
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view;
                        view = View.inflate(MainActivity.this, R.layout.dropdown_item, null);
                        TextView textView = (TextView) view.findViewById(R.id.tv_dropdown);
                        textView.setText(projectList[position]);
                        switch (position) {
                            case 0:
                                textView.setTextColor(getResources().getColor(R.color.purple_200));
                                break;
                            case 1:
                                textView.setTextColor(getResources().getColor(R.color.green));
                                break;
                            case 2:
                                textView.setTextColor(getResources().getColor(R.color.blue));
                                break;
                            case 3:
                                textView.setTextColor(getResources().getColor(R.color.grey));
                                break;
                        }

                        return view;
                    }
                };

                spinnerProject.setAdapter(spinner_adapter);
                spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.purple_200));
                                break;
                            case 1:
                                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.green));
                                break;
                            case 2:
                                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.blue));
                                break;
                            case 3:
                                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.grey));
                                break;
                        }
                        project = spinnerProject.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }

                });

                //finish
                //检查用户选择了finish还是don't finish并更新
                fSwitch = (Switch) sheetView.findViewById(R.id.finish);
                fSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) finish = true;
                        else finish = false;
                    }
                });

                //hide
                //检查用户选择了hide还是don't hide并更新
                hSwitch = (Switch) sheetView.findViewById(R.id.hide);
                hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) hide = true;
                        else hide = false;
                    }
                });
                //信息填写完毕最后确认
                confirmBtn = sheetView.findViewById(R.id.button_confirm);
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //将填写的信息都写入curTask
                        Task curTask = new Task();
                        name = etn.getText().toString();
                        if(name.equals("") || deadline.equals("")) {
                            return;
                        }
                        curTask.setName(name);
                        curTask.setDeadLine(year_, month_, dayOfMonth_, dayOfWeek_, hourOfDay_, minute_, deadline);
                        curTask.setProject(project);
                        curTask.setFinish(finish);
                        curTask.setHide(hide);
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
                        String createTime = sdf.format(System.currentTimeMillis());
                        curTask.setIndex(createTime); //用时间戳做index，保证唯一性
                        curTask.display();
                        //将curTask加入到属于它的任务队列中
                        if (curTask.getProject().equals("Study")) study.add(curTask);
                        else if (curTask.getProject().equals("Life")) life.add(curTask);
                        else if (curTask.getProject().equals("Work")) work.add(curTask);
                        else if (curTask.getProject().equals("Others")) others.add(curTask);
//                        addTask(new Task(curTask));
                        updateTaskListDate();
                        //updateList(); //将所有任务队列加入到任务数组中
                        item_hide_isChecked_state(show_hidden_checked);
                        item_finish_isChecked_state(show_finished_checked);
                        // adapter.notifyDataSetChanged(); //提醒adapter重新展示任务情况
                        // adapter_w_date.notifyDataSetChanged();
                        sheetDialog.cancel(); //让底部菜单自己落下
                    }
                });
                sheetDialog.setContentView(sheetView);
                sheetDialog.show();
            }
        });
    }

    private void updateList() {
        taskList.put("Study", study);
        taskList.put("Life", life);
        taskList.put("Work", work);
        taskList.put("Others", others);
        savePreferences(taskList);
    }

    private void updateList_show_hidden_checked() {
        taskList.put("Study", study);
        taskList.put("Life", life);
        taskList.put("Work", work);
        taskList.put("Others", others);
        savePreferences(taskList);

        taskList_w_date_record.putAll(taskList_w_date);
        adapter.notifyDataSetChanged(); //提醒adapter重新展示任务情况
        adapter_w_date.notifyDataSetChanged();
    }

    private void updateList_show_finished_checked() {
        taskList.put("Study", study);
        taskList.put("Life", life);
        taskList.put("Work", work);
        taskList.put("Others", others);
        savePreferences(taskList);

        taskList_w_date_record.putAll(taskList_w_date);

        adapter.notifyDataSetChanged(); //提醒adapter重新展示任务情况
        adapter_w_date.notifyDataSetChanged();
    }

    private void updateList_show_hidden_unchecked() {
        study_new.clear();
        life_new.clear();
        work_new.clear();
        others_new.clear();
        taskList_w_unhide_date.clear();
        // study
        for (int i = 0; i < study.size(); i++) {
            if (study.get(i).getHide() == true) {
                // 若这个item是隐藏了的，则不显示隐藏了的项目
            } else {
                // 若这个item是未隐藏的，则需要对其进行展示，将其添加到新的study列表当中
                study_new.add(study.get(i));
            }
        }

        // life
        for (int i = 0; i < life.size(); i++) {
            if (life.get(i).getHide() == true) {
                // 若这个item是隐藏了的，则不显示隐藏了的项目
            } else {
                // 若这个item是未隐藏的，则需要对其进行展示，将其添加到新的life列表当中
                life_new.add(life.get(i));
            }
        }

        // work
        for (int i = 0; i < work.size(); i++) {
            if (work.get(i).getHide() == true) {
                // 若这个item是隐藏了的，则不显示隐藏了的项目
            } else {
                // 若这个item是未隐藏的，则需要对其进行展示，将其添加到新的work列表当中
                work_new.add(work.get(i));
            }
        }

        // others
        for (int i = 0; i < others.size(); i++) {
            if (others.get(i).getHide() == true) {
                // 若这个item是隐藏了的，则不显示隐藏了的项目
            } else {
                // 若这个item是未隐藏的，则需要对其进行展示，将其添加到新的others列表当中
                others_new.add(others.get(i));
            }
        }

        //deadline
        taskList_w_date.forEach((key, value) -> {
            ArrayList<Task> temp = new ArrayList<>();
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).getHide() == true) {
                    // 若这个item是完成了的，则不显示完成了的项目
                } else {
                    // 若这个item是未完成的，则需要对其进行展示，将其添加到新的others列表当中
                    temp.add(value.get(i));
                }
            }
            taskList_w_unhide_date.put(key, temp);
        });


        // update
        taskList.clear();
        taskList.put("Study", study_new);
        taskList.put("Life", life_new);
        taskList.put("Work", work_new);
        taskList.put("Others", others_new);
        savePreferences(taskList);

        taskList_w_date_record.clear();
        taskList_w_date_record.putAll(taskList_w_unhide_date);

        adapter.notifyDataSetChanged(); //提醒adapter重新展示任务情况
        adapter_w_date.notifyDataSetChanged();

    }

    private void updateList_show_finished_unchecked() {
        study_unfinish.clear();
        life_unfinish.clear();
        work_unfinish.clear();
        others_unfinish.clear();
        taskList_w_unfinish_date.clear();


        // study
        for (int i = 0; i < study.size(); i++) {
            if (study.get(i).getFinish() == true) {
                // 若这个item是完成了的，则不显示完成了的项目
            } else {
                // 若这个item是未完成的，则需要对其进行展示，将其添加到新的study列表当中
                study_unfinish.add(study.get(i));
            }
        }

        // life
        for (int i = 0; i < life.size(); i++) {
            if (life.get(i).getFinish() == true) {
                // 若这个item是完成了的，则不显示完成了的项目
            } else {
                // 若这个item是未完成的，则需要对其进行展示，将其添加到新的life列表当中
                life_unfinish.add(life.get(i));
            }
        }

        // work
        for (int i = 0; i < work.size(); i++) {
            if (work.get(i).getFinish() == true) {
                // 若这个item是完成了的，则不显示完成了的项目
            } else {
                // 若这个item是未完成的，则需要对其进行展示，将其添加到新的work列表当中
                work_unfinish.add(work.get(i));
            }
        }

        // others
        for (int i = 0; i < others.size(); i++) {
            if (others.get(i).getFinish() == true) {
                // 若这个item是完成了的，则不显示完成了的项目
            } else {
                // 若这个item是未完成的，则需要对其进行展示，将其添加到新的others列表当中
                others_unfinish.add(others.get(i));
            }
        }

        //deadline
        taskList_w_date.forEach((key, value) -> {
            ArrayList<Task> temp = new ArrayList<>();
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).getFinish() == true) {
                    // 若这个item是完成了的，则不显示完成了的项目
                } else {
                    // 若这个item是未完成的，则需要对其进行展示，将其添加到新的others列表当中
                    temp.add(value.get(i));
                }
            }
            taskList_w_unfinish_date.put(key, temp);
        });

        // update
        taskList.clear();
        taskList.put("Study", study_unfinish);
        taskList.put("Life", life_unfinish);
        taskList.put("Work", work_unfinish);
        taskList.put("Others", others_unfinish);
        savePreferences(taskList);

        taskList_w_date_record.clear();

        taskList_w_date_record.putAll(taskList_w_unfinish_date);

        adapter.notifyDataSetChanged(); //提醒adapter重新展示任务情况
        adapter_w_date.notifyDataSetChanged();

    }

    public void item_hide_isChecked_state(boolean flag) {
        if (flag) {
            // 显示隐藏了的项目
            updateList_show_hidden_checked();
        } else {
            // 不显示隐藏了的项目
            updateList_show_hidden_unchecked();
        }
    }

    public void item_finish_isChecked_state(boolean flag) {
        if (flag) {
            // 显示完成了的项目
            updateList_show_finished_checked();
        } else {
            // 不显示完成了的项目
            updateList_show_finished_unchecked();
        }
    }

    private void addTask(@NonNull Task task) {
        Log.i("DEBUG", "addTask");
        DateKey key = new DateKey(task.getYear(), task.getMonth(), task.getDayOfMonth(),
                task.getDayOfWeek(), 0, 0);
        Log.i("DEBUG", key.toString());
        if (!dateList.contains(key)) {
            dateList.add(key);
            Collections.sort(dateList);
            ArrayList<Task> newTaskList = new ArrayList<>();
            newTaskList.add(new Task(task));
            taskList_w_date.put(key, newTaskList);
        } else {
            taskList_w_date.get(key).add(new Task(task));
        }
        Collections.sort(taskList_w_date.get(key));
    }

    private void deleteTaskByIndex(int groupPosition, int childPosition) {
        Log.i("DEBUG", "deleteTaskByIndex");
        taskList_w_date.get(dateList.get(groupPosition)).remove(childPosition);
        if (taskList_w_date.get(dateList.get(groupPosition)).size() == 0) {
            taskList_w_date.remove(dateList.get(groupPosition));
            dateList.remove(groupPosition);
        }
    }

    private void deleteTaskByKey(@NonNull Task task, @NonNull DateKey key) {
        Log.i("deleteTaskByKey", "Start deleteTaskByKey");
        Log.i("deleteTaskByKey", key.toString());
        if (dateList.contains(key)) {
            task.display();
            taskList_w_date.get(key).get(0).display();
            boolean removed = taskList_w_date.get(key).remove(task);
            if (taskList_w_date.get(key).size() == 0) {
                taskList_w_date.remove(key);
                dateList.remove(key);
            }
        }
    }

    private void updateTaskListDate() {
        ArrayList<Task> record_ = new ArrayList<>();
        for (int i = 0; i < study.size(); i++) record_.add(study.get(i));
        for (int i = 0; i < life.size(); i++) record_.add(life.get(i));
        for (int i = 0; i < work.size(); i++) record_.add(work.get(i));
        for (int i = 0; i < others.size(); i++) record_.add(others.get(i));

        dateList.clear();
        taskList_w_date.clear();

        for (Task t : record_) {
            addTask(new Task(t));
        }
    }

    private void savePreferences(HashMap<String, ArrayList<Task>> taskList) {
        ArrayList<Task> record = null;
        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        record = new ArrayList<>();
        for (int i = 0; i < study.size(); i++) record.add(study.get(i));
        for (int i = 0; i < life.size(); i++) record.add(life.get(i));
        for (int i = 0; i < work.size(); i++) record.add(work.get(i));
        for (int i = 0; i < others.size(); i++) record.add(others.get(i));
        String json = gson.toJson(record);
        editor.putString("record", json);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("record", "");
        Type type = new TypeToken<ArrayList<Task>>() {
        }.getType();
        record = gson.fromJson(json, type);
        if (record == null)
            record = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the aaction bar if it is present
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_deadline:
                item.setChecked(item.isChecked() ? false : true);
                state = SortMode.DEADLINE;
                elvProject.setAdapter(adapter_w_date);
                //Toast.makeText(this, "menu_sort_by_deadline", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_sort_by_project:
                item.setChecked(item.isChecked() ? false : true);
                state = SortMode.PROJECT;
                elvProject.setAdapter(adapter);
                //Toast.makeText(this, "menu_sort_by_project", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_show_finished:
                item.setChecked(item.isChecked() ? false : true);
                show_finished_checked = item.isChecked();
                item_finish_isChecked_state(show_finished_checked);
                return true;

            case R.id.menu_show_hidden:
                item.setChecked(item.isChecked() ? false : true);
                show_hidden_checked = item.isChecked();
                item_hide_isChecked_state(show_hidden_checked);
                return true;
        }
        return false;
    }

//    public void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system;
//            // Can't change the importance or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

//    public void generateNotification(String detail) {
//        @SuppressLint("RemoteViewLayout") RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification);
//
//        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
//
//        // FIXME
//        intent.setAction("createTask");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        view.setTextViewText(R.id.tv_detail, detail);
//        view.setOnClickPendingIntent(R.id.button_notification, pendingIntent);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContent(view)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setOngoing(true);
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // FIXME
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        notificationManagerCompat.notify(0, builder.build()); // FIXME
//    }
}