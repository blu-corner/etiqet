package com.neueda.etiqet.db.fixture;

import org.apache.log4j.Logger;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name="DbServers")
public class DbBase {

    private static Logger logger = Logger.getLogger(DbBase.class);

    @XmlElements({
        @XmlElement(name = "DbServer", type = DbServer.class),
    })
    private List<DbServer> connConfigs;

    private static List<DbServer> allBrowsers;
    private static HashMap<String, DbServer> dbServerMap;
    private static String currentServer = "default";

    private DbBase() {}

    private void afterUnmarshal(Unmarshaller u, Object parent) {
        dbServerMap = new HashMap<>();
        allBrowsers = new ArrayList<>();
        for (DbServer conn : connConfigs) {
            dbServerMap.put(conn.getName(), conn);
            allBrowsers.add(conn);
            logger.info("Db connection config registered: " + conn.getName());
        }
    }

    public static DbServer getServerConfig() {
        return dbServerMap.get(currentServer);
    }

    public static DbServer getServerConfig(String alias) {
        if (dbServerMap.containsKey(alias)) {
            currentServer = alias;
            return getServerConfig();
        }
        else {
            throw new DbServerNotFoundException("No database server found for alias " + alias);
        }
    }
}
