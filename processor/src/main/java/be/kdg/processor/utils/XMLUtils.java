package be.kdg.processor.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Utility class for XML deserialization.
 *
 * @author C&eacute;dric Goffin
 */
@UtilityClass
public class XMLUtils {
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
    public <T> Optional<T> convertXMLToObject(String string, Class<T> objectClass) throws IOException {
        mapper.registerModule(timeModule);
        return Optional.of(mapper.readValue(string, objectClass));
    }

    /**
     * Generic method used to convert an object to an XML string.
     *
     * @param object object to serialize to XML string
     * @return an Optional string containing the serialized object. Can be empty when serialization failed.
     */
    public Optional<String> convertObjectToXML(Object object) throws JsonProcessingException {
        mapper.registerModule(timeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return Optional.of(mapper.writeValueAsString(object));
    }
}
