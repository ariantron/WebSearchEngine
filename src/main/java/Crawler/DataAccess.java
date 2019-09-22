package Crawler;

import java.sql.*;
import java.util.ArrayList;

public class DataAccess {
    private static DataAccess access;

    private PreparedStatement addNewNodeStatement;
    private PreparedStatement findIdForUrlStatement;
    private PreparedStatement setIndexTrueStatement;
    private PreparedStatement createLinkStatement;
    private PreparedStatement[] resetCrawlingTablesStatements;
    private PreparedStatement getWebsites;

    private DataAccess() throws SQLException {
        init();
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/search_engine", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static DataAccess getAccess() throws SQLException, ClassNotFoundException {
        if (access == null) {
            access = new DataAccess();
        }
        return access;
    }

    private void init() throws SQLException {
        Connection dbConnection = getConnection();
        findIdForUrlStatement = dbConnection.prepareStatement("SELECT id FROM urls WHERE url=?;");
        addNewNodeStatement = dbConnection.prepareStatement("INSERT INTO urls (url,title) VALUES(?,?);");
        createLinkStatement = dbConnection.prepareStatement("INSERT INTO links (source, target) VALUES(?,?);");
        resetCrawlingTablesStatements = new PreparedStatement[]{
                dbConnection.prepareStatement("DELETE FROM urls;"),
                dbConnection.prepareStatement("DELETE FROM links;"),
                dbConnection.prepareStatement("ALTER TABLE urls AUTO_INCREMENT = 1;"),
                dbConnection.prepareStatement("ALTER TABLE links AUTO_INCREMENT = 1;"),
        };
        setIndexTrueStatement = dbConnection.prepareStatement("UPDATE urls SET is_index=TRUE WHERE url=?");
        getWebsites = dbConnection.prepareStatement("SELECT url FROM websites");
    }

    public boolean addNode(String url,String title) throws SQLException {
        addNewNodeStatement.setString(1, url);
        addNewNodeStatement.setString(2, title);
        return addNewNodeStatement.execute();
    }

    public boolean isNodeAlreadyExisting(String url) throws SQLException {
        findIdForUrlStatement.setString(1, url);
        return findIdForUrlStatement.executeQuery().next();
    }

    public long getID(String url) throws SQLException {
        findIdForUrlStatement.setString(1, url);
        ResultSet resultSet = findIdForUrlStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("id");
    }

    public boolean setIndexTrue(String url) throws SQLException {
        setIndexTrueStatement.setString(1, url);
        return setIndexTrueStatement.executeUpdate() > 0;
    }

    public boolean createLink(long sourceID, long targetID) throws SQLException {
        createLinkStatement.setLong(1, sourceID);
        createLinkStatement.setLong(2, targetID);
        return createLinkStatement.execute();
    }

    public void resetCrawlingTables() throws SQLException {
        executeMultiStatement(resetCrawlingTablesStatements);
    }

    private void executeMultiStatement(PreparedStatement[] preparedStatements) throws SQLException {
        for (PreparedStatement preparedStatement : preparedStatements) {
            preparedStatement.executeUpdate();
        }
    }

    public ResultSet getWebsites() throws SQLException {
        return getWebsites.executeQuery();
    }
}

