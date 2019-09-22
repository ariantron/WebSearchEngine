package Crawler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Spider {

    private List<String> hasVisited = new ArrayList<>();
    private List<String> toVisit = new ArrayList<>();

    public static Spider getInstance() {
        return new Spider();
    }

    public static void start() throws SQLException, ClassNotFoundException {
        DataAccess dataAccess=DataAccess.getAccess();
        dataAccess.resetCrawlingTables();
        ResultSet resultSet = dataAccess.getWebsites();
        String url;
        while (true) {
            resultSet.first();
            do {
                url = resultSet.getString("url");
                getInstance().start(url, 10);
            } while (resultSet.next());
        }
    }

    public void start(String url, int size) throws SQLException, ClassNotFoundException {
        toVisit.add(url);

        while (hasVisited.size() < size) {
            String next = nextUrl();
            if (next != null) {
                SpiderLeg leg = new SpiderLeg(next);
                if (toVisit.size() < 10000) toVisit.addAll(leg.getLinks());
            } else break;
        }
//		System.out.println("[Done] Visited " + hasVisited.size() + " web page(s).");
        utill.writeALine();
    }

    public void start(String url) throws SQLException, ClassNotFoundException {
        toVisit.add(url);

        while (true) {
            String next = nextUrl();
            if (next != null) {
                SpiderLeg leg = new SpiderLeg(next);
                if (toVisit.size() < 10000) toVisit.addAll(leg.getLinks());
            } else break;
        }
//		System.out.println("[Done] Visited " + hasVisited.size() + " web page(s).");
        utill.writeALine();
    }

    private String nextUrl() {
        String nextUrl;
        do {
            if (toVisit.size() > 0) nextUrl = toVisit.remove(0);
            else return null;
        } while (hasVisited.contains(nextUrl));
        hasVisited.add(nextUrl);
        return nextUrl;
    }
}
