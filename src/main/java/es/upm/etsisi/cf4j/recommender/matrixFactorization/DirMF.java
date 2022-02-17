package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import org.apache.commons.math3.special.Gamma;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

/**
 * Implements Lara-Cabrera, R., Gonz&aacute;lez, &Aacute;., Ortega, F., &amp; Gonz&aacute;lez-Prieto, &Aacute;. (2022).
 * Dirichlet Matrix Factorization: A Reliable Classification-Based Recommender System. Applied Sciences, 12(3), 1223.
 */
public class DirMF extends Recommender {

    /** Number of latent factors */
    private int numFactors;

    /** Number of iterations */
    private int numIters;

    /** Learning rate */
    private double learningRate;

    /** Regularization parameter */
    private double regularization;

    /** Discrete ratings values **/
    private double[] ratings;

    /** Users factors **/
    private double[][][] P;

    /** Items factors **/
    private double[][][] Q;

    /**
     * Model constructor from a Map containing the model's hyper-parameters values. Map object must
     * contains the following keys:
     *
     * <ul>
     *   <li><b>numFactors</b>: int value with the number of latent factors.</li>
     *   <li><b>numIters:</b>: int value with the number of iterations.</li>
     *   <li><b>learningRate</b>: double value with the learning rate hyper-parameter.</li>
     *   <li><b>regularization</b>: double value with the regularization hyper-parameter.</li>
     *   <li><b>ratings</b>: discrete ratings values.</li>
     *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
     *       random value is used.
     * </ul>
     *
     * @param datamodel DataModel instance
     * @param params Model's hyper-parameters values
     */
    public DirMF(DataModel datamodel, Map<String, Object> params) {
        this(
                datamodel,
                (int) params.get("numFactors"),
                (int) params.get("numIters"),
                (double) params.get("learningRate"),
                (double) params.get("regularization"),
                (double[]) params.get("ratings"),
                params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis()
        );
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     * @param learningRate Learning rate
     * @param regularization Regularization
     * @param ratings Discrete ratings values
     */
    public DirMF(DataModel datamodel, int numFactors, int numIters, double learningRate, double regularization, double[] ratings) {
        this(datamodel, numFactors, numIters, learningRate, regularization, ratings, System.currentTimeMillis());
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     * @param learningRate Learning rate
     * @param regularization Regularization
     * @param ratings Discrete ratings values
     * @param seed Seed for random numbers generation
     */
    public DirMF(DataModel datamodel, int numFactors, int numIters, double learningRate, double regularization, double[] ratings, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.ratings = ratings;

        Random rand = new Random(seed);

        this.P = new double[ratings.length][datamodel.getNumberOfUsers()][numFactors];
        for (int r = 0; r < ratings.length; r++) {
            for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
                for (int k = 0; k < numFactors; k++) {
                    this.P[r][u][k] = rand.nextDouble();
                }
            }
        }

        this.Q = new double[ratings.length][datamodel.getNumberOfItems()][numFactors];
        for (int r = 0; r < ratings.length; r++) {
            for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
                for (int k = 0; k < numFactors; k++) {
                    this.Q[r][i][k] = rand.nextDouble();
                }
            }
        }
    }

    /**
     * Get the number of factors of the model
     *
     * @return Number of factors
     */
    public int getNumFactors() { return numFactors; }

