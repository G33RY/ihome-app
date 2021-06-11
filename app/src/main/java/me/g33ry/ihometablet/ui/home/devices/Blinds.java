package me.g33ry.ihometablet.ui.home.devices;

import android.os.AsyncTask;

import org.json.JSONObject;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihomeapi.IHOMEAPI.Devices.MagicHome;
import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.ui.home.views.EditBlindsView;

public class Blinds extends Device {
    public static EditBlindsView editView;

    public OnChangeListener onChangeListener;
    private int percentage;

    public Blinds(int percentage) {
        super(DeviceType.Blinds);
        this.percentage = percentage;
        setId("blinds");
        setTitle("Blinds");
        setIcon(R.drawable.blind_on);
        this.onChangeListener = (title, percentage_) -> {localeUpdate(percentage_);};
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try{
            json.put("type", getType().getValue());
            json.put("id", getId());
            json.put("title", getTitle());
            json.put("percentage", percentage);
        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void updateFromJson(JSONObject jsonObject) {
        try{
            setPercentage(jsonObject.getInt("percentage"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void setPercentage(int percentage){
        AsyncTask.execute(() -> {
            IHOMEAPI.Devices.Blinds.setPercentage(percentage);
        });
        this.percentage = percentage;
        onChangeListener.change(getTitle(), percentage);
    }

    public int getPercentage() {
        return percentage;
    }

    public void setName(String title){
        this.setTitle(title);
        onChangeListener.change(title, percentage);
    }

    public void localeUpdate(int percentage) {
        this.percentage = percentage;
    }

    public interface OnChangeListener{
        void change(String title, int percentage);
    }
}
