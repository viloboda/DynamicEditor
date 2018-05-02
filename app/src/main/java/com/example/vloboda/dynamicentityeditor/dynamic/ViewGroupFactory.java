package com.example.vloboda.dynamicentityeditor.dynamic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.bl.ui.DymamicViewGroup;
import com.example.bl.ui.DynamicView;
import com.example.bl.ui.DynamicViewContainer;
import com.example.model.EditObjectTemplateDto;
import com.example.model.FieldDto;
import com.example.model.FieldSetting;
import com.example.model.StringHelperKt;

public class ViewGroupFactory {

    ViewGroupFactory() {
    }

    public DymamicViewGroup getView(final DynamicViewContainer container,
                                    final String groupName) {

        final LayoutInflater inflater = (LayoutInflater)container.getLayoutInflater();
        final DynamicViewGroupControl dvc = new DynamicViewGroupControl(inflater.getContext());
        dvc.setCaption(groupName);

        final LinearLayout mainLayout = new LinearLayout(inflater.getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mainLayout.setLayoutParams(llParams);

        dvc.addDynamicView(mainLayout);

        return new DymamicViewGroup() {
            @Override
            public void addView(DynamicView view) {
                View androidView = (View) view.getView();
                androidView.setTag(view);

                view.setViewGroup(this);

                if (androidView.getParent() != null) {
                    ((ViewGroup) androidView.getParent()).removeView(androidView);
                }

                mainLayout.addView(androidView, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            }

            @Override
            public void removeAllViews() {
                mainLayout.removeAllViews();
            }

            @Override
            public boolean isEmpty() {
                return mainLayout.getChildCount() == 0;
            }

            @Override
            public void expand() {
                dvc.expand();
            }

            @Override
            public Object getView() {
                return dvc;
            }

            @Override
            public FieldDto getValue() {
                return null;
            }

            @Override
            public void setValue(FieldDto attribute) {
            }

            @Override
            public boolean hasChanges() {
                return false;
            }

            @Override
            public String getAttributeId() {
                return FieldSetting.ATTRIBUTE_START_GROUP;
            }

            @Override
            public void setTemplate(EditObjectTemplateDto.ItemDto templateItem) {
                dvc.setTemplate(templateItem);

                if (!StringHelperKt.isNullOrEmpty(templateItem.getLayoutOrientation()) && templateItem.getLayoutOrientation().equals("horizontal")) {
                    mainLayout.setOrientation(LinearLayout.HORIZONTAL);
                }
            }

            @Override
            public void refresh() {
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public void showWarning(String warning) {
            }

            @Override
            public void setViewGroup(DymamicViewGroup viewGroup) {
            }

            @Override
            public DymamicViewGroup getViewGroup() {
                return null;
            }
        };
    }
}
