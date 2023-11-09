package com.sohaib.eventcheck.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "ListModel")
public class ListModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "NAME")
    private String Name;

    @ColumnInfo(name = "Icon")
    private byte[] icon;

    @ColumnInfo(name = "IconColor")
    private int iconColor;

    @ColumnInfo(name = "RemindCount")
    private int RemindCount;

    public ListModel() {

    }

    public ListModel(int id,String name, byte[] icon, int remindCount,int color) {
        this.id = id;
        Name = name;
        this.icon = icon;
        RemindCount = remindCount;
        this.iconColor = color;
    }

    @Ignore
    public ListModel(String name, byte[] icon, int remindCount,int color) {
        Name = name;
        this.icon = icon;
        RemindCount = remindCount;
        this.iconColor = color;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public int getRemindCount() {
        return RemindCount;
    }

    public void setRemindCount(int remindCount) {
        RemindCount = remindCount;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void IncRemindCount()
    {
        this.RemindCount += 1;
    }

    public void DecRemindCount()
    {
        this.RemindCount -= 1;
    }
}
