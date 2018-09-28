package be.kdg.simulator.messenger;

import be.kdg.simulator.model.CameraMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * @author Cédric Goffin
 * 25/09/2018 14:17
 */
public class XMLSerializer {
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
            e.printStackTrace();
        }
        return object;
    }
}
