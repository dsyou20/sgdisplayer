package com.vuzix.hud.actionmenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

/**
 * Vuzix의 ActionMenuActivity를 확장하여 Android 13 이상에서 BroadcastReceiver 등록 관련 문제를 해결하는 클래스입니다.
 * 이 클래스를 상속받는 모든 액티비티는 Android 13 이상에서도 올바르게 BroadcastReceiver를 등록할 수 있습니다.
 */
public class CustomActionMenuActivity extends ActionMenuActivity {

    /**
     * BroadcastReceiver 등록 메서드를 오버라이드하여 Android 13 이상에서 필요한 플래그를 추가합니다.
     */
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            return super.registerReceiver(receiver, filter);
        }
    }
} 