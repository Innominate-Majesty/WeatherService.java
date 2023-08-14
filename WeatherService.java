package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;

public class WeatherService {

    private static final String API_KEY = "";
    private static final String API_URL = "";

    public static String getWeatherForCity(String city, String userId) {
        StringBuilder response = new StringBuilder();

        try {
            String urlString = String.format(API_URL, city, API_KEY);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (Exception e) {
            return "Sorry, I was unable to fetch the weather information for " + city;
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        double temperature = jsonResponse.getJSONObject("main").getDouble("temp");
        double tempMin = jsonResponse.getJSONObject("main").getDouble("temp_min");
        double tempMax = jsonResponse.getJSONObject("main").getDouble("temp_max");
        int humidity = jsonResponse.getJSONObject("main").getInt("humidity");
        double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");
        long timestamp = jsonResponse.getLong("dt");
        ZonedDateTime dateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault());
        String date = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String time = dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

        return String.format(
                "<@%s>, the weather for %s on %s at %s is:\n" + "Current Temperature: %.2f°C\n"
                        + "Highest Temperature: %.2f°C\n"
                        + "Lowest Temperature: %.2f°C\n"
                        + "Humidity: %d%%\n"
                        + "Windspeed: %.2f m/s\n",
                userId, city, date, time, temperature, tempMax, tempMin, humidity, windSpeed);
    }
}
