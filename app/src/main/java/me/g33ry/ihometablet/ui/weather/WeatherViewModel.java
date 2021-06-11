package me.g33ry.ihometablet.ui.weather;

import android.os.AsyncTask;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihometablet.OnErrorListener;

public class WeatherViewModel extends ViewModel {
    private OnErrorListener onErrorListener;
    private final MutableLiveData<IHOMEAPI.Weather.WeatherModel> currentWeather;
    private final MutableLiveData<ArrayList<IHOMEAPI.Weather.WeatherModel>> hourlyForecast;
    private final MutableLiveData<ArrayList<IHOMEAPI.Weather.WeatherModel>> dailyForecast;

    public WeatherViewModel(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        currentWeather = new MutableLiveData<>();
        hourlyForecast = new MutableLiveData<>();
        dailyForecast = new MutableLiveData<>();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                AsyncTask.execute(() -> {
                    IHOMEAPI.Weather.WeatherModel currentWeatherModel = IHOMEAPI.Weather.currentWeather();
                    ArrayList<IHOMEAPI.Weather.WeatherModel> hourlyForecastModels = IHOMEAPI.Weather.hourlyForecast();
                    ArrayList<IHOMEAPI.Weather.WeatherModel> dailyForecastModels = IHOMEAPI.Weather.dailyForecast();

                    if(currentWeatherModel != null) currentWeather.postValue(currentWeatherModel);
                    else onErrorListener.error("Something went wrong when requesting weather");
                    if(hourlyForecastModels != null) hourlyForecast.postValue(hourlyForecastModels);
                    else onErrorListener.error("Something went wrong when requesting weather");
                    if(dailyForecastModels != null) dailyForecast.postValue(dailyForecastModels);
                    else onErrorListener.error("Something went wrong when requesting weather");
                });
            }
        };

        timer.schedule(timerTask, 0, 1000 * 60); // 1min
    }

    public MutableLiveData<IHOMEAPI.Weather.WeatherModel> getCurrentWeather() {
        return currentWeather;
    }

    public MutableLiveData<ArrayList<IHOMEAPI.Weather.WeatherModel>> getHourlyForecast() {
        return hourlyForecast;
    }

    public MutableLiveData<ArrayList<IHOMEAPI.Weather.WeatherModel>> getDailyForecast() {
        return dailyForecast;
    }
}
