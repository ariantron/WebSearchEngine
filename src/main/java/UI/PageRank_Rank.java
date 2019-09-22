package UI;

import Ranker.PageRank.PageRank;

import java.sql.SQLException;

public class PageRank_Rank {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        PageRank.start(100);
    }
}
