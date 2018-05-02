package com.example.vloboda.dynamicentityeditor.dynamic;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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


public class EditTextFactory extends ViewFactoryBase {

    protected LayoutInflater inflater;

    EditTextFactory(UICommonServices commonServices) {
        super(commonServices);
    }

    protected int getInputType() {
        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
    }

    private View getMainView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.dynamic_edit_text, null);
    }

    @Override
    public DynamicView getView(DynamicViewContainer container,
                               final FieldConfiguration configuration) {
        inflater = (LayoutInflater)container.getLayoutInflater();

        View mainView = getMainView(inflater);
        final TextInputLayout textLayout = mainView.findViewById(R.id.det_text_layout);
        textLayout.setHintAnimationEnabled(false);
        final EditText editText = mainView.findViewById(R.id.det_main_text);
        if (configuration.hint != null) {
            textLayout.setHint(configuration.hint);
        } else {
            textLayout.setHint(configuration.caption);
        }
        editText.setInputType(getInputType());
        editText.setTag(configuration.fieldCode);

        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_GO){
                editText.clearFocus();
                InputMethodManager imm = (InputMethodManager)inflater.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
            return false;
        });
        textLayout.setHintAnimationEnabled(true);

        final DynamicEditableViewBase dynamicView = new DynamicEditableViewBase(getLocationManager()) {

            @Override
            public View getView() {
                return mainView;
            }

            @Override
            protected FieldDto getCurrentValue() {
                return new FieldDto(configuration.fieldCode, editText.getText().toString());
            }

            @Override
            public void setValue(final FieldDto attribute) {
                setInitialValue(attribute);
                textLayout.setHintAnimationEnabled(false);
                editText.setText(attribute.getValue());
                textLayout.setHintAnimationEnabled(true);

                editText.setOnLongClickListener(v -> {
                    if (attribute.getChangeInfo() != null) {
                        getToaster().showLongMessage(attribute.getChangeInfo().toString());
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
                    textLayout.setHint(template.getCaption());
                }
            }
        };

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                dynamicView.rememberChangeLocation();
            }
        });

        return dynamicView;
    }
}

