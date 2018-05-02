package com.example.vloboda.dynamicentityeditor.dynamic;

import android.text.InputType;

import com.example.bl.ui.UICommonServices;

public class EditNumberFactory extends EditTextFactory {
    EditNumberFactory(UICommonServices commonServices) {
        super(commonServices);
    }

    @Override
    protected int getInputType() {
        return InputType.TYPE_CLASS_NUMBER;
    }
}

