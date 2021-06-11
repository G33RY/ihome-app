package me.g33ry.ihometablet.ui.home.devices;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihomeapi.IHOMEAPI.Devices.MagicHome;
import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.ui.home.views.EditLightView;

public class Light extends Device {
    public static EditLightView editView;

    private MagicHome.Device controller;
    private String macAddress;
    private String ip;
    private MagicHome.Device.State state;
    public OnChangeListener onChangeListener;

    public Light(MagicHome.Device controller) {
        super(DeviceType.LIGHT);
        this.controller = controller;
        setId(controller.id);
        setTitle(controller.name);
        setIcon(R.drawable.light_on);
        this.macAddress = controller.macAddress;
        this.ip = controller.ip;
        this.state = controller.state;
        this.onChangeListener = (title, percentage, color, on) -> {};
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try{
            json.put("type", getType().getValue());
            json.put("id", getId());
            json.put("title", getTitle());
            json.put("macAddress", getMacAddress());
            json.put("ip", getIp());
            json.put("state.red", state.red);
            json.put("state.green", state.green);
            json.put("state.blue", state.blue);
            json.put("state.on", state.on);
            json.put("state.percentage", state.percentage);
            json.put("state.mode", state.mode);
            json.put("state.isWhite", state.isWhite);
        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void updateFromJson(JSONObject jsonObject) {
        try{
            if(!jsonObject.getBoolean("state.on")) {
                toggle(false);
                return;
            }
            changeColor(jsonObject.getInt("state.red"), jsonObject.getInt("state.green"), jsonObject.getInt("state.blue"));
            setPercentage(jsonObject.getInt("state.percentage"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void setColorInt(int color){
         int R = (color >> 16) & 0xff;
         int G = (color >>  8) & 0xff;
         int B = (color      ) & 0xff;

        changeColor(R,G,B);
    }

    public void toggle(boolean on){
        AsyncTask.execute(() -> {
            controller.toggle(on);
        });
        onChangeListener.change(getTitle(), state.percentage, getColorInt(), on);
    }

    public void changeColor(int red, int green, int blue){
        AsyncTask.execute(() -> {
            controller.changeColor(red, green, blue);
        });
        onChangeListener.change(getTitle(), state.percentage, getColorInt(red, green, blue), true);
    }

    public void setName(String title){
        AsyncTask.execute(() -> {
            controller.changeName(title);
        });
        onChangeListener.change(title, state.percentage, getColorInt(), state.on);
    }

    public void setPercentage(int percentage){
        AsyncTask.execute(() -> {
            controller.setPercentage(percentage);
        });
        onChangeListener.change(getTitle(), percentage, getColorInt(), true);
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getIp() {
        return ip;
    }

    public MagicHome.Device.State getState() {
        return state;
    }

    public int getColorInt(){
         return (0xff) << 24 | (controller.state.red & 0xff) << 16 | (controller.state.green & 0xff) << 8 | (controller.state.blue & 0xff);
    }

    public int getColorInt(int r, int g, int b){
         return (0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    public void localeUpdate(int percentage, int color, boolean on) {
         int R = (color >> 16) & 0xff;
         int G = (color >>  8) & 0xff;
         int B = (color      ) & 0xff;

        this.state.percentage = percentage;
        this.state.red = R;
        this.state.green = G;
        this.state.blue = B;
        this.state.on = on;
    }

    public interface OnChangeListener{
        void change(String title, int percentage, int color, boolean on);
    }

    @Override
    public String toString() {
        return "Light{" +
                "controller=" + controller +
                ", id='" + getId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", ip='" + ip + '\'' +
                ", state=" + state +
                ", onChangeListener=" + onChangeListener +
                '}';
    }
}
