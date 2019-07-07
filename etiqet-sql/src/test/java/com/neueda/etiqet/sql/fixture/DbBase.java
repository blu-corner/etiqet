package com.neueda.etiqet.sql.fixture;

import org.apache.log4j.Logger;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name="DbConns")
public class DbBase {

    private static Logger logger = Logger.getLogger(DbBase.class);

    @XmlElements({
        @XmlElement(name = "DbConn", type = DbConn.class),
    })
    private List<DbConn> connConfigs;

    private static List<DbConn> allDbConns;
    private static HashMap<String, DbConn> dbConnMap;
    private static String currentDb = "default";

    private DbBase() {}

    private void afterUnmarshal(Unmarshaller u, Object parent) {
        dbConnMap = new HashMap<>();
        allDbConns = new ArrayList<>();
        for (DbConn conn : connConfigs) {
            dbConnMap.put(conn.getName(), conn);
            allDbConns.add(conn);
            logger.info("Db connection config registered: " + conn.getName());
        }
    }

    public static DbConn getDbConfig() {
        return dbConnMap.get(currentDb);
    }

    public static DbConn getDbConfig(String alias) {
        if (dbConnMap.containsKey(alias)) {
            currentDb = alias;
            return getDbConfig();
        }
        else {
            throw new DbServerNotFoundException("No database connection found for alias " + alias);
        }
    }

    @Override
    public String toString() {
        return "DbBase{" +
            "connConfigs=" + connConfigs +
            '}';
    }
}
