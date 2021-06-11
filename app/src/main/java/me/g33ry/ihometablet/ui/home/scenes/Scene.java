package me.g33ry.ihometablet.ui.home.scenes;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.telephony.gsm.GsmCellLocation;
import android.util.ArraySet;

import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.databse.Database;
import me.g33ry.ihometablet.ui.home.HomeFragment;
import me.g33ry.ihometablet.ui.home.devices.Device;
import me.g33ry.ihometablet.ui.home.devices.Light;

public class Scene {
    private String title;
    private int icon;
    private long id;
    private JSONArray devices;
    private ArrayList<String> deviceIds;

    public Scene(JSONObject json) {
        try{
            this.title = json.getString("title");
            this.icon = json.getInt("icon");
            this.id = json.getLong("id");
            this.devices = json.getJSONArray("devices");
        }catch (Exception e){
            e.printStackTrace();
            this.title = "Scene";
            this.icon = R.drawable.ic_home;
            this.devices = new JSONArray();
            this.id = new Random().nextLong();
        }
    }

    public Scene() {
        this.title = "Scene";
        this.icon = R.drawable.ic_home;
        this.devices = new JSONArray();
        this.id = new Random().nextLong();
    }

    public void onClick(){
        for (int i = 0; i < devices.length(); i++) {
            try{
                JSONObject json = devices.getJSONObject(i);
                for (Device device : HomeFragment.devices) {
                    if(device.getId().equals(json.getString("id"))){
                        device.updateFromJson(json);
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public ArrayList<String> getDeviceIds() {
        ArrayList<String> ids = new ArrayList<>();

        for (int i = 0; i < devices.length(); i++) {
            try{
                JSONObject json = devices.getJSONObject(i);
                ids.add(json.getString("id"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return ids;
    }




    public long getId(){
        return this.id;
    }

    public void save(Context context, ArrayList<String> deviceIds) {
        System.out.println(deviceIds);

        if(devices.length() == 0){
            this.devices = new JSONArray();
            for (Device device : HomeFragment.devices) {
                if(deviceIds.contains(device.getId())){
                    this.devices.put(device.toJson());
                }
            }
        }

        Database.getInstance(context).addOrUpdateScene(this);

        ArrayList<Scene> newScenes = HomeFragment.scenes.getValue();
        boolean contains = false;
        for (int i = 0; i < newScenes.size(); i++) {
            Scene scene = newScenes.get(i);
            if(scene.getId() == getId()){
                newScenes.set(i, this);
                contains = true;
                break;
            }
        }

        if(!contains) newScenes.add(this);

        HomeFragment.scenes.postValue(newScenes);
    }

    public String toJson(){
        JSONObject json = new JSONObject();
        try{
            json.put("id", getId());
            json.put("icon", getIcon());
            json.put("title", getTitle());
            json.put("devices", devices);
        }catch (Exception e){
            e.printStackTrace();
        }

        return json.toString();
    }

    public void delete(Context context) {
        Database.getInstance(context).deleteScene(this);

        ArrayList<Scene> newScenes = HomeFragment.scenes.getValue();
        for (int i = 0; i < newScenes.size(); i++) {
            Scene scene = newScenes.get(i);
            if(scene.getId() == getId()){
                newScenes.remove(i);
                break;
            }
        }

        HomeFragment.scenes.postValue(newScenes);
    }
}
