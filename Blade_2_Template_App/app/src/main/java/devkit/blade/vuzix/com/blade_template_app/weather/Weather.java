package devkit.blade.vuzix.com.blade_template_app.weather;

/**
 * 날씨 정보를 저장하는 모델 클래스
 */
public class Weather {
    private String temperature;
    private String condition;
    private String city;
    private String humidity;
    private long timestamp;

    public Weather() {
        // 기본 날씨 정보
        this.temperature = "25°C";
        this.condition = "맑음";
        this.city = "서울";
        this.humidity = "60%";
        this.timestamp = System.currentTimeMillis();
    }

    public Weather(String temperature, String condition, String city, String humidity) {
        this.temperature = temperature;
        this.condition = condition;
        this.city = city;
        this.humidity = humidity;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    public String getCity() {
        return city;
    }

    public String getHumidity() {
        return humidity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return city + ": " + temperature + ", " + condition + ", 습도: " + humidity;
    }
} 