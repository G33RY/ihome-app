package me.g33ry.ihometablet.ui.home.devices;

import android.os.AsyncTask;

import org.json.JSONObject;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihomeapi.IHOMEAPI.Devices.MagicHome;
import me.g33ry.ihometablet.R;

public class PC extends Device {
    public OnChangeListener onChangeListener;
    private boolean on;

    public PC(boolean isOn) {
        super(DeviceType.PC);
        this.on = isOn;
        setId("pc");
        setTitle("PC");
        setIcon(R.drawable.pc_on);
        this.onChangeListener = (title, on) -> {localeUpdate(on);};
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try{
            json.put("type", getType().getValue());
            json.put("id", getId());
            json.put("title", getTitle());
            json.put("on", on);
        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void updateFromJson(JSONObject jsonObject) {
        try{
            toggle(jsonObject.getBoolean("on"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void toggle(boolean on){
        AsyncTask.execute(() -> {
            IHOMEAPI.Devices.PC.toggle(on);
        });
        this.on = on;
        onChangeListener.change(getTitle(), on);
    }

    public boolean isOn() {
        return on;
    }

    public void setName(String title){
        this.setTitle(title);
        onChangeListener.change(title, on);
    }

    public void localeUpdate(boolean on) {
        this.on = on;
    }

    public interface OnChangeListener{
        void change(String title, boolean on);
    }
}
