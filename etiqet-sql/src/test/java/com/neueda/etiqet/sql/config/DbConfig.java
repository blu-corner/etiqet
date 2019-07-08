package com.neueda.etiqet.sql.config;

import com.neueda.etiqet.sql.fixture.DbConn;
import com.neueda.etiqet.sql.fixture.DbBase;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class DbConfig {

    private static Logger logger = Logger.getLogger(DbConn.class);

    public static void init() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL configUrl = classLoader.getResource("config/db-conn.xml");
        if (configUrl == null) {
            return;
        }
        try {
            logger.info("Initializing etiqet-sql config...");
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
            logger.error("Failed to unmarshall DbConn to setup connection configs");
            e.printStackTrace();
        }
    }
}
