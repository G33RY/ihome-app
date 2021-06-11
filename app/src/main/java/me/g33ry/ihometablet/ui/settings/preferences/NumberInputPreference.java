package me.g33ry.ihometablet.ui.settings.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.text.DecimalFormat;

import me.g33ry.ihometablet.MainActivity;
import me.g33ry.ihometablet.R;

public class NumberInputPreference extends Preference {
    private float number;
    private int decimalCount;
    private int multiplyWith;
    private float min;
    private float max;
    private float difference;

    private ImageView number_input_image_minus;
    private EditText number_input_edit_text_number;
    private ImageView number_input_image_plus;

    private DecimalFormat format;

    public NumberInputPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    public NumberInputPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public NumberInputPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }


    private void init(AttributeSet attrs, int defStyle){
         TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NumberInputPreference, defStyle, defStyle);

        number = MainActivity.preferences.getFloat(getKey(), a.getFloat(R.styleable.NumberInputPreference_value, 1)) ;
        decimalCount = a.getInt(R.styleable.NumberInputPreference_decimalCount, 0);
        multiplyWith = a.getInt(R.styleable.NumberInputPreference_multiplyWith, 1);
        min = a.getFloat(R.styleable.NumberInputPreference_minimum, 0);
        max = a.getFloat(R.styleable.NumberInputPreference_maximum, 100);
        difference = a.getFloat(R.styleable.NumberInputPreference_difference, 1);

        number /= multiplyWith;

        setWidgetLayoutResource(R.layout.preference_number_input);



        StringBuilder decimalPlaces = new StringBuilder("0");
        if(decimalCount > 0){
            decimalPlaces.append(".");
            for (int i = 0; i < decimalCount; i++) {
                decimalPlaces.append("0");
            }
        }
        format = new DecimalFormat(decimalPlaces.toString());
        setSummary(format.format(number));

        setOnPreferenceChangeListener((preference, newValue) -> {
            number_input_edit_text_number.setText(format.format(number));
            setSummary(format.format(number));
            return true;
        });
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        number_input_image_minus = (ImageView) holder.findViewById(R.id.number_input_image_minus);
        number_input_edit_text_number = (EditText) holder.findViewById(R.id.number_input_edit_text_number);
        number_input_image_plus = (ImageView) holder.findViewById(R.id.number_input_image_plus);


        number_input_image_minus.setOnClickListener(v -> {
            number -= difference;
            number = Math.max(min, number);
            number = Math.min(max, number);
            number = (float) (Math.round(number * Math.pow(10, decimalCount)) / Math.pow(10, decimalCount));
            setValue();
        });
        number_input_image_plus.setOnClickListener(v -> {
            number += difference;
            number = Math.max(min, number);
            number = Math.min(max, number);
            number = (float) (Math.round(number * Math.pow(10, decimalCount)) / Math.pow(10, decimalCount));
            setValue();
        });


        number_input_edit_text_number.setText(format.format(number));
        number_input_edit_text_number.setText(number + "");
    }


    private void sync(){

    }


    public void setValue() {
        if (callChangeListener(number*multiplyWith)) {
            persistFloat(number*multiplyWith);
            notifyChanged();
            System.out.println(MainActivity.preferences.getFloat(getKey(), 0));
        }
    }
}
