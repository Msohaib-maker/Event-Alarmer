package com.sohaib.eventcheck;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import java.sql.Time;

import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.sohaib.eventcheck.Adapter.ReminderAdapter;
import com.sohaib.eventcheck.Fragment.ReminderBottomSheet;
import com.sohaib.eventcheck.Interface.BottomSheetListener;
import com.sohaib.eventcheck.Interface.ReminderBottomSheetListener;
import com.sohaib.eventcheck.Model.ListModel;
import com.sohaib.eventcheck.Model.ReminderModel;
import com.sohaib.eventcheck.Receivers.Notifier;
import com.sohaib.eventcheck.Repository.DataBase;


import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ListRemindersActivity extends AppCompatActivity implements ReminderBottomSheetListener, ReminderAdapter.ReminderListener {

    Toolbar myToolbar;
    RelativeLayout newReminder;
    LinearLayout Root_of_layout;
    ReminderBottomSheet reminderBottomSheet;
    RecyclerView recyclerView;
    ReminderAdapter reminderAdapter;
    List<ReminderModel> reminderModelList = new ArrayList<>();
    boolean isUpdate = false;
    int currUpdateItemIndex = -1;

    DataBase MyDB;
    TextView DoneToolBar;
    ImageView EditToolBar;
    ImageView DeleteToolBar;
    View divider;


    // Passed from AppMainScreen
    TextView listName;
    int listId;
    String list_name;
    int listColor;
    String special;



    private static final String TAG = "M-TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylist_reminders);

        MyDB = DataBase.getInstance(this);

        listName = findViewById(R.id.ListNameId);



        DeleteToolBar = findViewById(R.id.deleteReminderId);
        myToolbar = findViewById(R.id.customToolBarId);
        recyclerView = findViewById(R.id.reminderRecycleViewId);
        DoneToolBar = findViewById(R.id.ToolBarDoneBtnId);
        EditToolBar = findViewById(R.id.editId);
        Root_of_layout = findViewById(R.id.RootOfListReminderId);


        LinearLayout backView = findViewById(R.id.backBtnId);
        setSupportActionBar(myToolbar);




        listId = (getIntent().getIntExtra("ListId",-1));
        list_name = getIntent().getStringExtra("ListName");
        listColor = getIntent().getIntExtra("ListColor",-1);
        special = getIntent().getStringExtra("Special");




        if(MyDB.reminderModelDao().getAllReminders(listId) != null && listId!= -1)
        {
            reminderModelList = MyDB.reminderModelDao().getAllReminders(listId);
            for(ReminderModel reminderModel2 : reminderModelList)
            {
                Log.d(TAG, "onCreate of ListReminder: " + reminderModel2.getId() + " " + reminderModel2.getTitle());
            }

        }


        if(list_name != null)
        {
            listName.setText(list_name);
            listName.setTextColor(listColor);
        }
        else if(special.equals("Today"))
        {
            listName.setText(special);
            listName.setTextColor(0xFF1976D2);
            List<ReminderModel> reminderModelListCopy = MyDB.reminderModelDao().getEveryReminders();
            fillListForTodayReminds();
        }
        else if(special.equals("Schedule"))
        {
            listName.setText(special);
            listName.setTextColor(0xFFFF0000);
            fillListForScheduledReminds();
        }

        reminderAdapter = new ReminderAdapter(this,reminderModelList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reminderAdapter);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListRemindersActivity.this, "Inv", Toast.LENGTH_SHORT).show();
            }
        });


        newReminder = findViewById(R.id.addNewReminderInListId);

        if(special != null)
        {
            newReminder.setVisibility(View.GONE);
        }


        newReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderBottomSheet = ReminderBottomSheet.newInstance(null);
                reminderBottomSheet.show(getSupportFragmentManager(),reminderBottomSheet.getTag());
            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListRemindersActivity.this,AppMainScreen.class));
            }
        });

        Root_of_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DoneToolBar.setVisibility(View.GONE);
                EditToolBar.setVisibility(View.GONE);*/
            }
        });




    }


    void fillListForTodayReminds()
    {
        List<ReminderModel> reminderModelListCopy = MyDB.reminderModelDao().getEveryReminders();
        for(ReminderModel reminderModel2 : reminderModelListCopy)
        {
            if(reminderModel2.getDate().equals(getCurrentDateInString()))
            {
                reminderModelList.add(reminderModel2);
            }
        }
    }

    void fillListForScheduledReminds()
    {
        List<ReminderModel> reminderModelListCopy = MyDB.reminderModelDao().getEveryReminders();
        for(ReminderModel reminderModel2 : reminderModelListCopy)
        {
            if(!reminderModel2.getTime().isEmpty())
            {
                reminderModelList.add(reminderModel2);
            }
        }
    }


    public String getCurrentDateInString() {
        // Create a SimpleDateFormat object with the desired format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("EEE d, MMMM yyyy");

        // Get the current date using Calendar
        Calendar calendar = Calendar.getInstance();

        // Return the formatted current date as a string
        return format.format(calendar.getTime());
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void DoneClick(ReminderModel reminderModel) {


        if(isUpdate)
        {
            if (currUpdateItemIndex >= 0 && currUpdateItemIndex < reminderModelList.size()) {
                ReminderModel updatedReminder = reminderModel.copy();
                updatedReminder.setTitle(reminderModel.getTitle()); // Set the updated title here
                reminderModelList.set(currUpdateItemIndex, updatedReminder);
                reminderAdapter.notifyItemChanged(currUpdateItemIndex);
                MyDB.reminderModelDao().UpdateReminder(updatedReminder);
                Log.d(TAG, "DoneClick: " + updatedReminder.getId());

                if(updatedReminder.getAlarm_time() != 0)
                {
                    scheduleNotification(updatedReminder);
                }


            }
            currUpdateItemIndex = -1;
            isUpdate = false;

            for(ReminderModel reminderModel1 : MyDB.reminderModelDao().getAllReminders(listId))
            {
                Log.d(TAG, "DoneClick: " + reminderModel1.getId() + " " + reminderModel1.getList_id());


            }

        }
        else
        {
            reminderModel.setList_id(listId);
            reminderModelList.add(reminderModel);


            MyDB.reminderModelDao().addReminder(reminderModel);
            reminderAdapter.notifyDataSetChanged();

            ListModel listModel = MyDB.listModelDao().getListModel(reminderModel.getList_id());
            listModel.IncRemindCount();
            MyDB.listModelDao().UpdateListModel(listModel);

            reminderModelList.clear();
            //Log.d(TAG, "DoneClick: " + reminderModel1.getId() + " " + reminderModel1.getAlarm_time());
            reminderModelList.addAll(MyDB.reminderModelDao().getAllReminders(listId));


            for(ReminderModel reminderModel1 : MyDB.reminderModelDao().getAllReminders(listId))
            {
                Log.d(TAG, "DoneClick: " + reminderModel1.getId() + " " + reminderModel1.getList_id());
            }

            if(reminderModelList.get(reminderModelList.size() - 1).getAlarm_time() != 0)
            {
                scheduleNotification(reminderModelList.get(reminderModelList.size() - 1));
            }



        }

        reminderBottomSheet.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scheduleNotification(ReminderModel reminderModel) {

        Toast.makeText(this, "Alarm scheduled for " + reminderModel.getDate() + " , " + reminderModel.getTime(), Toast.LENGTH_SHORT).show();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, Notifier.class)
                .putExtra("rem_id",reminderModel.getId())
                .putExtra("rem_title",reminderModel.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminderModel.getId(), intent, PendingIntent.FLAG_IMMUTABLE);

        long notificationTime = reminderModel.getAlarm_time();

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Use setExact() for Marshmallow (API level 23) and above
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            } else {
                // Use set() for lower API levels
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
                }
            }
        }
    }





    @Override
    public void CancelClick() {
        reminderBottomSheet.dismiss();
    }

    @Override
    public Pair<View,View> SimpleClick() {


        DoneToolBar.setVisibility(View.VISIBLE);
        EditToolBar.setVisibility(View.VISIBLE);
        return new Pair<>(DoneToolBar,EditToolBar);
    }

    @Override
    public void RemindOnCLick(ReminderModel reminderModel,int pos) {
        Log.d(TAG, "RemindOnCLick: " + reminderModel.getId());
        reminderBottomSheet = ReminderBottomSheet.newInstance(reminderModel);
        reminderBottomSheet.show(getSupportFragmentManager(),reminderBottomSheet.getTag());
        isUpdate = true;
        currUpdateItemIndex = pos;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void UpdateCheckBoxState(ReminderModel reminderModel) {
        MyDB.reminderModelDao().UpdateReminder(reminderModel);


        for(ReminderModel reminderModel1 : MyDB.reminderModelDao().getAllReminders(listId))
        {
            Log.d(TAG, "UpdateCheckBoxState: " + reminderModel1.isCheckStatus());
        }
        /*reminderModelList.clear();
        reminderModelList.addAll(MyDB.reminderModelDao().getAllReminders(listId));
        reminderAdapter.notifyDataSetChanged();*/


    }

    @Override
    public View ReturnDeleteView() {

        DeleteToolBar.setVisibility(View.VISIBLE);
        return DeleteToolBar;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void DeleteRemindModel(ReminderModel reminderModel) {

        if(reminderModel.getAlarm_time() != 0)
        {
            Intent intent = new Intent(this, Notifier.class);
            intent.putExtra("rem_id",reminderModel.getId());
            intent.putExtra("rem_title",reminderModel.getTitle());
            // Set any extra data if required for your BroadcastReceiver
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminderModel.getId(), intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }


        int rListId = reminderModel.getList_id();
        MyDB.reminderModelDao().DeleteReminder(reminderModel);
        ListModel listModel = MyDB.listModelDao().getListModel(reminderModel.getList_id());
        listModel.DecRemindCount();
        MyDB.listModelDao().UpdateListModel(listModel);

        if(special == null)
        {
            reminderModelList.clear();
            reminderModelList.addAll(MyDB.reminderModelDao().getAllReminders(rListId));
        }
        else
        {
            reminderModelList.clear();
            if(special.equals("Today"))
            {
                fillListForTodayReminds();
            }
            else
            {
                fillListForScheduledReminds();
            }
        }


        reminderAdapter.notifyDataSetChanged();

    }

    @Override
    public View dividerView() {
        divider = findViewById(R.id.belowLine3Id);
        divider.setVisibility(View.VISIBLE);
        return divider;
    }


}
