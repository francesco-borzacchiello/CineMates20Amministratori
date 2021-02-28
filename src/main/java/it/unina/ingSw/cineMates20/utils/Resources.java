package it.unina.ingSw.cineMates20.utils;

import it.unina.ingSw.cineMates20.view.MessageDialog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Resources {

    private static final Properties properties = new Properties(),
                                    secretKeysProperties = new Properties();

    private static final String NAME_RESOURCES_FIlE = "resources.xml";

    static {
        try {
            properties.loadFromXML(Resources.class.getClassLoader().getResourceAsStream(NAME_RESOURCES_FIlE));
            secretKeysProperties.load(new FileInputStream("secretKeys.properties"));
        } catch (Exception e) {
            MessageDialog.error("Si è verificato un errore", "Si è verificato un errore, riprova riavviando l'applicativo.");
        }
    }

    private Resources(){}

    public static String get(NameResources name) {
        return properties.getProperty(name.toString());
    }

    public static void removeEmail() {
        File file = new File("src/main/resources/loggedUser.txt");
        if(file.exists())
            file.delete();
    }

    public static void storeEmail(String email) {
        File file = new File("src/main/resources/loggedUser.txt");

        try {
            if(!file.exists())
                if(!file.createNewFile())
                    return;
            FileWriter writer = new FileWriter(file);
            writer.write(email);
            writer.close();
        } catch (IOException ignore) {}
    }

    public static String getEmail() {
        try {
            File file = new File("src/main/resources/loggedUser.txt");
            if(file.exists())
                return Files.readString(Paths.get("src/main/resources/loggedUser.txt"));
        } catch (IOException ignore) {}
        return null;
    }

    public static String getAwsAccessKey() {
        return secretKeysProperties.getProperty("AWS_ACCESS_KEY");
    }

    public static String getAwsSecretKey() {
        return secretKeysProperties.getProperty("AWS_SECRET_KEY");
    }

    public static String getS3BucketName() {
        return secretKeysProperties.getProperty("S3_BUCKET_NAME");
    }

    public static String getDbPath() {
        return secretKeysProperties.getProperty("DB_PATH");
    }

    public static String getTmdbApiKey() {
        return secretKeysProperties.getProperty("TMDB_API_KEY");
    }

}
