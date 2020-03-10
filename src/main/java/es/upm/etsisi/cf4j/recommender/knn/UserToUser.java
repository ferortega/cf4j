package es.upm.etsisi.cf4j.recommender.knn;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallel;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetrics.UserSimilarities;
import es.upm.etsisi.cf4j.utils.Methods;

import java.lang.reflect.InvocationTargetException;

public class UserToUser extends Recommender {

    public enum AggregationApproach {MEAN, WEIGTHED_MEAN, DEVIATION_FROM_MEAN}

    protected int[][] neighbors;

    protected double[][] similarities;

    protected int K;

    private Class<? extends UserSimilarities> metricClass;

    private AggregationApproach aa;


    public UserToUser(DataModel datamodel, int K, Class<? extends UserSimilarities> metricClass, AggregationApproach aa) {
        super(datamodel);

        this.K = K;

        this.similarities = new double[datamodel.getNumberOfTestUsers()][datamodel.getNumberOfUsers()];
        this.neighbors = new int[datamodel.getNumberOfTestUsers()][K];

        this.aa = aa;

        this.metricClass = metricClass;
    }

    @Override
    public void fit() {
        UserSimilarities metric = null;

        try {
            metric = metricClass.getDeclaredConstructor(DataModel.class, double[][].class).newInstance(this.datamodel, this.similarities);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Parallel.run(this.datamodel.getTestUsers(), metric);
        Parallel.run(this.datamodel.getTestUsers(), new UserNeighbors());
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

            User neighbor = this.datamodel.getUserAt(neighborIndex);

            int i = neighbor.getItemIndex(itemIndex);
            if (i != -1) {
                prediction += neighbor.getRatingAt(i);
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

            User neighbor = this.datamodel.getUserAt(neighborIndex);

            int i = neighbor.getItemIndex(itemIndex);
            if (i != -1) {
                double similarity = this.similarities[userIndex][neighborIndex];
                double rating = neighbor.getRatingAt(i);
                num += similarity * rating;
                den += similarity;
            }
        }

        return (den == 0) ? Double.NaN : num / den;
    }

    private double predictDeviationFromMean(int userIndex, int itemIndex) {
        User user = this.datamodel.getUserAt(userIndex);

        double num = 0;
        double den = 0;

        for (int neighborIndex : this.neighbors[userIndex]) {
            if (neighborIndex == -1) break; // Neighbors array are filled with -1 when no more neighbors exists

            User neighbor = this.datamodel.getUserAt(neighborIndex);

            int i = neighbor.getItemIndex(itemIndex);
            if (i != -1) {
                double similarity = this.similarities[userIndex][neighborIndex];
                double rating = neighbor.getRatingAt(i);
                double avg = neighbor.getRatingAverage();

                num += similarity * (rating - avg);
                den += similarity;
            }
        }

        return (den == 0)
                ? Double.NaN
                : user.getRatingAverage() + num / den;
    }

    private class UserNeighbors implements Partible<TestUser> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(TestUser testUser) {
            int testUserIndex = testUser.getTestIndex();
            neighbors[testUserIndex] = Methods.findTopN(similarities[testUserIndex], K);
        }

        @Override
        public void afterRun() { }
    }
}
