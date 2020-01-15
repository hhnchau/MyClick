package kingpesdev.firebaseapp.myclick.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import kingpesdev.firebaseapp.myclick.DragListener;
import kingpesdev.firebaseapp.myclick.R;

public class FloatingClickService extends Service {
    private static final String TAG = "FloatingClickService";
    private WindowManager manager;
    private View v;
    private int[] location = new int[2];
    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        Log.d(TAG, "FloatingClickService onCreated");
        int startDragDistance = dp2px(10);
        v = LayoutInflater.from(this).inflate(R.layout.view, null);

        int FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (manager != null)
            manager.addView(v, params);


        v.setOnTouchListener(new DragListener(params, startDragDistance) {
            @Override
            public void onTouch() {
                viewOnClick(v);
            }

            @Override
            public void onDrag() {
                if (manager != null)
                    manager.updateViewLayout(v, params);
            }
        });
    }

    private boolean isOn;

    private void viewOnClick(final View v) {
        v.getLocationOnScreen(location);

        Toast.makeText(this, (location[0] + v.getRight() + 10) + " - " + (location[1] + v.getBottom() + 10), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "location 0:" + location[0]);
        Log.d(TAG, "location 1:" + location[1]);
        Log.d(TAG, "view getRight:" + v.getRight());
        Log.d(TAG, "view getBottom:" + v.getBottom());
        Log.d(TAG, "view getWidth:" + v.getHeight());
        Log.d(TAG, "view getHeight:" + v.getHeight());

        if (isOn) {
            timer.cancel();
        } else {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    AutoClickService.autoClickService.click(location[0] + v.getRight() + 10, location[1] + v.getBottom() + 10);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FloatingClickService.this, "A", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }, 0, 5000);
        }


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AutoClickService.autoClickService.click(location[0] + v.getRight() + 10, location[1] + v.getBottom() + 10);
//            }
//        }, 3000);

        //createPoint(location[0] + v.getRight() + 10, location[1] + v.getBottom() + 10);
    }

    private int dp2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
        if (manager != null)
            manager.removeView(v);
    }

    private void createPoint(int x, int y) {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (wm != null) {
            View v = new View(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            v.setLayoutParams(layoutParams);

            final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                    10, 10,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            parameters.x = x;
            parameters.y = y;
            wm.addView(v, parameters);
        }
    }

}
