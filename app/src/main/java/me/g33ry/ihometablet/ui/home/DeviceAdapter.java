package me.g33ry.ihometablet.ui.home;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.ui.home.devices.Blinds;
import me.g33ry.ihometablet.ui.home.devices.Device;
import me.g33ry.ihometablet.ui.home.devices.Light;
import me.g33ry.ihometablet.ui.home.devices.PC;
import me.g33ry.ihometablet.ui.home.views.EditBlindsView;
import me.g33ry.ihometablet.ui.home.views.EditLightView;

public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ConstraintLayout container;
    private ArrayList<Device> devices;

    public DeviceAdapter(ArrayList<Device> devices, ConstraintLayout container) {
        this.devices = devices;
        this.container = container;
    }

    @Override
    public int getItemViewType(int position) {
        return devices.get(position).getType().getValue();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        if(viewType == Device.DeviceType.LIGHT.getValue()){
            View view = inflater.inflate(Device.DeviceType.LIGHT.getLayout(), parent, false);
            viewHolder = new LightViewHolder(view);
        }else if(viewType == Device.DeviceType.PC.getValue()){
            View view = inflater.inflate(Device.DeviceType.PC.getLayout(), parent, false);
            viewHolder = new PCViewHolder(view);
        }else if(viewType == Device.DeviceType.Blinds.getValue()){
            View view = inflater.inflate(Device.DeviceType.Blinds.getLayout(), parent, false);
            viewHolder = new BlindsViewHolder(view);
        }else{
            View view = inflater.inflate(Device.DeviceType.UNKNOWN.getLayout(), parent, false);
            viewHolder = new DefaultViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if(viewType == Device.DeviceType.LIGHT.getValue()){
            LightViewHolder viewHolder = (LightViewHolder) holder;
            viewHolder.init((Light) devices.get(position), container);
            viewHolder.setOnAllChangedListener((percentage, color, on) -> {
                System.out.println("ON ALL CHANGED");
                for (Device device: devices) {
                    if(device.getType() == Device.DeviceType.LIGHT){
                        Light light = (Light) device;
                        light.localeUpdate(percentage, color, on);
                    }
                }
                notifyDataSetChanged();
            });
            viewHolder.update();
        }else if(viewType == Device.DeviceType.PC.getValue()){
            PCViewHolder viewHolder = (PCViewHolder) holder;
            viewHolder.init((PC) devices.get(position), container);
        }else if(viewType == Device.DeviceType.Blinds.getValue()){
            BlindsViewHolder viewHolder = (BlindsViewHolder) holder;
            viewHolder.init((Blinds) devices.get(position), container);
        }

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DefaultViewHolder extends RecyclerView.ViewHolder {
        public DefaultViewHolder(@NonNull View view) {
            super(view);
        }
    }

    public static class LightViewHolder extends RecyclerView.ViewHolder {
        private OnAllChangedListener onAllChangedListener;
        public ConstraintLayout container;
        public Light light = null;
        public ConstraintLayout con_light;
        public ImageView light_image_icon;
        public TextView light_text_title;
        public TextView light_text_percent;
        public ImageView light_image_color;

        @SuppressLint("ClickableViewAccessibility")
        public LightViewHolder(@NonNull View view) {
            super(view);
            con_light = view.findViewById(R.id.con_light);
            light_image_icon = view.findViewById(R.id.light_image_icon);
            light_text_title = view.findViewById(R.id.light_text_title);
            light_text_percent = view.findViewById(R.id.light_text_percent);
            light_image_color = view.findViewById(R.id.light_image_color);

            con_light.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    con_light.animate().scaleX(0.9f).setDuration(50);
                    con_light.animate().scaleY(0.9f).setDuration(50);
                }else{
                    con_light.animate().scaleX(1f).setDuration(50);
                    con_light.animate().scaleY(1f).setDuration(50);
                }
                return false;
            });
            con_light.setOnClickListener(v -> toggle());
            con_light.setOnLongClickListener(v -> longClick());
            this.onAllChangedListener= (percentage, color, on) -> {};
        }

        public void toggle(){
            boolean on = !light.getState().on;

            light.toggle(on);
            update(light.getTitle(), light.getState().percentage, light.getColorInt(), on);
        }

        public boolean longClick(){
            if(Light.editView != null) {
                container.removeView(Light.editView);
                Light.editView = null;
            }

            Light.editView = new EditLightView(container.getContext(), light);
            Light.editView .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Light.editView .setId(HomeFragment.lightId);
            container.addView(Light.editView );

            ConstraintSet set = new ConstraintSet();
            set.clone(container);
            set.connect(Light.editView .getId(), ConstraintSet.START, container.getId(), ConstraintSet.START);
            set.connect(Light.editView .getId(), ConstraintSet.END, container.getId(), ConstraintSet.END);
            set.connect(Light.editView .getId(), ConstraintSet.BOTTOM, container.getId(), ConstraintSet.BOTTOM, 0);
            set.applyTo(container);
            return true;
        }

        private void init(Light light, ConstraintLayout container){
            this.light = light;
            this.light.setOnChangeListener((name, on, percentage, color) -> update(name, on, percentage, color));
            this.container = container;
        }

        public void update(){
            if(light == null) return;
            light_text_title.setText(light.getTitle());
            light_text_percent.setText(light.getState().percentage + "%");
            light_image_color.setBackgroundTintList(ColorStateList.valueOf(light.getColorInt()));
            con_light.setBackgroundTintList(ContextCompat.getColorStateList(con_light.getContext(), light.getState().on  ? R.color.device_enabled : R.color.device_disabled));
            light_image_icon.setImageDrawable(ContextCompat.getDrawable(con_light.getContext(), light.getState().on  ? R.drawable.light_on: R.drawable.light_off));
        }

        public void update(String name, int percentage, int color, boolean on){
            if(light == null) return;
            if(light.getId().equals("all")) onAllChangedListener.allChanged(percentage, color, on);
            light_text_title.setText(name);
            light_text_percent.setText(percentage + "%");
            light_image_color.setBackgroundTintList(ColorStateList.valueOf(color));
            con_light.setBackgroundTintList(ContextCompat.getColorStateList(con_light.getContext(), on  ? R.color.device_enabled : R.color.device_disabled));
            light_image_icon.setImageDrawable(ContextCompat.getDrawable(con_light.getContext(), on  ? R.drawable.light_on: R.drawable.light_off));
        }

        public void setOnAllChangedListener(OnAllChangedListener onAllChangedListener){
            this.onAllChangedListener = onAllChangedListener;
        }

        public interface OnAllChangedListener{
            void allChanged(int percentage, int color, boolean on);
        }
    }

    public static class PCViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout container;
        public PC pc = null;
        public ConstraintLayout con_pc;
        public ImageView pc_image_icon;
        public TextView pc_text_title;
        public TextView pc_text_status;

        @SuppressLint("ClickableViewAccessibility")
        public PCViewHolder(@NonNull View view) {
            super(view);
            con_pc = view.findViewById(R.id.con_pc);
            pc_image_icon = view.findViewById(R.id.pc_image_icon);
            pc_text_title = view.findViewById(R.id.pc_text_title);
            pc_text_status = view.findViewById(R.id.pc_text_status);

            con_pc.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    con_pc.animate().scaleX(0.9f).setDuration(50);
                    con_pc.animate().scaleY(0.9f).setDuration(50);
                }else{
                    con_pc.animate().scaleX(1f).setDuration(50);
                    con_pc.animate().scaleY(1f).setDuration(50);
                }
                return false;
            });
            con_pc.setOnClickListener(v -> toggle());
        }

        public void toggle(){
            boolean on = !pc.isOn();

            pc.toggle(on);
            update(pc.getTitle(), on);
        }


        private void init(PC pc, ConstraintLayout container){
            this.pc = pc;
            this.pc.setOnChangeListener(this::update);
            this.container = container;
            update(pc.getTitle(), pc.isOn());
        }

        public void update(String name,  boolean on){
            if(pc == null) return;
            pc_text_title.setText(name);
            pc_text_status.setText(on ? R.string.on : R.string.off);
            con_pc.setBackgroundTintList(ContextCompat.getColorStateList(con_pc.getContext(), on  ? R.color.device_enabled : R.color.device_disabled));
            pc_image_icon.setImageDrawable(ContextCompat.getDrawable(con_pc.getContext(), on  ? R.drawable.pc_on: R.drawable.pc_off));
        }

    }

    public static class BlindsViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout container;
        public Blinds blinds = null;
        public ConstraintLayout con_blinds;
        public ImageView blinds_image_icon;
        public TextView blinds_text_title;
        public TextView blinds_text_status;

        @SuppressLint("ClickableViewAccessibility")
        public BlindsViewHolder(@NonNull View view) {
            super(view);
            con_blinds = view.findViewById(R.id.con_blinds);
            blinds_image_icon = view.findViewById(R.id.blinds_image_icon);
            blinds_text_title = view.findViewById(R.id.blinds_text_title);
            blinds_text_status = view.findViewById(R.id.blinds_text_status);

            con_blinds.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    con_blinds.animate().scaleX(0.9f).setDuration(50);
                    con_blinds.animate().scaleY(0.9f).setDuration(50);
                }else{
                    con_blinds.animate().scaleX(1f).setDuration(50);
                    con_blinds.animate().scaleY(1f).setDuration(50);
                }
                return false;
            });
            con_blinds.setOnLongClickListener(v -> longClick());
            con_blinds.setOnClickListener(v -> toggle());
        }

        public void toggle(){
            blinds.setPercentage(blinds.getPercentage() > 0 ? 0 : 100);
            update(blinds.getTitle(), blinds.getPercentage() > 0 ? 100 : 0);
        }

          public boolean longClick(){
                if(Blinds.editView != null) {
                    container.removeView(Blinds.editView);
                    Blinds.editView = null;
                }

                Blinds.editView = new EditBlindsView(container.getContext(), blinds);
                Blinds.editView .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Blinds.editView .setId(HomeFragment.lightId);
                container.addView(Blinds.editView );

                ConstraintSet set = new ConstraintSet();
                set.clone(container);
                set.connect(Blinds.editView .getId(), ConstraintSet.START, container.getId(), ConstraintSet.START);
                set.connect(Blinds.editView .getId(), ConstraintSet.END, container.getId(), ConstraintSet.END);
                set.connect(Blinds.editView .getId(), ConstraintSet.BOTTOM, container.getId(), ConstraintSet.BOTTOM, 0);
                set.applyTo(container);
                return true;
        }

        private void init(Blinds blinds, ConstraintLayout container){
            this.blinds = blinds;
            this.blinds.setOnChangeListener(this::update);
            this.container = container;
            update(blinds.getTitle(), blinds.getPercentage());
        }

        public void update(String name,  int percentage){
            if(blinds == null) return;
            blinds_text_title.setText(name);
            blinds_text_status.setText(percentage + "%");
            con_blinds.setBackgroundTintList(ContextCompat.getColorStateList(con_blinds.getContext(), percentage > 0  ? R.color.device_enabled : R.color.device_disabled));
            blinds_image_icon.setImageDrawable(ContextCompat.getDrawable(con_blinds.getContext(), percentage > 0  ? R.drawable.blind_on: R.drawable.blind_off));
        }

    }

}
