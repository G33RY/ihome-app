package me.g33ry.ihometablet.ui.home.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import me.g33ry.ihometablet.MainActivity;
import me.g33ry.ihometablet.R;

public class PopupView extends ConstraintLayout {
    private final Context context;
    private ConstraintLayout popup_con;
    private RelativeLayout popup_con_header;
    private ImageView popup_image_line;
    private LinearLayout popup_con_content;
    public int pullDistance = 100;
    public int animationTime = 300;

    public PopupView(Context context) {
        super(context);
        this.context = context;
        initControl();
    }

    public PopupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initControl();
    }

    private void initControl() {
        inflate(context, R.layout.popup, this);
        popup_con = findViewById(R.id.popup_con);
        popup_con_header = findViewById(R.id.popup_con_header);
        popup_image_line = findViewById(R.id.popup_image_line);
        popup_con_content = findViewById(R.id.popup_con_content);

        FrameLayout.LayoutParams popupParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupParams.gravity = Gravity.BOTTOM;
        this.setLayoutParams(popupParams);

        PopupView view = this;
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.setTranslationY(view.getHeight());
                view.animate().translationY(0).setDuration(animationTime);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        this.setOnTouchListener(this::onTouch);

        MainActivity.isDialogShown = true;
    }

    public void close() {
        this.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
        );
        this.animate().translationY(this.getHeight()).setDuration(animationTime).withEndAction(() -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            ((ViewGroup) this.getParent()).removeView(this);
        });
        MainActivity.isDialogShown = false;
    }


    private float touchDownY = 0;

    public boolean onTouch(View v, MotionEvent event) {
        float Y = event.getY(); // Mouse Y
        float percentage = Math.min(1, Math.max(0, ((Y - touchDownY) / pullDistance)));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchDownY = Y;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            popup_image_line.setScaleX((float) (1 - (0.8 * percentage)));
            popup_con_content.setTranslationY((pullDistance * percentage));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            touchDownY = 0;
            popup_con_content.animate().translationY(0);
            popup_image_line.animate().scaleX(1);

            if (percentage == 1) {
                close();
            }
        }
        return true;
    }

    private float currentY = 0;

    public boolean onTouchWithScroll(View v, MotionEvent event) {
//        CREATE A SCROLL LOGIC
        return true;
    }

    @Override
    public void addView(View child) {
        if (child.getId() != R.id.popup_con) {
            popup_con_content.addView(child);
            return;
        }
        popup_con = (ConstraintLayout) child;
        super.addView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child.getId() != R.id.popup_con) {
            ((LinearLayout) popup_con.findViewById(R.id.content)).addView(child, params);
            return;
        }
        popup_con = (ConstraintLayout) child;
        super.addView(child, index, params);
    }
}
