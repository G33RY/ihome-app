package me.g33ry.ihometablet.ui.home;

import android.annotation.SuppressLint;
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
import me.g33ry.ihometablet.ui.home.scenes.Scene;
import me.g33ry.ihometablet.ui.home.views.EditSceneView;

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneViewHolder> {
    private ArrayList<Scene> scenes;

    public SceneAdapter(ArrayList<Scene> scenes) {
        this.scenes = scenes;
    }

    @NonNull
    @Override
    public SceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new SceneViewHolder(inflater.inflate(R.layout.view_scene, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SceneViewHolder holder, int position) {
        holder.init(scenes.get(position));
    }

    @Override
    public int getItemCount() {
        return scenes.size();
    }

    public static class SceneViewHolder extends RecyclerView.ViewHolder{
        private final ConstraintLayout scene_con;
        private final ImageView scene_image_icon;
        private final TextView scene_text_title;

        public SceneViewHolder(@NonNull View itemView) {
            super(itemView);

            scene_con = itemView.findViewById(R.id.scene_con);
            scene_image_icon = itemView.findViewById(R.id.scene_image_icon);
            scene_text_title = itemView.findViewById(R.id.scene_text_title);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void init(Scene scene){
            scene_text_title.setText(scene.getTitle());
            scene_image_icon.setImageDrawable(ContextCompat.getDrawable(scene_con.getContext(), scene.getIcon()));

            scene_con.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    scene_con.animate().scaleX(0.9f).setDuration(50);
                    scene_con.animate().scaleY(0.9f).setDuration(50);
                }else{
                    scene_con.animate().scaleX(1f).setDuration(50);
                    scene_con.animate().scaleY(1f).setDuration(50);
                }
                return false;
            });
            scene_con.setOnClickListener((v) -> scene.onClick());
            scene_con.setOnLongClickListener(v -> {
                ConstraintLayout con = ((ConstraintLayout) scene_con.getParent().getParent().getParent());
                if(HomeFragment.editSceneView != null) {
                    con.removeView(HomeFragment.editSceneView);
                    HomeFragment.editSceneView = null;
                }

                HomeFragment.editSceneView = new EditSceneView(scene_con.getContext(), scene);
                HomeFragment.editSceneView.setOnChangeListener((title, icon) -> {
                    scene_text_title.setText(title);
                    scene_image_icon.setImageDrawable(ContextCompat.getDrawable(scene_con.getContext(), icon));
                });
                HomeFragment.editSceneView .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                HomeFragment.editSceneView .setId(HomeFragment.sceneId);
                con.addView(HomeFragment.editSceneView );

                ConstraintSet set = new ConstraintSet();
                set.clone(con);
                set.connect(HomeFragment.editSceneView .getId(), ConstraintSet.START, con.getId(), ConstraintSet.START);
                set.connect(HomeFragment.editSceneView .getId(), ConstraintSet.END, con.getId(), ConstraintSet.END);
                set.connect(HomeFragment.editSceneView .getId(), ConstraintSet.BOTTOM, con.getId(), ConstraintSet.BOTTOM, 0);
                set.applyTo(con);
                return true;
            });



        }
    }
}
