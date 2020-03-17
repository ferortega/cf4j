package es.upm.etsisi.cf4j.recommender.knn;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.itemToItemMetrics.ItemToItemMetric;
import es.upm.etsisi.cf4j.utils.Methods;

import java.lang.reflect.InvocationTargetException;

public class ItemToItem extends Recommender {

    public enum AggregationApproach {MEAN, WEIGTHED_MEAN}

    protected int[][] neighbors;

    protected double[][] similarities;

    protected int K;

    private Class<? extends ItemToItemMetric> metricClass;

    private AggregationApproach aa;


    public ItemToItem(DataModel datamodel, int K, Class<? extends ItemToItemMetric> metricClass, AggregationApproach aa) {
        super(datamodel);

        this.K = K;

        int numUsers = this.datamodel.getNumberOfUsers();

        this.neighbors = new int[numUsers][K];
        this.similarities = new double[numUsers][numUsers];

        this.aa = aa;

        this.metricClass = metricClass;
    }

    @Override
    public void fit() {
        ItemToItemMetric metric = null;

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

        Parallelizer.exec(this.datamodel.getItems(), metric);
        Parallelizer.exec(this.datamodel.getItems(), new ItemNeighbors());
    }

    @Override
    public double predict(int userIndex, int itemIndex) {
        switch(this.aa) {
            case MEAN:
                return predictMean(userIndex, itemIndex);
            case WEIGTHED_MEAN:
                return predictWeigthedMean(userIndex, itemIndex);
            default:
                return Double.NaN;
        }
    }

    private double predictMean(int userIndex, int itemIndex) {
        User user = this.datamodel.getUser(userIndex);

        double prediction = 0;
        int count = 0;

        for (int neighborIndex : this.neighbors[itemIndex]) {
            if (neighborIndex == -1) break; // Neighbors array are filled with -1 when no more neighbors exists

            int pos = user.findItem(neighborIndex);
            if (pos != -1) {
                prediction += user.getRatingAt(pos);
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
        User user = this.datamodel.getUser(userIndex);

        double num = 0;
        double den = 0;

        for (int neighborIndex : this.neighbors[itemIndex]) {
            if (neighborIndex == -1) break; // Neighbors array are filled with -1 when no more neighbors exists

            int pos = user.findItem(neighborIndex);
            if (pos != -1) {
                double similarity = this.similarities[itemIndex][neighborIndex];
                double rating = user.getRatingAt(pos);
                num += similarity * rating;
                den += similarity;
            }
        }

        return (den == 0) ? Double.NaN : num / den;
    }

    private class ItemNeighbors implements Partible<Item> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(Item item) {
            int itemIndex = item.getItemIndex();
            neighbors[itemIndex] = Methods.findTopN(similarities[itemIndex], K);
        }

        @Override
        public void afterRun() { }
    }
}
