package com.example.vloboda.dynamicentityeditor.dynamic;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.bl.ui.DynamicEditableViewBase;
import com.example.bl.ui.DynamicView;
import com.example.bl.ui.ViewFactoryBase;
import com.example.bl.ui.DynamicViewContainer;
import com.example.bl.ui.UICommonServices;
import com.example.model.FieldConfiguration;
import com.example.model.EditObjectTemplateDto;
import com.example.model.FieldDto;
import com.example.model.StringHelperKt;
import com.example.vloboda.dynamicentityeditor.R;

public class EditSwitchBoxFactory extends ViewFactoryBase {

    EditSwitchBoxFactory(UICommonServices commonServices) {
        super(commonServices);
    }

    @Override
    public DynamicView getView(DynamicViewContainer container,
                               final FieldConfiguration configuration) {

        LayoutInflater inflater = (LayoutInflater)container.getLayoutInflater();

        final View mainView = inflater.inflate(R.layout.dynamic_switch_edit, null);
        mainView.setTag(configuration.attributeId);
        mainView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        Switch checkBoxValue = mainView.findViewById(R.id.dswe_value);
        checkBoxValue.setText(configuration.caption);

        final DynamicEditableViewBase dynamicView = new DynamicEditableViewBase(getLocationManager()) {

            @Override
            public View getView() {
                return mainView;
            }

            @Override
            protected FieldDto getCurrentValue() {
                String value = checkBoxValue.isChecked() ? "True" : null;
                return new FieldDto(configuration.attributeId, value);
            }

            @Override
            public void setValue(final FieldDto attribute) {
                setInitialValue(attribute);

                checkBoxValue.setChecked(Boolean.parseBoolean(getInitialValue().getValue()));
            }

            @Override
            public String getAttributeId() {
                return configuration.attributeId;
            }

            @Override
            public void setTemplate(EditObjectTemplateDto.ItemDto template) {
                if (!StringHelperKt.isNullOrEmpty(template.getCaption())) {
                    checkBoxValue.setText(template.getCaption());
                }
            }

            @Override
            public void refresh() {
            }
        };

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            dynamicView.rememberChangeLocation();
        };
        checkBoxValue.setOnCheckedChangeListener(listener);

        return dynamicView;
    }
}
