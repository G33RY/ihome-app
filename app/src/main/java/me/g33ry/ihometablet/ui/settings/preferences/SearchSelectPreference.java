package me.g33ry.ihometablet.ui.settings.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import androidx.preference.Preference;

import java.util.ArrayList;

import me.g33ry.ihometablet.MainActivity;
import me.g33ry.ihometablet.dialogs.SearchSelectDialog;

public class SearchSelectPreference extends Preference {
    private ArrayList<Pair<String, String>> entries = new ArrayList<>();
    private Pair<String, String> selected;

    public SearchSelectPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public SearchSelectPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SearchSelectPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchSelectPreference(Context context) {
        super(context);
        init();
    }

    private void init(){
        this.setSelected(MainActivity.preferences.getString(getKey(), "1"));
    }

    @Override
    protected void onClick() {
        super.onClick();
        SearchSelectDialog dialog = new SearchSelectDialog(entries, selected, this::setValue, this.getContext());

        dialog.show();
    }

    private void setValue(Pair<String, String> item){
        this.selected = item;
        if(callChangeListener(item.first)){
            persistString(item.first);
            notifyChanged();
            setSummary(item.second);
        }
    }

    public void setEntries(ArrayList<Pair<String, String>> entries){
        this.entries = entries;

        if(entries.size() != 0) {
            this.setSelected(MainActivity.preferences.getString(getKey(), "1"));
            if(selected == null)  this.selected = entries.get(0);
            setSummary(selected.second);
        }
    }

    public void setSelected(String key){
        for (Pair<String, String> entry : entries) {
            if(entry.first.equals(key)) {
                this.selected = entry;
                setSummary(selected.second);
                return;
            };
        }
    }
}
