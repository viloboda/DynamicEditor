package com.example.vloboda.dynamicentityeditor.controls;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;

import com.example.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonsValueAdapter extends RecyclerView.Adapter<RadioButtonViewHolder>
        implements Filterable {

    private LayoutInflater inflater;
    private List<AttributeValue> values;
    private List<AttributeValue> originalValues;
    private Long selectedValue;

    private LongSparseArray<RadioButtonViewHolder> buttons = new LongSparseArray<>();

    private CompoundButton.OnCheckedChangeListener changeListener;
    private CompoundButton.OnCheckedChangeListener changeListener2;
    private OnFilteredListener filteredListener;

    public RadioButtonsValueAdapter(LayoutInflater inflater, List<AttributeValue> values) {
        this.inflater = inflater;
        this.values = values;
    }

    @Override
    public RadioButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RadioButtonViewHolder.createView(inflater, parent);
    }

    @Override
    public void onBindViewHolder(RadioButtonViewHolder holder, int position) {
        AttributeValue value = values.get(position);
        holder.init(value);

        RadioButton radioButton = (RadioButton) holder.itemView;
        radioButton.setOnClickListener(null);

        buttons.put(value.id, holder);
        if (selectedValue != null && value.id == selectedValue) {
            radioButton.setChecked(true);
        } else {
            radioButton.setChecked(false);
        }

        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                return;
            }
            if (selectedValue != null && !selectedValue.equals(buttonView.getTag())) {
                RadioButtonViewHolder rb = buttons.get(selectedValue);
                if (rb != null) {
                    rb.unCheck();
                }
            }

            Long newValue = (Long) buttonView.getTag();
            if (newValue.equals(selectedValue)) {
                return;
            }

            selectedValue = newValue;

            if (changeListener != null) {
                changeListener.onCheckedChanged(buttonView, isChecked);
            }
            if (changeListener2 != null) {
                changeListener2.onCheckedChanged(buttonView, isChecked);
            }
        });
    }

    public List<Long> getValue() {
        if (selectedValue == null)
            return null;

        List<Long> result = new ArrayList<>();
        result.add(selectedValue);

        return result;
    }

    public Long getSelectedValue() {
        return selectedValue;
    }

    public List<AttributeValue> getValues() {
        return originalValues;
    }

    public String getValueName() {
        AttributeValue result = getSelectedItem();

        return result != null ? result.caption : null;
    }

    public AttributeValue getSelectedItem() {
        if (selectedValue == null) {
            return null;
        }

        int index = values.indexOf(new AttributeValue(selectedValue, ""));
        if(index > -1) {
            return values.get(index);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setValueBeforeBind(Long value) {
        selectedValue = value;
    }

    public void setValue(List<Long> values) {
        if (selectedValue != null) {
            RadioButtonViewHolder rb = buttons.get(selectedValue);
            if (rb != null) {
                rb.unCheck();
            }
        }
        if (values == null || values.isEmpty()) {
            selectedValue = null;
        } else {
            selectedValue = values.get(0);
        }
        if (selectedValue == null)
            return;

        RadioButtonViewHolder viewHolder = buttons.get(selectedValue);
        if (viewHolder != null) {
            viewHolder.check();
        } else if (buttons.size() != 0) {
            selectedValue = null;
        }
    }

    void setRowValue(Long value) {
        RadioButtonViewHolder viewHolder = buttons.get(value);
        if (viewHolder != null) {
            viewHolder.check();
        }
    }

    public void setOnChangeListener(CompoundButton.OnCheckedChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void setOnChangeListener2(CompoundButton.OnCheckedChangeListener changeListener) {
        this.changeListener2 = changeListener;
    }

    public void setOnFilteredListener(OnFilteredListener listener) {
        this.filteredListener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                values = (List<AttributeValue>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
                if (filteredListener != null) {
                    filteredListener.onFiltered();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<AttributeValue> filteredValues = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(values); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = originalValues.size();
                    results.values = originalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        Log.i("RBA", "Filter : " + originalValues.get(i).id + " -> " + originalValues.get(i).caption);
                        String data = originalValues.get(i).caption;
                        if (data.toUpperCase().contains(constraint.toString().toUpperCase())) {
                            filteredValues.add(originalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredValues.size();
                    results.values = filteredValues;
                }
                return results;
            }
        };
    }

    public interface OnFilteredListener {
        void onFiltered();
    }
}
