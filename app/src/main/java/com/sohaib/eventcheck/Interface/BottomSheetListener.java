package com.sohaib.eventcheck.Interface;

import android.widget.ImageView;

import com.sohaib.eventcheck.Model.ListModel;
import com.sohaib.eventcheck.Utility.Padding;

public interface BottomSheetListener {
    void onDoneButtonClicked(String text, ImageView icon, int bg_iconColor);
    void onCancelButtonClicked();
    void OnUpdate(ListModel listModel,ImageView icon);
}
