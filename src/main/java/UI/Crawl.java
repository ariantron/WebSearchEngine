package UI;

import Crawler.Spider;

import java.sql.SQLException;

public class Crawl {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Spider.start();
    }
}
