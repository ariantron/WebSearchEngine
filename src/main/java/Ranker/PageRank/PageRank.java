package Ranker.PageRank;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PageRank {

    private static final double COMPARE_DELTA = 0.0001;
    private static final double DAMPING_FACTOR = 0.9;
    private static final boolean IS_CALCULATION_DAMPED = true;
    private DataAccess dbAccess;
    private Double equalDistributionValue;
    private long startId, endId;

    /*
    connect to database and initialize vektor of every node with 1/amountOfNodes
     */
    public PageRank(long startId, long endId) throws SQLException, ClassNotFoundException {
        this.startId = startId;
        this.endId = endId;
        dbAccess = DataAccess.getAccess();
        initialize();
    }

    public static PageRank getInstance(long startId, long endId) throws SQLException, ClassNotFoundException {
        return new PageRank(startId, endId);
    }

    public static void start(long limit) throws SQLException, ClassNotFoundException {
        DataAccess dataAccess = DataAccess.getAccess();
        dataAccess.resetPageRankTable();
        long startId, endId;
        int i = 0;
        while (true) {
            int chapter = i + 1;
            System.out.println("PageRanker (chapter " + chapter + ") : started!");
            System.out.println("PageRanker (chapter " + chapter + ") : loading ...");
            startId = i * limit;
            endId = (i + 1) * limit;
            try {
                getInstance(startId, endId).calculatePageRank();
            } catch (Exception ignored) {
            }
            System.out.println("PageRanker (chapter " + chapter + ") : finished!");
            i++;
        }
    }

    /*
    set every vektor at the beginning of the calculation to initial value which is 1/amountOfNodes
     */
    private void initializeVector() throws SQLException {
        dbAccess.initVektor(getEqualDistributionVektorValue(), startId, endId);
    }

    private void initialize() throws SQLException {
        ResultSet resultSet = dbAccess.getUrls(startId, endId);
        resultSet.first();
        if (resultSet.next()) {
            do {
                long docUrlID = resultSet.getLong("id");
                dbAccess.insertUrl(docUrlID);
                initializeVector();
                int amountOutgoingLinks = getOutgoingLinksCount(docUrlID);
                dbAccess.setOutgoingLinksValue(amountOutgoingLinks, docUrlID, startId, endId);
            } while (resultSet.next());
        }
    }

    /*
    looping calculation while pagerank != vektor, every loop is headed by setting vektor = pagerank and pagerank = 0
     */
    public void calculatePageRank() throws SQLException {
        calculate();
        while (!isCalculationFinished()) {
            //System.out.println(dbAccess.getVektorSum());
            prepareNextRound();
            calculate();
        }
    }

    /*
    count amount of nodes
     */
    public long getAmountOfNodes() throws SQLException {
//        return dbAccess.getNodesCount(limit);
        return endId - startId;
    }

    /*
    iterate over all nodes, calculate increasing value by dividing vektor by the amount of outgoing links and
    increase the pagerank value of the nodes of the outgoing links
     */
    public void calculate() throws SQLException {
        ResultSet nodes = dbAccess.getPageRankTable(startId, endId);
        while (nodes.next()) {
            int id = nodes.getInt("doc_url_id");
            double vektor = nodes.getDouble("vektor");
            int outgoingLinksCount = nodes.getInt("outgoing");
            double increasingValue = vektor / outgoingLinksCount;
            increaseOutgoingLinksByValue(id, increasingValue);
        }
        if (isCalculationDamped()) dampPageRank();
    }

    private void dampPageRank() throws SQLException {
        dbAccess.dampPageRank(DAMPING_FACTOR, equalDistributionValue, startId, endId);
    }

    /*
    get amount of outgoing links for the specified sourceId
     */
    private int getOutgoingLinksCount(long sourceId) throws SQLException {
        return dbAccess.countOutgoingLinks(sourceId);
    }

    /*
    get all outgoing links and increase the value of the Pagerank of the connected nodes
     */
    private void increaseOutgoingLinksByValue(int sourceId, double value) throws SQLException {
        ResultSet outgoingLink = dbAccess.getOutgoingLinks(sourceId);

        while (outgoingLink.next()) {
            int targetId = outgoingLink.getInt("target");
            increasePagerankByValue(targetId, value);
        }
    }

    /*
    increases the pageranke of a specified node by the given value
     */
    private void increasePagerankByValue(int targetId, double increasingValue) throws SQLException {
        dbAccess.increasePageRank(increasingValue, targetId, startId, endId);
    }

    /*
    prepare the next calculation round by setting vektor to pagerank and pagerank to 0
     */
    private void prepareNextRound() throws SQLException {
        dbAccess.prepareCalculation(startId, endId);
    }

    private boolean isCalculationDamped() {
        return IS_CALCULATION_DAMPED;
    }

    /*
    check every node if the vektor and pagerank are similar, if not it returns false by the first node which dont fit
     */
    private boolean isCalculationFinished() throws SQLException {
        ResultSet node = dbAccess.getPageRankTable(startId, endId);
        while (node.next()) {
            if (!isPageRankSimilarToVektor(node)) {
                return false;
            }
        }
        return true;
    }

    /*
    compare vektor and pagerank of given node by given COMPARE_DELTA
     */
    private boolean isPageRankSimilarToVektor(ResultSet node) throws SQLException {
        double vektor = node.getDouble("Vektor");
        double pageRank = node.getDouble("pagerank");
        return !(Math.abs(vektor - pageRank) >= COMPARE_DELTA);
    }

    private double getEqualDistributionVektorValue() throws SQLException {
        if (equalDistributionValue == null) {
            equalDistributionValue = (1.0 / dbAccess.getNodesCount());
        }
        return equalDistributionValue;
    }
}