    /**
     * Get the number of iterations
     *
     * @return Number of iterations
     */
    public int getNumIters() {
        return numIters;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Learning rate
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * Get the regularization parameter of the model
     *
     * @return Regularization
     */
    public double getRegularization() {
        return regularization;
    }

    /**
     * Get the discrete ratings values
     *
     * @return Discrete ratings values
     */
    public double[] getRatings() {
        return ratings;
    }

    @Override
    public void fit() {
        System.out.println("\nFitting " + this.toString());

        for (int iter = 1; iter <= this.numIters; iter++) {
            Parallelizer.exec(datamodel.getUsers(), new UpdateUsersFactors());
            Parallelizer.exec(datamodel.getItems(), new UpdateItemsFactors());

            if ((iter % 10) == 0) System.out.print(".");
            if ((iter % 100) == 0) System.out.println(iter + " iterations");
        }
    }

    @Override
    public double predict(int userIndex, int itemIndex) {
        double max = this.getProbability(userIndex, itemIndex, 0);
        int index = 0;

        for (int r = 1; r < this.ratings.length; r++) {
            double prob = this.getProbability(userIndex, itemIndex, r);
            if (max < prob) {
                max = prob;
                index = r;
            }
        }

        return this.ratings[index];
    }

    /**
     * Compute the probability that an user rates an item with the rating at r position
     *
     * @param userIndex Index of the user in the array of Users of the DataModel instance
     * @param itemIndex Index of the item in the array of Items of the DataModel instance
     * @param r Rating position on the discrete rating values array
     * @return Prediction probability
     */
    private double getProbability(int userIndex, int itemIndex, int r) {
        double dot = Maths.logistic(Maths.dotProduct(this.P[r][userIndex], this.Q[r][itemIndex]));

        double sum = 0;
        for (int i = 0; i < this.ratings.length; i++) {
            sum += Maths.logistic(Maths.dotProduct(this.P[i][userIndex], this.Q[i][itemIndex]));
        }

        return dot / sum;
    }

    /**
     * Computes a prediction probability
     *
     * @param userIndex Index of the user in the array of Users of the DataModel instance
     * @param itemIndex Index of the item in the array of Items of the DataModel instance
     * @return Prediction probability
     */
    public double predictProba(int userIndex, int itemIndex) {
        double prediction = this.predict(userIndex, itemIndex);

        int r = 0;
        while (this.ratings[r] != prediction) {
            r++;
        }

        return this.getProbability(userIndex, itemIndex, r);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("DirMF(")
                .append("numFactors=").append(this.numFactors)
                .append("; ")
                .append("numIters=").append(this.numIters)
                .append("; ")
                .append("learningRate=").append(this.learningRate)
                .append("; ")
                .append("regularization=").append(this.regularization)
                .append("; ")
                .append("ratings=").append(Arrays.toString(this.ratings))
                .append(")");
        return str.toString();
    }

    /**
     * Auxiliary inner class to parallelize user factors computation
     */
    private class UpdateUsersFactors implements Partible<User> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(User user) {
            int userIndex = user.getUserIndex();

            for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                int itemIndex = user.getItemAt(pos);

                double sum = 0;
                for (int s = 0; s < ratings.length; s++) {
                    double dot = Maths.dotProduct(P[s][userIndex], Q[s][itemIndex]);
                    sum += Maths.logistic(dot);
                }

                for (int s = 0; s < ratings.length; s++) {
                    double rating = user.getRatingAt(pos);
                    double r_ui = (rating == ratings[s] ? Math.exp(rating) : 1) / (ratings.length - 1 + Math.exp(rating));

                    double dot = Maths.dotProduct(P[s][userIndex], Q[s][itemIndex]);
                    double logit = Maths.logistic(dot);

                    for (int k = 0; k < numFactors; k++) {
                        double gradient = Q[s][itemIndex][k] * logit * (1 - logit) * (Gamma.digamma(logit) - Gamma.digamma(sum) - Math.log(r_ui));
                        P[s][userIndex][k] -= learningRate * (gradient + regularization * P[s][userIndex][k]);
                    }
                }
            }
        }

        @Override
        public void afterRun() { }
    }

    /**
     * Auxiliary inner class to parallelize item factors computation
     */
    private class UpdateItemsFactors implements Partible<Item> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(Item item) {
            int itemIndex = item.getItemIndex();

            for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
                int userIndex = item.getUserAt(pos);

                double sum = 0;
                for (int s = 0; s < ratings.length; s++) {
                    double dot = Maths.dotProduct(P[s][userIndex], Q[s][itemIndex]);
                    sum += Maths.logistic(dot);
                }

                for (int s = 0; s < ratings.length; s++) {
                    double rating = item.getRatingAt(pos);
                    double r_ui = (rating == ratings[s] ? Math.exp(rating) : 1) / (ratings.length - 1 + Math.exp(rating));

                    double dot = Maths.dotProduct(P[s][userIndex], Q[s][itemIndex]);
                    double logit = Maths.logistic(dot);

                    for (int k = 0; k < numFactors; k++) {
                        double gradient = P[s][userIndex][k] * logit * (1 - logit) * (Gamma.digamma(logit) - Gamma.digamma(sum) - Math.log(r_ui));
                        Q[s][itemIndex][k] -= learningRate * (gradient + regularization * Q[s][itemIndex][k]);
                    }
                }
            }
        }

        @Override
        public void afterRun() { }
    }
}