package be.kdg.processor.licenseplate.services;

import be.kdg.sa.services.LicensePlateNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service used to recognise license plate ids from images. Uses OpenALPR API for image recognition.
 *
 * @author C&eacute;dric Goffin
 */
@Service
public class CloudALPRService {
    private static final Logger LOGGER = Logger.getLogger(CloudALPRService.class.getName());

    @Value("${openalpr.secret_key}")
    private String secret_key;
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Method that contacts the OpenALPR API to recognise a license plate id from an image.
     *
     * @param image is an array of bytes that represents the image to be analysed
     * @return a license plate id
     * @throws LicensePlateNotFoundException when no (valid) license plate could be recognised from the supplied image
     */
    public String getLicenseplate(byte[] image) throws LicensePlateNotFoundException {
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
                StringBuilder json_content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    json_content.append(inputLine);
                in.close();

                // Try to parse json as Map
                try {
                    Map jsonMap = objectMapper.readValue(json_content.toString(), Map.class);
                    licensePlate = (String) ((LinkedHashMap) ((ArrayList) jsonMap.get("results")).get(0)).get("plate");
                } catch (Exception ignored) {
                    // Last-ditch effort to find license plate via regex
                    Pattern pattern = Pattern.compile(".*\\{\"plate\": \"([1-9][a-zA-Z]{3}[0-9]{3})\".*");
                    Matcher matcher = pattern.matcher(json_content.toString());
                    if (matcher.matches())
                        licensePlate = matcher.group(1);
                    else {
                        LOGGER.severe(String.format("License plate not recognised: %s", json_content));
                        throw new LicensePlateNotFoundException("License plate id could not be recognised from supplied image!");
                    }
                }

                licensePlate = licensePlate.charAt(0) + "-" + licensePlate.substring(1, 4) + "-" + licensePlate.substring(4, 7);
            } else {
                LOGGER.warning(String.format("Got non-200 response: %d", status_code));
            }
        } catch (MalformedURLException e) {
            LOGGER.severe("Bad URL for CloudALPR connection");
        } catch (IOException e) {
            LOGGER.severe("Failed to open connection to CloudALPR");
        }

        return licensePlate;
    }
}
