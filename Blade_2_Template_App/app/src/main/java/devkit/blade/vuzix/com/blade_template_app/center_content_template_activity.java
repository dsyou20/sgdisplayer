package devkit.blade.vuzix.com.blade_template_app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

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
    private TextView[] infoTexts = new TextView[5];
    private ImageView sectionIcon;
    
    // 정보 관리자
    private RegionInfoManager infoManager;
    
    // 핸들러
    private Handler handler;
    
    // 현재 표시할 섹션 인덱스
    private int currentSectionIndex = 0;
    
    // 섹션 표시 간격 (밀리초)
    private static final long SECTION_DISPLAY_INTERVAL = 2000;
    
    // 현재 RegionInfo 객체
    private RegionInfo currentInfo;
    
    // 아이콘 리소스 ID 배열
    private final int[] sectionIcons = {
        R.drawable.ic_environment,
        R.drawable.ic_weather,
        R.drawable.ic_control
    };
    
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
        
        // UI 요소 초기화
        infoTexts[0] = findViewById(R.id.text_info1);
        infoTexts[1] = findViewById(R.id.text_info2);
        infoTexts[2] = findViewById(R.id.text_info3);
        infoTexts[3] = findViewById(R.id.text_info4);
        infoTexts[4] = findViewById(R.id.text_info5);
        sectionIcon = findViewById(R.id.section_icon);
        
        // 정보 관리자 초기화
        infoManager = RegionInfoManager.getInstance();
        infoManager.init(this);
        
        // 정보 업데이트 시작
        infoManager.startInfoUpdates();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        infoManager.addListener(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        infoManager.removeListener(this);
        handler.removeCallbacks(sectionDisplayRunnable);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        infoManager.stopInfoUpdates();
        infoManager.cleanup();
    }
    
    private Runnable sectionDisplayRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentInfo != null) {
                updateDisplay();
                
                // 다음 섹션으로 이동
                currentSectionIndex = (currentSectionIndex + 1) % 3;
                
                // 2초 후에 다음 섹션 표시
                handler.postDelayed(this, SECTION_DISPLAY_INTERVAL);
            }
        }
    };
    
    private void updateDisplay() {
        // 모든 TextView 초기화
        for (TextView tv : infoTexts) {
            tv.setVisibility(View.VISIBLE);
        }
        
        // 아이콘 설정
        sectionIcon.setImageResource(sectionIcons[currentSectionIndex]);
        
        switch (currentSectionIndex) {
            case 0: // 환경 정보
                infoTexts[0].setText("온도: " + currentInfo.getTemperature() + "°C");
                infoTexts[1].setText("습도: " + currentInfo.getHumidity() + "%");
                infoTexts[2].setText("CO2: " + currentInfo.getCo2() + "ppm");
                infoTexts[3].setVisibility(View.GONE);
                infoTexts[4].setVisibility(View.GONE);
                break;
                
            case 1: // 기상 정보
                infoTexts[0].setText("외부온도: " + currentInfo.getOutdoorTemp() + "°C");
                infoTexts[1].setText("일사량: " + currentInfo.getRadiation() + "W/㎡");
                infoTexts[2].setText("풍향: " + currentInfo.getWindDirection());
                infoTexts[3].setText("풍속: " + currentInfo.getWindSpeed() + "m/s");
                infoTexts[4].setText("강우량: " + currentInfo.getRainfall() + "mm");
                break;
                
            case 2: // 제어 정보
                infoTexts[0].setText("천창: " + currentInfo.getSkyWindow());
                infoTexts[1].setText("CO2발생기: " + currentInfo.getCo2Generator());
                infoTexts[2].setText("냉난방기: " + currentInfo.getHvacStatus());
                infoTexts[3].setVisibility(View.GONE);
                infoTexts[4].setVisibility(View.GONE);
                break;
        }
    }
    
    @Override
    public void onRegionInfoUpdated(RegionInfo info) {
        currentInfo = info;
        handler.removeCallbacks(sectionDisplayRunnable);
        handler.post(sectionDisplayRunnable);
    }
}
