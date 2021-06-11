package me.g33ry.ihometablet.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.g33ry.ihometablet.MainActivity;
import me.g33ry.ihometablet.R;

public class IconSelectDialog extends Dialog {
    private final ArrayList<Integer> entries = new ArrayList<Integer>(){{
        add(R.drawable.ic_home);
        add(R.drawable.ic_breakfast);
        add(R.drawable.ic_coffe);
        add(R.drawable.ic_dinner);
        add(R.drawable.ic_exit);
        add(R.drawable.ic_heart);
        add(R.drawable.ic_movie);
        add(R.drawable.ic_night);
        add(R.drawable.ic_party);
        add(R.drawable.ic_popcorn);
        add(R.drawable.ic_sunrise);
        add(R.drawable.ic_sunset);
        add(R.drawable.ic_tv);
    }};;
    private int selected;
    private RecyclerView icon_select_recycler_items;
    private OnSelectChangeListener onSelectChangeListener;

    public IconSelectDialog(int selected, OnSelectChangeListener onSelectChangeListener, @NonNull Context context) {
        super(context);
        this.selected = selected;
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public IconSelectDialog(OnSelectChangeListener onSelectChangeListener, @NonNull Context context) {
        super(context);
        this.selected = entries.get(0);
        this.onSelectChangeListener = onSelectChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_icon_select);

        icon_select_recycler_items = findViewById(R.id.icon_select_recycler_items);
        IconItemsAdapter adapter = new IconItemsAdapter(entries, selected, onSelectChangeListener);
        icon_select_recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 5));
        icon_select_recycler_items.setAdapter(adapter);
    }

    @Override
    public void show() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.isDialogShown = true;
    }

    @Override
    protected void onStop() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        MainActivity.isDialogShown = false;
    }


    public static class IconItemsAdapter extends RecyclerView.Adapter<IconItemsAdapter.IconItemViewHolder>{
        private ArrayList<Integer> icons;
        private int selected;
        private final OnSelectChangeListener onSelectChangeListener;

        public IconItemsAdapter(ArrayList<Integer> entries, int selected, OnSelectChangeListener onSelectChangeListener) {
            this.icons = entries;
            this.selected = selected;
            this.onSelectChangeListener = onSelectChangeListener;
        }

        @NonNull
        @Override
        public IconItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
             View view = inflater.inflate(R.layout.dialog_icon_select_item, parent, false);
             return new IconItemViewHolder(view, icon -> {
                 this.selected = icon;
                 onSelectChangeListener.onChange(icon);
                 notifyDataSetChanged();
             });
        }

        @Override
        public void onBindViewHolder(@NonNull IconItemViewHolder holder, int position) {
            holder.update(icons.get(position), icons.get(position) == selected);
        }

        @Override
        public int getItemCount() {
            return icons.size();
        }

        public static class IconItemViewHolder extends RecyclerView.ViewHolder{
            private int icon;
            private final LinearLayout icon_select_item_con;
            private final ImageView icon_select_item_image_icon;
            public IconItemViewHolder(@NonNull View itemView, OnSelectChangeListener onSelectChangeListener) {
                super(itemView);
                icon_select_item_con = itemView.findViewById(R.id.icon_select_item_con);
                icon_select_item_image_icon = itemView.findViewById(R.id.icon_select_item_image_icon);
                icon_select_item_con.setOnClickListener(v -> {
                    onSelectChangeListener.onChange(icon);
                });
            }

            public void update(int icon, boolean isSelected){
                this.icon = icon;
                icon_select_item_image_icon.setImageDrawable(ContextCompat.getDrawable(icon_select_item_con.getContext(), icon));
                icon_select_item_image_icon.setBackground( isSelected ? ContextCompat.getDrawable(icon_select_item_con.getContext(), R.drawable.icon_change_bg) : null);
            }
        }
    }

    public interface OnSelectChangeListener{
        void onChange(int item);
    }
}
