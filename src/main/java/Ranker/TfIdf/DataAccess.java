package Ranker.TfIdf;

import java.sql.*;

public class DataAccess {
    private static DataAccess access;

    private PreparedStatement insertTfIdfStatement;
    private PreparedStatement getDocsSizeStatement;
    private PreparedStatement findWordFreqStatement;
    private PreparedStatement getDocSizeByUrlIdStatement;
    private PreparedStatement getWordFreqInAllDocsStatement;
    private PreparedStatement getRightUrlIdsStatement;
    private PreparedStatement getUrlWordIdsStatement;
    private PreparedStatement[] resetTfIdfTableStatements;


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
        getDocsSizeStatement = dbConnection.prepareStatement("SELECT COUNT(*) as c from urls");
        insertTfIdfStatement = dbConnection.prepareStatement("INSERT INTO tf_idf (score, doc_url_id, word_id) VALUES (?,?,?)");
        findWordFreqStatement = dbConnection.prepareStatement("SELECT freq FROM word_doc WHERE word_id=? AND doc_url_id=?;");
        getDocSizeByUrlIdStatement = dbConnection.prepareStatement("SELECT doc_size FROM doc_size WHERE doc_url_id=?");
        getWordFreqInAllDocsStatement = dbConnection.prepareStatement("SELECT COUNT(*) AS c FROM word_doc WHERE word_id=?");
        getRightUrlIdsStatement = dbConnection.prepareStatement("SELECT id FROM urls WHERE is_index=TRUE");
        getUrlWordIdsStatement = dbConnection.prepareStatement("SELECT word_id FROM word_doc WHERE doc_url_id=?");
        resetTfIdfTableStatements = new PreparedStatement[]{
                dbConnection.prepareStatement("DELETE FROM tf_idf;"),
                dbConnection.prepareStatement("ALTER TABLE tf_idf AUTO_INCREMENT=1")
        };
    }

    public long getDocsSize() throws SQLException {
        ResultSet resultSet = getDocsSizeStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("c");
    }

    public boolean insertTfId(double score, long docUrlId, long wordId) throws SQLException {
        insertTfIdfStatement.setDouble(1, score);
        insertTfIdfStatement.setLong(2, docUrlId);
        insertTfIdfStatement.setLong(3, wordId);
        return insertTfIdfStatement.execute();
    }

    public long getWordFreq(long wordId, long docUrlId) throws SQLException {
        findWordFreqStatement.setLong(1, wordId);
        findWordFreqStatement.setLong(2, docUrlId);
        ResultSet resultSet = findWordFreqStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("freq");
    }

    public long getDocSizeByUrlId(long docUrlId) throws SQLException {
        getDocSizeByUrlIdStatement.setLong(1, docUrlId);
        ResultSet resultSet = getDocSizeByUrlIdStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("doc_size");
    }

    public long getWordFreqInAllDocs(long wordId) throws SQLException {
        getWordFreqInAllDocsStatement.setLong(1, wordId);
        ResultSet resultSet = getWordFreqInAllDocsStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("c");
    }

    public ResultSet getRightUrlIds() throws SQLException {
        return getRightUrlIdsStatement.executeQuery();
    }

    public ResultSet getUrlWordIds(long docUrlId) throws SQLException {
        getUrlWordIdsStatement.setLong(1, docUrlId);
        return getUrlWordIdsStatement.executeQuery();
    }

    public void resetTfIdfTable() throws SQLException {
        executeMultiStatement(resetTfIdfTableStatements);
    }

    private void executeMultiStatement(PreparedStatement[] preparedStatements) throws SQLException {
        for (PreparedStatement preparedStatement : preparedStatements) {
            preparedStatement.execute();
        }
    }
}
