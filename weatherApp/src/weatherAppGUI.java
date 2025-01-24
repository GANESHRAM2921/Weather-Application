import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class weatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public weatherAppGUI() {
        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addGUIComponents();
    }

    private void addGUIComponents() {
        //search bar
        JTextField searchBar = new JTextField();
        searchBar.setBounds(15, 15, 351, 45);
        searchBar.setFont(new Font("Serif", Font.PLAIN, 24));
        add(searchBar);
        //weatherConditionImage
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);
        //temperatureText
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Serif",Font.BOLD,48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
        //weatherConditionDesc
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Serif",Font.PLAIN,32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);
        //humidityImage
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        humidityImage.setFont(new Font("Serif",Font.PLAIN,16));
        add(humidityImage);
        //humidityText
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Serif",Font.PLAIN,16));
        add(humidityText);
        //windSpeedImage
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(228,500,74,66);
        add(windSpeedImage);
        //winSpeedText
        JLabel winSpeedText = new JLabel("<html><b>Windspeed</b> 15k/h<html>");
        winSpeedText.setBounds(310,500,85,55);
        winSpeedText.setFont(new Font("Serif",Font.PLAIN,16));
        add(winSpeedText);
        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = searchBar.getText();
                if(city.replaceAll("\\s","").length() <= 0)
                    return;
                //updating the image to corresponding weather condition
                weatherData = weatherApp.getWeatherData(city);
                String weather = (String) weatherData.get("weatherCondition");
                switch (weather){
                    case"Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case"Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case"Rainy":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case"Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }
                //update temperature
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature+" C");
                //update weather condition
                weatherConditionDesc.setText(weather);
                //update humidity data
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");
                //update windspeed
                double windspeed = (double) weatherData.get("windspeed");
                winSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }
    //method to load the image
    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not find the resource");
        return null;
    }
}
