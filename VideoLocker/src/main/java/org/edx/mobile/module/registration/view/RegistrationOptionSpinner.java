package org.edx.mobile.module.registration.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.edx.mobile.R;
import org.edx.mobile.module.registration.model.RegistrationOption;

import java.util.List;

public class RegistrationOptionSpinner extends Spinner {

    private ArrayAdapter<RegistrationOption> adapter;

    public RegistrationOptionSpinner(Context context) {
        super(context);
    }

    public RegistrationOptionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RegistrationOptionSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean hasValue(@Nullable String value){
        boolean ret = false;
        if (adapter != null) {
            ret = (getAdapterPosition(value) >= 0);
        }
        return ret;
    }

    public void select(@Nullable String value){
        if (adapter != null && value != null) {
            int pos = getAdapterPosition(value);
            if (pos >= 0) {
                setSelection(pos);
            }
        }
    }

    public @Nullable String getSelectedItemValue() {
        String ret = null;
        RegistrationOption selected = (RegistrationOption)getSelectedItem();
        if (selected != null) {
            ret = selected.getValue();
        }
        return ret;
    }

    private int getAdapterPosition(@Nullable String input) {
        int ret = -1;
        if (input != null && adapter != null) {
            for(int i=0 ; i<adapter.getCount() ; i++){
                RegistrationOption item = adapter.getItem(i);
                if (item != null && input.equals(item.toString())) {
                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    public void setItems(@NonNull List<RegistrationOption> options, @Nullable RegistrationOption defaultOption) {
        adapter = new ArrayAdapter<>(getContext(), R.layout.registration_spinner_item, options);
        setAdapter(adapter);
        if (defaultOption != null) {
            select(defaultOption.toString());
        }
    }
}
