package com.example.vloboda.dynamicentityeditor.dynamic;

import android.util.SparseArray;

import com.example.bl.DynamicFieldsFactory;
import com.example.bl.ui.DymamicViewGroup;
import com.example.bl.ui.DynamicView;
import com.example.bl.ui.ViewFactory;
import com.example.bl.ui.DynamicViewContainer;
import com.example.bl.ui.UICommonServices;
import com.example.model.FieldConfiguration;
import com.example.model.EditObjectTemplateDto;

public class DynamicAttributesFactoryImpl implements DynamicFieldsFactory {
    private SparseArray<ViewFactory> map = new SparseArray<>(15);

    public DynamicAttributesFactoryImpl(UICommonServices commonServices) {
        map.put(FieldConfiguration.CONTROL_TYPE_TEXT, new EditTextFactory(commonServices));
        map.put(FieldConfiguration.CONTROL_TYPE_BOOLEAN, new EditSwitchBoxFactory(commonServices));
        map.put(FieldConfiguration.CONTROL_TYPE_NUMBER, new EditNumberFactory(commonServices));
        map.put(FieldConfiguration.CONTROL_TYPE_RADIO_LIST, new EditRadioGroupListFactory(commonServices));
    }

    @Override
    public DynamicView getView(DynamicViewContainer container, FieldConfiguration config, EditObjectTemplateDto.ItemDto templateItem) {
        int controlType = templateItem != null && templateItem.getEditControlType() != 0 ? templateItem.getEditControlType() : config.controlType;

        ViewFactory factory = map.get(controlType);
        if (factory == null) {
            throw new RuntimeException("Can't find factory for " + controlType);
        }
        return factory.getView(container, config);
    }

    @Override
    public DymamicViewGroup getViewGroup(DynamicViewContainer container, String groupName) {
        return new ViewGroupFactory().getView(container, groupName);
    }
}
