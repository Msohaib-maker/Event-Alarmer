package com.sohaib.eventcheck.Model;

import java.util.List;

public class AllListModel {

    private String listName;
    int color;
    private List<ReminderModel> reminderModelList;

    AllListModel()
    {

    }

    public AllListModel(String listName, List<ReminderModel> reminderModelList,int Colr) {
        this.listName = listName;
        this.reminderModelList = reminderModelList;
        this.color = Colr;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<ReminderModel> getReminderModelList() {
        return reminderModelList;
    }

    public void setReminderModelList(List<ReminderModel> reminderModelList) {
        this.reminderModelList = reminderModelList;
    }
}
