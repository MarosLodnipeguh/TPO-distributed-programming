package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

public class UI {
    private JFXPanel browserPanel;
    private WebEngine webEngine;
    private JFrame mainFrame;
    private JLabel weatherInfo;
    private JLabel exchangeRateInfo;
    private JLabel nbpRateInfo;
    public UI() {
        mainFrame = new JFrame("Travel Information");
        browserPanel = new JFXPanel();
        setupMainFrame();
        setupContentPanel();
        mainFrame.setVisible(true);
    }

    private void setupMainFrame() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(900, 700));
    }

    private void setupContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(createInputsPanel(), BorderLayout.NORTH);
        contentPanel.add(createOutputPanel(), BorderLayout.CENTER);
        mainFrame.getContentPane().add(contentPanel);
    }

    private JPanel createInputsPanel() {
        JPanel inputsPanel = new JPanel();
        inputsPanel.setLayout(new BoxLayout(inputsPanel, BoxLayout.Y_AXIS));

        JComboBox<String> countrySelector = new JComboBox<>(listCountries());
        JComboBox<String> currencySelector = new JComboBox<>(listCurrencies());
        JTextField cityField = new JTextField();
        JButton fetchButton = new JButton("Fetch Data");

        inputsPanel.add(new JLabel("Select Country:"));
        inputsPanel.add(countrySelector);
        inputsPanel.add(new JLabel("Enter City:"));
        inputsPanel.add(cityField);
        inputsPanel.add(new JLabel("Select Currency:"));
        inputsPanel.add(currencySelector);
        inputsPanel.add(fetchButton);

        setupFetchButtonListener(fetchButton, countrySelector, currencySelector, cityField);

        return inputsPanel;
    }

    private JPanel createOutputPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout());

        weatherInfo = new JLabel("Weather Info: ");
        exchangeRateInfo = new JLabel("Exchange Rate Info: ");
        nbpRateInfo = new JLabel("NBP Rate Info: ");

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(weatherInfo);
        infoPanel.add(exchangeRateInfo);
        infoPanel.add(nbpRateInfo);

        displayPanel.add(infoPanel, BorderLayout.NORTH);
        displayPanel.add(browserPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            webEngine = webView.getEngine();
            browserPanel.setScene(new Scene(webView));
        });

        return displayPanel;
    }


    private void setupFetchButtonListener(JButton button, JComboBox<String> countryBox, JComboBox<String> currencyBox, JTextField cityField) {
        button.addActionListener(e -> {
            String country = (String) countryBox.getSelectedItem();
            String currency = (String) currencyBox.getSelectedItem();
            String city = cityField.getText();

            displayTravelInfo(country, city, currency);
        });
    }

    private void displayTravelInfo(String country, String city, String currency) {
        Service service = new Service(country);

        String weatherText = service.getWeatherDescription(city);
        weatherInfo.setText("Waether: " + weatherText);

        Double rate1 = service.getRateFor(currency);
        exchangeRateInfo.setText(currency + " exchange rate: " + rate1);

        Double rate2 = service.getNBPRate();
        nbpRateInfo.setText("NBP rate: " + rate2);

        String url = "https://en.wikipedia.org/wiki/" + city;
        Platform.runLater(() -> {
            webEngine.load(url);
        });
    }

    private String[] listCountries() {
        return Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::getDisplayCountry)
                .filter(country -> !country.isEmpty())
                .distinct()
                .sorted()
                .toArray(String[]::new);
    }

    private String[] listCurrencies() {
        return Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .sorted()
                .toArray(String[]::new);
    }

}
