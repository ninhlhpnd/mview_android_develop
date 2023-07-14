package com.example.sensorcollection.model;

import android.view.View;
import android.widget.PopupWindow;

public class CustomPopupwindow extends PopupWindow {
    public CustomPopupwindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        anchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
