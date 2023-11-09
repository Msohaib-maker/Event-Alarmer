package com.sohaib.eventcheck.Repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sohaib.eventcheck.Model.ReminderModel;

import java.util.List;

@Dao
public interface ReminderModelDao {

    @Query("select * from ReminderTable where ListId = :listId")
    List<ReminderModel> getAllReminders(int listId);

    @Query("select * from ReminderTable")
    List<ReminderModel> getEveryReminders();

    @Insert
    void addReminder(ReminderModel reminderModel);

    @Update
    void UpdateReminder(ReminderModel reminderModel);

    @Delete
    void DeleteReminder(ReminderModel reminderModel);


    @Query("DELETE FROM ReminderTable WHERE ListId = :id")
    void deleteRemindsOfList(int id);
}
