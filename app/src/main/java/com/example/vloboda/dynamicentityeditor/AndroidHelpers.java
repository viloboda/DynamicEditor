package com.example.vloboda.dynamicentityeditor;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.widget.ToggleButton;

public final class AndroidHelpers {
    public static int getPixelsFromDp(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
