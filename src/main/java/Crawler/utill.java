package Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utill {
    public static void writeALine() {
        System.out.println("--------------------");
    }

    public static String getPageTitleFromHtml(String html) {
        Pattern p = Pattern.compile("<head>.*?<title>(.*?)</title>.*?</head>", Pattern.DOTALL);
        Matcher m = p.matcher(html);
        String title = "";
        while (m.find()) {
            title = m.group(1);
        }
        return title;
    }

    public static String getPageTitleFromUrl(String url) throws IOException {
        return getPageTitleFromHtml(getUrlHtml(url));
    }

    public static String getUrlHtml(String url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL theUrl = new URL(url);
            URLConnection conn = theUrl.openConnection();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            br.close();
        } catch (Exception ignored) {
        }
        return stringBuilder.toString();
    }
}
