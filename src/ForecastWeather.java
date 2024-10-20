import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ForecastWeather {
    public Location location;
    public Current current;
    public Forecast forecast;
    public Alerts alerts;

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

    public static ForecastWeather getWeather(String key, String city) {
        String queryCity;
        if (isCyrillic(city)) {
            try {
                queryCity = translateToEnglish(city);
            } catch (Exception e) {
                queryCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            }
        } else {
            queryCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        }
        String apiUrl = "https://api.weatherapi.com/v1/forecast.json?key=" + key + "&q=" + queryCity + "&days=3&aqi=yes&alerts=yes";
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
            return gson.fromJson(response.toString(), ForecastWeather.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "ForecastWeather{" +
                "location=" + location +
                ", current=" + current +
                ", forecast=" + forecast +
                ", alerts=" + alerts +
                '}';
    }
}

class Location {
    public String name;
    public String region;
    public String country;
    public double lat;
    public double lon;
    public String tz_id;
    public long localtime_epoch;
    public String localtime;

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

class Current {
    public double temp_c;
    public double temp_f;
    public Condition condition;
    public double humidity;
    public double wind_mph;
    public double wind_kph;
    public CurrentWeather.AirQuality air_quality;

    @Override
    public String toString() {
        return "Current{" +
                "temp_c=" + temp_c +
                ", temp_f=" + temp_f +
                ", condition=" + condition +
                ", humidity=" + humidity +
                ", wind_mph=" + wind_mph +
                ", wind_kph=" + wind_kph +
                ", air_quality=" + air_quality +
                '}';
    }
}

class Condition {
    public String text;
    public String icon;
    public int code;

    @Override
    public String toString() {
        return "Condition{" +
                "text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", code=" + code +
                '}';
    }
}

class Forecast {
    public List<Forecastday> forecastday;

    @Override
    public String toString() {
        return "Forecast{" +
                "forecastday=" + forecastday +
                '}';
    }
}

class Forecastday {
    public String date;
    public Day day;

    @Override
    public String toString() {
        return "Forecastday{" +
                "date='" + date + '\'' +
                ", day=" + day +
                '}';
    }
}

class Day {
    public double maxtemp_c;
    public double mintemp_c;
    public Condition condition;

    @Override
    public String toString() {
        return "Day{" +
                "maxtemp_c=" + maxtemp_c +
                ", mintemp_c=" + mintemp_c +
                ", condition=" + condition +
                '}';
    }
}

class Alerts {
    public List<Alert> alert;

    @Override
    public String toString() {
        return "Alerts{" +
                "alert=" + alert +
                '}';
    }
}

class Alert {
    public String headline;
    public String desc;

    @Override
    public String toString() {
        return "Alert{" +
                "headline='" + headline + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
