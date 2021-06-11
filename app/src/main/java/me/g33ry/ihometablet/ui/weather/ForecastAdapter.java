package me.g33ry.ihometablet.ui.weather;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihometablet.R;

public class ForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static enum ForecastType {
        hourly(R.layout.view_hourly_forecast), daily(R.layout.view_daily_forecast);

        private final int layout;
        ForecastType(int layout) {
            this.layout = layout;
        }

        public int getLayout() {
            return layout;
        }
    }
    private ArrayList<IHOMEAPI.Weather.WeatherModel> items;
    private final ForecastType forecastType;

    public ForecastAdapter(ArrayList<IHOMEAPI.Weather.WeatherModel> items, ForecastType forecastType) {
        this.items = items;
        this.forecastType = forecastType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(forecastType.getLayout(), parent, false);
        if(forecastType == ForecastType.hourly){
            viewHolder = new HourlyWeatherViewHolder(view);
        }else{
            viewHolder = new DailyWeatherViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(forecastType == ForecastType.hourly){
            ((HourlyWeatherViewHolder) holder).init(items.get(position));
        }else{
            ((DailyWeatherViewHolder) holder).init(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout hourly_forecast_con;
        private final TextView hourly_forecast_text_title;
        private final ImageView hourly_forecast_image_icon;
        private final TextView hourly_forecast_text_temperature;

        public HourlyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            hourly_forecast_con = itemView.findViewById(R.id.hourly_forecast_con);
            hourly_forecast_text_title = itemView.findViewById(R.id.hourly_forecast_text_title);
            hourly_forecast_image_icon = itemView.findViewById(R.id.hourly_forecast_image_icon);
            hourly_forecast_text_temperature = itemView.findViewById(R.id.hourly_forecast_text_temperature);
        }

        public void init(IHOMEAPI.Weather.WeatherModel weather){
            hourly_forecast_text_title.setText(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(weather.date));
            GlideToVectorYou.init().with(hourly_forecast_con.getContext()).load(Uri.parse(weather.desc.iconUrl), hourly_forecast_image_icon);
            hourly_forecast_text_temperature.setText(weather.temp.intValue() + "°");
        }
    }

    public static class DailyWeatherViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout daily_forecast_con;
        private final TextView daily_forecast_text_title;
        private final ImageView daily_forecast_image_icon;
        private final TextView daily_forecast_text_high_temperature;
        private final TextView daily_forecast_text_low_temperature;


        public DailyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            daily_forecast_con = itemView.findViewById(R.id.daily_forecast_con);
            daily_forecast_text_title = itemView.findViewById(R.id.daily_forecast_text_title);
            daily_forecast_image_icon = itemView.findViewById(R.id.daily_forecast_image_icon);
            daily_forecast_text_high_temperature = itemView.findViewById(R.id.daily_forecast_text_high_temperature);
            daily_forecast_text_low_temperature = itemView.findViewById(R.id.daily_forecast_text_low_temperature);
        }

        public void init(IHOMEAPI.Weather.WeatherModel weather){
            daily_forecast_text_title.setText(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(weather.date));
            GlideToVectorYou.init().with(daily_forecast_con.getContext()).load(Uri.parse(weather.desc.iconUrl), daily_forecast_image_icon);
            daily_forecast_text_high_temperature.setText(weather.temp_max.intValue() + "°");
            daily_forecast_text_low_temperature.setText(weather.temp_min.intValue() + "°");
        }
    }
}
