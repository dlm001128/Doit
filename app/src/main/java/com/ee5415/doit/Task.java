package com.ee5415.doit;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Task implements Serializable, Comparable<Task> {
    private String name_;
    private String deadline_;
    private int year_;
    private int month_;
    private int dayOfMonth_;
    private int dayOfWeek_;
    private int hour_;
    private int minute_;
    private String project_;
    private boolean finish_;
    private boolean hide_;
    private String index_;

    public Task() {}
    public Task(@NonNull Task other) {
        this.name_ = other.name_;
        this.deadline_ = other.deadline_;
        this.year_ = other.year_;
        this.month_ = other.month_;
        this.dayOfMonth_ = other.dayOfMonth_;
        this.dayOfWeek_ = other.dayOfWeek_;
        this.hour_ = other.hour_;
        this.minute_ = other.minute_;
        this.project_ = other.project_;
        this.finish_ = other.finish_;
        this.hide_ = other.hide_;
        this.index_ = other.index_;
    }
    public String getName() {
        return this.name_;
    }
    public String getDeadline() {
        return this.deadline_;
    }
    public int getYear() { return this.year_; }
    public int getMonth() { return this.month_; }
    public int getDayOfMonth() { return this.dayOfMonth_; }
    public int getDayOfWeek() { return this.dayOfWeek_; }
    public int getHour() { return this.hour_; }
    public int getMinute() { return this.minute_; }
    public String getProject() {
        return this.project_;
    }
    public boolean getFinish() {
        return this.finish_;
    }
    public boolean getHide() {
        return this.hide_;
    }
    public String getIndex() {
        return this.index_;
    }
    public void setName(String name) {
        this.name_ = name;
    }
    public void setDeadLine(int year, int month, int dayOfMonth, int dayOfWeek, int hour, int minute, String deadline){
        this.year_ = year;
        this.month_ = month;
        this.dayOfMonth_ = dayOfMonth;
        this.dayOfWeek_ = dayOfWeek;
        this.hour_ = hour;
        this.minute_ = minute;
        this.deadline_ = deadline;
    }
    public void setProject(String project) {
        this.project_ = project;
    }
    public void setFinish(boolean finish) {
        this.finish_ = finish;
    }
    public void setHide(boolean hide) {
        this.hide_ = hide;
    }
    public void setIndex(String index) {
        this.index_ = index;
    }
    public void display(){
        System.out.println("name: " + this.name_);
        System.out.println("project: " + this.project_);
        System.out.println("finish: " + this.finish_);
        System.out.println("hide: " + this.hide_);
        System.out.println("deadline: " + this.deadline_);
        System.out.println("index: " + this.index_);
        System.out.println("dayOfWeek: " + this.dayOfWeek_);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        if (name_ != other.name_)
            return false;
        if (deadline_ != other.deadline_)
            return false;
        if (year_ != other.year_)
            return false;
        if (month_ != other.month_)
            return false;
        if (dayOfMonth_ != other.dayOfMonth_)
            return false;
        if (dayOfWeek_ != other.dayOfWeek_)
            return false;
        if (hour_ != other.hour_)
            return false;
        if (minute_ != other.minute_)
            return false;
        if (project_ != other.project_)
            return false;
        if (finish_ != other.finish_)
            return false;
        if (hide_ != other.hide_)
            return false;
//        if (index_ != other.index_)
//            return false;
        return true;
    }

    @Override
    public int compareTo(Task other) {
        if (this.year_ != other.year_)
            return this.year_ - other.year_;
        if (this.month_ != other.month_)
            return this.month_ - other.month_;
        if (this.dayOfMonth_ != other.dayOfMonth_)
            return this.dayOfMonth_ - other.dayOfMonth_;
        if (this.hour_ != other.hour_)
            return this.hour_ - other.hour_;
        return this.minute_ - other.minute_;
    }
}