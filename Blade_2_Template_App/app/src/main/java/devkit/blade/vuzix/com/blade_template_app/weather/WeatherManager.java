package devkit.blade.vuzix.com.blade_template_app.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 날씨 정보를 관리하는 싱글톤 클래스
 * 날씨 업데이트를 시작하고 중지하는 기능과 날씨 정보를 리스너에게 전달하는 기능을 제공합니다.
 */
public class WeatherManager {
    private static final String TAG = "WeatherManager";
    private static WeatherManager instance;
    
    // SharedPreferences 관련 상수
    private static final String PREFS_NAME = "WeatherPrefs";
    private static final String KEY_WEATHER_ACTIVE = "weather_active";

    // 날씨 정보
    private Weather currentWeather;
    
    // 업데이트 주기(밀리초)
    private static final long UPDATE_INTERVAL_MS = 60000; // 1분
    
    // 날씨 변경 리스너
    private List<OnWeatherUpdateListener> listeners = new ArrayList<>();
    
    // 핸들러를 사용한 주기적 업데이트
    private Handler handler;
    private Runnable updateRunnable;
    
    // 날씨 업데이트 활성화 상태
    private boolean isUpdateActive = false;
    
    // 애플리케이션 컨텍스트 참조
    private Context appContext;
    
    // 임시 데이터를 위한 랜덤 생성기
    private Random random = new Random();
    
    // 더미 날씨 데이터 (실제로는 API로 대체)
    private final String[] conditions = {"맑음", "흐림", "비", "눈", "안개"};
    private final String[] cities = {"서울", "부산", "대구", "인천", "광주"};

    private WeatherManager() {
        currentWeather = new Weather();
        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateWeather();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
    }
    
    /**
     * 컨텍스트를 설정하고 저장된 상태를 복원합니다.
     */
    public void init(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
            // 저장된 상태 복원
            SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean savedState = prefs.getBoolean(KEY_WEATHER_ACTIVE, false);
            if (savedState) {
                startWeatherUpdates();
            }
        }
    }

    public static synchronized WeatherManager getInstance() {
        if (instance == null) {
            instance = new WeatherManager();
        }
        return instance;
    }

    /**
     * 날씨 업데이트를 시작합니다.
     */
    public void startWeatherUpdates() {
        if (!isUpdateActive) {
            Log.d(TAG, "날씨 업데이트 시작");
            isUpdateActive = true;
            updateWeather(); // 즉시 한 번 업데이트
            handler.postDelayed(updateRunnable, UPDATE_INTERVAL_MS);
            
            // 상태 저장
            saveState();
        }
    }

    /**
     * 날씨 업데이트를 중지합니다.
     */
    public void stopWeatherUpdates() {
        if (isUpdateActive) {
            Log.d(TAG, "날씨 업데이트 중지");
            isUpdateActive = false;
            handler.removeCallbacks(updateRunnable);
            
            // 상태 저장
            saveState();
        }
    }
    
    /**
     * 현재 활성화 상태를 SharedPreferences에 저장합니다.
     */
    private void saveState() {
        if (appContext != null) {
            SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_WEATHER_ACTIVE, isUpdateActive);
            editor.apply();
        }
    }

    /**
     * 날씨 정보를 업데이트합니다. 
     * 실제 구현에서는 API 호출로 대체됩니다.
     */
    private void updateWeather() {
        // 더미 데이터로 날씨 업데이트 (실제로는 API 호출)
        String temperature = (15 + random.nextInt(20)) + "°C";
        String condition = conditions[random.nextInt(conditions.length)];
        String city = cities[random.nextInt(cities.length)];
        String humidity = (30 + random.nextInt(50)) + "%";
        
        currentWeather = new Weather(temperature, condition, city, humidity);
        Log.d(TAG, "날씨 업데이트: " + currentWeather);
        
        // 모든 리스너에게 업데이트 알림
        notifyListeners();
    }

    /**
     * 현재 날씨 정보를 반환합니다.
     */
    public Weather getCurrentWeather() {
        return currentWeather;
    }

    /**
     * 날씨 업데이트 리스너를 등록합니다.
     */
    public void addListener(OnWeatherUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            // 리스너 등록 시 현재 날씨 정보를 즉시 전달
            if (currentWeather != null) {
                listener.onWeatherUpdated(currentWeather);
            }
        }
    }

    /**
     * 날씨 업데이트 리스너를 제거합니다.
     */
    public void removeListener(OnWeatherUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * 모든 리스너에게 날씨 업데이트를 알립니다.
     */
    private void notifyListeners() {
        for (OnWeatherUpdateListener listener : listeners) {
            listener.onWeatherUpdated(currentWeather);
        }
    }

    /**
     * 날씨 업데이트 활성화 상태를 반환합니다.
     */
    public boolean isUpdateActive() {
        return isUpdateActive;
    }

    /**
     * 날씨 업데이트 리스너 인터페이스
     */
    public interface OnWeatherUpdateListener {
        void onWeatherUpdated(Weather weather);
    }
} 