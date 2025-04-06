package devkit.blade.vuzix.com.blade_template_app.displayinfo;

/**
 * 온실 환경 정보를 담는 데이터 클래스
 */
public class RegionInfo {
    // 환경 정보
    private String temperature;    // 온도
    private String humidity;       // 습도
    private String co2;           // 이산화탄소
    private String ph;            // PH
    
    // 제어 정보
    private String skyWindow;     // 천창 상태
    private String co2Generator;  // CO2발생기 상태
    private String hvacStatus;    // 냉난방기 상태
    
    // 기상 정보
    private String outdoorTemp;   // 외부 온도
    private String radiation;     // 일사량
    private String windDirection; // 풍향
    private String windSpeed;     // 풍속
    private String rainfall;      // 강우량
    
    // 표시용 텍스트
    private String displayText;

    /**
     * 기본 생성자
     */
    public RegionInfo() {
        this.temperature = "--";
        this.humidity = "--";
        this.co2 = "--";
        this.ph = "--";
        this.skyWindow = "--";
        this.co2Generator = "--";
        this.hvacStatus = "--";
        this.outdoorTemp = "--";
        this.radiation = "--";
        this.windDirection = "--";
        this.windSpeed = "--";
        this.rainfall = "--";
        this.displayText = "";
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

    public String getCo2() {
        return co2;
    }

    public void setCo2(String co2) {
        this.co2 = co2;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getSkyWindow() {
        return skyWindow;
    }

    public void setSkyWindow(String skyWindow) {
        this.skyWindow = skyWindow;
    }

    public String getCo2Generator() {
        return co2Generator;
    }

    public void setCo2Generator(String co2Generator) {
        this.co2Generator = co2Generator;
    }

    public String getHvacStatus() {
        return hvacStatus;
    }

    public void setHvacStatus(String hvacStatus) {
        this.hvacStatus = hvacStatus;
    }

    public String getOutdoorTemp() {
        return outdoorTemp;
    }

    public void setOutdoorTemp(String outdoorTemp) {
        this.outdoorTemp = outdoorTemp;
    }

    public String getRadiation() {
        return radiation;
    }

    public void setRadiation(String radiation) {
        this.radiation = radiation;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * 현재 정보를 보기 좋게 포맷팅하여 반환
     */
    public String getFormattedDisplayText() {
        StringBuilder sb = new StringBuilder();
        
        // 환경 정보
        sb.append("<big><b>■ 환경 정보</b></big>\n")
          .append("<big>온도: ").append(temperature).append("°C  ")
          .append("습도: ").append(humidity).append("%</big>\n")
          .append("<big>CO2: ").append(co2).append("ppm  ")
          .append("PH: ").append(ph).append("</big>\n\n");
          
        // 제어 정보
        sb.append("<big><b>■ 제어 상태</b></big>\n")
          .append("<big>천창: ").append(skyWindow).append("  ")
          .append("CO2발생기: ").append(co2Generator).append("</big>\n")
          .append("<big>냉난방기: ").append(hvacStatus).append("</big>\n\n");
          
        // 기상 정보
        sb.append("<big><b>■ 기상 정보</b></big>\n")
          .append("<big>외부온도: ").append(outdoorTemp).append("°C  ")
          .append("일사량: ").append(radiation).append("W/㎡</big>\n")
          .append("<big>풍향: ").append(windDirection).append("  ")
          .append("풍속: ").append(windSpeed).append("m/s</big>\n")
          .append("<big>강우량: ").append(rainfall).append("mm</big>");
          
        return sb.toString();
    }
} 