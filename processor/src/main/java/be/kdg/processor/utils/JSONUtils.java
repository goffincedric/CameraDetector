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
 * @author C&eacute;dric Goffin
 */
@UtilityClass
public class JSONUtils {
    private final Logger LOGGER = Logger.getLogger(JSONUtils.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private final JavaTimeModule timeModule = new JavaTimeModule();

    /**
     * Generic method used to convert a JSON string to an object of type T.
     *
     * @param string      is a JSON string that needs to be deserialized
     * @param objectClass is the class the JSON string needs to be serialized to
     * @param <T>         is the generic type used for deserialization
     * @return an Optional of type T containing the deserialized object. Can be empty when deserialization failed.
     */
    public <T> Optional<T> convertJSONToObject(String string, Class<T> objectClass) {
        mapper.registerModule(timeModule);
        Optional<T> object = Optional.empty();
        try {
            object = Optional.of(mapper.readValue(string, objectClass));
        } catch (IOException e) {
            LOGGER.severe(String.format("Object: '%s' could not be deserialized!", string));
        }
        return object;
    }

}
