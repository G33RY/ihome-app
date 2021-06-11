package me.g33ry.ihometablet.ui.settings.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import me.g33ry.ihometablet.MainActivity;
import me.g33ry.ihometablet.Utils;
import me.g33ry.ihometablet.dialogs.TimePickerDialog;

public class TimePickerPreference extends Preference {
    private Utils.Clock clock;

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init(){
        System.out.println(getKey());
        clock = new Utils.Clock(MainActivity.preferences.getLong(getKey(), new Utils.Clock().getTimestamp()));
        System.out.println(clock.toString());

        syncSummary();
    }

    @Override
    protected void onClick() {
        super.onClick();

        TimePickerDialog timePicker = new TimePickerDialog(this.getContext(), (view, h, m) -> {
            clock.update(h,m,0);
            if(callChangeListener(clock.getTimestamp())){
                System.out.println(clock.toString());
                persistLong(clock.getTimestamp());
                notifyChanged();
                syncSummary();
            }

        }, clock.getHour(), clock.getMinutes(), true);
        timePicker.setTitle("Select Time");
        timePicker.show();
    }


    private void syncSummary(){
        setSummary(clock.getHourString() + ":" + clock.getMinutesString());
    }
}
