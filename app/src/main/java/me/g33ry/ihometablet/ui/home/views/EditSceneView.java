package me.g33ry.ihometablet.ui.home.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import com.google.android.material.button.MaterialButton;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;

import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.dialogs.IconSelectDialog;
import me.g33ry.ihometablet.ui.home.HomeFragment;
import me.g33ry.ihometablet.ui.home.devices.Device;
import me.g33ry.ihometablet.ui.home.scenes.Scene;

public class EditSceneView extends PopupView {
    private OnChangeListener onChangeListener;
    private ConstraintLayout scene_popup_parent;
    private ConstraintLayout scene_popup_con_name;
    private ImageView scene_popup_image_icon;
    private EditText scene_popup_input_name;
    private RecyclerView scene_popup_recycler_items;
    private MaterialButton scene_popup_button_delete;
    private MaterialButton scene_popup_button_save;

    private Scene scene;
    private String title;
    private int icon;
    private DeviceAdapter deviceAdapter;

    public EditSceneView(@NonNull Context context, Scene scene) {
        super(context);
        super.addView(View.inflate(context, R.layout.view_scene_popup_layout, null));
        this.scene = scene;
        initControl();
    }

    public EditSceneView(@NonNull Context context) {
        super(context);
        super.addView(View.inflate(context, R.layout.view_scene_popup_layout, null));
        this.scene = new Scene();
        initControl();
    }

    public EditSceneView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.addView(View.inflate(context, R.layout.view_scene_popup_layout, null));
        initControl();
    }

    private void initControl() {
        this.title = scene.getTitle();
        this.icon = scene.getIcon();

        onChangeListener = (title, icon) -> {};
        scene_popup_parent = findViewById(R.id.scene_popup_parent);
        scene_popup_con_name = findViewById(R.id.scene_popup_con_name);
        scene_popup_image_icon = findViewById(R.id.scene_popup_image_icon);
        scene_popup_input_name = findViewById(R.id.scene_popup_input_name);
        scene_popup_recycler_items = findViewById(R.id.scene_popup_recycler_items);
        scene_popup_button_delete = findViewById(R.id.scene_popup_button_delete);
        scene_popup_button_save = findViewById(R.id.scene_popup_button_save);


        scene_popup_input_name.setText(scene.getTitle());
        scene_popup_image_icon.setImageDrawable(ContextCompat.getDrawable(getContext(), scene.getIcon()));
        scene_popup_recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 5));

        scene_popup_image_icon.setOnClickListener((v -> {
            IconSelectDialog dialog = new IconSelectDialog(icon, icon -> {
                this.icon = icon;
                scene_popup_image_icon.setImageDrawable(ContextCompat.getDrawable(getContext(), icon));
            }, this.getContext());

            dialog.show();
        }));

        scene_popup_button_delete.setOnClickListener((v) -> {
            scene.delete(getContext());
            close();
        });

        scene_popup_button_save.setOnClickListener((v) -> {
            scene.setIcon(this.icon);
            scene.setTitle(this.title);
            scene.save(getContext(), deviceAdapter.getCheckedIds());
            onChangeListener.onChange(scene.getTitle(), scene.getIcon());
        });


        ArrayList<Device> devices = (ArrayList<Device>) HomeFragment.devices.clone();
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            if (device.getId().equals("all")) {
                devices.remove(i);
                break;
            }
        }

        this.deviceAdapter = new DeviceAdapter(devices, scene.getDeviceIds());
        scene_popup_recycler_items.setAdapter(deviceAdapter);

//        SET FULLSCREEN WHEN KEYBOARD CLOSED
        KeyboardVisibilityEvent.setEventListener((Activity) getContext(), isOpen -> {
            if(!isOpen){
                ((Activity) getContext()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                this.title = scene_popup_input_name.getText().toString();
            }
        });
    }


    @Override
    public void close() {
        super.close();
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public static class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
        private final ArrayList<Device> devices;
        private final ArrayList<String> checkedIds;
        public DeviceAdapter(ArrayList<Device> devices, ArrayList<String> checkedIds) {
            this.devices = devices;
            this.checkedIds = checkedIds;
        }

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new DeviceViewHolder(inflater.inflate(R.layout.view_edit_scene_device, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            Device device = devices.get(position);
            holder.init(device, checkedIds.contains(device.getId()), (v) -> {
                if(!checkedIds.contains(device.getId())) checkedIds.add(device.getId());
                else checkedIds.remove(device.getId());
            });
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public ArrayList<String> getCheckedIds() {
            return checkedIds;
        }

        public static class DeviceViewHolder extends RecyclerView.ViewHolder{
            private boolean checked;

            private final ConstraintLayout scene_device_con;
            private final ImageView scene_device_image_icon;
            private final TextView scene_device_text_title;
            private final ImageView scene_device_image_radio;

            public DeviceViewHolder(@NonNull View itemView) {
                super(itemView);
                this.checked = false;
                scene_device_con = itemView.findViewById(R.id.scene_device_con);
                scene_device_image_icon = itemView.findViewById(R.id.scene_device_image_icon);
                scene_device_text_title = itemView.findViewById(R.id.scene_device_text_title);
                scene_device_image_radio = itemView.findViewById(R.id.scene_device_image_radio);
            }

            public void init(Device device, boolean checked, OnClickListener onClickListener){
                this.checked = checked;

                scene_device_image_icon.setImageDrawable(ContextCompat.getDrawable(scene_device_con.getContext(), device.getIcon()));
                scene_device_text_title.setText(device.getTitle());
                scene_device_image_radio.setImageDrawable(ContextCompat.getDrawable(scene_device_con.getContext(), checked ? R.drawable.ic_checked : R.drawable.ic_unchecked));


                scene_device_con.setOnClickListener((v) -> {
                    this.checked = !this.checked;
                    scene_device_image_radio.setImageDrawable(ContextCompat.getDrawable(scene_device_con.getContext(), this.checked ? R.drawable.ic_checked : R.drawable.ic_unchecked));
                    onClickListener.onClick(v);
                });
            }
        }
    }


    public interface OnChangeListener{
        void onChange(String title, int icon);
    }
}

