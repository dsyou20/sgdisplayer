package devkit.blade.vuzix.com.blade_template_app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

import devkit.blade.vuzix.com.blade_template_app.displayinfo.RegionInfo;
import devkit.blade.vuzix.com.blade_template_app.displayinfo.RegionInfoManager;

/**
 * 온도 정보 표시 앱
 * 
 * 이 앱은 스마트 글래스에 외부온도와 순환온도 정보를 표시합니다.
 * API를 통해 실시간 데이터를 가져와 사용자에게 보여줍니다.
 */
public class center_content_template_activity extends Activity implements RegionInfoManager.OnRegionInfoUpdateListener {
    private static final String TAG = "TempInfoActivity";
    
    // UI 요소
    private TextView mainText;
    
    // 정보 관리자
    private RegionInfoManager infoManager;
    
    // 핸들러
    private Handler handler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 타이틀바 숨기기
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        
        // 레이아웃 설정
        setContentView(R.layout.activity_center_content_template_style);
        
        // 핸들러 초기화
        handler = new Handler(Looper.getMainLooper());
        
        // 정보 표시를 위한 텍스트뷰 초기화
        mainText = findViewById(R.id.main_text);
        
        // 정보 관리자 초기화
        infoManager = RegionInfoManager.getInstance();
        infoManager.init(this);
        
        // 정보 업데이트 시작
        infoManager.startInfoUpdates();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 리스너 등록
        infoManager.addListener(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 리스너 해제
        infoManager.removeListener(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 앱 종료 시 정보 업데이트 중지 및 자원 해제
        infoManager.stopInfoUpdates();
        infoManager.cleanup();
    }
    
    /**
     * 정보 업데이트 이벤트 핸들러
     */
    @Override
    public void onRegionInfoUpdated(RegionInfo info) {
        runOnUiThread(() -> {
            // 화면에 정보 표시
            mainText.setText(info.getTemperature());
        });
    }
}
