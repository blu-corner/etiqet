package com.neueda.etiqet.sql.config;

import com.neueda.etiqet.sql.UnsupportedDialectException;
import com.neueda.etiqet.sql.fixture.DbConn;

public class ConfigUtils {

    public static String buildUrl(DbConn dbConn) throws UnsupportedDialectException {
        switch (dbConn.getDialect()) {
            case MYSQL:
            case POSTGRES:
                return joinUrlParts(dbConn.getSubprotocol(), dbConn.getHost(), dbConn.getDbName(),
                    dbConn.getPort());
            default:
                throw new UnsupportedDialectException("The dialect " + dbConn.getDialect().toString() + " is unsupported.");
        }
    }

    public static String joinUrlParts(String subprotocol, String host, String dbName, Integer port) {
        StringBuilder url = new StringBuilder();
        url.append(subprotocol);
        url.append(host);
        url.append(":");
        url.append(port);
        url.append("/");
        url.append(dbName);
        return url.toString();
    }
}
