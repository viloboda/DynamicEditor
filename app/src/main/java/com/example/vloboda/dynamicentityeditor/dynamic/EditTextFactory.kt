package com.example.vloboda.dynamicentityeditor.dynamic

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import com.example.bl.ui.DynamicEditableViewBase
import com.example.bl.ui.DynamicView
import com.example.bl.ui.ViewFactoryBase
import com.example.bl.ui.DynamicViewContainer
import com.example.bl.ui.UICommonServices
import com.example.model.FieldConfiguration
import com.example.model.EditObjectTemplateDto
import com.example.model.FieldDto
import com.example.model.*
import com.example.vloboda.dynamicentityeditor.R

open class EditTextFactory internal constructor(commonServices: UICommonServices) : ViewFactoryBase(commonServices) {

    protected lateinit var inflater: LayoutInflater

    protected open val inputType: Int
        get() = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

    private fun getMainView(inflater: LayoutInflater): View {
        return inflater.inflate(R.layout.dynamic_edit_text, null)
    }

    override fun getView(container: DynamicViewContainer,
                         configuration: FieldConfiguration): DynamicView {
        inflater = container.layoutInflater as LayoutInflater

        val mainView = getMainView(inflater)
        val textLayout = mainView.findViewById<TextInputLayout>(R.id.det_text_layout)
        textLayout.isHintAnimationEnabled = false
        val editText = mainView.findViewById<EditText>(R.id.det_main_text)
        if (configuration.hint != null) {
            textLayout.hint = configuration.hint
        } else {
            textLayout.hint = configuration.caption
        }
        editText.inputType = inputType
        editText.tag = configuration.fieldCode

        editText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                editText.clearFocus()
                val imm = inflater.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            }
            false
        }
        textLayout.isHintAnimationEnabled = true

        val dynamicView = object : DynamicEditableViewBase(locationManager) {

            override val view: View
                get() = mainView

            override val currentValue: FieldDto
                get() = FieldDto(configuration.fieldCode, editText.text.toString())

            override val fieldCode: String
                get() = configuration.fieldCode

            override fun setValue(value: FieldDto) {
                initialValue = value
                textLayout.isHintAnimationEnabled = false
                editText.setText(value.value)
                textLayout.isHintAnimationEnabled = true

                editText.setOnLongClickListener {
                    if (value.changeInfo != null) {
                        toaster.showLongMessage(value.changeInfo.toString())
                    }
                    true
                }
            }

            override fun setTemplate(templateItem: EditObjectTemplateDto.ItemDto) {
                if (!templateItem.Caption.isNullOrEmpty()) {
                    textLayout.hint = templateItem.Caption
                }
            }
        }

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                dynamicView.rememberChangeLocation()
            }
        }

        return dynamicView
    }
}

