package com.ee5415.doit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.DayOfWeek;

// This class is a key to search the same day in the taskList.

public class DateKey implements Serializable, Comparable<DateKey> {
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

    public DateKey (int year, int month, int dayOfMonth, int dayOfWeek, int hour, int minute) {
        this.year_ = year;
        this.month_ = month;
        this.dayOfMonth_ = dayOfMonth;
        this.dayOfWeek_ = dayOfWeek;
        this.hour_ = hour;
        this.minute_ = minute;
    }

    public int getYear_() {
        return year_;
    }

    public int getMonth_() { return month_; }

    public int getDayOfMonth_() {
        return dayOfMonth_;
    }

    public int getDayOfWeek_() {
        return dayOfWeek_;
    }

    public int getHour_() {
        return hour_;
    }

    public int getMinute_() {
        return minute_;
    }

    public void setYear_(int year_) {
        this.year_ = year_;
    }

    public void setMonth_(int month_) {
        this.month_ = month_;
    }

    public void setDayOfMonth_(int dayOfMonth_) {
        this.dayOfMonth_ = dayOfMonth_;
    }

    public void setDayOfWeek_(int dayOfWeek_) {
        this.dayOfWeek_ = dayOfWeek_;
    }

    public void setHour_(int hour_) {
        this.hour_ = hour_;
    }

    public void setMinute_(int minute_) {
        this.minute_ = minute_;
    }

    public String getTitle() {
        String monthStr = this.month_ < 10 ? "0" + this.month_ : String.valueOf(this.month_);
        String dayOfMonthStr = this.dayOfMonth_ < 10 ? "0" + this.dayOfMonth_ : String.valueOf(this.dayOfMonth_);
        String dayOfWeekStr = String.valueOf(this.dayOfWeek_);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfWeekStr = DayOfWeek.of(this.dayOfWeek_).toString();
        }
        return monthStr + "/" + dayOfMonthStr + " " + dayOfWeekStr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * (this.year_ + this.month_ + this.dayOfMonth_ + this.dayOfWeek_ + this.hour_ + this.minute_);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DateKey other = (DateKey) obj;
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
        return true;
    }

    @Override
    public String toString() {
        return this.year_ + "/" + this.month_ + "/" + this.dayOfMonth_ + " " + this.hour_ + ":" + this.minute_;
    }

    @Override
    public int compareTo(DateKey other) {
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
