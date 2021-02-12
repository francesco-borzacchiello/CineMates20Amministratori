package it.unina.ingSw.cineMates20.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static void removeHashEmail() {
        File file = new File("src/main/resources/loggedUser.txt");
        file.delete();
    }

    public static void storeHashEmail(String emailHash) {
        File file = new File("src/main/resources/loggedUser.txt");

        try {
            if(!file.exists())
                if(!file.createNewFile())
                    return;
            FileWriter writer = new FileWriter(file);
            writer.write(emailHash);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEmailHash() {
        try {
            File file = new File("src/main/resources/loggedUser.txt");
            if(file.exists())
                return Files.readString(Paths.get("src/main/resources/loggedUser.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
