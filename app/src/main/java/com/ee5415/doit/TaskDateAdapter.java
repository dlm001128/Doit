package com.ee5415.doit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskDateAdapter extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<DateKey, ArrayList<Task>> taskList_w_date;// 子条目
    private ArrayList<DateKey> dateList;// 父条目

    public TaskDateAdapter(Context context, HashMap<DateKey, ArrayList<Task>> taskList, ArrayList<DateKey> dateList) {
        super();
        this.context = context;
        this.taskList_w_date = taskList;
        this.dateList = dateList;
//        dateList = new ArrayList<>();
//        for (Map.Entry<DateKey, ArrayList<Task>> set : taskList_w_date.entrySet()) {
//            this.dateList.add(set.getKey());
//        }
    }

    /**
     * 返回父条目的个数
     */
    @Override
    public int getGroupCount() {
        return this.dateList.size();
    }

    /**
     * 返回当前父条目中子条目的个数 展开子条目时调用
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.taskList_w_date.get(this.dateList.get(groupPosition)).size();
    }

    /**
     * 返回父条目对象
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this.dateList.get(groupPosition);
    }

    /**
     * 返回每一个子条目对象（与要显示的内容无关）
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.taskList_w_date.get(this.dateList.get(groupPosition)).get(childPosition);
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
            holder = new ParentViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.group_list, null);
            holder.tvTitle = convertView.findViewById(R.id.list_title);
            convertView.setTag(holder);
        }
        else{
            holder = (ParentViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(dateList.get(groupPosition).getTitle());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        final Task curTask = (Task) getChild(groupPosition, childPosition);
        ChildViewHolder holder = new ChildViewHolder();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_list, null);
        }
        else{
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.colorBar = convertView.findViewById(R.id.color_bar);
        holder.tvCharacter = convertView.findViewById(R.id.tvCharacter);
        holder.tvDeadline = convertView.findViewById(R.id.tvDeadline);
        //set different to different checkbox, but not implemented yet

        holder.cb = convertView.findViewById(R.id.checkbox);
        holder.iv = convertView.findViewById(R.id.imageview_hide);
        convertView.setTag(holder);
        holder.tvCharacter.setText(curTask.getName());
//        holder.tvDeadline.setText(curTask.getDeadline());
        String hourStr = curTask.getHour() < 10 ? "0" + curTask.getHour() : String.valueOf(curTask.getHour());
        String minuteStr = curTask.getMinute() < 10 ? "0" + curTask.getMinute() : String.valueOf(curTask.getMinute());
        switch (curTask.getProject()) { //"Study", "Life", "Work", "Others"
            case "Study":
                holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.purple_200));
                break;
            case "Life":
                holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.green));
                break;
            case "Work":
                holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.blue));
                break;
            case "Others":
                holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.grey));
                break;
        }
        holder.tvDeadline.setText(hourStr + ":" + minuteStr);
        if(curTask.getHide() == true)
            holder.iv.setVisibility(View.VISIBLE);
        else
            holder.iv.setVisibility(View.INVISIBLE);

        if(curTask.getFinish() == true) holder.cb.setChecked(true);
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
        View colorBar;
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
