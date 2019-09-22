package Indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class utill {
    public static void writeALine() {
        System.out.println("--------------------");
    }
    public static ArrayList<String> textToWordsList(String text) {
        String[] words = text.split("[\\s/(,='-]");
        ArrayList<String> arrayList = new ArrayList<>();
        for (String item : words) {
            if (!(item.equals(""))) arrayList.add(item);
        }
        return arrayList;
    }
    public static String getUrlHtml(String url) {
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
