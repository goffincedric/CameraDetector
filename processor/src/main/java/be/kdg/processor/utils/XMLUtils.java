package be.kdg.processor.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 25/09/2018 14:17
 */
public class XMLUtils {
    private static final Logger LOGGER = Logger.getLogger(XMLUtils.class.getName());

    public static <T> T convertXMLToObject(String string, Class<T> objectClass) {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());

        T object = null;
        try {
            object = mapper.readValue(string, objectClass);
        } catch (IOException e) {
            LOGGER.severe("Message: '" + string + "' is niet van het verwachtte formaat!");
            e.printStackTrace();
        }
        return object;
    }
}
