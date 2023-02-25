package com.example.doit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Task implements Serializable {
    //private static final long serialVersionUID = 6465198351058235015L;
    @SerializedName("name_")
    private String name_;
    @SerializedName("deadline_")
    private String deadline_;
    @SerializedName("year_")
    private int year_;
    @SerializedName("month_")
    private int month_;
    @SerializedName("dayOfMonth_")
    private int dayOfMonth_;
    @SerializedName("dayOfWeek_")
    private int dayOfWeek_;
    @SerializedName("hour_")
    private int hour_;
    @SerializedName("minute_")
    private int minute_;
    @SerializedName("project_")
    private String project_;
    @SerializedName("finish_")
    private boolean finish_;
    @SerializedName("hide_")
    private boolean hide_;
    @SerializedName("index_")
    private int index_;

    public Task() {}
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
    public int getIndex() {
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
    public void setIndex(int index) {
        this.index_ = index;
    }
    public void display(){
        System.out.println("name: " + this.name_);
        System.out.println("project: " + this.project_);
        System.out.println("finish: " + this.finish_);
        System.out.println("hide: " + this.hide_);
        System.out.println("deadline: " + this.deadline_);
    }
}