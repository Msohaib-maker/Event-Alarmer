package com.sohaib.eventcheck.Repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sohaib.eventcheck.Model.ListModel;
import com.sohaib.eventcheck.Model.ReminderModel;

@Database(entities = {ListModel.class, ReminderModel.class},exportSchema = true,version = 1)
public abstract class DataBase extends RoomDatabase {

    private static final String DB_NAME = "reminderdb";
    private static DataBase instance;

    public static synchronized DataBase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context, DataBase.class, DB_NAME)
                    .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }

        return instance;
    }

    public abstract ListModelDao listModelDao();
    public abstract ReminderModelDao reminderModelDao();


}
