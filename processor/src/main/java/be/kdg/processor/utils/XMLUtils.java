package be.kdg.processor.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 25/09/2018 14:17
 */
@UtilityClass
public class XMLUtils {
    private final Logger LOGGER = Logger.getLogger(XMLUtils.class.getName());
    private final XmlMapper mapper = new XmlMapper();
    private final JavaTimeModule timeModule = new JavaTimeModule();

    public <T> Optional<T> convertXMLToObject(String string, Class<T> objectClass) {
        mapper.registerModule(timeModule);

        Optional<T> object;
        try {
            object = Optional.of(mapper.readValue(string, objectClass));
        } catch (IOException e) {
            LOGGER.severe("Message: '" + string + "' could not be deserialized!");
            object = Optional.empty();
        }
        return object;
    }
}
