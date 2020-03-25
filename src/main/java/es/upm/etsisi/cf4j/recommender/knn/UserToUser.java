package es.upm.etsisi.cf4j.recommender.knn;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.userToUserMetrics.UserToUserMetric;
import es.upm.etsisi.cf4j.utils.Search;

public class UserToUser extends Recommender {

    public enum AggregationApproach {MEAN, WEIGTHED_MEAN, DEVIATION_FROM_MEAN}

    protected int[][] neighbors;

    protected double[][] similarities;

    protected int K;

    private UserToUserMetric metric;

    private AggregationApproach aa;


    public UserToUser(DataModel datamodel, int K, UserToUserMetric metric, AggregationApproach aa) {
        super(datamodel);

        this.K = K;

        int numUsers = this.datamodel.getNumberOfUsers();

        this.neighbors = new int[numUsers][K];
        this.similarities = new double[numUsers][numUsers];

        this.aa = aa;

        this.metric = metric;
        this.metric.setDatamodel(datamodel);
        this.metric.setSimilarities(this.similarities);
    }

    @Override
    public void fit() {
        Parallelizer.exec(this.datamodel.getUsers(), this.metric);
        Parallelizer.exec(this.datamodel.getUsers(), new UserNeighbors());
    }

    @Override
    public double predict(int userIndex, int itemIndex) {
        switch(this.aa) {
            case MEAN:
                return predictMean(userIndex, itemIndex);
            case WEIGTHED_MEAN:
                return predictWeigthedMean(userIndex, itemIndex);
            case DEVIATION_FROM_MEAN:
                return predictDeviationFromMean(userIndex, itemIndex);
            default:
                return Double.NaN;
        }
    }

    private double predictMean(int userIndex, int itemIndex) {
        double prediction = 0;
        int count = 0;

        for (int neighborIndex : this.neighbors[userIndex]) {
            if (neighborIndex == -1) break; // Neighbors array are filled with -1 when no more neighbors exists

            User neighbor = this.datamodel.getUser(neighborIndex);

            int pos = neighbor.findItem(itemIndex);
            if (pos != -1) {
                prediction += neighbor.getRatingAt(pos);
                count++;
            }
        }

        if (count == 0) {
            return Double.NaN;
        } else {
            prediction /= count;
            return prediction;
        }
    }

    private double predictWeigthedMean(int userIndex, int itemIndex) {
        double num = 0;
        double den = 0;

        for (int neighborIndex : this.neighbors[userIndex]) {
            if (neighborIndex == -1) break; // Neighbors array are filled with -1 when no more neighbors exists

            User neighbor = this.datamodel.getUser(neighborIndex);

            int pos = neighbor.findItem(itemIndex);
            if (pos != -1) {
                double similarity = this.similarities[userIndex][neighborIndex];
                double rating = neighbor.getRatingAt(pos);
                num += similarity * rating;
                den += similarity;
            }
        }

        return (den == 0) ? Double.NaN : num / den;
    }

    private double predictDeviationFromMean(int userIndex, int itemIndex) {
        User user = this.datamodel.getUser(userIndex);

        double num = 0;
        double den = 0;

        for (int neighborIndex : this.neighbors[userIndex]) {
            if (neighborIndex == -1) break; // Neighbors array are filled with -1 when no more neighbors exists

            User neighbor = this.datamodel.getUser(neighborIndex);

            int pos = neighbor.findItem(itemIndex);
            if (pos != -1) {
                double similarity = this.similarities[userIndex][neighborIndex];
                double rating = neighbor.getRatingAt(pos);
                double avg = neighbor.getRatingAverage();

                num += similarity * (rating - avg);
                den += similarity;
            }
        }

        return (den == 0)
                ? Double.NaN
                : user.getRatingAverage() + num / den;
    }

    private class UserNeighbors implements Partible<User> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(User user) {
            int userIndex = user.getUserIndex();
            neighbors[userIndex] = Search.findTopN(similarities[userIndex], K);
        }

        @Override
        public void afterRun() { }
    }
}
