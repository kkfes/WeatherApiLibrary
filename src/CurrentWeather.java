import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CurrentWeather {
    private Location location;
    private Current current;

    public static class Location {
        private String name;
        private String region;
        private String country;
        private double lat;
        private double lon;
        private String tz_id;
        private long localtime_epoch;
        private String localtime;

        // Getters and toString() for debugging


        @Override
        public String toString() {
            return "Location{" +
                    "name='" + name + '\'' +
                    ", region='" + region + '\'' +
                    ", country='" + country + '\'' +
                    ", lat=" + lat +
                    ", lon=" + lon +
                    ", tz_id='" + tz_id + '\'' +
                    ", localtime_epoch=" + localtime_epoch +
                    ", localtime='" + localtime + '\'' +
                    '}';
        }
    }

    public static class Current {
        private long last_updated_epoch;
        private String last_updated;
        private double temp_c;
        private double temp_f;
        private int is_day;
        private Condition condition;
        private double wind_mph;
        private double wind_kph;
        private int wind_degree;
        private String wind_dir;
        private double pressure_mb;
        private double pressure_in;
        private double precip_mm;
        private double precip_in;
        private int humidity;
        private int cloud;
        private double feelslike_c;
        private double feelslike_f;
        private double windchill_c;
        private double windchill_f;
        private double heatindex_c;
        private double heatindex_f;
        private double dewpoint_c;
        private double dewpoint_f;
        private double vis_km;
        private double vis_miles;
        private double uv;
        private double gust_mph;
        private double gust_kph;
        private AirQuality air_quality;

        // Getters and toString() for debugging


        @Override
        public String toString() {
            return "Current{" +
                    "last_updated_epoch=" + last_updated_epoch +
                    ", last_updated='" + last_updated + '\'' +
                    ", temp_c=" + temp_c +
                    ", temp_f=" + temp_f +
                    ", is_day=" + is_day +
                    ", condition=" + condition +
                    ", wind_mph=" + wind_mph +
                    ", wind_kph=" + wind_kph +
                    ", wind_degree=" + wind_degree +
                    ", wind_dir='" + wind_dir + '\'' +
                    ", pressure_mb=" + pressure_mb +
                    ", pressure_in=" + pressure_in +
                    ", precip_mm=" + precip_mm +
                    ", precip_in=" + precip_in +
                    ", humidity=" + humidity +
                    ", cloud=" + cloud +
                    ", feelslike_c=" + feelslike_c +
                    ", feelslike_f=" + feelslike_f +
                    ", windchill_c=" + windchill_c +
                    ", windchill_f=" + windchill_f +
                    ", heatindex_c=" + heatindex_c +
                    ", heatindex_f=" + heatindex_f +
                    ", dewpoint_c=" + dewpoint_c +
                    ", dewpoint_f=" + dewpoint_f +
                    ", vis_km=" + vis_km +
                    ", vis_miles=" + vis_miles +
                    ", uv=" + uv +
                    ", gust_mph=" + gust_mph +
                    ", gust_kph=" + gust_kph +
                    ", air_quality=" + air_quality +
                    '}';
        }
    }

    public static class Condition {
        private String text;
        private String icon;
        private int code;

        // Getters and toString() for debugging


        @Override
        public String toString() {
            return "Condition{" +
                    "text='" + text + '\'' +
                    ", icon='" + icon + '\'' +
                    ", code=" + code +
                    '}';
        }
    }

    public static class AirQuality {
        private double co;
        private double no2;
        private double o3;
        private double so2;
        private double pm2_5;
        private double pm10;
        private int us_epa_index;
        private int gb_defra_index;

        // Getters and toString() for debugging


        @Override
        public String toString() {
            return "AirQuality{" +
                    "co=" + co +
                    ", no2=" + no2 +
                    ", o3=" + o3 +
                    ", so2=" + so2 +
                    ", pm2_5=" + pm2_5 +
                    ", pm10=" + pm10 +
                    ", us_epa_index=" + us_epa_index +
                    ", gb_defra_index=" + gb_defra_index +
                    '}';
        }
    }

    public static String translateToEnglish(String text) {
        try {
            String apiUrl = "https://api.mymemory.translated.net/get?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&langpair=ru|en";

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Парсинг JSON-ответа
                String jsonResponse = response.toString();
                return jsonResponse.split("\"translatedText\":\"")[1].split("\"")[0]; // Извлечение переведенного текста
            } else {
                System.out.println("Translation API returned HTTP response code: " + responseCode);
                return text; // Возврат оригинального текста в случае ошибки
            }
        } catch (Exception e) {
            e.printStackTrace();
            return text; // Возврат оригинального текста в случае исключения
        }
    }

    // Метод для проверки, содержит ли строка кириллические символы
    private static boolean isCyrillic(String text) {
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.CYRILLIC) {
                return true; // Если есть хотя бы один кириллический символ, возвращаем true
            }
        }
        return false; // Если кириллических символов нет, возвращаем false
    }

    public static CurrentWeather getWeather(String key, String city) {
        String queryCity;
        if(isCyrillic(city)){
            try {
                queryCity=translateToEnglish(city);
            }catch (Exception e){
                queryCity=URLEncoder.encode(city, StandardCharsets.UTF_8);
            }
        }else {
            queryCity=URLEncoder.encode(city, StandardCharsets.UTF_8);
        }
        String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + queryCity + "&aqi=yes";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new GsonBuilder().create();
            CurrentWeather weather = gson.fromJson(response.toString(), CurrentWeather.class);

            // Подстановка значений по умолчанию
            if (weather.location == null) {
                weather.location = new Location();
                weather.location.name = "N/A";
                weather.location.region = "N/A";
                weather.location.country = "N/A";
                weather.location.lat = 0.0;
                weather.location.lon = 0.0;
                weather.location.tz_id = "N/A";
                weather.location.localtime_epoch = 0;
                weather.location.localtime = "N/A";
            }
            if (weather.current == null) {
                weather.current = new Current();
                weather.current.last_updated_epoch = 0;
                weather.current.last_updated = "N/A";
                weather.current.temp_c = 0.0;
                weather.current.temp_f = 0.0;
                weather.current.is_day = 0;
                weather.current.condition = new Condition();
                weather.current.condition.text = "N/A";
                weather.current.condition.icon = "N/A";
                weather.current.condition.code = 0;
                weather.current.wind_mph = 0.0;
                weather.current.wind_kph = 0.0;
                weather.current.wind_degree = 0;
                weather.current.wind_dir = "N/A";
                weather.current.pressure_mb = 0.0;
                weather.current.pressure_in = 0.0;
                weather.current.precip_mm = 0.0;
                weather.current.precip_in = 0.0;
                weather.current.humidity = 0;
                weather.current.cloud = 0;
                weather.current.feelslike_c = 0.0;
                weather.current.feelslike_f = 0.0;
                weather.current.windchill_c = 0.0;
                weather.current.windchill_f = 0.0;
                weather.current.heatindex_c = 0.0;
                weather.current.heatindex_f = 0.0;
                weather.current.dewpoint_c = 0.0;
                weather.current.dewpoint_f = 0.0;
                weather.current.vis_km = 0.0;
                weather.current.vis_miles = 0.0;
                weather.current.uv = 0.0;
                weather.current.gust_mph = 0.0;
                weather.current.gust_kph = 0.0;
                weather.current.air_quality = new AirQuality();
                weather.current.air_quality.co = 0.0;
                weather.current.air_quality.no2 = 0.0;
                weather.current.air_quality.o3 = 0.0;
                weather.current.air_quality.so2 = 0.0;
                weather.current.air_quality.pm2_5 = 0.0;
                weather.current.air_quality.pm10 = 0.0;
                weather.current.air_quality.us_epa_index = 0;
                weather.current.air_quality.gb_defra_index = 0;
            }
            return weather;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String toString() {
        return "CurrentWeather{" +
                "location=" + location +
                ", current=" + current +
                '}';
    }
}
