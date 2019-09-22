package Crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpiderLeg {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64)" +
            " AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static int count;
    private List<String> links = new ArrayList<>();
    private DataAccess dataAccess;

    public SpiderLeg(String url) throws SQLException, ClassNotFoundException {
        dataAccess = DataAccess.getAccess();
        count++;
        System.out.println("[Number] " + count);
        boolean success;
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            if (connection.response().statusCode() == 200) { // 200 is the HTTP OK status code
                if (connection.response().contentType().contains("text/html")) {
                    String title = utill.getPageTitleFromUrl(url);
                    if (!isUrlAlreadyAdded(url)) dataAccess.addNode(url, title);
                    dataAccess.setIndexTrue(url);
                    System.out.println("[Visit] " + url);
                    ArrayList<String> linksOnPage = extractUrlsFromSite(htmlDocument.baseUri());
                    System.out.println("  - Found (" + linksOnPage.size() + ") links");
                    System.out.println("title: " + title);
                    utill.writeALine();
                    long sourceId = getID(url);
                    for (String link : linksOnPage) {
                        links.add(link);
                        if (!isUrlAlreadyAdded(link)) dataAccess.addNode(link, utill.getPageTitleFromUrl(link));
                        long targetId = getID(link);
                        if (sourceId != targetId) dataAccess.createLink(sourceId, targetId);
                    }
                    success = true;
                } else {
                    System.out.println("[FailureA] " + url);
                    utill.writeALine();
                    success = false;
                }
            }
        } catch (Exception e) {
            System.out.println("[FailureB] " + url);
            utill.writeALine();
            success = false;
        }
    }

    private ArrayList<String> extractUrlsFromSite(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
        Elements links = doc.getElementsByTag("a");
        return getValidateUrls(doc, links);
    }

    private ArrayList<String> getValidateUrls(Document doc, Elements links) {
        ArrayList<String> urls = new ArrayList<>();

        for (Element link : links) {
            String linkUrl = link.attr("href");

            if (linkUrl.length() > 0) {
                if (linkUrl.length() < 4) {
                    linkUrl = doc.baseUri() + linkUrl.substring(1);
                } else if (!linkUrl.substring(0, 4).equals("http")) {
                    linkUrl = doc.baseUri() + linkUrl.substring(1);
                }
            }
            if (linkUrl.isEmpty()) {
                continue;
            }
            urls.add(linkUrl);
        }

        return urls;
    }

    private boolean isUrlAlreadyAdded(String url) throws SQLException {
        return dataAccess.isNodeAlreadyExisting(url);
    }

    private long getID(String url) throws SQLException {
        return dataAccess.getID(url);
    }

    public List<String> getLinks() {
        return links;
    }

}
