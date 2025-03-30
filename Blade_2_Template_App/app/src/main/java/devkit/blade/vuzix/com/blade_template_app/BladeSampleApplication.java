package devkit.blade.vuzix.com.blade_template_app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

/**
 * 앱의 기본 Application 클래스
 * 애플리케이션 수준의 초기화와 리소스 관리를 담당합니다.
 */
public class BladeSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 애플리케이션 초기화 코드를 여기에 작성합니다
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
