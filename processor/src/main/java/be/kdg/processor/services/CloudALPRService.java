package be.kdg.processor.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Logger;

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

        try
        {
            // Setup the HTTPS connection to api.openalpr.com
            URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=eu&secret_key=" + secret_key);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setFixedLengthStreamingMode(image.length);
            http.setDoOutput(true);

            // Send our Base64 content over the stream
            try(OutputStream os = http.getOutputStream()) {
                os.write(image);
            }

            int status_code = http.getResponseCode();
            if (status_code == 200)
            {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getInputStream()));
                String json_content = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    json_content += inputLine;
                in.close();

                licensePlate = json_content.split("\"plate\": ")[1];

                System.out.println(json_content);
            }
            else
            {
                System.out.println("Got non-200 response: " + status_code);
            }
        }
        catch (MalformedURLException e)
        {
            System.out.println("Bad URL");
        }
        catch (IOException e)
        {
            System.out.println("Failed to open connection");
        }

        return null;
    }
}
