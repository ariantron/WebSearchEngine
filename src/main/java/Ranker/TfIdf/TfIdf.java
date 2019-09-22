package Ranker.TfIdf;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TfIdf {
    private DataAccess dataAccess;
    private long docsSize;
    private long urlId;
    private long wordId;
    private double tfIdf;

    private TfIdf(long urlId, long wordId) throws SQLException, ClassNotFoundException {
        this.dataAccess = DataAccess.getAccess();
        this.urlId = urlId;
        this.wordId = wordId;
        this.docsSize = dataAccess.getDocsSize();
        tfIdf = tf() * idf();
    }

    public static TfIdf getInstance(long urlId, long wordId) throws SQLException, ClassNotFoundException {
        return new TfIdf(urlId, wordId);
    }

    public static void start() throws SQLException, ClassNotFoundException {
        TfIdf tfIdf;
        DataAccess dataAccess = DataAccess.getAccess();
        ResultSet resultSet = dataAccess.getRightUrlIds();
        resultSet.first();
        ResultSet urlWordIdsResultSet;
        if (resultSet.next()) {
            do {
                long urlId = resultSet.getLong("id");
                urlWordIdsResultSet = dataAccess.getUrlWordIds(urlId);
                urlWordIdsResultSet.first();
                if (urlWordIdsResultSet.next()) {
                    do {
                        long wordId = urlWordIdsResultSet.getLong("word_id");
                        tfIdf = TfIdf.getInstance(urlId, wordId);
                        tfIdf.rank();
                        System.out.println("new ranking submitted -> url_id: " + urlId + "\nword_id: " + wordId + "\n\n");
                    } while (urlWordIdsResultSet.next());
                }
            } while (resultSet.next());
        }
    }

    public void rank() throws SQLException {
        dataAccess.insertTfId(tfIdf, urlId, wordId);
    }

    public double tf() throws SQLException {
        long wordFreq = dataAccess.getWordFreq(wordId, urlId);
        long docSize = dataAccess.getDocSizeByUrlId(urlId);
        return (double) wordFreq / docSize;
    }

    public double idf() throws SQLException {
        long wordFreqInAllDocs = dataAccess.getWordFreqInAllDocs(wordId);
        return Math.log(docsSize / wordFreqInAllDocs);
    }
}
