package com.neueda.etiqet.sql.fixture;

import org.apache.log4j.Logger;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name="SqlServers")
public class SqlBase {

    private static Logger logger = Logger.getLogger(SqlBase.class);

    @XmlElements({
        @XmlElement(name = "SqlServer", type = SqlServer.class),
    })
    private List<SqlServer> connConfigs;

    private static List<SqlServer> allBrowsers;
    private static HashMap<String, SqlServer> dbServerMap;
    private static String currentServer = "default";

    private SqlBase() {}

    private void afterUnmarshal(Unmarshaller u, Object parent) {
        dbServerMap = new HashMap<>();
        allBrowsers = new ArrayList<>();
        for (SqlServer conn : connConfigs) {
            dbServerMap.put(conn.getName(), conn);
            allBrowsers.add(conn);
            logger.info("Sql connection config registered: " + conn.getName());
        }
    }

    public static SqlServer getServerConfig() {
        return dbServerMap.get(currentServer);
    }

    public static SqlServer getServerConfig(String alias) {
        if (dbServerMap.containsKey(alias)) {
            currentServer = alias;
            return getServerConfig();
        }
        else {
            throw new SqlServerNotFoundException("No database server found for alias " + alias);
        }
    }
}
