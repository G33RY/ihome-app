package me.g33ry.ihometablet.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import me.g33ry.ihometablet.R;

public class WeatherFragment extends Fragment {
    private ScrollView weather_scrollview;
    private ConstraintLayout weather_con;
    private ConstraintLayout weather_con_header;
    private TextView weather_text_header_temperature;
    private TextView weather_text_header_type;
    private TextView weather_text_header_day;
    private TextView weather_text_header_high_low;
    private TextView weather_text_header_city;
    private ImageView weather_image_line;
    private ImageView weather_image_line2;
    private RecyclerView weather_recycler_hourly_forecasts;
    private RecyclerView weather_recycler_daily_forecasts;
    private TextView weather_text_sunrise_title;
    private TextView weather_text_humidity_title;
    private TextView weather_text_sunset_title;
    private TextView weather_text_wind_title;
    private TextView weather_text_clouds_title;
    private TextView weather_text_clouds_value;
    private TextView weather_text_wind_value;
    private TextView weather_text_humidity_value;
    private TextView weather_text_sunset_value;
    private TextView weather_text_sunrise_value;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeatherViewModel weatherViewModel = new WeatherViewModel(System.out::println);

        //Current Weather
        weatherViewModel.getCurrentWeather().observe(getViewLifecycleOwner(), currentWeather -> {
            weather_text_header_temperature.setText(currentWeather.temp.intValue() + "°");
            weather_text_header_type.setText(currentWeather.desc.title);
            weather_text_header_day.setText(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(currentWeather.date));
            weather_text_header_high_low.setText("H: " + currentWeather.temp_max.intValue() + "° L: " + currentWeather.temp_min.intValue() + "°");
            weather_text_header_city.setText("Gödöllő");
            weather_text_sunrise_value.setText(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(currentWeather.sunrise));
            weather_text_sunset_value.setText(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(currentWeather.sunset));
            weather_text_wind_value.setText(currentWeather.wind_speed.intValue() + " km/h");
            weather_text_humidity_value.setText(currentWeather.humidity.intValue() + "%");
            weather_text_clouds_value.setText(currentWeather.clouds.intValue() + "%");
        });

        //Hourly Forecast
        weatherViewModel.getHourlyForecast().observe(getViewLifecycleOwner(), hourlyForecast -> weather_recycler_hourly_forecasts.setAdapter(new ForecastAdapter(hourlyForecast, ForecastAdapter.ForecastType.hourly)));

        //Daily Forecast
        weatherViewModel.getDailyForecast().observe(getViewLifecycleOwner(), dailyForecast -> weather_recycler_daily_forecasts.setAdapter(new ForecastAdapter(dailyForecast, ForecastAdapter.ForecastType.daily)));
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weather, container, false);

        weather_scrollview = root.findViewById(R.id.weather_scrollview);
        weather_con = root.findViewById(R.id.weather_con);
        weather_con_header = root.findViewById(R.id.weather_con_header);
        weather_text_header_temperature = root.findViewById(R.id.weather_text_header_temperature);
        weather_text_header_type = root.findViewById(R.id.weather_text_header_type);
        weather_text_header_day = root.findViewById(R.id.weather_text_header_day);
        weather_text_header_high_low = root.findViewById(R.id.weather_text_header_high_low);
        weather_text_header_city = root.findViewById(R.id.weather_text_header_city);
        weather_image_line = root.findViewById(R.id.weather_image_line);
        weather_image_line2 = root.findViewById(R.id.weather_image_line2);
        weather_recycler_hourly_forecasts = root.findViewById(R.id.weather_recycler_hourly_forecasts);
        weather_recycler_daily_forecasts = root.findViewById(R.id.weather_recycler_daily_forecasts);
        weather_text_sunrise_title = root.findViewById(R.id.weather_text_sunrise_title);
        weather_text_humidity_title = root.findViewById(R.id.weather_text_humidity_title);
        weather_text_sunset_title = root.findViewById(R.id.weather_text_sunset_title);
        weather_text_wind_title = root.findViewById(R.id.weather_text_wind_title);
        weather_text_clouds_title = root.findViewById(R.id.weather_text_clouds_title);
        weather_text_clouds_value = root.findViewById(R.id.weather_text_clouds_value);
        weather_text_wind_value = root.findViewById(R.id.weather_text_wind_value);
        weather_text_humidity_value = root.findViewById(R.id.weather_text_humidity_value);
        weather_text_sunset_value = root.findViewById(R.id.weather_text_sunset_value);
        weather_text_sunrise_value = root.findViewById(R.id.weather_text_sunrise_value);


        weather_recycler_hourly_forecasts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        weather_recycler_daily_forecasts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }
}