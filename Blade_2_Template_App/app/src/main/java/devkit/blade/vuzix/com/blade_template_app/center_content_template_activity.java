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
        mainText = findViewById(R.id.main_text);
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
        // 리스너 등록
        infoManager.addListener(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 리스너 해제
        infoManager.removeListener(this);
        // 섹션 표시 중지
        handler.removeCallbacks(sectionDisplayRunnable);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 앱 종료 시 정보 업데이트 중지 및 자원 해제
        infoManager.stopInfoUpdates();
        infoManager.cleanup();
    }
    
    /**
     * 섹션을 순차적으로 표시하는 Runnable
     */
    private Runnable sectionDisplayRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentInfo != null) {
                String displayText = "";
                int iconResId = 0;
                
                switch (currentSectionIndex) {
                    case 0:
                        displayText = getEnvironmentInfo();
                        iconResId = sectionIcons[0];
                        break;
                    case 1:
                        displayText = getWeatherInfo();
                        iconResId = sectionIcons[1];
                        break;
                    case 2:
                        displayText = getControlInfo();
                        iconResId = sectionIcons[2];
                        break;
                }
                
                // 아이콘 설정
                sectionIcon.setImageResource(iconResId);
                
                // 텍스트 설정
                mainText.setText(Html.fromHtml(displayText, Html.FROM_HTML_MODE_LEGACY));
                
                // 다음 섹션으로 이동
                currentSectionIndex = (currentSectionIndex + 1) % 3;
                
                // 2초 후에 다음 섹션 표시
                handler.postDelayed(this, SECTION_DISPLAY_INTERVAL);
            }
        }
    };
    
    /**
     * 환경 정보 섹션 텍스트 생성
     */
    private String getEnvironmentInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("<big>온도: ").append(currentInfo.getTemperature()).append("°C</big>\n\n")
          .append("<big>    습도: ").append(currentInfo.getHumidity()).append("%</big>\n\n")
          .append("<big>    CO2: ").append(currentInfo.getCo2()).append("ppm</big>");
        return sb.toString();
    }
    
    /**
     * 기상 정보 섹션 텍스트 생성
     */
    private String getWeatherInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("<big>외부온도: ").append(currentInfo.getOutdoorTemp()).append("°C</big>\n\n")
          .append("<big>    일사량: ").append(currentInfo.getRadiation()).append("W/㎡</big>\n\n")
          .append("<big>    풍향: ").append(currentInfo.getWindDirection()).append("</big>\n\n")
          .append("<big>    풍속: ").append(currentInfo.getWindSpeed()).append("m/s</big>\n\n")
          .append("<big>    강우량: ").append(currentInfo.getRainfall()).append("mm</big>");
        return sb.toString();
    }
    
    /**
     * 제어 정보 섹션 텍스트 생성
     */
    private String getControlInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("<big>천창: ").append(currentInfo.getSkyWindow()).append("</big>\n\n")
          .append("<big>    CO2발생기: ").append(currentInfo.getCo2Generator()).append("</big>\n\n")
          .append("<big>    냉난방기: ").append(currentInfo.getHvacStatus()).append("</big>");
        return sb.toString();
    }
    
    /**
     * 정보 업데이트 이벤트 핸들러
     */
    @Override
    public void onRegionInfoUpdated(RegionInfo info) {
        currentInfo = info;
        // 섹션 표시 시작
        handler.removeCallbacks(sectionDisplayRunnable);
        handler.post(sectionDisplayRunnable);
    }
}
