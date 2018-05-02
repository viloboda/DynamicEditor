package com.example.vloboda.dynamicentityeditor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.model.SimpleDto;
import com.example.model.StringHelperKt;

public class ObjectSimpleView extends RecyclerView.ViewHolder {

    public static ObjectSimpleView createView(ViewGroup parent, LayoutInflater inflater, SimpleDto simpleDto)
    {
        return new ObjectSimpleView(inflater.inflate(R.layout.object_simple_view, parent, false), simpleDto);
    }

    private ObjectSimpleView(View itemView, SimpleDto dto) {
        super(itemView);
        initView(dto);
    }

    public void initView(SimpleDto simpleDto) {
        TextView vName = this.itemView.findViewById(R.id.osv_name);
        TextView vDescription1 = this.itemView.findViewById(R.id.osv_description1);
        TextView vDescription2 = this.itemView.findViewById(R.id.osv_description2);

        vDescription1.setVisibility(View.VISIBLE);
        vDescription2.setVisibility(View.VISIBLE);

        this.itemView.setTag(simpleDto);
        vName.setText(simpleDto.getName());

        setDescription(vDescription1, simpleDto.getDescription());
        setDescription(vDescription2, simpleDto.getDescription2());
    }

    private void setDescription(TextView vDescription, String description) {
        if (StringHelperKt.isNullOrEmpty(description)) {
            vDescription.setVisibility(View.GONE);
        } else {
            vDescription.setText(description);
        }
    }
}
