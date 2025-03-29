package devkit.blade.vuzix.com.blade_template_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.hud.actionmenu.CustomActionMenuActivity;
import com.vuzix.hud.actionmenu.DefaultActionMenuItemView;

import devkit.blade.vuzix.com.blade_template_app.weather.Weather;
import devkit.blade.vuzix.com.blade_template_app.weather.WeatherManager;


/**
 * Main Template Activity, This application follows the Center Lock style of the Vuzix Camera App.
 * All Navigation buttons are MenuItems and the Rotation is handle by the ActionMenuActivity.
 * The Center of the screen is your normal Layout.
 * For more information on the ActionMenuActivity read the JavaDocs in Android Studio or download the
 * Java docs at:  https://www.vuzix.com/support/Downloads_Drivers
 */
public class center_content_template_activity extends CustomActionMenuActivity implements WeatherManager.OnWeatherUpdateListener {

    private boolean statusState = true;
    private int statusCount = 1;

    private MenuItem HelloMenuItem;
    private MenuItem VuzixMenuItem;
    private MenuItem BladeMenuItem;
    private MenuItem WeatherMenuItem;
    private SwitchMenuItemView switchMenuItemView;
    private TextView mainText;
    private TextView weatherText;
    
    // 날씨 관리자
    private WeatherManager weatherManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_content_template_style);

        mainText = findViewById(R.id.main_text);
        weatherText = findViewById(R.id.weather_text);
        
        // 날씨 관리자 초기화
        weatherManager = WeatherManager.getInstance();
        weatherManager.init(this); // 컨텍스트 전달 및 저장된 상태 복원
        
        // 앱 시작 시 자동으로 날씨 업데이트 시작
        if (!weatherManager.isUpdateActive()) {
            weatherManager.startWeatherUpdates();
        }
        
        // 날씨 텍스트뷰 표시
        weatherText.setVisibility(android.view.View.VISIBLE);
    }

    /**
     * ActionMenuActivity의 onResume 메서드를 오버라이드하여 BroadcastReceiver 등록 문제를 해결합니다.
     * 상위 클래스의 onResume 메서드 대신 직접 필요한 초기화를 수행합니다
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        // 날씨 업데이트 리스너 등록
        weatherManager.addListener(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // 날씨 업데이트 리스너 해제
        weatherManager.removeListener(this);
    }

    /**
     *  Main override to create the ACTION MENU. Notice that this is onCreate-ACTION-MENU. Not to be
     *  confuse with onCreate-Option-Menu which will create the basic Android menu that will not
     *  display properly in the small device screen.
     * @param menu Menu to inflate too.
     * @return Return if menu was setup correctly.
     */
    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        HelloMenuItem = menu.findItem(R.id.action_menu_hello);
        VuzixMenuItem = menu.findItem(R.id.action_menu_vuzix);
        BladeMenuItem = menu.findItem(R.id.action_menu_blade);
        BladeMenuItem.setActionView(switchMenuItemView = new SwitchMenuItemView(this));
        
        // 날씨 메뉴 아이템 추가
        WeatherMenuItem = menu.findItem(R.id.action_menu_weather);
        if (WeatherMenuItem == null) {
            // 메뉴 항목이 없으면 프로그래밍 방식으로 추가
            WeatherMenuItem = menu.add(Menu.NONE, R.id.action_menu_weather, Menu.NONE, 
                    weatherManager.isUpdateActive() ? "날씨 비활성화" : "날씨 활성화");
            WeatherMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        
        updateMenuItems();

        return true;
    }

    /**
     * Override of the ActionMenuActivity. TRUE will tell the system to always show the Action Menu in
     * the position that you ask for. If this is false, the action menu will be hidden and will be
     * presented upon the Menu Option Gesture (1 Finger hold for 1 second.)
     * https://www.vuzix.com/Developer/KnowledgeBase/Detail/65
     */
    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }

    /**
     * Override of the ActionMenuActivity. This will tell the BladeOS which item to start at and
     * which one if the default action to start at on activity restarts.
     * @return
     */
    @Override
    protected int getDefaultAction() {
        return 1;
    }

    private void updateMenuItems() {
        if (HelloMenuItem == null) {
            return;
        }

        VuzixMenuItem.setEnabled(false);
        BladeMenuItem.setEnabled(false);
        switchMenuItemView.setSwitchState(statusState, statusCount);
        
        // 날씨 메뉴 아이템 텍스트 업데이트
        if (WeatherMenuItem != null) {
            WeatherMenuItem.setTitle(weatherManager.isUpdateActive() ? "날씨 비활성화" : "날씨 활성화");
        }
    }

    // 날씨 업데이트 토글 메서드
    public void toggleWeather(MenuItem item) {
        if (weatherManager.isUpdateActive()) {
            weatherManager.stopWeatherUpdates();
            showToast("날씨 업데이트가 중지되었습니다.");
        } else {
            weatherManager.startWeatherUpdates();
            showToast("날씨 업데이트가 시작되었습니다.");
        }
        
        // 메뉴 아이템 텍스트 업데이트
        if (WeatherMenuItem != null) {
            WeatherMenuItem.setTitle(weatherManager.isUpdateActive() ? "날씨 비활성화" : "날씨 활성화");
        }
    }

    // WeatherManager.OnWeatherUpdateListener 인터페이스 구현
    @Override
    public void onWeatherUpdated(Weather weather) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (weatherText != null) {
                    weatherText.setText(weather.toString());
                    // 날씨 업데이트 시 항상 표시되도록 설정
                    weatherText.setVisibility(android.view.View.VISIBLE);
                }
            }
        });
    }

    //Action Menu Click events
    //This events where register via the XML for the menu definitions.
    public void showHello(MenuItem item){

        showToast(getString(R.string.Hello));
        mainText.setText(getString(R.string.Hello));
        VuzixMenuItem.setEnabled(true);
        BladeMenuItem.setEnabled(true);
    }

    public void showVuzix(MenuItem item){
        showToast(getString(R.string.Vuzix));
        mainText.setText(getString(R.string.Vuzix));
    }

    public void showBlade(MenuItem item){
        showToast(getString(R.string.Blade1));
        statusState = !statusState;
        statusCount++;
        switchMenuItemView.setSwitchState(statusState, statusCount);
        mainText.setText(getString(R.string.Blade,statusCount));
    }

    public void showbottomlock(MenuItem item)
    {
        startActivity(new Intent(this, around_content_template_activity.class));
    }

    public void showpopUp(MenuItem item)
    {
        startActivity(new Intent(this, center_content_pop_up_menu_template_activity.class));
    }

    private void showToast(final String text){

        final Activity activity = this;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Custom Class to allow the view to change on the dynamicly.
     * Notice that we utilize this class on the actual MenuItem for its ActionView class.
     * This will allow us to access internal fields like icon to modify the action view itself.
     * For more information see the Class definition for DefaultActionMenuItemView and for
     * onCreateActionMenu.
     */
    private static class SwitchMenuItemView extends DefaultActionMenuItemView {

        public SwitchMenuItemView(Context context) {
            super(context);
        }

        private void setSwitchState(boolean on, int times) {
            if (on) {
                icon.setImageTintList(getResources().getColorStateList(com.vuzix.hud.actionmenu.R.color.action_menu_item_text_color));
                setIcon(getResources().getDrawable(R.drawable.baseline_perm_device_information_24, getContext().getTheme()));
                setTitle(getResources().getString(R.string.Blade,times));
            } else {
                icon.setImageTintList(getResources().getColorStateList(com.vuzix.hud.actionmenu.R.color.hud_blue));
                setIcon(getResources().getDrawable(R.drawable.baseline_copyright_24,getContext().getTheme()));
                setTitle(getResources().getString(R.string.Blade,times));
            }
        }
    }
}
