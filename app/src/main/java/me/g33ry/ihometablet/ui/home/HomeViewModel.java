package me.g33ry.ihometablet.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihometablet.OnErrorListener;
import me.g33ry.ihometablet.ui.home.devices.Blinds;
import me.g33ry.ihometablet.ui.home.devices.Device;
import me.g33ry.ihometablet.ui.home.devices.Light;
import me.g33ry.ihometablet.ui.home.devices.PC;

public class HomeViewModel extends ViewModel {
    private OnErrorListener onErrorListener;
    private Timer timer;

    private MutableLiveData<ArrayList<Device>> devices;
    private MutableLiveData<String> clockText;
    private MutableLiveData<IHOMEAPI.Weather.WeatherModel> weatherModel;


    public HomeViewModel(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        this.timer = new Timer();

        this.devices = new MutableLiveData<>();
        this.clockText = new MutableLiveData<>();
        this.weatherModel = new MutableLiveData<>();



        update();
    }

    public MutableLiveData<ArrayList<Device>> getDevices() {
        return devices;
    }

    public MutableLiveData<String> getClockText() {
        return clockText;
    }

    public MutableLiveData<IHOMEAPI.Weather.WeatherModel> getWeatherModel() {
        return weatherModel;
    }

    public void update(){

        //Clock
        TimerTask clockTask = new TimerTask() {
            @Override
            public void run() {
                Date currentTime = Calendar.getInstance().getTime();
                DateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                clockText.postValue(formatter.format(currentTime));
            }
        };
        timer.schedule(clockTask, 0, 1000 * 5);

        //Devices
        TimerTask devicesTask = new TimerTask() {
            @Override
            public void run() {
                AsyncTask.execute(() -> {
                    ArrayList<Device> devicesArrayList = new ArrayList<>();
                    ArrayList<IHOMEAPI.Devices.MagicHome.Device> lights =  IHOMEAPI.Devices.MagicHome.getAll();

                    if(lights != null){
                        for (IHOMEAPI.Devices.MagicHome.Device light: lights) {
                            devicesArrayList.add(new Light(light));
                        }
                    }else{
                        onErrorListener.error("Something went wrong when requesting devices");
                    }

                    devicesArrayList.add(new PC(IHOMEAPI.Devices.PC.status()));
                    devicesArrayList.add(new Blinds(IHOMEAPI.Devices.Blinds.status()));

                    devices.postValue(devicesArrayList);
                });
            }
        };
        timer.schedule(devicesTask, 0, 1000 * 60 * 3);

        //Weather
        TimerTask weatherTask = new TimerTask() {
            @Override
            public void run() {
                AsyncTask.execute(() -> {

                    IHOMEAPI.Weather.WeatherModel currentWeather = IHOMEAPI.Weather.currentWeather();
                    if(currentWeather != null) weatherModel.postValue(currentWeather);
                    else onErrorListener.error("Something went wrong when requesting weather");
                });
            }
        };
        timer.schedule(weatherTask, 0, 1000 * 60 * 5);
    }
}