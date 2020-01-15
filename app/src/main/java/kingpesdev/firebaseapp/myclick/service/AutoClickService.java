package kingpesdev.firebaseapp.myclick.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.DataOutputStream;

import kingpesdev.firebaseapp.myclick.MainActivity;


public class AutoClickService extends AccessibilityService {
    private static final String TAG = "AutoClickService";
    public static AutoClickService autoClickService;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    public void click(int x, int y) {
        Log.d(TAG, "AutoClickService onClick");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            handClick(x, y);
        } else {
            Path path = new Path();
            path.moveTo((float) x, (float) y);
            GestureDescription gestureDescription = new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path, 10, 10)).build();
            dispatchGesture(gestureDescription, null, null);
        }
    }

    private void handClick(final float x, final float y) {

        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    Process process = Runtime.getRuntime().exec("su", null, null);
                    DataOutputStream os = new DataOutputStream(process.getOutputStream());
                    String cmd = "/system/bin/input tap " + x + " " + y + "\n";
                    os.writeBytes(cmd);
                    os.writeBytes("exit\n");
                    os.flush();
                    os.close();
                    process.waitFor();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    //Not Root
                }
            }
        };

        thread.start();

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "AutoClickService onConnected");
        autoClickService = this;
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "AutoClickService onUnbind");
        autoClickService = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AutoClickService onDestroy");
        autoClickService = null;
        super.onDestroy();
    }
}
