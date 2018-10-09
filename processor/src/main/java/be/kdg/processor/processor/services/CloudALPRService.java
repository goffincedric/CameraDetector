package be.kdg.processor.processor.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Cédric Goffin
 * 08/10/2018 15:58
 */
@Component
public class CloudALPRService {
    private static final Logger LOGGER = Logger.getLogger(CloudALPRService.class.getName());

    @Value("${openalpr.secret_key}")
    private String secret_key;

    public String getLicenseplate(byte[] image) {
        String licensePlate = null;

        try {
            // Setup the HTTPS connection to api.openalpr.com
            URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=eu&secret_key=" + secret_key);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setFixedLengthStreamingMode(image.length);
            http.setDoOutput(true);

            // Send our Base64 content over the stream
            try (OutputStream os = http.getOutputStream()) {
                os.write(image);
            }

            int status_code = http.getResponseCode();
            if (status_code == 200) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getInputStream()));
                String json_content = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    json_content += inputLine;
                in.close();

                Pattern pattern = Pattern.compile(".*\"plate\": \"([a-zA-Z1-9]*)\".*");
                Matcher matcher = pattern.matcher(json_content);
                if (matcher.matches())
                    licensePlate = matcher.group(1);

                json_content = json_content.substring(json_content.lastIndexOf("\"plate\": \""), json_content.lastIndexOf("\"plate\": \"") + json_content.split("\"plate\": \"")[1].indexOf("\""));
                if (json_content.matches("^[1-8][A-Z]{3}[0-9]{3}$")) {
                    licensePlate = json_content.substring(0, 1) + "-" + json_content.substring(1, 4) + "-" + json_content.substring(4, 7);
                } else {
                    LOGGER.severe("License plate not recognised: " + json_content);
                    return "";
                }
            } else {
                System.out.println("Got non-200 response: " + status_code);
            }
        } catch (MalformedURLException e) {
            System.out.println("Bad URL");
        } catch (IOException e) {
            System.out.println("Failed to open connection");
        }

        return licensePlate;
    }
}
