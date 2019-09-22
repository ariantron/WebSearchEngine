package Indexer;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Indexer {
    private DataAccess dataAccess;

    private Indexer() throws SQLException {
        this.dataAccess = DataAccess.getAccess();
    }

    public static Indexer getInstance() throws SQLException {
        return new Indexer();
    }

    private static String[] getKeywordsFromUrl(String url) {
        String html = utill.getUrlHtml(url);
        String text = Jsoup.clean(html, Whitelist.simpleText());
        ArrayList<String> arrayList = utill.textToWordsList(text);
        Tokenizer tokenizer = new Tokenizer(arrayList, EnglishStopWords.stopWords);
        return tokenizer.getProcessedWords().split(" ");
    }

    public static void start() throws SQLException {
        DataAccess.getAccess().resetIndexingTables();
        while (true)
            getInstance().websitesIndex();
    }

    private void insertWords(String url) throws SQLException {
        String[] words = getKeywordsFromUrl(url);
        long docUrlId = dataAccess.getID(url);
        int docSize = words.length;
        if (!dataAccess.isAlreadyInsertedInDocSize(docUrlId))
            dataAccess.insertDocSize(docUrlId, docSize);
        boolean isAlreadyAdded;
        long wordId;
        for (String item : words) {
            isAlreadyAdded = dataAccess.isWordAlreadyExisting(item);
            if (isAlreadyAdded) {
                wordId = dataAccess.getWordId(item);
                if (dataAccess.isAlreadyWordAddedToUrl(wordId, docUrlId)) {
                    dataAccess.increaseWordFreq(docUrlId, wordId);
                    System.out.println("update word--doc:" + docUrlId + ",word:" + item);
                } else {
                    dataAccess.addWord2(wordId, docUrlId, 1);
                    System.out.println("new word--doc:" + docUrlId + ",word:" + item);
                }
            } else {
                dataAccess.addWord(item);
                wordId = dataAccess.getWordId(item);
                dataAccess.addWord2(wordId, docUrlId, 1);
                System.out.println("new word--doc:" + docUrlId + ",word:" + item);
            }
        }
    }

    private void websitesIndex() throws SQLException {
        ResultSet resultSet = dataAccess.getRightUrls();
        String url;
        int id;
        resultSet.first();
        do {
            url = resultSet.getString("url");
            id = resultSet.getInt("id");
            insertWords(url);
            System.out.println("url -> id:" + id + "\n" + url);
            utill.writeALine();
        } while (resultSet.next());
    }

}
