package com.example.vloboda.dynamicentityeditor;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.bl.ui.DynamicToaster;

public class DynamicToasterImpl implements DynamicToaster {
    private Context context;

    DynamicToasterImpl(Context context) {
        this.context = context;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this.context, error, Toast.LENGTH_LONG).show();
        Log.e("Y", error);
    }

    @Override
    public void showError(String error, Throwable ex) {
        showError(error);

        ex.printStackTrace();
    }

    @Override
    public void showLongMessage(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
    }
}
