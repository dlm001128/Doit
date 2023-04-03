package com.ee5415.doit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskAdapter extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<String, ArrayList<Task>> taskList;// 子条目
    private String[] projectList;// 父条目

    public TaskAdapter(Context context, HashMap<String, ArrayList<Task>> taskList, String[] projectList) {
        super();
        this.context = context;
        this.taskList = taskList;
        this.projectList = projectList;
    }

    /**
     * 返回父条目的个数
     */
    @Override
    public int getGroupCount() {
        return projectList.length;
    }

    /**
     * 返回当前父条目中子条目的个数 展开子条目时调用
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return taskList.get(projectList[groupPosition]).size();
    }

    /**
     * 返回父条目对象
     */
    @Override
    public Object getGroup(int groupPosition) {
        return projectList[groupPosition];
    }

    /**
     * 返回每一个子条目对象（与要显示的内容无关）
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return taskList.get(projectList[groupPosition]).get(childPosition);
    }

    /**
     * 返回父条目的id 默认使用索引值
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 返回子条目的id 默认使用索引值
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        ParentViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.group_list_item, null);
            holder = new ParentViewHolder();
            if(groupPosition == 0){
                holder.tvTitle = convertView.findViewById(R.id.studyTitle);
                convertView.setTag(holder);
            }
            else if(groupPosition == 1){
                holder.tvTitle = convertView.findViewById(R.id.lifeTitle);
                convertView.setTag(holder);
            }
            else if(groupPosition == 2){
                holder.tvTitle = convertView.findViewById(R.id.workTitle);
                convertView.setTag(holder);
            }
            else if(groupPosition == 3){
                holder.tvTitle = convertView.findViewById(R.id.othersTitle);
                convertView.setTag(holder);
            }
        }
        else{
            holder = (ParentViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(projectList[groupPosition]);
        if (groupPosition == 0) {
            holder.tvTitle.setTextColor(-4487428);
        } else if (groupPosition == 1) {
            holder.tvTitle.setTextColor(-10108268);
        } else if (groupPosition == 2) {
            holder.tvTitle.setTextColor(-14000982);
        } else if (groupPosition == 3) {
            holder.tvTitle.setTextColor(-7697266);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        Task curTask = (Task) getChild(groupPosition, childPosition);
        ChildViewHolder holder = new ChildViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_list_item, null);
        }
        else{
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.tvCharacter = convertView.findViewById(R.id.tvCharacter);
        holder.tvDeadline = convertView.findViewById(R.id.tvDeadline);
        //set different to different checkbox, but not implemented yet
        if(groupPosition == 0) holder.cb = convertView.findViewById(R.id.study_checkbox);
        else if(groupPosition == 1) holder.cb = convertView.findViewById(R.id.life_checkbox);
        else if(groupPosition == 2) holder.cb = convertView.findViewById(R.id.work_checkbox);
        else if(groupPosition == 3) holder.cb = convertView.findViewById(R.id.others_checkbox);
        holder.cb = convertView.findViewById(R.id.others_checkbox);
        holder.iv = convertView.findViewById(R.id.imageview_hide);
        convertView.setTag(holder);
        holder.tvCharacter.setText(curTask.getName());
        holder.tvDeadline.setText(curTask.getDeadline());
        if(taskList.get(projectList[groupPosition]).get(childPosition).getHide() == true) holder.iv.setVisibility(View.VISIBLE);
        else holder.iv.setVisibility(View.INVISIBLE);

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Task t = (Task) getChild(groupPosition, childPosition);
                t.setFinish(isChecked);
            }
        });

        if(taskList.get(projectList[groupPosition]).get(childPosition).getFinish() == true) holder.cb.setChecked(true);
        else holder.cb.setChecked(false);

        return convertView;
    }

    /**
     * 子条目是否可以选中
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //子视图容器
    public class ChildViewHolder {
        TextView tvCharacter;
        TextView tvDeadline;
        CheckBox cb;
        ImageView iv;
    }

    //组视图容器
    public class ParentViewHolder {
        TextView tvTitle;
    }
}
