package com.sohaib.eventcheck.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sohaib.eventcheck.Adapter.ReminderAdapter;
import com.sohaib.eventcheck.Interface.BottomSheetListener;
import com.sohaib.eventcheck.Interface.ReminderBottomSheetListener;
import com.sohaib.eventcheck.Model.ReminderModel;
import com.sohaib.eventcheck.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderBottomSheet extends BottomSheetDialogFragment {

    private ReminderBottomSheetListener mListener;
    boolean DateSwitchState = false;
    boolean TimeSwitchState = false;
    boolean FlagSwitchState = false;
    Calendar calendar = Calendar.getInstance();
    Calendar currentTimer;
    long TimeMillis = 0;


    String dateStr;
    String timeStr;


    // Debug
    private static final String TAG = "BR-TAG";


    public static ReminderBottomSheet newInstance(ReminderModel customObject) {
        ReminderBottomSheet fragment = new ReminderBottomSheet();

        if(customObject != null)
        {
            Bundle args = new Bundle();
            args.putSerializable("key_custom_object", customObject);
            fragment.setArguments(args);
        }

        return fragment;
    }


    public ReminderBottomSheet()
    {}

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.reminderbottomsheet,container,false);
        EditText TitleField = view.findViewById(R.id.TitleId);
        EditText NotesField = view.findViewById(R.id.NotesId);
        EditText UrlField = view.findViewById(R.id.UrlId);
        TextView Done = view.findViewById(R.id.Done1Id);
        Switch DateSwitch = view.findViewById(R.id.dateSwitchId);
        Switch TimeSwitch = view.findViewById(R.id.TimeSwitchId);
        Switch FlagSwitch = view.findViewById(R.id.flagSwitchId);
        DatePicker datePickerWidget = view.findViewById(R.id.datePickerWidgetId);
        TimePicker timePickerWidget = view.findViewById(R.id.TimePickerWidgetId);
        TextView dateDisplay = view.findViewById(R.id.DateDisplayId);
        TextView timeDisplay = view.findViewById(R.id.timeDisplayId);

        TextView dateText = view.findViewById(R.id.DateTextId);
        TextView timeText = view.findViewById(R.id.TimeTextId);
        RelativeLayout AddImage = view.findViewById(R.id.AddImageId);

        TextView cancel = view.findViewById(R.id.cancel1Id);


        timePickerWidget.setMinimumWidth(5);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.CancelClick();
            }
        });




        Bundle args = getArguments();

        // Update part
        if (args != null) {
            ReminderModel customObject = (ReminderModel) args.getSerializable("key_custom_object");
            TitleField.setText(customObject.getTitle());
            NotesField.setText(customObject.getNote());
            UrlField.setText(customObject.getUrl());


            if(!customObject.getDate().isEmpty())
            {
                dateStr = customObject.getDate();
                parseDateStr(dateStr);
                datePickerWidget.init(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        (DateView, year, month, dayOfMonth) -> {

                            calendar.set(year,month,dayOfMonth);
                            dateStr = formatDate(year,month,dayOfMonth);
                            dateDisplay.setText(dateStr);
                        }
                );

                toggleTextViewSize(dateText);
                dateDisplay.setText(customObject.getDate());
                DateSwitch.setChecked(true);
                dateDisplay.setVisibility(View.VISIBLE);
                datePickerWidget.setVisibility(View.VISIBLE);
                TimeMillis = calendar.getTimeInMillis();
                DateSwitchState = true;

            }

            if(!customObject.getTime().isEmpty())
            {
                timeStr = customObject.getTime();
                parseTimeStr(timeStr);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    timePickerWidget.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    timePickerWidget.setMinute(calendar.get(Calendar.MINUTE));
                }

                timePickerWidget.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        timeStr = formatTime(hourOfDay,minute);
                        timeDisplay.setText(timeStr);

                    }
                });



                toggleTextViewSize(timeText);
                timeDisplay.setText(customObject.getTime());
                TimeSwitch.setChecked(true);
                timePickerWidget.setVisibility(View.VISIBLE);
                timeDisplay.setVisibility(View.VISIBLE);
                TimeSwitchState = true;
            }


            FlagSwitch.setChecked(customObject.isFlag()); // Flag View
            FlagSwitchState = customObject.isFlag(); // bool Var

            Done.setEnabled(true);
            Done.setTextColor(Color.rgb(25,118,210));

            // Done 1
            Done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Notes = NotesField.getText().toString();
                    String url = UrlField.getText().toString();

                    customObject.setNote(Notes);
                    customObject.setUrl(url);

                    if(DateSwitchState)
                    {
                        customObject.setDate(dateStr);
                        parseDateStr(dateStr);

                        if(TimeSwitchState)
                        {
                            customObject.setTime(timeStr);
                            parseTimeStr(timeStr);
                        }
                        else
                        {
                            customObject.setTime("");
                            Calendar timeCalendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

                        }
                        TimeMillis = calendar.getTimeInMillis();
                        currentTimer = Calendar.getInstance();
                        if(currentTimer.getTimeInMillis() - 500 <= TimeMillis)
                        {
                            customObject.setAlarm_time(TimeMillis);
                        }
                        else
                        {
                            customObject.setAlarm_time(0);
                        }
                    }
                    else
                    {
                        customObject.setDate("");
                        customObject.setTime("");
                        customObject.setAlarm_time(0);
                    }


                    customObject.setFlag(FlagSwitchState);

                    mListener.DoneClick(customObject);
                    assert getArguments() != null;
                    getArguments().remove("key_custom_object");
                }
            });
        }
        else
        {
            // Initial time when Insert
            setDefaultDateTime(dateDisplay, timeDisplay);
        }




        // add new part
        TitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isInputEmpty = TextUtils.isEmpty(s);
                Done.setEnabled(!isInputEmpty);


                if(!isInputEmpty)
                {
                    Done.setTextColor(Color.rgb(25,118,210));
                    // Done 2
                    Done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Update part
                            if(args != null)
                            {
                                ReminderModel customObject = (ReminderModel) args.getSerializable("key_custom_object");
                                customObject.setTitle(s.toString());
                                String Notes = NotesField.getText().toString();
                                String url = UrlField.getText().toString();

                                customObject.setNote(Notes);
                                customObject.setUrl(url);

                                if(DateSwitchState)
                                {

                                    customObject.setDate(dateStr);
                                    parseDateStr(dateStr);


                                    if(TimeSwitchState)
                                    {
                                        customObject.setTime(timeStr);
                                        parseTimeStr(timeStr);
                                    }
                                    else
                                    {
                                        customObject.setTime("");
                                        Calendar timeCalendar = Calendar.getInstance();
                                        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                                        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
                                    }
                                    TimeMillis = calendar.getTimeInMillis();
                                    currentTimer = Calendar.getInstance();
                                    if(currentTimer.getTimeInMillis() - 500 <= TimeMillis)
                                    {
                                        customObject.setAlarm_time(TimeMillis);
                                    }
                                    else
                                    {
                                        customObject.setAlarm_time(0);
                                    }



                                }
                                else
                                {
                                    customObject.setDate("");
                                    customObject.setTime("");
                                    customObject.setAlarm_time(0);
                                }

                                customObject.setFlag(FlagSwitchState);

                                mListener.DoneClick(customObject);

                            }
                            else
                            {
                                // Insert part
                                ReminderModel reminderModel = new ReminderModel();
                                reminderModel.setTitle(s.toString());
                                reminderModel.setNote(NotesField.getText().toString());
                                reminderModel.setUrl(UrlField.getText().toString());
                                if(DateSwitchState)
                                {
                                    TimeMillis = calendar.getTimeInMillis();
                                    currentTimer = Calendar.getInstance();
                                    if(currentTimer.getTimeInMillis() - 500<= TimeMillis)
                                    {
                                        reminderModel.setAlarm_time(TimeMillis);
                                    }
                                    else
                                    {
                                        reminderModel.setAlarm_time(0);
                                    }
                                    reminderModel.setDate(dateStr);
                                    if(TimeSwitchState)
                                    {
                                        TimeMillis = calendar.getTimeInMillis();
                                        currentTimer = Calendar.getInstance();

                                        if(currentTimer.getTimeInMillis() - 500 <= TimeMillis)
                                        {
                                            Log.d(TAG, "onClick: " + String.valueOf(currentTimer.getTimeInMillis() - 500) + " " + TimeMillis);
                                            reminderModel.setAlarm_time(TimeMillis);
                                        }
                                        else
                                        {
                                            reminderModel.setAlarm_time(0);
                                        }

                                        reminderModel.setTime(timeStr);
                                    }
                                    else
                                    {
                                        reminderModel.setTime("");
                                    }
                                }
                                else
                                {
                                    reminderModel.setDate("");
                                    reminderModel.setTime("");
                                    reminderModel.setAlarm_time(0);
                                }


                                reminderModel.setFlag(FlagSwitchState);
                                reminderModel.setCheckStatus(false);
                                mListener.DoneClick(reminderModel);
                            }
                        }
                    });
                }
                else
                {
                    Done.setTextColor(Color.rgb(128,128,128));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        DateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSwitchState = DateSwitch.isChecked();

                // Date On
                if(DateSwitchState)
                {
                    datePickerWidget.setVisibility(View.VISIBLE);
                    dateDisplay.setVisibility(View.VISIBLE);
                    toggleTextViewSize(dateText);

                    setDefaultDateTime(dateDisplay,timeDisplay);
                    calendar =Calendar.getInstance();

                    dateStr = formatDate(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));


                    datePickerWidget.init(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            (DateView, year, month, dayOfMonth) -> {
                                calendar.set(year,month,dayOfMonth);
                                dateStr = formatDate(year,month,dayOfMonth);
                                dateDisplay.setText(dateStr);
                            }
                    );

                }
                else
                {
                    datePickerWidget.setVisibility(View.GONE);
                    dateDisplay.setVisibility(View.GONE);
                    toggleTextViewSize(dateText);
                    dateStr = "";
                    if(TimeSwitchState)
                    {
                        TimeSwitch.setChecked(false);
                        TimeSwitchState = false;
                        timePickerWidget.setVisibility(View.GONE);
                        timeDisplay.setVisibility(View.GONE);
                        toggleTextViewSize(timeText);
                        timeStr = "";
                    }

                }
            }
        });

        TimeSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimeSwitchState = TimeSwitch.isChecked();

                //Time On
                if(TimeSwitchState)
                {
                    if(!DateSwitchState)
                    {
                        datePickerWidget.setVisibility(View.VISIBLE);
                        dateDisplay.setVisibility(View.VISIBLE);
                        toggleTextViewSize(dateText);
                        DateSwitchState = true;
                        DateSwitch.setChecked(true);
                        calendar = Calendar.getInstance();
                        dateStr = formatDate( calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));

                        datePickerWidget.init(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                (DateView, year, month, dayOfMonth) -> {
                                    calendar.set(year,month,dayOfMonth);
                                    dateStr = formatDate(year,month,dayOfMonth);
                                    dateDisplay.setText(dateStr);
                                }
                        );
                    }


                    // Time
                    timePickerWidget.setVisibility(View.VISIBLE);
                    timeDisplay.setVisibility(View.VISIBLE);
                    toggleTextViewSize(timeText);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        Calendar tempCalender = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,tempCalender.get(Calendar.HOUR_OF_DAY));
                        calendar.set(Calendar.MINUTE,tempCalender.get(Calendar.MINUTE));

                        timePickerWidget.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                        timePickerWidget.setMinute(calendar.get(Calendar.MINUTE));
                    }

                    timeStr = formatTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
                    timeDisplay.setText(timeStr);

                    timePickerWidget.setMinimumWidth(5);

                    timePickerWidget.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                            timeStr = formatTime(hourOfDay,minute);
                            timeDisplay.setText(timeStr);

                        }
                    });
                }
                else
                {
                    timePickerWidget.setVisibility(View.GONE);
                    timeDisplay.setVisibility(View.GONE);
                    toggleTextViewSize(timeText);
                    Calendar calendar1 = Calendar.getInstance(); // Get the current date and time

                    int currentHour = calendar1.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = calendar1.get(Calendar.MINUTE);

                    calendar.set(Calendar.HOUR_OF_DAY, currentHour);
                    calendar.set(Calendar.MINUTE, currentMinute);
                }
            }
        });

        FlagSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlagSwitchState = FlagSwitch.isChecked();
            }
        });


        return view;
    }

    // Below are utility functions

    private void parseTimeStr(String str)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        Date time;


        Calendar timeCalendar = Calendar.getInstance();

        try {

            time = timeFormat.parse(timeStr);
            assert time != null;
            timeCalendar.setTime(time);



        } catch (ParseException e) {
            // Handle the exception if the date/time string is not in the correct format.
            e.printStackTrace();
        }


        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
    }

    private void parseDateStr(String str)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("EEE d, MMMM yyyy");
        Date date;
        try {
            date = format.parse(dateStr);
            assert date != null;
            calendar.setTime(date);



        } catch (ParseException e) {
            // Handle the exception if the date string is not in the correct format.
            e.printStackTrace();
        }
    }

    private String formatDate(int year, int month, int day) {
        String[] monthNames = new String[] {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };



        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d, MMMM yyyy", Locale.US);
        return dateFormat.format(calendar.getTime());
    }

    private String formatTime(int hour, int minute) {
        // Create a Calendar instance to get AM/PM

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // Format the time using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        return sdf.format(calendar.getTime());
    }

    private void toggleTextViewSize(TextView myTextView) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) myTextView.getLayoutParams();
        if (layoutParams.height == LinearLayout.LayoutParams.MATCH_PARENT) {
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        }
        myTextView.setLayoutParams(layoutParams);
    }

    private void setDefaultDateTime(TextView dateTextView, TextView timeTextView) {
        // Set default values to date and time TextViews
        //calendar = Calendar.getInstance();
        calendar = Calendar.getInstance();


        // Format the date and time using SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d, MMMM yyyy", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

        dateStr = dateFormat.format(calendar.getTime());
        timeStr = timeFormat.format(calendar.getTime());


        TimeMillis = calendar.getTimeInMillis();

        dateTextView.setText(dateStr);
        timeTextView.setText(timeStr);
    }

    private void showPopup(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.customdialog, null);

        Button btnChooseFromGallery = popupView.findViewById(R.id.btnChooseFromGallery);
        Button btnTakePhoto = popupView.findViewById(R.id.btnTakePhoto);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Set click listener for "Choose from Gallery" button
        btnChooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Choose from Gallery" option here
                popupWindow.dismiss(); // Dismiss the pop-up after handling the option
            }
        });

        // Set click listener for "Take a Photo" button
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Take a Photo" option here
                popupWindow.dismiss(); // Dismiss the pop-up after handling the option
            }
        });

        // Calculate the screen height
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;

        // Calculate the height of the pop-up
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();

        // Calculate the y-offset to show the pop-up above the anchor view
        int[] location = new int[2];
        anchorView.getLocationInWindow(location);
        int yOffsetAbove = location[1] - popupHeight;

        // Calculate the y-offset to show the pop-up below the anchor view
        int yOffsetBelow = location[1] + anchorView.getHeight();

        // Show the pop-up above the anchor view if there is enough space; otherwise, show it below
        if (yOffsetAbove > 0) {
            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], yOffsetAbove);
        } else {
            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], yOffsetBelow);
        }

        // Set elevation to create a floating effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(8f); // You can adjust the elevation value as needed
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ReminderBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ReminderBottomSheetListener");
        }
    }

}
