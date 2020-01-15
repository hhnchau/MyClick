package kingpesdev.firebaseapp.myclick;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.util.List;

import kingpesdev.firebaseapp.myclick.service.AutoClickService;
import kingpesdev.firebaseapp.myclick.service.FloatingClickService;

public class MainActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(MainActivity.this)) {
                    intent = new Intent(MainActivity.this, FloatingClickService.class);
                    startService(intent);
                    onBackPressed();
                } else {
                    askPermission();
                    Toast.makeText(MainActivity.this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkAccess()) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(MainActivity.this, "You need turn on Service to do this", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
    }

    @Override
    protected void onDestroy() {
        if (intent != null) {
            stopService(intent);
        }
        if (AutoClickService.autoClickService != null) {
            AutoClickService.autoClickService.stopSelf();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                AutoClickService.autoClickService.disableSelf();
            AutoClickService.autoClickService = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1000);
    }

    private boolean checkAccess() {
        String packages = getString(R.string.accessibility_service_id);
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (manager == null) return false;
        List<AccessibilityServiceInfo> list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo id : list) {
            if (packages.equals(id.getId())) {
                return true;
            }
        }
        return false;
    }
}
