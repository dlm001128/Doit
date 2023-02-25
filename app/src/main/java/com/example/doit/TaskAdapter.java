package com.example.doit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            convertView.setTag(holder);
        }
        else{
            holder = (ParentViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(projectList[groupPosition]);

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
        convertView.setTag(holder);
        holder.tvCharacter.setText(curTask.getName());
        holder.tvDeadline.setText(curTask.getDeadline());

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
    }

    //组视图容器
    public class ParentViewHolder {
        TextView tvTitle;
    }
}
