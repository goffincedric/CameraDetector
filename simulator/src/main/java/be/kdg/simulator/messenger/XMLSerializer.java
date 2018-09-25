package be.kdg.simulator.messenger;

import be.kdg.simulator.model.CameraMessage;
import com.fasterxml.jackson.xml.XmlMapper;

import java.io.IOException;

/**
 * @author Cédric Goffin
 * 25/09/2018 14:17
 */
public class XMLSerializer {
    public static String convertObjectToXML(Object object) {
        XmlMapper xmlMapper = new XmlMapper();
        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xml;
    }

    public static Object convertXMLToObject(String string, Class objectClass) {
        XmlMapper xmlMapper = new XmlMapper();
        Object object = null;
        try {
            object = xmlMapper.readValue(string, objectClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }
}
