package com.neueda.etiqet.sql.fixture;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import static org.hamcrest.CoreMatchers.containsString;
import static org.jooq.impl.DSL.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import com.neueda.etiqet.sql.UnsupportedDialectException;
import com.neueda.etiqet.sql.config.ConfigUtils;
import com.neueda.etiqet.sql.config.Settings;
import org.apache.log4j.Logger;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SqlHandlers {

    private static final Logger LOG = Logger.getLogger(SqlHandlers.class);
    private static final Settings settings;
    private static final HashSet<SQLDialect> usesDictinctOn;
    public static final String DEFAULT_SERVER_ALIAS = "default";

    private static Session session;
    private static Connection conn;
    private static DSLContext dslContext;

    private static Result<Record> results;
    private static final HashMap<String, SelectQuery<Record>> queries;

    static {
        queries = new HashMap<>();
        usesDictinctOn = new HashSet<SQLDialect>() {{
            add(SQLDialect.POSTGRES); add(SQLDialect.POSTGRES_9_3); add(SQLDialect.POSTGRES_9_4);
            add(SQLDialect.POSTGRES_9_5); add(SQLDialect.POSTGRES_10);
        }};
        settings = Settings.loadSettings();
    }

    public static void connect(String dbAlias) throws UnsupportedDialectException {
        DbConn dbConn = DbBase.getDbConfig(dbAlias);
        connect(dbConn);
    }

    public static void connect(DbConn dbConn) throws UnsupportedDialectException {
        if (dbConn.getSshTunnel() != null) {
            prepareTunnelSession(dbConn);
            createTunnel(dbConn.getSshTunnel());
        }

        try {
            initSqlDriver(dbConn.getDriverClass());
            String url = ConfigUtils.buildUrl(dbConn);
            conn = DriverManager.getConnection(url, dbConn.getUser(), dbConn.getPassword());
            dslContext = DSL.using(conn, dbConn.getDialect());
            dslContext.configuration().settings().withExecuteLogging(settings.getUseJooqLogging());
            LOG.info("Successfully connected to " + url);
        }
        catch (ClassNotFoundException | SQLException e) {
            LOG.error("Failed to connect to database with connection settings " + dbConn, e);
        }
    }

    public static void initSqlDriver(String driverClass) throws ClassNotFoundException {
        Class.forName(driverClass);
    }

    public static void prepareTunnelSession(DbConn dbConn) {
        try {
            JSch jSch = new JSch();
            if (dbConn.getSshTunnel().getKeyPath() != null) {
                jSch.addIdentity(dbConn.getSshTunnel().getKeyPath());
            }
            session = jSch.getSession(dbConn.getUser(), dbConn.getHost(), dbConn.getPort());
        }
        catch (JSchException e) {
            LOG.error("Failed to setup ssh session with " + dbConn.getUser() +
                " for host " + dbConn.getHost() + " " +
                " on port " + dbConn.getPort() + ".\n", e);
        }
    }

    public static void createTunnel(SshTunnel sshTunnel) {
        try {
            session.setPortForwardingL(sshTunnel.getLocalPort(), sshTunnel.getRemoteHost(), sshTunnel.getRemotePort());

        }
        catch (JSchException e) {
            LOG.error("Failed to setup ssh tunnel from local port " + sshTunnel.getLocalPort() +
                " to remote host " + sshTunnel.getRemoteHost() + " " +
                " on remote port " + sshTunnel.getRemotePort() + ".\n", e);
        }
    }

    public static void connectToTunnel(SshTunnel sshTunnel) {
        if (sshTunnel.getPassword() != null) {
            session.setPassword(sshTunnel.getPassword());
        }
        try {
            session.connect();
        }
        catch (JSchException e) {
            LOG.error("Failed to connect to ssh tunnel session. \n", e);
        }
    }

    /**QUERIES*/

    public static void selectAll(String tableName, String distinctColumnNames) {
        Name table = DSL.name(tableName);
        if (distinctColumnNames != null && usesDictinctOn.contains(dslContext.dialect())) {
            ArrayList<Field<Object>> distinctColumns = SqlUtils.resolveToFieldList(distinctColumnNames);
            results = dslContext.select().distinctOn(distinctColumns).from(table).fetch();
        }
        else {
            results = dslContext.select().from(table).fetch();
        }
    }

    public static void selectAllWithCondition(String tableName, String conditionExp, String distinctColumnNames) {
        Name table = DSL.name(tableName);
        Condition condition = DSL.condition(conditionExp);
        if (distinctColumnNames != null && usesDictinctOn.contains(dslContext.dialect())) {
            ArrayList<Field<Object>> distinctColumns = SqlUtils.resolveToFieldList(distinctColumnNames);
            results = dslContext.select().distinctOn(distinctColumns).from(table).where(condition).fetch();
        }
        else {
            results = dslContext.select().from(table).where(condition).fetch();
        }
    }

    public static void selectColumns(ArrayList<String> columnNames, String tableName, String distinctColumnNames) {
        Name table = DSL.name(tableName);
        ArrayList<Field<Object>> columns = SqlUtils.resolveToFieldList(columnNames);
        if (distinctColumnNames != null && usesDictinctOn.contains(dslContext.dialect())) {
            ArrayList<Field<Object>> distinctColumns = SqlUtils.resolveToFieldList(distinctColumnNames);
            results = dslContext.select(columns).distinctOn(distinctColumns).from(table).fetch();
        }
        else {
            results = dslContext.select(columns).from(table).fetch();
        }
    }

    public static void selectColumnsWithCondition(ArrayList<String> columnNames, String tableName, String conditionExp, String distinctColumnNames) {
        Name table = DSL.name(tableName);
        Condition condition = DSL.condition(conditionExp);

        ArrayList<Field<Object>> columns = SqlUtils.resolveToFieldList(columnNames);
        if (distinctColumnNames != null && usesDictinctOn.contains(dslContext.dialect())) {
            ArrayList<Field<Object>> distinctColumns = SqlUtils.resolveToFieldList(distinctColumnNames);
            results = dslContext.select(columns).distinctOn(distinctColumns).from(table).where(condition).fetch();
        }
        else {
            results = dslContext.select(columns).from(table).where(condition).fetch();
        }
    }

    public static void sendRawSQLQuery(String query) {
        results = dslContext.fetch(query);
    }

    /**QUERY BUILDER*/

    public static void initQueryBuilder(String alias) {
        queries.put(alias, dslContext.selectQuery());
    }

    public static void addSelect(String alias) {
        queries.get(alias).addSelect();
    }

    public static void addSelect(ArrayList<String> columnNames,  String alias) {
        ArrayList<Field<Object>> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(field(DSL.name(columnName)));
        }
        queries.get(alias).addSelect(columns);
    }

    public static void addFrom(String tableName, String alias) {
        Table<Record> table = DSL.table(tableName);
        queries.get(alias).addFrom(table);
    }

    public static void addCondition(String conditionExp, String alias) {
        Condition condition = DSL.condition(conditionExp);
        queries.get(alias).addConditions(condition);
    }

    public static void addGroupBy(ArrayList<String> columnNames, String alias) {
        queries.get(alias).addGroupBy(SqlUtils.resolveToFieldList(columnNames));
    }

    public static void addHaving(String conditionExp, String alias) {
        Condition condition = DSL.condition(conditionExp);
        queries.get(alias).addHaving(condition);
    }

    public static void addDistinctOn(ArrayList<String> distinctColumnNames, String alias) {
        queries.get(alias).addDistinctOn(SqlUtils.resolveToFieldList(distinctColumnNames));
    }

    public static void addOrderBy(ArrayList<String> columnNames, String alias) {
        queries.get(alias).addOrderBy(SqlUtils.resolveToFieldList(columnNames));
    }

    public static void addJoin(String tableName, JoinType joinType, String alias) {
        Table<Record> table = DSL.table(tableName);
        queries.get(alias).addJoin(table, joinType);
    }

    public static void addJoinWithCondition(String tableName, JoinType joinType, String conditionExp,  String alias) {
        Table<Record> table = DSL.table(tableName);
        Condition condition = DSL.condition(conditionExp);
        queries.get(alias).addJoin(table, joinType, condition);
    }

    public static void executeQuery(String alias) {
        results = queries.get(alias).fetch();
    }

    /**UPDATE*/

    public static void updateWithCondition(String tableName, String newFieldValueParams, String conditionExp) {
        Table<Record> table = DSL.table(tableName);
        Condition condition = DSL.condition(conditionExp);
        HashMap<String, String> newFieldValues = SqlUtils.resolveParamsToFieldValMap(newFieldValueParams);
        HashMap<Field<Object>, Object> typeResolvedFieldValues = SqlUtils.resolveToFieldValMap(
            dslContext, tableName, newFieldValues);
        dslContext.update(table).set(typeResolvedFieldValues).where(condition).execute();
    }

    public static void insertInto(ArrayList<String> values, String tableName) {
        Table<Record> table = DSL.table(tableName);
        dslContext.insertInto(table).values(SqlUtils.resolveToFieldList(values)).execute();
    }

    public static void insertInto(ArrayList<String> values, ArrayList<String> columnNames, String tableName) {
        Table<Record> table = DSL.table(tableName);
        dslContext.insertInto(table).columns(SqlUtils.resolveToFieldList(columnNames)).values(values).execute();
    }

    public static void deleteAll(String tableName) {
        Table<Record> table = DSL.table(tableName);
        dslContext.delete(table).execute();
    }

    public static void deleteWithCondition(String tableName, String conditionExp) {
        Table<Record> table = DSL.table(tableName);
        Condition condition = DSL.condition(conditionExp);
        dslContext.delete(table).where(condition).execute();
    }

    /**VALIDATIONS*/

    public static void checkRowCountGreaterThan(int expectedRows) {
        assertTrue(expectedRows > results.size());
    }

    public static void checkRowCountLessThan(int expectedRows) {
        assertTrue(expectedRows < results.size());
    }

    public static void checkRowCountEqualTo(int expectedRows) {
        assertEquals(expectedRows, results.size());
    }

    public static void checkValueForColumnAtRow(String value, int rowIndex, String columnName) {
        assertEquals(value, results.get(rowIndex).get(field(DSL.name(columnName))));
    }

    public static void checkValueForColumnAtRowContains(String value, int rowIndex, String columnName) {
        assertThat(results.get(rowIndex).get(field(DSL.name(columnName))).toString(), containsString(value));
    }

    public static void checkValuesAcrossRows(ArrayList<String> values, String columnName) {
        for (int i = 0; i < results.size(); i++) {
            assertEquals(values.get(i), results.getValue(i, field(DSL.name(columnName))));
        }
    }

    public static void checkValuesAcrossColumns(ArrayList<String> values, int rowIndex) {
        Record row = results.get(rowIndex);
        for (int i = 0; i < row.size(); i++) {
            assertEquals(values.get(i), row.get(i));
        }
    }

    /**UTILS*/

    public static void printResults() {
        if (results != null) {
            System.out.println();
            System.out.println(results);
            System.out.println();
        }
    }
}
