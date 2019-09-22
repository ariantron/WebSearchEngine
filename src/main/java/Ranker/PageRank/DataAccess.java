package Ranker.PageRank;

import java.sql.*;

public class DataAccess {
    private static DataAccess access;

    private PreparedStatement initVektorStatement;
    private PreparedStatement[] resetPageRankTableStatements;
    private PreparedStatement getUrlsStatement;
    private PreparedStatement setOutgoingStatement;
    private PreparedStatement countNodesStatement;
    private PreparedStatement getPageRankTableStatement;
    private PreparedStatement prepareDampStatement;
    private PreparedStatement countOutgoingLinksStatement;
    private PreparedStatement getOutgoingLinksStatement;
    private PreparedStatement increasePageRankStatement;
    private PreparedStatement prepareCalculationStatement;
    private PreparedStatement insertUrlStatement;
    private PreparedStatement getAllUrlsStatement;


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

    public static DataAccess getAccess() throws SQLException {
        if (access == null) {
            access = new DataAccess();
        }
        return access;
    }

    private void init() throws SQLException {
        Connection dbConnection = getConnection();
        initVektorStatement = dbConnection.prepareStatement("Update pagerank SET vektor=? WHERE doc_url_id>=? AND doc_url_id<=?;");
        resetPageRankTableStatements = new PreparedStatement[]{
                dbConnection.prepareStatement("DELETE FROM pagerank;"),
                dbConnection.prepareStatement("ALTER TABLE pagerank AUTO_INCREMENT=1;")
        };
        getUrlsStatement = dbConnection.prepareStatement("SELECT * FROM urls WHERE id>=? AND id<=?;");
        getAllUrlsStatement = dbConnection.prepareStatement("SELECT * FROM urls;");
        setOutgoingStatement = dbConnection.prepareStatement("UPDATE pagerank SET outgoing = ? WHERE doc_url_id=? AND (doc_url_id>=? AND doc_url_id<=?);");
        countNodesStatement = dbConnection.prepareStatement("SELECT Count(*) AS c FROM urls;");
        getPageRankTableStatement = dbConnection.prepareStatement("SELECT * FROM pagerank WHERE doc_url_id>=? AND doc_url_id<=?;");
        prepareDampStatement = dbConnection.prepareStatement("UPDATE pagerank SET pagerank=pagerank*?+?*? WHERE doc_url_id>=? AND doc_url_id<=?;");
        countOutgoingLinksStatement = dbConnection.prepareStatement("SELECT Count(*) AS c FROM links WHERE source = ?;");
        getOutgoingLinksStatement = dbConnection.prepareStatement("SELECT * FROM links WHERE source=?;");
        increasePageRankStatement = dbConnection.prepareStatement("Update pagerank SET pagerank=pagerank+? WHERE id=? AND (doc_url_id>=? AND doc_url_id<=?);");
        prepareCalculationStatement = dbConnection.prepareStatement("Update pagerank SET vektor=pagerank, pagerank=0 WHERE doc_url_id>=? AND doc_url_id<=?;");
        insertUrlStatement = dbConnection.prepareStatement("INSERT INTO pagerank (doc_url_id) VALUES (?)");
    }

    public void resetPageRankTable() throws SQLException {
        executeMultiStatement(resetPageRankTableStatements);
    }

    private void executeMultiStatement(PreparedStatement[] preparedStatements) throws SQLException {
        for (PreparedStatement preparedStatement : preparedStatements) {
            preparedStatement.execute();
        }
    }

    public boolean initVektor(Double equalDistributionVektorValue, long startId, long endId) throws SQLException {
        initVektorStatement.setDouble(1, equalDistributionVektorValue);
        initVektorStatement.setDouble(2, startId);
        initVektorStatement.setDouble(3, endId);
        return initVektorStatement.execute();
    }

    public ResultSet getUrls(long startId, long endId) throws SQLException {
        getUrlsStatement.setLong(1, startId);
        getUrlsStatement.setLong(2, endId);
        return getUrlsStatement.executeQuery();
    }

    public ResultSet getAllUrls() throws SQLException {
        return getUrlsStatement.executeQuery();
    }

    public ResultSet getPageRankTable(long startId, long endId) throws SQLException {
        getPageRankTableStatement.setLong(1, startId);
        getPageRankTableStatement.setLong(2, endId);
        return getPageRankTableStatement.executeQuery();
    }

    public boolean setOutgoingLinksValue(int outgoing, long id,long startId,long endId) throws SQLException {
        setOutgoingStatement.setInt(1, outgoing);
        setOutgoingStatement.setLong(2, id);
        setOutgoingStatement.setLong(3, startId);
        setOutgoingStatement.setLong(4, endId);
        return setOutgoingStatement.execute();
    }

    public long getNodesCount() throws SQLException {
        ResultSet resultSet = countNodesStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("c");
    }

    public boolean dampPageRank(double dampingFactor, double equalDistributionVektorValue, long startId, long endId) throws SQLException {
        prepareDampStatement.setDouble(1, dampingFactor);
        prepareDampStatement.setDouble(2, equalDistributionVektorValue);
        prepareDampStatement.setDouble(3, 1 - dampingFactor);
        prepareDampStatement.setDouble(4, startId);
        prepareDampStatement.setDouble(5, endId);
        return prepareDampStatement.execute();
    }

    public int countOutgoingLinks(long sourceID) throws SQLException {
        countOutgoingLinksStatement.setLong(1, sourceID);
        ResultSet resultSet = countOutgoingLinksStatement.executeQuery();
        resultSet.first();
        return resultSet.getInt("c");
    }

    public ResultSet getOutgoingLinks(long sourceID) throws SQLException {
        getOutgoingLinksStatement.setLong(1, sourceID);
        return getOutgoingLinksStatement.executeQuery();
    }

    public boolean increasePageRank(double increasingValue, long targetID, long startId, long endId) throws SQLException {
        increasePageRankStatement.setDouble(1, increasingValue);
        increasePageRankStatement.setLong(2, targetID);
        increasePageRankStatement.setLong(3,startId);
        increasePageRankStatement.setLong(4,endId);
        return increasePageRankStatement.execute();
    }

    public boolean prepareCalculation(long startId, long endId) throws SQLException {
        prepareCalculationStatement.setLong(1,startId);
        prepareCalculationStatement.setLong(2,endId);
        return prepareCalculationStatement.execute();
    }

    public boolean insertUrl(long docUrlId) throws SQLException {
        insertUrlStatement.setLong(1, docUrlId);
        return insertUrlStatement.execute();
    }

}
