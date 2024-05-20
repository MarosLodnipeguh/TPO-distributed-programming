/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;
import java.util.stream.Collectors;


public class Service {


    private final String OPENWEATHER_API_KEY = "23e82f974742b0ad14517c0e182b2b87";
    private final String OPENWEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%s";

    private final String EXCHANGE_RATES_API_KEY = "61a4cd533f033026ebed3151";
    private final String EXCHANGE_RATES_API_URL = "https://v6.exchangerate-api.com/v6/%s/pair/%s/%s";

    private final String NBP_RATES_TABLE = "http://api.nbp.pl/api/exchangerates/rates/%s/%s/?format=json";

    private String country;
    private String countryCode;
    private String primaryCurrency;

    public Service (String country) {
        this.country = country;

        // Get country code
        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            if (locale.getDisplayCountry().equals(country)) {
                countryCode = iso;
            }
        }

        // Get primary currency
        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getDisplayCountry().equals(country)) {
                Currency currency = Currency.getInstance(locale);
                primaryCurrency = currency.getCurrencyCode();
            }
        }

    }

    public String getWeather (String city) {
        String weatherURL = String.format(OPENWEATHER_API_URL, city, countryCode, OPENWEATHER_API_KEY);
        String text = httpCall(weatherURL);
        System.out.println("Weather in " + city + ":");
        System.out.println(text + "\n");
        return text;
    }

    public String getWeatherDescription (String city) {
        String weatherURL = String.format(OPENWEATHER_API_URL, city, countryCode, OPENWEATHER_API_KEY);
        String text = httpCall(weatherURL);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(text);
            JsonNode weather = json.get("weather");
            JsonNode description = weather.get(0).get("description");
            return description.asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Double getRateFor (String currencySymbol) {

        // USD/PLN
        String exchangeRateURL = String.format(EXCHANGE_RATES_API_URL, EXCHANGE_RATES_API_KEY, currencySymbol, primaryCurrency);
        String text = httpCall(exchangeRateURL);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(text);
            JsonNode conversionRate = json.get("conversion_rate");

            System.out.println(primaryCurrency + "/" + currencySymbol + ": " + conversionRate.asDouble() + "\n");
            return conversionRate.asDouble();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Double getNBPRate () {

        // test
//        primaryCurrency = "HUF";

        String nbpURL = null;
        String table = null;
        String text = null;


        // Table A
        try {
            table = "A";
            nbpURL = String.format(NBP_RATES_TABLE, table, primaryCurrency);
            text = httpCall(nbpURL);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(text);
            JsonNode rates = json.get("rates");
            JsonNode conversionRate = rates.get(0).get("mid");

            System.out.println("NBP: PLN/" + primaryCurrency + ": " + conversionRate.asDouble());
            return conversionRate.asDouble();

        } catch (RuntimeException e) {
            System.out.println("NBP API returned null for table " + table + " and currency " + primaryCurrency + ". Trying table B");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Table B
        try {
            table = "B";
            nbpURL = String.format(NBP_RATES_TABLE, table, primaryCurrency);
            text = httpCall(nbpURL);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(text);
            JsonNode rates = json.get("rates");
            JsonNode conversionRate = rates.get(0).get("mid");

            System.out.println("NBP: PLN/" +primaryCurrency + ": " + conversionRate.asDouble());
            return conversionRate.asDouble();

        } catch (RuntimeException e) {
            System.out.println("NBP API returned null for table " + table + " and currency " + primaryCurrency);
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        try {
//            table = "C";
//            nbpURL = String.format(nbp, table, curr);
//            text = httpCall(nbpURL);
//        }
//        catch (RuntimeException e) {
//            System.out.println("NBP API returned null for table " + table + " and currency " + curr);
//            return null;
//        }


    }

    public String httpCall (String url) {

        try {
            URL urlObj = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            InputStream is = connection.getInputStream();

            String text = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));

            connection.disconnect();
            return text;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}
