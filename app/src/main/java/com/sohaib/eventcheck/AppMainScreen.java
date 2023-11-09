package com.sohaib.eventcheck;

import android.annotation.SuppressLint;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sohaib.eventcheck.Adapter.ListAdapter;
import com.sohaib.eventcheck.Fragment.BottomSheetFrag;
import com.sohaib.eventcheck.Interface.BottomSheetListener;

import com.sohaib.eventcheck.Model.ListModel;
import com.sohaib.eventcheck.Model.ReminderModel;
import com.sohaib.eventcheck.Receivers.Notifier;
import com.sohaib.eventcheck.Repository.DataBase;
import com.sohaib.eventcheck.Utility.Padding;
import com.sohaib.eventcheck.ViewModel.ListViewModel;
import com.google.android.material.search.SearchBar;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AppMainScreen extends AppCompatActivity implements BottomSheetListener, ListAdapter.ListClickListener {

    BottomSheetFrag bottomSheetFrag;
    RecyclerView ListRecycleView;
    ListAdapter listAdapter;
    List<ListModel> listModels = new ArrayList<>();
    ListViewModel listViewModel;
    DataBase MyDb;
    TextView Cancel;
    SearchView SearchBar;
    TextView doneToolbar;
    ImageView EditToolbar;
    ImageView DeleteToolbar;
    boolean isUpdate = false;
    int curr_pos = -1;
    TextView AllCount;
    TextView Schedule_count;
    TextView today_count;
    View divider;
    ScrollView scrollView;
    ProgressBar today_bar;
    TextView today_bar_percent;

    ProgressBar Sch_bar;
    TextView Sch_bar_percent;
    ProgressBar ContBar;
    TextView ContShow;

    // Debug test
    private static final String TAG = "R-TAG";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyDb = DataBase.getInstance(this);

        scrollView = findViewById(R.id.TheScrollView);
        today_bar = findViewById(R.id.Today_progress_bar);
        today_bar_percent = findViewById(R.id.Today_progressTextId);
        Sch_bar = findViewById(R.id.Sch_progress_bar);
        Sch_bar_percent = findViewById(R.id.Sch_progressTextId);
        ContBar = findViewById(R.id.Total_progressBar);
        ContShow = findViewById(R.id.totalRemindId);


        divider = findViewById(R.id.bottomLine2Id);
        DeleteToolbar = findViewById(R.id.deleteListId);
        doneToolbar = findViewById(R.id.ToolBarDoneBtnId2);
        EditToolbar = findViewById(R.id.edit2Id);
        SearchBar = findViewById(R.id.SearchBarId);
        RelativeLayout newList = findViewById(R.id.addNewListId);
        LinearLayout linearLayout = findViewById(R.id.addReminderId);
        ListRecycleView = findViewById(R.id.recycleViewOfListId);
        CardView All = findViewById(R.id.allListInfoId);
        CardView Today = findViewById(R.id.todayInfoId);
        today_count = findViewById(R.id.todayCountId);
        CardView Schedule = findViewById(R.id.ScheduledId);
        Schedule_count = findViewById(R.id.ScheduledCountId);
        AllCount = findViewById(R.id.AllRemindCountId);
        RelativeLayout guide = findViewById(R.id.GuideSearchId);




        String displayToday = "Total tasks : " + String.valueOf( CountTodayReminds());
        String displaySch = "Total tasks : " + String.valueOf( CountScheduledReminds());
        String TotalDisplay = "List count : " + String.valueOf( CountAllReminds());
        AllCount.setText(TotalDisplay);
        today_count.setText(displayToday);
        Schedule_count.setText(displaySch);

        Today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppMainScreen.this, ListRemindersActivity.class)
                        .putExtra("Special","Today"));
            }
        });

        Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppMainScreen.this, ListRemindersActivity.class)
                        .putExtra("Special","Schedule"));
            }
        });

        All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppMainScreen.this, All_List_Info.class));
            }
        });




        if(MyDb.listModelDao().getAllListModels() != null)
        {
            listModels = MyDb.listModelDao().getAllListModels();
        }


        listAdapter = new ListAdapter(listModels,this, this);

        ListRecycleView.setLayoutManager(new LinearLayoutManager(this));
        ListRecycleView.setAdapter(listAdapter);
        ListRecycleView.setNestedScrollingEnabled(false);

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getItemCount(); i++) {
            View itemView = listAdapter.onCreateViewHolder(ListRecycleView, listAdapter.getItemViewType(i)).itemView;
            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += itemView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams layoutParams = ListRecycleView.getLayoutParams();
        layoutParams.height = totalHeight;
        ListRecycleView.setLayoutParams(layoutParams);

        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);


        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetFrag = new BottomSheetFrag();
                bottomSheetFrag.show(getSupportFragmentManager(),bottomSheetFrag.getTag());

            }
        });



        LinearLayout root = findViewById(R.id.rootMainScreen);

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });


        SearchBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // If the SearchView gains focus, hide the other view
                if (hasFocus) {
                    guide.setVisibility(View.GONE);
                }
            }
        });

        

        SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");

                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {

                List<ListModel> listModelsCopy = new ArrayList<>();
                for(ListModel listModel1 : listModels)
                {
                    if(listModel1.getName().toLowerCase().contains(newText.toLowerCase()))
                    {
                        listModelsCopy.add(listModel1);
                    }
                }

                listAdapter.setDataList(listModelsCopy);
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });



        SearchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onClose() {
                guide.setVisibility(View.VISIBLE);
                listAdapter.setDataList(listModels);
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });






        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0); // Scroll to top
            }
        }, 200); // Adjust the delay time as needed


    }


    @SuppressLint("SetTextI18n")
    private int CountAllReminds()
    {
        if(MyDb.listModelDao().getAllListModels() != null)
        {



            if(MyDb.reminderModelDao().getEveryReminders() != null)
            {
                int remindersSize = MyDb.reminderModelDao().getEveryReminders().size();
                if(remindersSize == 0)
                {
                    ContShow.setText("100");
                    ContBar.setProgress(100);
                }
                else
                {
                    int ticked = 0;
                    for(ReminderModel reminderModel : MyDb.reminderModelDao().getEveryReminders())
                    {
                        if(reminderModel.isCheckStatus())
                        {
                            ticked++;
                        }
                    }
                    int percent = (ticked * 100)/remindersSize;
                    ContShow.setText(String.valueOf(percent));
                    ContBar.setProgress(percent);
                }

            }
            else
            {
                ContShow.setText("0");
                ContBar.setProgress(0);
            }

            return MyDb.listModelDao().getAllListModels().size();

        }

        ContShow.setText("0");
        ContBar.setProgress(0);
        return 0;
    }


    private int CountScheduledReminds()
    {
        if(MyDb.reminderModelDao().getEveryReminders() != null)
        {
            int i = 0;
            int j = 0;
            for(ReminderModel reminderModel : MyDb.reminderModelDao().getEveryReminders())
            {
                if(!reminderModel.getTime().isEmpty())
                {
                    i++;
                    if(reminderModel.isCheckStatus())
                    {
                        j++;
                    }
                }
            }

            if(i!= 0)
            {
                int percent = (j*100)/i;
                Sch_bar.setProgress(percent);
                Sch_bar_percent.setText(String.valueOf(percent));
            }
            else
            {
                Sch_bar.setProgress(0);
                Sch_bar_percent.setText("0");
            }
            return i;
        }

        Sch_bar.setProgress(0);
        Sch_bar_percent.setText("0");
        return 0;
    }

    private int CountTodayReminds()
    {

        if(MyDb.reminderModelDao().getEveryReminders() != null)
        {
            int i = 0;
            int j = 0;
            for(ReminderModel reminderModel : MyDb.reminderModelDao().getEveryReminders())
            {
                if(reminderModel.getDate().equals(getCurrentDateInString()))
                {
                    i++;
                    if(reminderModel.isCheckStatus())
                    {
                        j++;
                    }
                }
            }

            if(i != 0)
            {
                int percent = (j*100)/i;
                today_bar.setProgress(percent);
                today_bar_percent.setText(String.valueOf(percent));
            }
            else
            {
                today_bar.setProgress(0);
                today_bar_percent.setText("0");
            }
            return i;
        }

        today_bar.setProgress(0);
        today_bar_percent.setText("0");
        return 0;
    }


    public String getCurrentDateInString() {
        // Create a SimpleDateFormat object with the desired format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("EEE d, MMMM yyyy");

        // Get the current date using Calendar
        Calendar calendar = Calendar.getInstance();

        // Return the formatted current date as a string
        return format.format(calendar.getTime());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDoneButtonClicked(String text, ImageView icon, int bg_iconColor) {


        ListModel listModel = new ListModel();
        listModel.setName(text);

        Bitmap bitmap = imageViewToBitmapWithPadding(icon,bg_iconColor);
        byte[] pixels = bitmapToByteArray(bitmap);

        listModel.setIcon(pixels);
        listModel.setIconColor(bg_iconColor);
        listModel.setRemindCount(0);

        MyDb.listModelDao().addListModel(listModel);

        listModels.clear();

        listModels.addAll(MyDb.listModelDao().getAllListModels());

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getItemCount(); i++) {
            View itemView = listAdapter.onCreateViewHolder(ListRecycleView, listAdapter.getItemViewType(i)).itemView;
            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += itemView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams layoutParams = ListRecycleView.getLayoutParams();
        layoutParams.height = totalHeight;
        ListRecycleView.setLayoutParams(layoutParams);


        listAdapter.notifyDataSetChanged();
        bottomSheetFrag.dismiss();
    }


    @Override
    public void onCancelButtonClicked() {
        bottomSheetFrag.dismiss();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void OnUpdate(ListModel listModel,ImageView icon) {

        Bitmap bitmap = imageViewToBitmapWithPadding(icon,listModel.getIconColor());
        byte[] pixels = bitmapToByteArray(bitmap);

        listModel.setIcon(pixels);


        MyDb.listModelDao().UpdateListModel(listModel);
        bottomSheetFrag.dismiss();
        listModels.clear();
        listModels.addAll(MyDb.listModelDao().getAllListModels());
        listAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(ListModel listModel) {
        startActivity(new Intent(AppMainScreen.this, ListRemindersActivity.class)
                .putExtra("ListName", listModel.getName())
                .putExtra("ListId",listModel.getId())
                .putExtra("ListColor",listModel.getIconColor()));
    }

    @Override
    public Pair<View, View> onLongClick() {

        doneToolbar.setVisibility(View.VISIBLE);
        EditToolbar.setVisibility(View.VISIBLE);
        return new Pair<>(doneToolbar,EditToolbar);
    }

    @Override
    public void UpdateListInfo(int pos) {
        bottomSheetFrag = BottomSheetFrag.newInstance(listModels.get(pos));
        bottomSheetFrag.show(getSupportFragmentManager(),bottomSheetFrag.getTag());
        isUpdate = true;
        curr_pos = pos;
    }

    @Override
    public View RetDeleteView() {

        DeleteToolbar.setVisibility(View.VISIBLE);
        return DeleteToolbar;
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void DeleteListModel(ListModel listModel) {
        int ListId = listModel.getId();
        MyDb.listModelDao().DeleteListModel(listModel);
        List<ReminderModel> reminderModels = MyDb.reminderModelDao().getAllReminders(ListId);

        for(ReminderModel reminderModel1 : reminderModels)
        {
            if(reminderModel1.getAlarm_time() != 0)
            {
                cancelAlarm(reminderModel1.getId());
            }
        }

        MyDb.reminderModelDao().deleteRemindsOfList(ListId);

        listModels.clear();

        listModels.addAll(MyDb.listModelDao().getAllListModels());

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getItemCount(); i++) {
            View itemView = listAdapter.onCreateViewHolder(ListRecycleView, listAdapter.getItemViewType(i)).itemView;
            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += itemView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams layoutParams = ListRecycleView.getLayoutParams();
        layoutParams.height = totalHeight;
        ListRecycleView.setLayoutParams(layoutParams);

        listAdapter.notifyDataSetChanged();

        today_count.setText(String.valueOf(CountTodayReminds()));
        Schedule_count.setText(String.valueOf(CountScheduledReminds()));
        AllCount.setText(String.valueOf(CountAllReminds()));


    }

    @Override
    public View dividerView() {
        divider.setVisibility(View.VISIBLE);
        return divider;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, Notifier.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

        // Cancel the alarm
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    public Bitmap imageViewToBitmapWithPadding(ImageView imageView, int backgroundColor) {
        if (imageView == null || imageView.getDrawable() == null) {
            return null;
        }

        Drawable drawable = imageView.getDrawable();
        Bitmap originalBitmap;

        if (drawable instanceof BitmapDrawable) {
            // If the drawable is a BitmapDrawable, get the Bitmap directly
            originalBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            // If the drawable is not a BitmapDrawable (e.g., VectorDrawable), create a new Bitmap and draw the drawable onto it
            originalBitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(originalBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        if (originalBitmap == null) {
            return null;
        }

        // Create a new Bitmap with the same properties as the original Bitmap
        Bitmap bitmapWithProperties = originalBitmap.copy(originalBitmap.getConfig(), true);

        // Create a canvas with the new Bitmap
        Canvas canvas = new Canvas(bitmapWithProperties);

        // Draw the background color onto the Bitmap
        canvas.drawColor(backgroundColor);

        // Draw the original drawable onto the Bitmap
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmapWithProperties;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle(); // Recycle the bitmap to free up memory

        return byteArray;

    }

}
