package be.kdg.processor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:02
 */
public class JSONUtils {
    private static final Logger LOGGER = Logger.getLogger(JSONUtils.class.getName());

    public static <T> T convertJSONToObject(String string, Class<T> objectClass) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        T object = null;
        try {
            object = mapper.readValue(string, objectClass);
        } catch (IOException e) {
            LOGGER.severe("Object: '" + string + "' is niet van het verwachtte formaat!");
            e.printStackTrace();
        }
        return object;
    }

}
