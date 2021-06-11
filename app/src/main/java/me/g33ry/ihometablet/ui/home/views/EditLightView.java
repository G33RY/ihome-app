package me.g33ry.ihometablet.ui.home.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.concurrent.atomic.AtomicInteger;

import me.g33ry.ihometablet.R;
import me.g33ry.ihometablet.ui.home.devices.Light;

public class EditLightView extends PopupView {
    private ConstraintLayout light_popup_parent;
    private ConstraintLayout light_popup_con_name;
    private ImageView light_popup_image_icon;
    private EditText light_popup_input_name;
    private ColorPickerView light_popup_color_picker;
    private CustomSeekBarView light_popup_seekbar;
    private Light light;
    private Color[] colors = new Color[] {
            new Color(255, 255, 255, 94, 120), //25
            new Color(255, 0, 64, 85, 0),  //1
            new Color(255, 0, 128, 53, 9), //2
            new Color(255, 0, 192, 21, 27), //3
            new Color(255, 0, 255, -1, 50), //4

            new Color(192, 0, 255, -16, 74), //5
            new Color(128, 0, 255, -25, 103), //6
            new Color(64, 0, 255, -21, 135), //7
            new Color(0, 0, 255, -14, 163), //8

            new Color(0, 64, 255, -3, 192), //9
            new Color(0, 128, 255, 22, 215), //10
            new Color(0, 192, 255, 50, 228), //11
            new Color(0, 255, 255, 77, 234), //12

            new Color(0, 255, 192, 107, 234), //13
            new Color(0, 255, 128, 135, 227), //14
            new Color(0, 255, 64, 162, 205), //15
            new Color(0, 255, 0, 183, 186), //16

            new Color(64, 255, 0, 202, 165), //17
            new Color(128, 255, 0, 212, 136), //18
            new Color(192, 255, 0, 214, 103), //19
            new Color(255, 255, 0, 205, 73), //20

            new Color(255, 192, 0, 187, 46), //21
            new Color(255, 128, 0, 165, 23), //22
            new Color(255, 64, 0, 138, 11), //23
            new Color(255, 0, 0, 110, 1), //24
        };

    public EditLightView(@NonNull Context context, Light light) {
        super(context);
        super.addView(View.inflate(context, R.layout.view_light_popup_layout, null));
        this.light = light;
        initControl(context);
        initListeners(context);
    }

    public EditLightView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.addView(View.inflate(context, R.layout.view_light_popup_layout, null));
        initControl(context);

    }

    private void initControl(Context context) {
        light_popup_parent = findViewById(R.id.light_popup_parent);
        light_popup_con_name = findViewById(R.id.light_popup_con_name);
        light_popup_image_icon = findViewById(R.id.light_popup_image_icon);
        light_popup_input_name = findViewById(R.id.light_popup_input_name);
        light_popup_color_picker = findViewById(R.id.light_popup_color_picker);
        light_popup_seekbar = findViewById(R.id.light_popup_seekbar);

        light_popup_color_picker.setActionMode(ActionMode.LAST);
        light_popup_color_picker.setPaletteDrawable(ContextCompat.getDrawable(context, R.drawable.color_palatte));

        light_popup_input_name.setText(light.getTitle());

        ViewTreeObserver vto = light_popup_seekbar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                light_popup_seekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                light_popup_seekbar.setPercent(context, light.getState().percentage);
                light_popup_seekbar.setColor(context, light.getColorInt());
            }
        });


        ViewTreeObserver vto1 = light_popup_color_picker.getViewTreeObserver();
        final int[] counter = {0};
        vto1.addOnGlobalLayoutListener (() -> {
            if(counter[0] == 0){
                int[] pos = getColorPos(light.getColorInt());
                light_popup_color_picker.setSelectorPoint(pos[0], pos[1]);
                counter[0]++;
            }
        });


    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners(Context context){
        AtomicInteger counter = new AtomicInteger();

        //Color Debugger
//        AtomicReference<String> out = new AtomicReference<>("");
//        final int[] i = {0};
//        light_popup_image_icon.setOnClickListener((v) -> {
//            out.set("+\n " +(i[0]+1) + ". COLOR POS: X: " + colors[i[0]].getX() + " | Y: " + colors[i[0]].getY() + "\n" + "color R: " + colors[i[0]].getRed() + "| G: " + colors[i[0]].getGreen() + "| B: " + colors[i[0]].getBlue());
//            light_popup_color_picker.setSelectorPoint(colors[i[0]].getX(), colors[i[0]].getY());
//            i[0]++;
//            if(i[0] >= colors.length) i[0] = 0;
//        });

        light_popup_color_picker.setColorListener((ColorListener) (color, fromUser) -> {
            if(counter.get() <= 1){
                counter.getAndIncrement();
                return;
            }
            light_popup_seekbar.setColor(context, color);
            light.setColorInt(color);
        });

        light_popup_seekbar.setListener(percentage -> {
            light.setPercentage(percentage);
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


                light.setName(light_popup_input_name.getText().toString());
            }
        });
    }


    private int[] getColorPos(int _color){
        int[] pos = new int[2];
        int R = (_color >> 16) & 0xff;
        int G = (_color >>  8) & 0xff;
        int B = (_color      ) & 0xff;

        Color selected = null;

        for (Color color : colors) {
            int Rdis = Math.abs(R - color.getRed());
            int Gdis = Math.abs(G - color.getGreen());
            int Bdis = Math.abs(B - color.getBlue());

            color.Rdis = Rdis;
            color.Gdis = Gdis;
            color.Bdis = Bdis;

            if(selected == null){
                if(Rdis < 32 && Gdis < 32 && Bdis < 32){
                    selected = color;
                }
            }else{
                if(Rdis < selected.Rdis && Gdis < selected.Gdis && Bdis < selected.Bdis){
                    selected = color;
                }
            }
        }

        if(selected == null) selected = colors[0];

        pos[0] = selected.getX();
        pos[1] = selected.getY();

        return pos;
    }

    static class Color {
        private final int red;
        private final int green;
        private final int blue;
        private final int X;
        private final int Y;

        public int Rdis;
        public int Gdis;
        public int Bdis;

        public Color(int red, int green, int blue, int x, int y) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            X = x + 34;
            Y = y + 34;
        }

        public int getRed() {
            return red;
        }

        public int getGreen() {
            return green;
        }

        public int getBlue() {
            return blue;
        }

        public int getX() {
            return X;
        }

        public int getY() {
            return Y;
        }
    }
}

