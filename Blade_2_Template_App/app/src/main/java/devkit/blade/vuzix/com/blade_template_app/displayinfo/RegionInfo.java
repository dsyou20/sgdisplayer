package devkit.blade.vuzix.com.blade_template_app.displayinfo;

/**
 * 온도 정보를 담는 데이터 클래스
 */
public class RegionInfo {
    // 지역명 (기존 location 필드 활용)
    private String location;
    
    // 온도 정보 내용 (기존 temperature 필드 활용)
    private String temperature;
    
    // 외부 온도 (기존 humidity 필드 활용)
    private String humidity;
    
    // 순환 온도 (기존 windSpeed 필드 활용)
    private String windSpeed;

    /**
     * 기본 생성자
     */
    public RegionInfo() {
        this("", "", "", "");
    }

    /**
     * 매개변수가 있는 생성자
     */
    public RegionInfo(String location, String temperature, String humidity, String windSpeed) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
} 