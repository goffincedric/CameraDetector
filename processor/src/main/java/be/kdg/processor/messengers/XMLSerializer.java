package be.kdg.processor.messengers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 25/09/2018 14:17
 */
public class XMLSerializer {
    private static final Logger LOGGER = Logger.getLogger(XMLSerializer.class.getName());

    public static String convertObjectToXML(Object object) {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String xml = null;
        try {
            xml = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return xml;
    }

    public static Object convertXMLToObject(String string, Class objectClass) {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());

        Object object = null;
        try {
            object = mapper.readValue(string, objectClass);
        } catch (IOException e) {
            LOGGER.severe("Message: '" + string + "' is niet van het verwachtte formaat!");
            e.printStackTrace();
        }
        return object;
    }
}
