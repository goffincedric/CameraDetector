package be.kdg.processor.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Utility class for XML deserialization.
 *
 * @author Cédric Goffin
 */
@UtilityClass
public class XMLUtils {
    private final Logger LOGGER = Logger.getLogger(XMLUtils.class.getName());
    private final XmlMapper mapper = new XmlMapper();
    private final JavaTimeModule timeModule = new JavaTimeModule();

    /**
     * Generic method used to convert an XML string to an object of type T.
     *
     * @param string      is an XML string that needs to be deserialized
     * @param objectClass is the class the XML string needs to be serialized to
     * @param <T>         is the generic type used for deserialization
     * @return an Optional of type T containing the deserialized object. Can be empty when deserialization failed.
     */
    public <T> Optional<T> convertXMLToObject(String string, Class<T> objectClass) {
        mapper.registerModule(timeModule);

        Optional<T> object;
        try {
            object = Optional.of(mapper.readValue(string, objectClass));
        } catch (IOException e) {
            LOGGER.severe(String.format("Message: '%s' could not be deserialized!", string));
            object = Optional.empty();
        }
        return object;
    }
}
