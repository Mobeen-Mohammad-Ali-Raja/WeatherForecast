import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Scanner;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecastInformation{
    public static void main(String[] args) {
        try{
            Scanner scanner = new Scanner(System.in);
            String city;
            do{
                // Retrieve city name from user
                System.out.println("___________________________________________________________");
                System.out.print("Enter a city name or \"X\" to exit: ");
                city = scanner.nextLine();

                // Checking to see if program should exit
                if(city.equalsIgnoreCase("x")){
                    break;
                }

                // Getting location information
                JSONObject cityLocationInformation = (JSONObject) getLocationInformation(city);
                double latitude = (double) cityLocationInformation.get("latitude");
                double longitude = (double) cityLocationInformation.get("longitude");

                displayWeatherInformation(latitude, longitude);
            } while(!city.equalsIgnoreCase("x"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static JSONObject getLocationInformation (String city){
        city = city.replaceAll(" ","+"); // replacing white spaces with "+" as links do not allow for white spaces

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + // Geocoding API URL
                city + // adding city to the url
                "&count=1&language=end&format=json"; // this string is needed for choosing the settings we want from Geocoding API where we filter how we want the information

        try{
            // get the API response based on the API link given
            HttpURLConnection apiConnection = getApiResponse(urlString);

            // checking for response status | a value of 200 means the connection was successful
            if(apiConnection.getResponseCode() != 200){
                System.out.println("failed connection attempt to Geocoding API");
                return null;
            }

            // read the response and convert it into a String
            String jsonResponse = readApiResponse(apiConnection);

            // parse the String into a JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(jsonResponse);

            // get location information
            JSONArray locationInformation = (JSONArray) resultJsonObject.get("results");

            /** the retrieval is starting at position "0" as the settings from GEOCoding API was adjusted to retrieve
             only 1 result so the position will always be "0" **/
            return (JSONObject) locationInformation.get(0);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }

    private static HttpURLConnection getApiResponse(String urlString){
        try{
            // try and create a connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // set request method to get
            connection.setRequestMethod("GET");

            return connection;
        } catch(IOException e){
            e.printStackTrace();
        }
        // if failed to make a connection
        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConnection){
        try{
            // creating a StringBuilder to store JSON information
            StringBuilder resultJson = new StringBuilder();

            // creating a scanner to read the InputStream from the HttpURLConnection
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            // looping through the result and appending it to the created StringBuilder
            while(scanner.hasNext()){
                // reading and appending the current line onto the StringBuilder
                resultJson.append(scanner.nextLine());
            }

            // closing scanner to free resources
            scanner.close();

            // returning the JSON information as a String
            return resultJson.toString();

        } catch(IOException e){
            e.printStackTrace();
        }

        // returning null if there was an error with the response reading
        return null;
    }

    private static void displayWeatherInformation(double latitude, double longitude){

        try {
            // Weather Forecast API URL
            String url = "https://api.open-meteo.com/v1/forecast?latitude="
                            + latitude +
                        "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,is_day,precipitation,rain";
            HttpURLConnection apiConnection = getApiResponse(url);

            // checking for response status | a value of 200 means the connection was successful
            if(apiConnection.getResponseCode() != 200){
                System.out.println("failed connection attempt to Weather Forecast API");
                return;
            }

            // read the response and convert it into a String
            String jsonResponse = readApiResponse(apiConnection);

            // parse the String into a JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJsonObject = (JSONObject) resultJsonObject.get("current");

            // storing the information into their corresponding data types and printing them

            System.out.println("___________________________________________________________");

            // Retrieving time data and displaying it
            String time = (String) currentWeatherJsonObject.get("time");
            System.out.println("Current time: " + time);

            // Retrieving temperature data and displaying it
            double temperature = (double) currentWeatherJsonObject.get("temperature_2m");
            System.out.println("Temperature: " + temperature);

            // Retrieving wind speed data and displaying it
            double windSpeed = (double) currentWeatherJsonObject.get("wind_speed_10m");
            System.out.println("Wind description: " + windSpeed);

            // Retrieving relative humidity data and displaying it
            long relativeHumidity = (long) currentWeatherJsonObject.get("relative_humidity_2m");
            System.out.println("Relative humidity: " + relativeHumidity);

            // Retrieving precipitation data and displaying it
            double precipitation = (double) currentWeatherJsonObject.get("precipitation");
            System.out.println("Precipitation: " + precipitation);

            // Retrieving day/nighttime data and displaying it
            long isDay = (long) currentWeatherJsonObject.get("is_day");
            System.out.println(isDay == 1 ? "It is currently Daytime" : "It is currently Nighttime"); // added a short if statement to print out  if it's day time or not depending on the value of "isDay"

            // Retrieving rain data and displaying it
            double rain = (double) currentWeatherJsonObject.get("rain");
            System.out.println("Rain: " + rain);

        } catch (Exception e){
            e.printStackTrace();
        }

    }


}
