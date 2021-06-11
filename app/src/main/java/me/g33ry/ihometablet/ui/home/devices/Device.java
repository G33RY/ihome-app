package me.g33ry.ihometablet.ui.home.devices;

import org.json.JSONObject;

import me.g33ry.ihometablet.R;

public abstract class Device {
    private int icon;
    private String title;
    private String id;


    public enum DeviceType {
        UNKNOWN(0, R.layout.view_light),
        LIGHT(1, R.layout.view_light),
        PC(2, R.layout.view_pc),
        Blinds(3, R.layout.view_blinds);

        private final int value;
        private final int layout;
        DeviceType(int value, int layout) {
            this.value = value;
            this.layout = layout;
        }

        public int getValue() {
            return value;
        }
        public int getLayout() {
            return layout;
        }
    }

    private final DeviceType deviceType;

    public DeviceType getType() {
        return this.deviceType;
    }
    public String getTitle(){ return title;}
    public int getIcon(){ return icon;}
    public String getId(){ return id;}

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Device(DeviceType deviceType) {
        this.deviceType = deviceType;
    }


    public abstract JSONObject toJson();
    public abstract void updateFromJson(JSONObject jsonObject);

}
