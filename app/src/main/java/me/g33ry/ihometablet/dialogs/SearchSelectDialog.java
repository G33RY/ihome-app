package me.g33ry.ihometablet.dialogs;

import android.app.Dialog;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.g33ry.ihometablet.MainActivity;
import me.g33ry.ihometablet.R;

public class SearchSelectDialog extends Dialog {
    private ArrayList<Pair<String, String>> entries;
    private Pair<String, String> selected;
    private EditText search_select_edit_text_search;
    private RecyclerView search_select_recycler_items;
    private OnSelectChangeListener onSelectChangeListener;

    public SearchSelectDialog(ArrayList<Pair<String, String>> entries, Pair<String, String> selected, OnSelectChangeListener onSelectChangeListener, @NonNull Context context) {
        super(context);
        this.entries = entries;
        this.selected = selected;
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public SearchSelectDialog(ArrayList<Pair<String, String>> entries, Pair<String, String> selected, OnSelectChangeListener onSelectChangeListener, @NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.entries = entries;
        this.selected = selected;
        this.onSelectChangeListener = onSelectChangeListener;
    }

    protected SearchSelectDialog(ArrayList<Pair<String, String>> entries, Pair<String, String> selected, OnSelectChangeListener onSelectChangeListener, @NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.entries = entries;
        this.selected = selected;
        this.onSelectChangeListener = onSelectChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search_select);

        search_select_edit_text_search = findViewById(R.id.search_select_edit_text_search);
        search_select_recycler_items = findViewById(R.id.search_select_recycler_items);
        SearchSelectItemsAdapter adapter = new SearchSelectItemsAdapter(entries ,selected, onSelectChangeListener);
        search_select_recycler_items.setLayoutManager(new LinearLayoutManager(getContext()));
        search_select_recycler_items.setAdapter(adapter);
        search_select_edit_text_search.setOnEditorActionListener((v, actionId, event) -> {
            System.out.println(search_select_edit_text_search.getText().toString());

            ArrayList<Pair<String, String>> entries = new ArrayList<>();
            for (Pair<String, String> entry: this.entries) {
                if(entry.second.toLowerCase().startsWith(search_select_edit_text_search.getText().toString().toLowerCase())) entries.add(entry);
            }
            adapter.setItems(entries);
            return true;
        });
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


    public static class SearchSelectItemsAdapter extends RecyclerView.Adapter<SearchSelectItemsAdapter.SearchSelectItemViewHolder>{
        private ArrayList<Pair<String, String>> items;
        private Pair<String, String> selected;
        private final OnSelectChangeListener onSelectChangeListener;

        public SearchSelectItemsAdapter(ArrayList<Pair<String, String>> items, Pair<String, String> selected, OnSelectChangeListener onSelectChangeListener) {
            this.items = items;
            this.selected = selected;
            this.onSelectChangeListener = onSelectChangeListener;
        }

        @NonNull
        @Override
        public SearchSelectItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
             View view = inflater.inflate(R.layout.dialog_search_select_item, parent, false);
             return new SearchSelectItemViewHolder(view, item -> {
                 onSelectChangeListener.onChange(item);
                 this.selected = item;
                 notifyDataSetChanged();
             });
        }

        @Override
        public void onBindViewHolder(@NonNull SearchSelectItemViewHolder holder, int position) {
            holder.update(items.get(position), items.get(position).first.equals(selected.first));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setItems(ArrayList<Pair<String, String>> items) {
            this.items  = items;
            notifyDataSetChanged();
        }

        public static class SearchSelectItemViewHolder extends RecyclerView.ViewHolder{
            private Pair<String, String> item;
            private final LinearLayout search_select_item_con;
            private final ImageView search_select_item_image_icon;
            private final TextView search_select_item_text_title;
            public SearchSelectItemViewHolder(@NonNull View itemView, OnSelectChangeListener onSelectChangeListener) {
                super(itemView);
                search_select_item_con = itemView.findViewById(R.id.search_select_item_con);
                search_select_item_image_icon = itemView.findViewById(R.id.search_select_item_image_icon);
                search_select_item_text_title = itemView.findViewById(R.id.search_select_item_text_title);
                search_select_item_con.setOnClickListener(v -> {
                    onSelectChangeListener.onChange(item);
                });
            }

            public void update(Pair<String, String> item, boolean isSelected){
                this.item = item;
                search_select_item_image_icon.setVisibility(isSelected ? View.VISIBLE : View.GONE);
                search_select_item_text_title.setText(item.second);
            }
        }
    }

    public interface OnSelectChangeListener{
        void onChange(Pair<String, String> item);
    }
}
