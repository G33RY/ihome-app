package me.g33ry.ihometablet.ui.afk;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.Utils;

import static me.g33ry.ihometablet.MainActivity.preferences;

public class AfkFragment extends Fragment {
    private ConstraintLayout afk_con;
    private Handler handler = null;
    private Runnable handlerTask = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler();

        handlerTask = new Runnable() {
              @Override
              public void run()  {
                if(handler == null) return;;
                //Set Brightness
                ContentResolver cResolver = requireActivity().getContentResolver();
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
                Settings.System.putInt(cResolver, Settings.System.SCREEN_OFF_TIMEOUT, 1000 * 60 * 60 * 24 * 7);

                requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                handler.postDelayed(this, 1000 * 60);
              }
          };
         handlerTask.run();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler = null;
        handlerTask = null;
        Log.i("INFO", "STOP");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_afk, container, false);

        afk_con = root.findViewById(R.id.afk_con);

        afk_con.setOnClickListener((v) -> {});

        return root;
    }

}
