package me.g33ry.ihometablet.ui.home.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.skydoves.colorpickerview.ActionMode;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.concurrent.atomic.AtomicInteger;

import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.ui.home.devices.Blinds;
import me.g33ry.ihometablet.ui.home.devices.Light;

public class EditBlindsView extends PopupView {
    private ConstraintLayout blinds_popup_parent;
    private ConstraintLayout blinds_popup_con_name;
    private ImageView blinds_popup_image_icon;
    private EditText blinds_popup_input_name;
    private CustomSeekBarView blinds_popup_seekbar;
    private Blinds blinds;

    public EditBlindsView(@NonNull Context context, Blinds blinds) {
        super(context);
        super.addView(View.inflate(context, R.layout.view_blinds_popup_layout, null));
        this.blinds = blinds;
        initControl(context);
        initListeners(context);
    }

    public EditBlindsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.addView(View.inflate(context, R.layout.view_blinds_popup_layout, null));
        initControl(context);

    }

    private void initControl(Context context) {
        blinds_popup_parent = findViewById(R.id.blinds_popup_parent);
        blinds_popup_con_name = findViewById(R.id.blinds_popup_con_name);
        blinds_popup_image_icon = findViewById(R.id.blinds_popup_image_icon);
        blinds_popup_input_name = findViewById(R.id.blinds_popup_input_name);
        blinds_popup_seekbar = findViewById(R.id.blinds_popup_seekbar);

        blinds_popup_input_name.setText(blinds.getTitle());

        ViewTreeObserver vto = blinds_popup_seekbar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                blinds_popup_seekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                blinds_popup_seekbar.setPercent(context, blinds.getPercentage());
                blinds_popup_seekbar.setColor(context, android.graphics.Color.rgb(50, 168, 162));
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners(Context context){
        blinds_popup_seekbar.setListener(percentage -> {
            blinds.setPercentage(percentage);
        });

//        SET FULLSCREEN WHEN KEYBOARD CLOSED
        KeyboardVisibilityEvent.setEventListener((Activity) context, isOpen -> {
            if(!isOpen){
                ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


                blinds.setName(blinds_popup_input_name.getText().toString());
            }
        });
    }
}

