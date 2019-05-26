package com.neueda.etiqet.db.fixture;

import com.jcraft.jsch.Session;

import static org.jooq.impl.DSL.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DbHandlers {

    public static final String DEFAULT_SERVER_ALIAS = "default";

    private static Session session;
    private static Connection conn;
    private static DSLContext dslContext;

    private static Result<Record> results;
    private static HashMap<String, SelectQuery<Record>> queries;

    public static void connect(String serverAlias) {
        DbServer dbServer = DbBase.getServerConfig(serverAlias);
        if (dbServer.getSshTunnel() != null) {
            createTunnel(dbServer.getSshTunnel());
        }
        connect(dbServer);
    }

    public static void connect(DbServer dbServer) {
        try {
            initSqlDriver(dbServer.getDriverClass());
            String url = "";
            if (dbServer.getDialect() == SQLDialect.POSTGRES) {
                url = buildUrlPostgres(dbServer.getSubprotocol(), dbServer.getHost(), dbServer.getDbName(),
                    dbServer.getPort());
            }
            conn = DriverManager.getConnection(url, dbServer.getUser(), dbServer.getPassword());
            dslContext = DSL.using(conn, dbServer.getDialect());
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initSqlDriver(String driverClass) throws ClassNotFoundException {
        Class.forName(driverClass);
    }

    public static String buildUrlPostgres(String subprotocol, String host, String dbName, Integer port) {
        StringBuilder url = new StringBuilder();
        url.append(subprotocol);
        url.append(host);
        url.append(":");
        url.append(port);
        url.append("/");
        url.append(dbName);
        return url.toString();
    }

    // todo
    public static void buildUrlSqlServer(String subprotocol, String serverName, String instanceName,
                                    Integer port, String[] properties) {
        StringBuilder url = new StringBuilder();
        url.append(subprotocol);
        if (serverName != null) {
            url.append(serverName);
        }
        if (instanceName != null) {
            url.append("\\");
            url.append(instanceName);
        }
        if (port != null) {
            url.append(":");
            url.append(port.toString());
        }
        if (properties.length > 0) {
            url.append(String.join(";", properties));
        }
    }

    public static void createTunnel(SshTunnel sshTunnel) {

    }

    /**QUERIES*/

    public static void selectAll(String tableName) {
        Name table = DSL.name(tableName);
        results = dslContext.select().from(table).fetch();
    }

    public static void selectAllWithCondition(String tableName, String conditionExp) {
        Name table = DSL.name(tableName);
        Condition condition = DSL.condition(conditionExp);
        results = dslContext.select().from(table).where(condition).fetch();
    }

    public static void selectColumns(ArrayList<String> columnNames, String tableName) {
        Name table = DSL.name(tableName);

        ArrayList<Field<Object>> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(field(DSL.name(columnName)));
        }
        results = dslContext.select(columns).from(table).fetch();
    }

    public static void selectColumnsWithCondition(ArrayList<String> columnNames, String tableName, String conditionExp) {
        Name table = DSL.name(tableName);
        Condition condition = DSL.condition(conditionExp);

        ArrayList<Field<Object>> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(field(DSL.name(columnName)));
        }
        results = dslContext.select(columns).from(table).where(condition).fetch();
    }

    /**FILTERS*/

    // todo - a method of saving values
    public static void getColumnValAtRow(Integer rowIndex, String column, String alias) {
        results.getValue(rowIndex, column);
    }

    /**VALIDATIONS*/

    public static void checkRowCountGreaterThan(int expectedRows) throws SQLException {
        assertTrue(expectedRows > results.size());
    }

    public static void checkRowCountLessThan(int expectedRows) throws SQLException {
        assertTrue(expectedRows < results.size());
    }

    public static void checkRowCountEqualTo(int expectedRows) throws SQLException {
        assertEquals(expectedRows, results.size());
    }

    public static void checkRowValuesEquals(ArrayList<String> values, String columnName) {
        for (int i = 0; i < results.size(); i++) {
            assertEquals(values.get(i), results.getValue(i, field(DSL.name(columnName))));
        }
    }

    /**UTILS*/

    public static void printResults() {
        if (results != null) {
            System.out.println(results);
        }
    }
}
