package com.sohaib.eventcheck.Model;

import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.net.URL;
import java.sql.Time;
import java.util.Date;

@Entity(tableName = "ReminderTable")
public class ReminderModel implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Note")
    private String Note;

    @ColumnInfo(name = "Date")
    private String date;
    @ColumnInfo(name = "Time")
    private String time;
    @ColumnInfo(name = "URL")
    private String Url;
    @ColumnInfo(name = "Flag")
    private boolean flag;

    @ColumnInfo(name = "CheckStatus")
    private boolean checkStatus;

    @ColumnInfo(name = "ListId")
    private int List_id;
    @ColumnInfo(name = "AlarmTime")
    private long alarm_time;


    public ReminderModel()
    {

    }


    public ReminderModel(String title, String note, String date, String time) {
        this.title = title;
        Note = note;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getList_id() {
        return List_id;
    }

    public void setList_id(int list_id) {
        List_id = list_id;
    }

    public long getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(long alarm_time) {
        this.alarm_time = alarm_time;
    }

    public ReminderModel copy() {
        ReminderModel copy = new ReminderModel();
        copy.setId(this.id);
        copy.setTitle(this.title);
        copy.setNote(this.Note);
        copy.setDate(this.date);
        copy.setTime(this.time);
        copy.setUrl(this.Url);
        copy.setFlag(this.flag);
        copy.setCheckStatus(this.checkStatus);
        copy.setList_id(this.List_id);
        copy.setAlarm_time(this.alarm_time);
        return copy;
    }
}
