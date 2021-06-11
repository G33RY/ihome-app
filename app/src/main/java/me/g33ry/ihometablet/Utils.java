package me.g33ry.ihometablet;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static class Clock {
        private int hour;
        private int minutes;
        private int seconds;

        public Clock(int hour, int minutes, int seconds) {
            this.hour = hour;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        public Clock(long timestamp) {
            DateFormat formatter = new SimpleDateFormat("HH;mm;ss", Locale.ENGLISH);
            String timeString = formatter.format(timestamp);
            String[] timeArray = timeString.split(";");

            this.hour = Integer.parseInt(timeArray[0]);
            this.minutes = Integer.parseInt(timeArray[1]);
            this.seconds = Integer.parseInt(timeArray[2]);
        }

        public Clock() {
            DateFormat formatter = new SimpleDateFormat("HH;mm;ss", Locale.ENGLISH);
            String timeString = formatter.format(Calendar.getInstance().getTime());
            String[] timeArray = timeString.split(";");

            this.hour = Integer.parseInt(timeArray[0]);
            this.minutes = Integer.parseInt(timeArray[1]);
            this.seconds = Integer.parseInt(timeArray[2]);
        }

        public long getTimestamp(){
            final String str =  getHourString()+":"+getMinutesString()+":"+getSecondsString();
            return Timestamp.valueOf( new SimpleDateFormat("yyyy-MM-dd ", Locale.ENGLISH).format(new Date()).concat(str)).getTime();
        }

        public void update(int h, int m, int s){
            this.hour = h;
            this.minutes = m;
            this.seconds = s;
        }

        public int getHour() {
            return hour;
        }

        public int getMinutes() {
            return minutes;
        }

        public int getSeconds() {
            return seconds;
        }

        public String getHourString() {
            return new DecimalFormat("00").format(hour);
        }

        public String getMinutesString() {
            return new DecimalFormat("00").format(minutes);
        }

        public String getSecondsString() {
            return new DecimalFormat("00").format(seconds);
        }


        public boolean equals(Clock obj) {
            return this.hour == obj.hour && this.minutes == obj.minutes && this.seconds == obj.seconds;
        }

        public boolean lessThan(Clock obj) {
            if(this.hour != obj.hour) return this.hour < obj.hour;
            if(this.minutes != obj.minutes) return this.minutes < obj.minutes;
            return this.seconds < obj.seconds;
        }

        public boolean moreThan(Clock obj) {
            if(this.hour != obj.hour) return this.hour > obj.hour;
            if(this.minutes != obj.minutes) return this.minutes > obj.minutes;
            return this.seconds > obj.seconds;
        }

        public boolean lessThanOrEquals(Clock obj) {
            if(this.hour != obj.hour) return this.hour <= obj.hour;
            if(this.minutes != obj.minutes) return this.minutes <= obj.minutes;
            return this.seconds <= obj.seconds;
        }

        public boolean moreThanOrEquals(Clock obj) {
            if(this.hour != obj.hour) return this.hour >= obj.hour;
            if(this.minutes != obj.minutes) return this.minutes >= obj.minutes;
            return this.seconds >= obj.seconds;
        }

        @NonNull
        @Override
        public String toString() {
            return getHourString()+":"+getMinutesString()+":"+getSecondsString() + " = " + getTimestamp();
        }
    }

}
