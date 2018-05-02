package com.example.vloboda.dynamicentityeditor.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bl.ui.YoulaOnItemSelectedListener;
import com.example.model.AttributeValue;
import com.example.vloboda.dynamicentityeditor.AndroidHelpers;
import com.example.vloboda.dynamicentityeditor.R;

public class SingleSpinnerSearch extends FrameLayout {
    private boolean showSearchButton = true;
    private boolean showNotSetButton = true;
    private String defaultText;
    protected String spinnerTitle = "";
    protected RadioButtonsValueAdapter adapter;
    protected LayoutInflater inflater;
    protected TextView vText;
    private YoulaOnItemSelectedListener itemSelectedListener;
    protected ImageView vArrow;
    private boolean readonly;

    public SingleSpinnerSearch(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflater = LayoutInflater.from(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleSpinnerSearch);
        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SingleSpinnerSearch_hintText) {
                defaultText = a.getString(attr);
                continue;
            }

            if (attr == R.styleable.SingleSpinnerSearch_popupTitle) {
                spinnerTitle = a.getString(attr);
                continue;
            }

            if (attr == R.styleable.SingleSpinnerSearch_show_search_button) {
                showSearchButton = a.getBoolean(attr, true);
                continue;
            }

            if (attr == R.styleable.SingleSpinnerSearch_show_not_set_button) {
                showNotSetButton = a.getBoolean(attr, true);
                continue;
            }
        }

        a.recycle();

        init();
    }

    protected void init() {
        inflater.inflate(R.layout.spinner_layout, this, true);
        vText = findViewById(R.id.sl_text);
        vArrow = findViewById(R.id.sl_arrow);

        if (getBackground() != null) {
            vArrow.setImageDrawable(getBackground());
        }
    }

    public void setAdapter(RadioButtonsValueAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setFocusable(false);
        setClickable(true);

        if (vText != null) {
            vText.setHint(defaultText);
        }
        configureOnClickListener();
    }

    private void configureOnClickListener() {
        OnClickListener listener = view2 -> {
            if (readonly) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            View dialogView = inflater.inflate(R.layout.single_spinner_view, null);
            builder.setView(dialogView);

            final TextView vTitle = dialogView.findViewById(R.id.ssv_title);
            vTitle.setText(spinnerTitle);

            final RecyclerView rv = dialogView.findViewById(R.id.ssv_list);
            rv.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(rv.getContext());
            rv.setLayoutManager(llm);
            DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            itemDecorator.setDrawable(getContext().getDrawable(R.drawable.rv_space));
            rv.addItemDecoration(itemDecorator);

            EditText editText = dialogView.findViewById(R.id.ssv_search_text);
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if(actionId != EditorInfo.IME_ACTION_GO) {
                    return false;
                }

                InputMethodManager imm = (InputMethodManager) inflater.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                return true;
            });

            final View searchButton = dialogView.findViewById(R.id.ssv_search_button);
            searchButton.setOnClickListener(v -> {
                vTitle.setVisibility(GONE);
                editText.setVisibility(VISIBLE);
                searchButton.setVisibility(GONE);

                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) inflater.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            });
            if (!showSearchButton) {
                searchButton.setVisibility(GONE);
            }

            builder.setPositiveButton(R.string.ready_button, (dialog, which) -> {
                onOkClick(dialog, editText);
            });

            if (showNotSetButton) {
                builder.setNegativeButton(R.string.not_set, (dialog, which) -> {
                    adapter.setValue(null);
                    vText.setText("");
                    adapter.setOnFilteredListener(null);
                    editText.setText("");

                    if (itemSelectedListener != null) {
                        itemSelectedListener.onItemSelected(adapter.getSelectedItem());
                    }
                });
            }

            configureDialog(builder);

            AlertDialog dialog = builder.create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = AndroidHelpers.getPixelsFromDp(getContext(), 488);
            dialog.getWindow().setAttributes(lp);

            final View emptyView = dialogView.findViewById(R.id.ssv_empty);
            adapter.setOnFilteredListener(() -> {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(VISIBLE);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    emptyView.setVisibility(GONE);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            });

            adapter.setOnChangeListener2((compoundButton, b) -> {
                onOkClick(dialog, editText);
            });

            dialog.show();
        };
        setListener(listener);
    }

    private void onOkClick(DialogInterface dialog, EditText searchView) {
        if (adapter.getValueName() != null) {
            vText.setText(adapter.getValueName());
        }
        searchView.setText("");

        if (itemSelectedListener != null) {
            AttributeValue selectedItem = adapter.getSelectedItem();
            if (selectedItem != null) {
                itemSelectedListener.onItemSelected(selectedItem);
            }
        }

        adapter.setOnFilteredListener(null);
        adapter.setOnChangeListener2(null);

        dialog.dismiss();
    }

    protected void setListener(OnClickListener listener) {
        setOnClickListener(listener);
        vText.setOnClickListener(listener);
        vArrow.setOnClickListener(listener);
    }

    protected void configureDialog(AlertDialog.Builder builder) {
    }

    public void updateText() {
        vText.setText(adapter.getValueName());
    }

    public void setHint(String hint) {
        defaultText = hint;
    }

    public void setSpinnerTitle(String title) {
        spinnerTitle = title;
    }

    public void setOnItemSelectedListener(YoulaOnItemSelectedListener listener) {
        this.itemSelectedListener = listener;
    }

    public void setShowSearchButton(boolean showSearchButton) {
        this.showSearchButton = showSearchButton;
    }

    public void setShowNotSetButton(boolean showNotSetButton) {
        this.showNotSetButton = showNotSetButton;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;

        vText.setEnabled(!readonly);
    }
}

