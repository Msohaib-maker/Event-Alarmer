package com.sohaib.eventcheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sohaib.eventcheck.Adapter.AllReminderAdapter;
import com.sohaib.eventcheck.Adapter.ReminderAdapter;
import com.sohaib.eventcheck.Model.AllListModel;
import com.sohaib.eventcheck.Model.ListModel;
import com.sohaib.eventcheck.Model.ReminderModel;
import com.sohaib.eventcheck.Repository.DataBase;

import java.util.ArrayList;
import java.util.List;

public class All_List_Info extends AppCompatActivity implements ReminderAdapter.ReminderListener {


    RecyclerView sfa_recycleView;
    List<AllListModel> allListModelList = new ArrayList<>();
    DataBase dataBase;
    View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_flag_all_screen);

        dataBase = DataBase.getInstance(this);
        LinearLayout linearLayout = findViewById(R.id.sfa_backBtnId);
        divider = findViewById(R.id.belowLineId);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(All_List_Info.this,AppMainScreen.class));
            }
        });


        for(ListModel listModel : dataBase.listModelDao().getAllListModels())
        {
            AllListModel allListModel = new AllListModel(listModel.getName(),dataBase.reminderModelDao().getAllReminders(listModel.getId()),listModel.getIconColor());
            allListModelList.add(allListModel);
        }



        sfa_recycleView = findViewById(R.id.sfa_AllList_reminderRecycleViewId);
        AllReminderAdapter allReminderAdapter = new AllReminderAdapter(this,allListModelList,this);
        sfa_recycleView.setLayoutManager(new LinearLayoutManager(this));
        sfa_recycleView.setAdapter(allReminderAdapter);

        sfa_recycleView.setNestedScrollingEnabled(false);

        /*int totalHeight = 0;
        for (int i = 0; i < allReminderAdapter.getItemCount(); i++) {
            View itemView = allReminderAdapter.onCreateViewHolder(sfa_recycleView, allReminderAdapter.getItemViewType(i)).itemView;
            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += itemView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams layoutParams = sfa_recycleView.getLayoutParams();
        layoutParams.height = totalHeight;
        sfa_recycleView.setLayoutParams(layoutParams);*/


    }


    @Override
    public Pair<View, View> SimpleClick() {
        return null;
    }

    @Override
    public void RemindOnCLick(ReminderModel reminderModel, int pos) {

    }

    @Override
    public void UpdateCheckBoxState(ReminderModel reminderModel) {
        dataBase.reminderModelDao().UpdateReminder(reminderModel);

    }

    @Override
    public View ReturnDeleteView() {
        return null;
    }

    @Override
    public void DeleteRemindModel(ReminderModel reminderModel) {

    }

    @Override
    public View dividerView() {
        //divider.setVisibility(View.VISIBLE);
        return divider;
    }


}