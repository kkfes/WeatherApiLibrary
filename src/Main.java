public class Main {
    public static void main(String[] args) {
        ForecastWeather forecastWeather = ForecastWeather.getWeather("3edc1f76cc774f06983204142241910","Алматы");
        System.out.println(forecastWeather.toString());
    }
}