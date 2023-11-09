package com.sohaib.eventcheck.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sohaib.eventcheck.Model.ReminderModel;
import com.sohaib.eventcheck.R;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private List<ReminderModel> reminderModelList;
    Context context;
    private ReminderListener reminderListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION;
    private static final String TAG = "RV-TAG";


    public interface ReminderListener
    {
        Pair<View,View> SimpleClick();
        void RemindOnCLick(ReminderModel reminderModel,int pos);
        void UpdateCheckBoxState(ReminderModel reminderModel);
        View ReturnDeleteView();
        void DeleteRemindModel(ReminderModel reminderModel);
        View dividerView();

    }


    // Constructor
    public ReminderAdapter(Context context1, List<ReminderModel> data,ReminderListener reminderListener1) {
        this.context = context1;
        this.reminderModelList = data;
        this.reminderListener = reminderListener1;

    }

    // Inflate the item layout and create the ViewHolder object
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_reminder,null);
        return new ViewHolder(view);
    }

    // Populate data into the item through the ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReminderModel item = reminderModelList.get(position);
        holder.ReminderTitle.setText(item.getTitle());

        // Set or hide the Notes TextView and its data
        if (!item.getNote().isEmpty()) {
            holder.RemindNotes.setVisibility(View.VISIBLE);
            holder.RemindNotes.setText(item.getNote());
        } else {
            holder.RemindNotes.setVisibility(View.GONE);
        }

        // Set or hide the URL CardView and its data
        if (!item.getUrl().isEmpty()) {
            holder.UrlCardView.setVisibility(View.VISIBLE);
            holder.RemindUrl.setText(item.getUrl());
        } else {
            holder.UrlCardView.setVisibility(View.GONE);
        }

        // Set or hide the Time TextView and its data
        if (!item.getTime().isEmpty()) {
            holder.ReminderTime.setVisibility(View.VISIBLE);
            holder.ReminderTime.setText(item.getTime());
        } else {
            holder.ReminderTime.setVisibility(View.GONE);
        }

        // Set or hide the Date TextView and its data
        if (!item.getDate().isEmpty()) {
            holder.RemindDate.setVisibility(View.VISIBLE);
            holder.RemindDate.setText(item.getDate());
        } else {
            holder.RemindDate.setVisibility(View.GONE);
        }

        // Set or hide the Flag ImageView based on the flag state
        if (item.isFlag()) {
            holder.flagView.setVisibility(View.VISIBLE);
        } else {
            holder.flagView.setVisibility(View.GONE);
        }

        if(item.isCheckStatus())
        {
            holder.checkBox.setChecked(true);
            holder.ReminderTitle.setPaintFlags(holder.ReminderTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.ReminderTitle.setPaintFlags(holder.ReminderTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if(selectedItemPosition == position)
        {
            holder.infoIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.infoIcon.setVisibility(View.GONE);
        }




    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return reminderModelList.size();
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ReminderTitle;
        TextView ReminderTime;
        TextView RemindDate;
        TextView RemindNotes;
        TextView RemindUrl;
        CardView UrlCardView;
        ImageView flagView;
        ImageView infoIcon;

        private TextView done;
        private ImageView edit;
        private ImageView delete;

        CheckBox checkBox;


        ViewHolder(View itemView) {
            super(itemView);
            ReminderTitle = itemView.findViewById(R.id.reminderTitle);
            ReminderTime = itemView.findViewById(R.id.reminderTime);
            RemindDate = itemView.findViewById(R.id.reminderDate);
            RemindNotes = itemView.findViewById(R.id.reminderNotes);
            RemindUrl = itemView.findViewById(R.id.reminderUrl);
            UrlCardView = itemView.findViewById(R.id.urlCardViewId);
            flagView = itemView.findViewById(R.id.flagId);
            infoIcon = itemView.findViewById(R.id.infoId);
            checkBox = itemView.findViewById(R.id.checkStatusId);



            infoIcon.setEnabled(true);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public boolean onLongClick(View v) {

                    try
                    {
                        Pair<View,View> viewViewPair =reminderListener.SimpleClick();
                        delete = (ImageView) reminderListener.ReturnDeleteView();
                        View divider = reminderListener.dividerView();
                        done = (TextView) viewViewPair.first;
                        edit = (ImageView) viewViewPair.second;

                        selectedItemPosition = getAdapterPosition();

                        notifyDataSetChanged();


                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                done.setVisibility(View.GONE);
                                delete.setVisibility(View.GONE);
                                edit.setVisibility(View.GONE);
                                divider.setVisibility(View.GONE);
                                Log.d(TAG, "onClick: " + selectedItemPosition + reminderModelList.get(selectedItemPosition).getList_id());
                                selectedItemPosition = RecyclerView.NO_POSITION;
                                notifyDataSetChanged();
                            }
                        });

                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int pos = getAdapterPosition();
                                reminderListener.RemindOnCLick(reminderModelList.get(selectedItemPosition),selectedItemPosition);
                                edit.setVisibility(View.GONE);
                                done.setVisibility(View.GONE);
                                delete.setVisibility(View.GONE);
                                divider.setVisibility(View.GONE);
                                selectedItemPosition = RecyclerView.NO_POSITION;
                                notifyDataSetChanged();
                            }
                        });


                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int pos = getAdapterPosition();
                                reminderListener.DeleteRemindModel(reminderModelList.get(selectedItemPosition));
                                edit.setVisibility(View.GONE);
                                done.setVisibility(View.GONE);
                                delete.setVisibility(View.GONE);
                                divider.setVisibility(View.GONE);
                                selectedItemPosition = RecyclerView.NO_POSITION;
                                notifyDataSetChanged();
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "ItemView: " + selectedItemPosition + " " + infoIcon.isEnabled());
                        Log.d(TAG, "onClick: " + getAdapterPosition());

                    }

                    return true;
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getAdapterPosition(); // Get the correct position here
                    if (pos != RecyclerView.NO_POSITION) { // Check if the position is valid
                        ReminderModel reminderModel = reminderModelList.get(pos);
                        reminderModel.setCheckStatus(isChecked);
                        reminderListener.UpdateCheckBoxState(reminderModel);
                    }
                }
            });






        }
    }
}
