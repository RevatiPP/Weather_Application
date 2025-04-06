import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class WeatherAppGUI extends JFrame {
    private JTextField cityField;
    private JTextArea resultArea;
    private final String API_KEY = "cad03d5afb1430cbf2e4009f35dc8fa9"; 
    public WeatherAppGUI() {
        setTitle("Weather App");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel cityLabel = new JLabel("Enter City (e.g., Delhi or Delhi,IN):");
        cityField = new JTextField(20);
        JButton fetchButton = new JButton("Get Weather");

        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPanel.add(cityLabel);
        rowPanel.add(cityField);
        rowPanel.add(fetchButton);

        inputPanel.add(rowPanel);

        // Output area
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Weather Info"));

        // Action listener
        fetchButton.addActionListener(e -> fetchWeather());

        // Add panels
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void fetchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            resultArea.setText("⚠️ Please enter a city name.");
            return;
        }

        try {
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    URLEncoder.encode(city, "UTF-8") +
                    "&units=metric&appid=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            InputStream stream = (responseCode == 200)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode != 200) {
                resultArea.setText("❌ Error: " + response.toString());
                return;
            }

            JSONObject json = new JSONObject(response.toString());
            String cityName = json.getString("name");
            JSONObject main = json.getJSONObject("main");
            double temp = main.getDouble("temp");
            int humidity = main.getInt("humidity");
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            String description = weather.getString("description");

            String result = String.format(
                "🌍 City: %s\n🌡️  Temperature: %.1f°C\n💧 Humidity: %d%%\n🌤️ Condition: %s",
                cityName, temp, humidity, description
            );

            resultArea.setText(result);

        } catch (Exception e) {
            resultArea.setText("⚠️ Error fetching weather data:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WeatherAppGUI().setVisible(true);
        });
    }
}
