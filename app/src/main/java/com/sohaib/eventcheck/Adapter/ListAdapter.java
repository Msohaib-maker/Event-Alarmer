package com.sohaib.eventcheck.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.sohaib.eventcheck.Model.ListModel;
import com.sohaib.eventcheck.Model.ReminderModel;
import com.sohaib.eventcheck.R;
import com.sohaib.eventcheck.Repository.DataBase;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ListModel> dataList;
    private Context context;
    private ListClickListener listClickListener;
    boolean isLongClick = false;
    private int selectedPos = RecyclerView.NO_POSITION;

    private final String TAG = "LA-TAG";
    private DataBase MyDb;

    public interface ListClickListener
    {
        public void onClick(ListModel listModel);
        public Pair<View,View> onLongClick();
        public void UpdateListInfo(int pos);
        public View RetDeleteView();
        public void DeleteListModel(ListModel listModel);
        public View dividerView();
    }


    public ListAdapter(List<ListModel> dataList, Context context, ListClickListener listClickListener1) {
        this.dataList = dataList;
        this.context = context;
        this.listClickListener = listClickListener1;
        MyDb = DataBase.getInstance(context);
    }

    public void setDataList(List<ListModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0 && dataList.size()==1)
        {
            return 1;
        }
        else if(position == 0 && dataList.size() > 1)
        {
            return 2;
        }
        else if(position == dataList.size() -1 && dataList.size() > 1)
        {
            return 3;
        }
        else
        {
            return 4;
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_list_ui, parent, false);
        /*view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singlelistitem_middle, parent, false);


        if(viewType == 1)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.singlelistitem, parent, false);
        }
        else if(viewType == 2)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.singlelistitem0, parent, false);
        }
        else if(viewType == 3)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.singlelistitem_last, parent, false);
        }*/
        return new ViewHolder(view);
    }


    // utility
    public Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) {
            return null;
        }

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListModel data = dataList.get(position);
        holder.list_name.setText(data.getName());
        if(data.getIcon() != null)
        {

            Bitmap ImgIcon = convertByteArrayToBitmap(data.getIcon());
            if(ImgIcon != null)
            {
                holder.Icon.setImageBitmap(ImgIcon);
                holder.Icon.setBackgroundColor(data.getIconColor());
                holder.Icon.setPadding(12,12,12,12);
            }

        }

        String countText = "Your list contains " + String.valueOf( data.getRemindCount()) + " tasks";
        holder.remindCount.setText(countText);

        int trueCount = 0;
        List<ReminderModel> reminderModels = MyDb.reminderModelDao().getAllReminders(data.getId());
        int R_count = reminderModels.size();
        for(ReminderModel reminderModel1 :reminderModels)
        {
            if(reminderModel1.isCheckStatus())
            {
                trueCount++;
            }
        }

        if(R_count == 0)
        {
            holder.progressBar.setProgress(0);
            holder.progressText.setText("0");

        }
        else
        {
            int progressPer = (((trueCount* 100)/R_count));
            holder.progressBar.setProgress(progressPer);
            holder.progressText.setText(String.valueOf(progressPer));
        }

        String completedTasks = "Completed tasks : " + String.valueOf(trueCount);
        holder.completedTasks.setText(completedTasks);




        if(position == selectedPos)
        {
            holder.indicator.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.indicator.setVisibility(View.GONE);
        }





    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView list_name;
        ImageView Icon;
        TextView remindCount;
        ImageView indicator;
        ProgressBar progressBar;
        TextView progressText;
        TextView completedTasks;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.listNameId);
            Icon = itemView.findViewById(R.id.listImageId);
            remindCount = itemView.findViewById(R.id.reminderCountId);
            indicator = itemView.findViewById(R.id.indicatorId);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progressText = itemView.findViewById(R.id.progressTextId);
            completedTasks = itemView.findViewById(R.id.completedTasksId);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!isLongClick)
                    {
                        int pos = getAdapterPosition();
                        ListModel listModel = dataList.get(pos);
                        listClickListener.onClick(listModel);
                    }

                    isLongClick = false;

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public boolean onLongClick(View v) {

                    isLongClick = true;
                    Pair<View,View> viewViewPair = listClickListener.onLongClick();
                    ImageView delete = (ImageView) listClickListener.RetDeleteView();
                    View divider = listClickListener.dividerView();
                    TextView done = (TextView) viewViewPair.first;
                    ImageView edit = (ImageView) viewViewPair.second;
                    selectedPos = getAdapterPosition();
                    notifyDataSetChanged();

                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            done.setVisibility(View.GONE);
                            edit.setVisibility(View.GONE);
                            delete.setVisibility(View.GONE);
                            divider.setVisibility(View.GONE);
                            isLongClick = false;
                            selectedPos = RecyclerView.NO_POSITION;
                            notifyDataSetChanged();

                        }
                    });

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            done.setVisibility(View.GONE);
                            edit.setVisibility(View.GONE);
                            delete.setVisibility(View.GONE);
                            divider.setVisibility(View.GONE);
                            listClickListener.UpdateListInfo(getAdapterPosition());
                            isLongClick = false;
                            selectedPos = RecyclerView.NO_POSITION;
                            notifyDataSetChanged();

                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            done.setVisibility(View.GONE);
                            edit.setVisibility(View.GONE);
                            delete.setVisibility(View.GONE);
                            divider.setVisibility(View.GONE);
                            listClickListener.DeleteListModel(dataList.get(getAdapterPosition()));
                            isLongClick = false;
                            selectedPos = RecyclerView.NO_POSITION;
                            notifyDataSetChanged();
                        }
                    });

                    return true;
                }
            });
        }
    }
}
