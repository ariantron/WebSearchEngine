package Indexer;

import java.sql.*;

public class DataAccess {
    private static DataAccess access;

    private PreparedStatement getRightUrlsStatement;
    private PreparedStatement[] resetIndexingTablesStatements;
    private PreparedStatement getUrlIdStatement;
    private PreparedStatement insertDocSizeStatement;
    private PreparedStatement findWordIdStatement;
    private PreparedStatement isAlreadyWordAddedToUrlStatement;
    private PreparedStatement increaseWordFreqStatement;
    private PreparedStatement addWordStatement;
    private PreparedStatement addWord2Statement;
    PreparedStatement isAlreadyInsertedInDocSizeStatement;

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
        getRightUrlsStatement = dbConnection.prepareStatement("SELECT * FROM urls WHERE is_index=TRUE;");
        resetIndexingTablesStatements = new PreparedStatement[]{
                dbConnection.prepareStatement("DELETE FROM words;"),
                dbConnection.prepareStatement("ALTER TABLE words AUTO_INCREMENT = 1;"),
                dbConnection.prepareStatement("DELETE FROM word_doc;"),
                dbConnection.prepareStatement("ALTER TABLE word_doc AUTO_INCREMENT = 1;"),
                dbConnection.prepareStatement("DELETE FROM doc_size;"),
                dbConnection.prepareStatement("ALTER TABLE doc_size AUTO_INCREMENT = 1;")
        };
        getUrlIdStatement = dbConnection.prepareStatement("SELECT id FROM urls WHERE url=?;");
        insertDocSizeStatement = dbConnection.prepareStatement("INSERT INTO doc_size (doc_url_id, doc_size) VALUES (?,?)");
        findWordIdStatement = dbConnection.prepareStatement("SELECT id FROM words WHERE word=?;");
        isAlreadyWordAddedToUrlStatement = dbConnection.prepareStatement("SELECT id FROM word_doc WHERE word_id=? AND doc_url_id=?");
        increaseWordFreqStatement = dbConnection.prepareStatement("UPDATE word_doc SET freq=freq+1 WHERE doc_url_id=? AND word_id=?");
        addWordStatement = dbConnection.prepareStatement("INSERT INTO words (word) VALUES (?);");
        addWord2Statement = dbConnection.prepareStatement("INSERT INTO word_doc (word_id, doc_url_id, freq) VALUES (?,?,?);");
        isAlreadyInsertedInDocSizeStatement = dbConnection.prepareStatement("SELECT * FROM doc_size WHERE doc_url_id=?");
    }

    public void resetIndexingTables() throws SQLException {
        executeMultiStatement(resetIndexingTablesStatements);
    }

    private void executeMultiStatement(PreparedStatement[] preparedStatements) throws SQLException {
        for (PreparedStatement preparedStatement : preparedStatements) {
            preparedStatement.execute();
        }
    }

    public ResultSet getRightUrls() throws SQLException {
        return getRightUrlsStatement.executeQuery();
    }

    public long getID(String url) throws SQLException {
        getUrlIdStatement.setString(1, url);
        ResultSet resultSet = getUrlIdStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("id");
    }

    public boolean insertDocSize(long docUrlId, long docSize) throws SQLException {
        insertDocSizeStatement.setLong(1, docUrlId);
        insertDocSizeStatement.setLong(2, docSize);
        return insertDocSizeStatement.executeUpdate() > 0;
    }

    public boolean isWordAlreadyExisting(String word) throws SQLException {
        findWordIdStatement.setString(1, word);
        return findWordIdStatement.executeQuery().next();
    }

    public long getWordId(String word) throws SQLException {
        findWordIdStatement.setString(1, word);
        ResultSet resultSet = findWordIdStatement.executeQuery();
        resultSet.first();
        return resultSet.getLong("id");
    }

    public boolean isAlreadyWordAddedToUrl(long wordId, long docUrlID) throws SQLException {
        isAlreadyWordAddedToUrlStatement.setLong(1, wordId);
        isAlreadyWordAddedToUrlStatement.setLong(2, docUrlID);
        return isAlreadyWordAddedToUrlStatement.executeQuery().next();
    }

    public boolean increaseWordFreq(long docUrlId, long wordId) throws SQLException {
        increaseWordFreqStatement.setLong(1, docUrlId);
        increaseWordFreqStatement.setLong(2, wordId);
        return increaseWordFreqStatement.executeUpdate() > 0;
    }

    public boolean addWord(String word) throws SQLException {
        addWordStatement.setString(1, word);
        return addWordStatement.execute();
    }

    public boolean addWord2(long wordId, long docUrlId, long freq) throws SQLException {
        addWord2Statement.setLong(1, wordId);
        addWord2Statement.setLong(2, docUrlId);
        addWord2Statement.setLong(3, freq);
        return addWord2Statement.execute();
    }

    public boolean isAlreadyInsertedInDocSize(long docUrlId) throws SQLException {
        isAlreadyInsertedInDocSizeStatement.setLong(1, docUrlId);
        return isAlreadyInsertedInDocSizeStatement.executeQuery().next();
    }
}
