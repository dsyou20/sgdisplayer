package devkit.blade.vuzix.com.blade_template_app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.vuzix.hud.resources.DynamicThemeApplication;

/**
 * Main Application reference in the Manifest. This is the main application instance that gets loaded
 * and those properties and instantiation are passed to the rest of the application.
 * The DynamicThemeApplication allow the user to intercept and modify the theme base on the BladeOS
 * Interpretation of Light around the user. This allows for dynamic theme modification for different
 * light environment situations.
 * For more information on Android manifest definitions: https://developer.android.com/guide/topics/manifest/manifest-intro
 * For more information on the DynamicThemeApplication read the JavaDocs in Android Studio or download the
 * Java docs at:  https://www.vuzix.com/support/Downloads_Drivers
 */
public class BladeSampleApplication extends DynamicThemeApplication {

    @Override
    public void onCreate() {
        // 부모 클래스의 onCreate 호출 전에 필요한 초기화를 수행합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상에서는 registerReceiver 메서드를 오버라이드하여 플래그 설정
            super.onCreate();
        } else {
            // 이전 Android 버전에서는 기본 동작 유지
            super.onCreate();
        }
    }

    @Override
    protected int getNormalThemeResId() {
        return R.style.AppTheme;
    }

    @Override
    protected int getLightThemeResId() {
        return R.style.AppTheme_Light;
    }
    
    // Android 13 (API 33, TIRAMISU) 이상에서 BroadcastReceiver 등록 메서드를 오버라이드
    @Override
    public Intent registerReceiver(android.content.BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            return super.registerReceiver(receiver, filter);
        }
    }
}
