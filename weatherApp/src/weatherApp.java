import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Formatter;
import java.util.Scanner;

// backend logic starts from here
//logic for call API, receive and  fetch data
public class weatherApp {
    // get location coordinates using the geolocation API
public static JSONObject getWeatherData(String city){
    // extract latitude and longitude data
    JSONArray location = getLocationData(city);
    JSONObject locationData= (JSONObject) location.get(0);
    //extracting latitude and longitude
    double latitude = (double)locationData.get("latitude");
    double longitude = (double)locationData.get("longitude");
    // build API request URL with location coordinates
    String urlString ="https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
    try{
        // call api and get response
        HttpURLConnection conn = fetchApiResponse(urlString);
        // check for response status
        if(conn.getResponseCode()!=200){
            System.out.println("Error: Could not connect to API");
            return null;
        }else{
            //storing API results
            StringBuilder jsonResponse = new StringBuilder();
            Scanner input = new Scanner(conn.getInputStream());
            // read and store the response
            while(input.hasNext()){
                jsonResponse.append(input.nextLine());
            }
            input.close();
            conn.disconnect();
            // using json parser to parse the api
            JSONParser parser = new JSONParser();
            JSONObject josnObj = (JSONObject)parser.parse(String.valueOf(jsonResponse));
            JSONObject hourly = (JSONObject) josnObj.get("hourly");
            // finding the index for date and time with current time and date
            JSONArray time = (JSONArray) hourly.get("time");
            // store the index for further usage
            int index = findIndexOfCurrentTime(time);
            //extracting wanted data
            //get temperature
            JSONArray temperatureData =(JSONArray)hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);
            //get weather details
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = covertWeatherCode((long)weatherCode.get(index));
            //get relative humidity
            JSONArray relativehumidity= (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long)relativehumidity.get(index);
            //get windspeed
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windSpeedData.get(index);
            //creating a json object to connect to the created backend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weatherCondition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
        return weatherData;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
    private static JSONArray getLocationData(String city){
        String locationName=city.replace(" ","+");
        String urlString="https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=1&language=en&format=json";
        try{
            // call api for response
            HttpURLConnection conn = fetchApiResponse(urlString);
            //make sure the request is good
            if(conn.getResponseCode()!=200){
                System.out.println("Error:Could not fin the API");
                return null;
            }else{
                //storing API results
                StringBuilder jsonResult= new StringBuilder();
                Scanner input = new Scanner(conn.getInputStream());
                // read and store the response
                while (input.hasNext()){
                    jsonResult.append(input.nextLine());
                }
                input.close();
                conn.disconnect();
                // using json parser to parse the api
                JSONParser parser = new JSONParser();
                JSONObject resultResponse= (JSONObject) parser.parse(String.valueOf(jsonResult));
                JSONArray result = (JSONArray) resultResponse.get("results");
                return  result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


private static String covertWeatherCode(long weatherCode) {
    //generalize weather for simplicity
        String weather = "";
        if(weatherCode == 0l) weather="Clear";
        else if (weatherCode<=3l&&weatherCode>0l) weather="Cloudy";
        else if ((weatherCode>=51l&&weatherCode<=67l)||(weatherCode>=80l&&weatherCode<=99l)) weather="Rainy";
        else if (weatherCode>=71l&&weatherCode<=77l) weather="Snow";
    return weather;
}

private static int findIndexOfCurrentTime(JSONArray timeArray) {
    String currentTime = getCurrentTime();
    //iterate through the json array to find the valid index
    for(int i=0;i<timeArray.size();i++){
        String time = (String)timeArray.get(i);
        if(currentTime.equalsIgnoreCase(time)){
            return i;
        }
    }
    return 0;
}

private static String getCurrentTime() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
        String formattedDateTime = time.format(formater);
        return formattedDateTime;
}

    // to receive geographical coordinates from API

private static HttpURLConnection fetchApiResponse(String urlString){
    try {
        URL url = new URL(urlString);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn;
    } catch (Exception e) {
        e.printStackTrace();
    }
return null;
}

}
