package me.g33ry.ihometablet.ui.home.views;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import me.g33ry.ihometablet.R;

public class CustomSeekBarView extends ConstraintLayout {
    private ConstraintLayout container;
    private View  progressView;
    private TextView valueView;

    private int percentage;

    public interface OnChangeListener {
        void onChange(int percentage);
    }

    private OnChangeListener listener;

    public CustomSeekBarView(@NonNull Context context) {
        super(context);
        initControl(context);
        this.listener = null;
    }

    public CustomSeekBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public CustomSeekBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public CustomSeekBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initControl(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_seek_bar, this);

        container = (ConstraintLayout) findViewById(R.id.seekbar_layout);
        progressView = (View) findViewById(R.id.progress);
        valueView = (TextView) findViewById(R.id.value);

        ViewTreeObserver vto = container.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                progressView.setTop(1);
                if(percentage == 100){
                    progressView.getLayoutParams().height = container.getHeight();
                }else{
                    progressView.getLayoutParams().height = (container.getHeight() / 100 * percentage) + 1;
                }
                progressView.setBackground(ContextCompat.getDrawable(context, percentage == 100 ? R.drawable.seek_bar_max : R.drawable.seek_bar));
//                progressView.getLayoutParams().height = container.getHeight() - (container.getHeight() / 100 * percentage);
            }
        });


//        progressView.getLayoutParams().height = container.getHeight();

        container.setOnTouchListener((v, event) -> {
            double h = container.getHeight();
            double Y = event.getY();
            Y = Math.min(Y, h);
            Y = Math.max(Y, 0);
            Y = Math.max(h - Y, 1);

            progressView.getLayoutParams().height = (int) Y;
            progressView.requestLayout();

            int percentage =  (int) ((Y / h) * 100);
            percentage = Math.max(percentage, 1);
            progressView.setBackground(ContextCompat.getDrawable(context, percentage == 100 ? R.drawable.seek_bar_max : R.drawable.seek_bar));

            if(event.getAction() == MotionEvent.ACTION_UP){
                valueView.setText(percentage + "%");
                this.percentage = percentage;
                this.listener.onChange(this.percentage);
            }

            return true;
        });


        container.setOnLongClickListener(v -> {
            ClipData clipData = ClipData.newPlainText("", "");
            DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
            v.startDrag(clipData, shadowBuilder, v, 0);
            return true;
        });

    }


    public void setColor(Context context, int color){
        progressView.setBackgroundTintList(ColorStateList.valueOf(color));
        valueView.setTextColor(ContextCompat.getColor(context, isColorDark(color) ? R.color.white : R.color.gray));
    }

    private boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114* Color.blue(color))/255;
        return !(darkness < 0.5);
    }

    public void setListener(OnChangeListener listener) {
        this.listener = listener;
    }

    public int getPercent() {
        return percentage;
    }

    public void setPercent(Context context, int percentage) {
        double h = container.getHeight();
        double Y = (((double) percentage) / 100) * h;
//        Y = h - Y;

        progressView.getLayoutParams().height = (int) Y;
        progressView.requestLayout();


        this.percentage = percentage;
        valueView.setText(percentage + "%");
        progressView.setBackground(ContextCompat.getDrawable(context, percentage == 100 ? R.drawable.seek_bar_max : R.drawable.seek_bar));
    }
}
