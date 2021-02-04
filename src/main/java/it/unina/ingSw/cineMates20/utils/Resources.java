package it.unina.ingSw.cineMates20.utils;

import java.util.Properties;

public class Resources {

    private static final Properties properties = new Properties();

    private static final String NAME_RESOURCES_FIlE = "resources.xml";

    static {
        try {
            properties.loadFromXML(Resources.class.getClassLoader().getResourceAsStream(NAME_RESOURCES_FIlE));
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private Resources(){}

    public static String get(NameResources name) {
        return properties.getProperty(name.toString());
    }
}
