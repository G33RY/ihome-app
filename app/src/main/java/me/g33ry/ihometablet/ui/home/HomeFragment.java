package me.g33ry.ihometablet.ui.home;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.g33ry.ihometablet.MainActivity;
import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.databse.Database;
import me.g33ry.ihometablet.ui.home.devices.Device;
import me.g33ry.ihometablet.ui.home.scenes.Scene;
import me.g33ry.ihometablet.ui.home.views.EditLightView;
import me.g33ry.ihometablet.ui.home.views.EditSceneView;

public class HomeFragment extends Fragment {
    public static EditSceneView editSceneView;
    public static ArrayList<Device> devices = new ArrayList<>();
    public static MutableLiveData<ArrayList<Scene>> scenes = new MutableLiveData<>();
    public static int lightId;
    public static int sceneId;

    private ConstraintLayout con;
    private RecyclerView devices_recycler;
    private RecyclerView scenes_recycler;
    private TextView header_text_clock;
    private TextView header_text_weather;
    private ImageView header_ic_weather;
    private ImageView scenes_image_add;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeFragment.scenes.postValue(Database.getInstance(getContext()).getAllScenes());

        HomeViewModel homeViewModel = new HomeViewModel((error) -> Log.e("API ERROR", error));

        homeViewModel.getDevices().observe(getViewLifecycleOwner(), devices -> {
            HomeFragment.devices = devices;
            System.out.println(devices.size());
            devices_recycler.setAdapter(new DeviceAdapter(devices, con));
        });
        homeViewModel.getClockText().observe(getViewLifecycleOwner(), clockText -> header_text_clock.setText(clockText));
        homeViewModel.getWeatherModel().observe(getViewLifecycleOwner(), weatherModel -> {
            header_text_weather.setText(weatherModel == null ?  "0°" : weatherModel.temp.intValue() + "°");
            assert weatherModel != null;
            GlideToVectorYou.init().with(this.getContext()).load(Uri.parse(weatherModel.desc.iconUrl), header_ic_weather);
        });

        HomeFragment.scenes.observe(getViewLifecycleOwner(), scenes -> {
            scenes_recycler.setAdapter(new SceneAdapter(scenes));
        });

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        HomeFragment.lightId = View.generateViewId();
        HomeFragment.sceneId = View.generateViewId();

        con = root.findViewById(R.id.container);
        devices_recycler = root.findViewById(R.id.devices_recycler);
        header_text_clock = root.findViewById(R.id.header_text_clock);
        header_text_weather = root.findViewById(R.id.header_text_weather);
        header_ic_weather = root.findViewById(R.id.header_ic_weather);
        scenes_image_add = root.findViewById(R.id.scenes_image_add);
        scenes_recycler = root.findViewById(R.id.scenes_recycler);

        devices_recycler.setLayoutManager(new GridLayoutManager(container.getContext(), 5));
        scenes_recycler.setLayoutManager(new GridLayoutManager(container.getContext(), 2, GridLayoutManager.HORIZONTAL, false));


        scenes_image_add.setOnClickListener((v) -> {
            if(HomeFragment.editSceneView != null) {
                con.removeView(HomeFragment.editSceneView);
                HomeFragment.editSceneView = null;
            }

            HomeFragment.editSceneView = new EditSceneView(requireContext(), new Scene());
            HomeFragment.editSceneView .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            HomeFragment.editSceneView .setId(HomeFragment.sceneId);
            con.addView(HomeFragment.editSceneView );

            ConstraintSet set = new ConstraintSet();
            set.clone(con);
            set.connect(HomeFragment.editSceneView .getId(), ConstraintSet.START, con.getId(), ConstraintSet.START);
            set.connect(HomeFragment.editSceneView .getId(), ConstraintSet.END, con.getId(), ConstraintSet.END);
            set.connect(HomeFragment.editSceneView .getId(), ConstraintSet.BOTTOM, con.getId(), ConstraintSet.BOTTOM, 0);
            set.applyTo(con);
        });

        return root;
    }
}