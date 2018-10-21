package be.kdg.processor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Utility class for JSON deserialization.
 *
 * @author Cédric Goffin
 */
@UtilityClass
public class JSONUtils {
    private final Logger LOGGER = Logger.getLogger(JSONUtils.class.getName());

    /**
     * Generic method used to convert a JSON string to an object of type T.
     *
     * @param string      is a JSON string that needs to be deserialized
     * @param objectClass is the class the JSON string needs to be serialized to
     * @param <T>         is the generic type used for deserialization
     * @return an Optional of type T containing the deserialized object. Can be empty when deserialization failed.
     */
    public <T> Optional<T> convertJSONToObject(String string, Class<T> objectClass) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Optional<T> object;
        try {
            object = Optional.of(mapper.readValue(string, objectClass));
        } catch (IOException e) {
            LOGGER.severe(String.format("Object: '%s' could not be deserialized!", string));
            object = Optional.empty();
        }
        return object;
    }

}
