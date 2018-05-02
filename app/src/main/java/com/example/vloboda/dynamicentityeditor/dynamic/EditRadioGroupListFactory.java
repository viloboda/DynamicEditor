package com.example.vloboda.dynamicentityeditor.dynamic;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.bl.ui.DynamicEditableViewBase;
import com.example.bl.ui.DynamicView;
import com.example.bl.ui.ViewFactoryBase;
import com.example.bl.ui.DynamicViewContainer;
import com.example.bl.ui.UICommonServices;
import com.example.model.FieldConfiguration;
import com.example.model.AttributeValue;
import com.example.model.EditObjectTemplateDto;
import com.example.model.FieldDto;
import com.example.model.ListFieldConfiguration;
import com.example.model.StringHelperKt;
import com.example.vloboda.dynamicentityeditor.R;
import com.example.vloboda.dynamicentityeditor.controls.RadioButtonsValueAdapter;
import com.example.vloboda.dynamicentityeditor.controls.SingleSpinnerSearch;

import java.util.List;

class EditRadioGroupListFactory extends ViewFactoryBase {

    EditRadioGroupListFactory(UICommonServices commonServices) {
        super(commonServices);
    }

    @Override
    public DynamicView getView(DynamicViewContainer container, final FieldConfiguration configuration) {
        LayoutInflater inflater = (LayoutInflater) container.getLayoutInflater();
        final View listView = inflater.inflate(R.layout.dynamic_list_edit, null);
        listView.setTag(configuration.fieldCode);

        final List<AttributeValue> values = ((ListFieldConfiguration) configuration).values;
        final RadioButtonsValueAdapter adapter = new RadioButtonsValueAdapter(inflater, values);

        TextView vCaption = listView.findViewById(R.id.dle_caption);
        vCaption.setText(configuration.caption);

        final SingleSpinnerSearch spinner = listView.findViewById(R.id.dvl_spinner);
        spinner.setAdapter(adapter);
        if (configuration.hint != null) {
            spinner.setHint(inflater.getContext().getResources().getString(R.string.not_set));
            spinner.setSpinnerTitle(configuration.hint);
        } else {
            spinner.setHint(inflater.getContext().getResources().getString(R.string.not_set));
            spinner.setSpinnerTitle(configuration.caption);
        }

        final DynamicEditableViewBase dynamicView = new DynamicEditableViewBase(getLocationManager()) {

            @Override
            public View getView() {
                return listView;
            }

            @Override
            protected FieldDto getCurrentValue() {
                return new FieldDto(configuration.fieldCode, adapter.getValue());
            }

            @Override
            public void setValue(final FieldDto attribute) {
                setInitialValue(attribute);
                adapter.setValue(attribute.getReferenceValues());
                spinner.updateText();

                listView.setOnLongClickListener(v -> {
                    if (attribute.getChangeInfo() == null) {
                        if (attribute.getChangeInfo() != null) {
                            getToaster().showLongMessage(attribute.getChangeInfo().toString());
                        }
                    }
                    return true;
                });

            }

            @Override
            public String getFieldCode() {
                return configuration.fieldCode;
            }

            @Override
            public void setTemplate(EditObjectTemplateDto.ItemDto template) {
                if (!StringHelperKt.isNullOrEmpty(template.getCaption())) {
                    vCaption.setText(template.getCaption());
                }
            }
        };

        adapter.setOnChangeListener((buttonView, isChecked) -> dynamicView.rememberChangeLocation());

        return dynamicView;
    }
}
