package com.neueda.etiqet.db.config;

import com.neueda.etiqet.db.fixture.DbBase;
import com.neueda.etiqet.db.fixture.DbServer;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Config {

    private static Logger logger = Logger.getLogger(DbServer.class);

    public static void init() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL configUrl = classLoader.getResource("config/db-conn.xml");
        if (configUrl == null) {
            return;
        }
        try {
            logger.info("Initializing etiqet-db config...");
            File file = new File(configUrl.toURI());
            JAXBContext jaxbContext = JAXBContext.newInstance(DbBase.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.unmarshal(file);
        }
        catch (URISyntaxException e) {
            logger.error("Unable to find config file with path " + configUrl);
            e.printStackTrace();
        }
        catch (JAXBException e) {
            logger.error("Failed to unmarshall DbServer to setup connection configs");
            e.printStackTrace();
        }
    }
}
