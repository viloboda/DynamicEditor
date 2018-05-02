package com.example.vloboda.dynamicentityeditor;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.bl.ui.DynamicAlertDialog;
import com.example.bl.ui.YoulaOnClickListener;

public class DynamicAlertDialogImpl extends Dialog implements DynamicAlertDialog {

    private String message;
    private String leftButtonText;
    private YoulaOnClickListener onLeftButtonClick;
    private String rightButtonText;
    private YoulaOnClickListener onRightButtonClick;
    private TextView vRightBtn;
    private TextView vText;

    public DynamicAlertDialogImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog);

        vText = findViewById(R.id.ad_text);
        vText.setText(message);

        TextView vLeftBtn = findViewById(R.id.ad_button_left);
        vLeftBtn.setText(leftButtonText);
        if (onLeftButtonClick != null) {
            vLeftBtn.setOnClickListener(v -> onLeftButtonClick.onClick());
        } else {
            vLeftBtn.setVisibility(View.GONE);
        }

        vRightBtn = findViewById(R.id.ad_button_right);
        vRightBtn.setText(rightButtonText);
        if (onRightButtonClick != null) {
            vRightBtn.setOnClickListener(v -> onRightButtonClick.onClick());
        } else {
            vRightBtn.setVisibility(View.GONE);
        }

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, AndroidHelpers.getPixelsFromDp(getContext(), 24));
        this.getWindow().setBackgroundDrawable(inset);

        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showDialog(String message, String leftButtonText, YoulaOnClickListener onLeftButtonClick, String rightButtonText, YoulaOnClickListener onRightButtonClick) {

        this.message = message;
        this.leftButtonText = leftButtonText;
        this.onLeftButtonClick = onLeftButtonClick;
        this.rightButtonText = rightButtonText;
        this.onRightButtonClick = onRightButtonClick;

        show();
    }

    @Override
    public void showDialog(String message, String rightButtonText, YoulaOnClickListener onRightButtonClick) {
        showDialog(message, "", null, rightButtonText, onRightButtonClick);
    }

    public void showDialog(int message, int leftButtonText, YoulaOnClickListener onLeftButtonClick, int rightButtonText, YoulaOnClickListener onRightButtonClick) {
        Resources res = getContext().getResources();
        showDialog(res.getString(message), res.getString(leftButtonText), onLeftButtonClick, res.getString(rightButtonText), onRightButtonClick);
    }

    public void showDialog(int message, int rightButtonText, YoulaOnClickListener onRightButtonClick) {
        Resources res = getContext().getResources();
        showDialog(res.getString(message), "", null, res.getString(rightButtonText), onRightButtonClick);
    }

    public void enableRightButton(boolean enable) {
        vRightBtn.setEnabled(enable);
        if (!enable) {
            vRightBtn.setTextColor(getContext().getResources().getColor(R.color.seashell));
        } else {
            vRightBtn.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void setMessage(String message) {
        vText.setText(message);
    }
}

