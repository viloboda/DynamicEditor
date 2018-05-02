package com.example.vloboda.dynamicentityeditor.controls;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.model.AttributeValue;
import com.example.vloboda.dynamicentityeditor.R;

class RadioButtonViewHolder extends RecyclerView.ViewHolder {
    private RadioButtonViewHolder(View itemView) {
        super(itemView);
    }

    public static RadioButtonViewHolder createView(LayoutInflater inflater, ViewGroup parent) {
        return new RadioButtonViewHolder(inflater.inflate(R.layout.dynamic_radio_edit, parent, false));
    }

    void init(AttributeValue value) {
        RadioButton radioButton = (RadioButton) itemView;
        radioButton.setText(value.caption);
        radioButton.setTag(value.id);

        if (value.bold) {
            radioButton.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    void unCheck() {
        RadioButton radioButton = (RadioButton) itemView;
        radioButton.setChecked(false);
    }

    void check() {
        RadioButton radioButton = (RadioButton) itemView;
        radioButton.setChecked(true);
    }
}
