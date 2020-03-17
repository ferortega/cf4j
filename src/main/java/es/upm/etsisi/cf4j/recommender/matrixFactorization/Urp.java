package es.upm.etsisi.cf4j.recommender.matrixFactorization;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.apache.commons.math3.special.Gamma;

import java.util.Arrays;
import java.util.Random;

public class Urp extends Recommender {

    private final static double EPSILON = 1E-2;

    private int numIters;

    private int numFactors;

    private double[] ratings; // must be sorted

    private double[][] gamma;

    private double[][][] beta;

    private double[] alpha;

    private double[][][] phi;

    public Urp(DataModel datamodel, int numFactors, double [] ratings, int numIters) {
        this(datamodel, numFactors, ratings, numIters, new Double(Math.random() * 1E100).longValue());
    }

    public Urp(DataModel datamodel, int numFactors, double [] ratings, int numIters, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.ratings = ratings;

        int numRatings = ratings.length;
        int numUsers = datamodel.getNumberOfUsers();
        int numItems = datamodel.getNumberOfItems();

        Random rand = new Random(seed);

        this.gamma = new double[numUsers][numFactors];
        for (int i = 0; i < this.gamma.length; i++) {
            for (int j = 0; j < this.gamma[i].length; j++) {
                this.gamma[i][j] = rand.nextDouble();
            }
        }

        this.beta = new double[numItems][numRatings][numFactors];
        for (int i = 0; i < this.beta.length; i++) {
            for (int j = 0; j < this.beta[i].length; j++) {
                for (int k = 0; k < this.beta[i][j].length; k++) {
                    this.beta[i][j][k] = rand.nextDouble();
                }
            }
        }

        this.alpha = new double[numFactors];
        for (int i = 0; i < this.alpha.length; i++) {
            this.alpha[i] = rand.nextDouble();
        }

        this.phi = new double[numUsers][numItems][numFactors];
    }

    @Override
    public void fit() {

        for (int iter = 1; iter <= this.numIters; iter++) {

            System.out.println("iteration " + iter + " of " + this.numIters);

            Parallelizer.exec(this.datamodel.getUsers(), new UpdatePhiGamma());
            Parallelizer.exec(this.datamodel.getItems(), new UpdateBeta());

            double diff;

            do {
                diff = 0;

                double as = 0; // alpha sum
                for (int z = 0; z < this.numFactors; z++) {
                    as += this.alpha[z];
                }

                for (int z = 0; z < this.numFactors; z++) {

                    double gs = 0; // gamma sum
                    for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
                        gs += this.gamma[userIndex][z];
                    }

                    double sum = 0;
                    for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
                        sum += Gamma.digamma(this.gamma[userIndex][z]) - Gamma.digamma(gs);
                    }

                    double psiAlpha = Gamma.digamma(as) + sum / datamodel.getNumberOfUsers();

                    double newAlpha = this.inversePsi(psiAlpha);

                    diff += Math.abs(this.alpha[z] - newAlpha);

                    this.alpha[z] = newAlpha;
                }
            } while (diff > EPSILON);
        }
    }

    @Override
    public double predict(int userIndex, int itemIndex) {
        double sum = 0;
        for (int j = 0; j < this.numFactors; j++) {
            sum += this.gamma[userIndex][j];
        }

        double[] probs = new double[this.ratings.length];
        double sumProbs = 0;
        for (int v = 0; v < this.ratings.length; v++) {
            for (int z = 0; z < this.numFactors; z++) {
                probs[v] += this.beta[itemIndex][v][z] * this.gamma[userIndex][z] / sum;
            }
            sumProbs += probs[v];
        }

        double acc = 0;
        int v = -1;

        do {
            v++;
            acc += probs[v] / sumProbs;
        } while(acc < 0.5);

        double prediction = this.ratings[v];

        return prediction;
    }

    private double inversePsi(double value) {

        // source: http://bariskurt.com/calculating-the-inverse-of-digamma-function/

        double inv = (value >= -2.22)
                ? Math.exp(value) + 0.5
                : -1.0 / (value - Gamma.digamma(1));

        for (int i = 0; i < 3; i++) {
            inv -= (Gamma.digamma(inv) - value) / Gamma.trigamma(inv);
        }

        return inv;
    }

    private class UpdatePhiGamma implements Partible<User> {

        @Override
        public void beforeRun() {

        }

        @Override
        public void run(User user) {
            int userIndex = user.getIndex();

            for (int h = 0; h < user.getNumberOfRatings(); h++) {

                // update phi

                double gs = 0; // gamma sum
                for (int z = 0; z < numFactors; z++) {
                    gs += gamma[userIndex][z];
                }

                int i = 0;

                for (int itemIndex = 0; itemIndex < datamodel.getNumberOfItems(); itemIndex++) {
                    Item item = datamodel.getItemAt(itemIndex);

                    boolean rated = i < user.getNumberOfRatings() && user.getItem(i) == itemIndex;

                    int v = -1;

                    if (rated) {
                        v = Arrays.binarySearch(ratings, user.getRatingAt(i));
                        i++;
                    }

                    double sum = 0;

                    for (int z = 0; z < Urp.this.numFactors; z++) {
                        phi[userIndex][itemIndex][z] = Math.exp(Gamma.digamma(gamma[userIndex][z]) - Gamma.digamma(gs));

                        if (rated) {
                            phi[userIndex][itemIndex][z] *= beta[itemIndex][v][z];
                        }

                        sum += phi[userIndex][itemIndex][z];
                    }

                    for (int z = 0; z < Urp.this.numFactors; z++) {
                        phi[userIndex][itemIndex][z] /= sum;
                    }
                }

                // update gamma

                for (int z = 0; z < numFactors; z++) {
                    gamma[userIndex][z] = Urp.this.alpha[z];
                    for (int itemIndex = 0; itemIndex < datamodel.getNumberOfItems(); itemIndex++) {
                        gamma[userIndex][z] += phi[userIndex][itemIndex][z];
                    }
                }
            }

        }

        @Override
        public void afterRun() {

        }
    }

    private class UpdateBeta implements Partible<Item> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(Item item) {
            int itemIndex = item.getIndex();

            beta[itemIndex] = new double[ratings.length][numFactors]; // reset beta

            int userIndex = 0;
            int u = 0;

            while (u < item.getNumberOfRatings() && userIndex < datamodel.getNumberOfUsers()) {
                User user = datamodel.getUserAt(userIndex);

                if (item.getUser(u) == userIndex) {
                    double rating = item.getRating(u);
                    int v = Arrays.binarySearch(ratings, rating);

                    for (int z = 0; z < numFactors; z++) {
                        beta[itemIndex][v][z] += phi[userIndex][itemIndex][z];
                    }

                    userIndex++;
                    u++;

                // userCode < item.getUserAt(u)
                } else {
                    userIndex++;
                }
            }
        }

        @Override
        public void afterRun() { }
    }
}
