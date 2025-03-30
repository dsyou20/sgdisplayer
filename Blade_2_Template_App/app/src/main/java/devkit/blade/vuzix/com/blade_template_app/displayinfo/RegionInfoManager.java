package devkit.blade.vuzix.com.blade_template_app.displayinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 온도 정보 디스플레이 관리 클래스
 */
public class RegionInfoManager {
    private static final String TAG = "InfoManager";
    private static RegionInfoManager instance;
    
    // API URL
    private static final String API_URL = "http://api.gcsmagma.com/gcs_my_api.php/Get_GCS_Data/mysb2/1";
    
    // SharedPreferences 관련 상수
    private static final String PREFS_NAME = "InfoPrefs";
    private static final String KEY_INFO_ACTIVE = "info_active";

    // 정보 데이터
    private RegionInfo currentInfo;
    
    // 업데이트 주기(밀리초)
    private static final long UPDATE_INTERVAL_MS = 30000; // 30초
    
    // 정보 변경 리스너
    private List<OnRegionInfoUpdateListener> listeners = new ArrayList<>();
    
    // 핸들러를 사용한 주기적 업데이트
    private Handler handler;
    private Runnable updateRunnable;
    
    // 스레드 풀
    private ExecutorService executorService;
    
    // 정보 업데이트 활성화 상태
    private boolean isUpdateActive = false;
    
    // 애플리케이션 컨텍스트 참조
    private Context appContext;
    
    // 임시 데이터를 위한 랜덤 생성기
    private Random random = new Random();
    
    // 시간 형식
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private RegionInfoManager() {
        currentInfo = new RegionInfo();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newSingleThreadExecutor();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateRegionInfo();
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
            boolean savedState = prefs.getBoolean(KEY_INFO_ACTIVE, false);
            if (savedState) {
                startInfoUpdates();
            }
        }
    }

    public static synchronized RegionInfoManager getInstance() {
        if (instance == null) {
            instance = new RegionInfoManager();
        }
        return instance;
    }

    /**
     * 정보 업데이트를 시작합니다.
     */
    public void startInfoUpdates() {
        if (!isUpdateActive) {
            Log.d(TAG, "정보 업데이트 시작");
            isUpdateActive = true;
            updateRegionInfo(); // 즉시 한 번 업데이트
            handler.postDelayed(updateRunnable, UPDATE_INTERVAL_MS);
            
            // 상태 저장
            saveState();
        }
    }

    /**
     * 정보 업데이트를 중지합니다.
     */
    public void stopInfoUpdates() {
        if (isUpdateActive) {
            Log.d(TAG, "정보 업데이트 중지");
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
            editor.putBoolean(KEY_INFO_ACTIVE, isUpdateActive);
            editor.apply();
        }
    }

    /**
     * API에서 데이터를 가져옵니다.
     */
    private void fetchDataFromApi() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        
                        parseJsonResponse(response.toString());
                    } else {
                        Log.e(TAG, "API 호출 실패. 응답 코드: " + responseCode);
                        updateWithFallbackData();
                    }
                    
                    connection.disconnect();
                } catch (IOException e) {
                    Log.e(TAG, "API 호출 중 오류 발생", e);
                    updateWithFallbackData();
                }
            }
        });
    }
    
    /**
     * JSON 응답을 파싱합니다.
     */
    private void parseJsonResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray fieldsArray = jsonObject.getJSONArray("fields");
            
            if (fieldsArray.length() > 0) {
                JSONObject data = fieldsArray.getJSONObject(0);
                
                // 외부온도 (xouttemp)
                String outTemp = data.getString("xouttemp");
                
                // 순환온도 (Xsupplytemp1)
                String supplyTemp = data.getString("Xsupplytemp1");
                
                // 현재 시간
                String currentTime = timeFormat.format(new Date());
                
                // 정보 텍스트 구성 (기존 형식으로 복원)
                String infoText = currentTime + " 외부온도: " + outTemp + "°C 순환온도: " + supplyTemp + "°C";
                
                final RegionInfo info = new RegionInfo("", infoText, outTemp, supplyTemp);
                
                // UI 스레드에서 업데이트
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentInfo = info;
                        notifyListeners();
                    }
                });
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON 파싱 오류", e);
            updateWithFallbackData();
        }
    }
    
    /**
     * API 호출이 실패할 경우 대체 데이터로 업데이트합니다.
     */
    private void updateWithFallbackData() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currentTime = timeFormat.format(new Date());
                // 기존 형식으로 복원
                String infoText = currentTime + " 외부온도: --°C 순환온도: --°C";
                
                currentInfo = new RegionInfo("", infoText, "--", "--");
                notifyListeners();
            }
        });
    }

    /**
     * 정보를 업데이트합니다.
     */
    private void updateRegionInfo() {
        fetchDataFromApi();
    }

    /**
     * 현재 정보를 반환합니다.
     */
    public RegionInfo getCurrentInfo() {
        return currentInfo;
    }

    /**
     * 정보 업데이트 리스너를 등록합니다.
     */
    public void addListener(OnRegionInfoUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            // 리스너 등록 시 현재 정보를 즉시 전달
            if (currentInfo != null) {
                listener.onRegionInfoUpdated(currentInfo);
            }
        }
    }

    /**
     * 정보 업데이트 리스너를 제거합니다.
     */
    public void removeListener(OnRegionInfoUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * 모든 리스너에게 정보 업데이트를 알립니다.
     */
    private void notifyListeners() {
        for (OnRegionInfoUpdateListener listener : listeners) {
            listener.onRegionInfoUpdated(currentInfo);
        }
    }

    /**
     * 정보 업데이트 활성화 상태를 반환합니다.
     */
    public boolean isUpdateActive() {
        return isUpdateActive;
    }

    /**
     * 자원 해제
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * 정보 업데이트 리스너 인터페이스
     */
    public interface OnRegionInfoUpdateListener {
        void onRegionInfoUpdated(RegionInfo info);
    }
} 