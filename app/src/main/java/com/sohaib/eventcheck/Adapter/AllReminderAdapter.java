package com.sohaib.eventcheck.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sohaib.eventcheck.Model.AllListModel;
import com.sohaib.eventcheck.R;

import java.util.List;

public class AllReminderAdapter extends RecyclerView.Adapter<AllReminderAdapter.Holder> {

    Context context;
    private List<AllListModel> allListModels;

    ReminderAdapter.ReminderListener reminderListener;

    public AllReminderAdapter(Context context, List<AllListModel> allListModels, ReminderAdapter.ReminderListener reminderListener1) {
        this.context = context;
        this.allListModels = allListModels;
        this.reminderListener = reminderListener1;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.single_all_list,null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.list_name.setText(allListModels.get(position).getListName());
        holder.list_name.setTextColor(allListModels.get(position).getColor());
        ReminderAdapter reminderAdapter = new ReminderAdapter(context,allListModels.get(position).getReminderModelList(),reminderListener);
        holder.recyclerView_reminders.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView_reminders.setAdapter(reminderAdapter);
    }

    @Override
    public int getItemCount() {
        return allListModels.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        TextView list_name;
        RecyclerView recyclerView_reminders;
        public Holder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.sfa_listNameId);
            recyclerView_reminders = itemView.findViewById(R.id.sfa_Recycle_remindersId);
        }
    }
}
