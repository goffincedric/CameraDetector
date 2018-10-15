package be.kdg.processor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:02
 */

@UtilityClass
public class JSONUtils {
    private final Logger LOGGER = Logger.getLogger(JSONUtils.class.getName());

    public <T> Optional<T> convertJSONToObject(String string, Class<T> objectClass) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Optional<T> object;
        try {
            object = Optional.of(mapper.readValue(string, objectClass));
        } catch (IOException e) {
            LOGGER.severe("Object: '" + string + "' is niet van het verwachtte formaat!");
            object = Optional.empty();
        }
        return object;
    }

}
