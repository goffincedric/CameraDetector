package be.kdg.simulator.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Utility class for XML serialization.
 *
 * @author C&eacute;dric Goffin
 */
@UtilityClass
public class XMLUtils {
    private final Logger LOGGER = Logger.getLogger(XMLUtils.class.getName());

    /**
     * Generic method used to convert an object to an XML string.
     *
     * @param object object to serialize to XML string
     * @return an Optional string containing the serialized object. Can be empty when serialization failed.
     */
    public Optional<String> convertObjectToXML(Object object) {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Optional<String> xml;
        try {
            xml = Optional.of(mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            LOGGER.severe("Could not serialize object " + object);
            xml = Optional.empty();
        }
        return xml;
    }
}
