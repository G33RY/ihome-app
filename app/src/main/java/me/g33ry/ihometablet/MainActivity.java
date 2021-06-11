package me.g33ry.ihometablet;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import me.g33ry.ihomeapi.IHOMEAPI;
import me.g33ry.ihometablet.databse.Database;
import me.g33ry.ihometablet.ui.afk.AfkFragment;
import me.g33ry.ihometablet.ui.home.scenes.Scene;

public class MainActivity extends AppCompatActivity {
    private AfkFragment afkFragment = null;
    public static boolean isAFK = false;
    public static boolean isDialogShown = false;
    public static SharedPreferences preferences = new SharedPreferences() {
        @Override
        public Map<String, ?> getAll() {
            return null;
        }

        @Nullable
        @Override
        public String getString(String key, @Nullable String defValue) {
            return null;
        }

        @Nullable
        @Override
        public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
            return null;
        }

        @Override
        public int getInt(String key, int defValue) {
            return 0;
        }

        @Override
        public long getLong(String key, long defValue) {
            return 0;
        }

        @Override
        public float getFloat(String key, float defValue) {
            return 0;
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            return false;
        }

        @Override
        public boolean contains(String key) {
            return false;
        }

        @Override
        public Editor edit() {
            return null;
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

        }
    };
    public static MutableLiveData<ArrayList<Pair<String, String>>> tokens = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        IHOMEAPI.setAuth("username", "password");

        AsyncTask.execute(() -> {
            ArrayList<IHOMEAPI.Crypto.Token> tokens = IHOMEAPI.Crypto.getAll();
            if(tokens != null) {
                ArrayList<Pair<String, String>> newTokens = new ArrayList<>();
                for (IHOMEAPI.Crypto.Token token : tokens) {
                    newTokens.add(new Pair<>(token.id + "", token.name));
                }
                MainActivity.tokens.postValue(newTokens);
            }
        });





        if(!Settings.System.canWrite(this)){
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnApplyWindowInsetsListener(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_weather, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);



        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");
        System.out.println(" \n");


    }

    private final Handler disconnectHandler = new Handler(msg -> true);
    private final Runnable disconnectCallback = () -> {
        if(!isDialogShown){
             FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            afkFragment = new AfkFragment();
            transaction.add(R.id.main_container, afkFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            MainActivity.isAFK = true;
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, (long) preferences.getFloat("disconnect_interval", 30000));
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
        if(isAFK){
            float brightness;

            Utils.Clock current = new Utils.Clock();
            Utils.Clock wake_at = new Utils.Clock(preferences.getLong("wake_at", 1620360000000L));
            Utils.Clock dim_at = new Utils.Clock(preferences.getLong("dim_at", 16204176000000L));

            if(current.lessThanOrEquals(wake_at) || current.moreThanOrEquals(dim_at) || preferences.getBoolean("dim_force", false)){
        //       Night mode
                brightness = preferences.getFloat("waked_night_brightness", 30f);
            }else{
        //      Day mode
                brightness = 255;
            }


            System.out.println((int) brightness);

            ContentResolver cResolver = getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, (int) brightness);
            Settings.System.putInt(cResolver, Settings.System.SCREEN_OFF_TIMEOUT, 1000 * 60 * 60 * 24 * 7);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(afkFragment);
            transaction.commit();
            MainActivity.isAFK = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

}