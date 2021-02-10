package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

/**
 * Implements Ortega, F., Lara-Cabrera, R., González-Prieto, Á., & Bobadilla, J. (2021). Providing reliability in
 * recommender systems through Bernoulli matrix factorization. Information Sciences, 553, 110-128.
 */
public class BeMF extends Recommender {

    /** Number of latent factors */
    private final int numFactors;

    /** Number of iterations */
    private final int numIters;

    /** Learning rate */
    private final double learningRate;

    /** Regularization parameter */
    private final double regularization;

    /** Discrete ratings values **/
    private final double[] ratings;

    /** Users factors **/
    private final double[][][] U;

    /** Items factors **/
    private final double[][][] V;

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
    public BeMF(DataModel datamodel, Map<String, Object> params) {
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
    public BeMF(DataModel datamodel, int numFactors, int numIters, double learningRate, double regularization, double[] ratings) {
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
    public BeMF(DataModel datamodel, int numFactors, int numIters, double learningRate, double regularization, double[] ratings, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.ratings = ratings;

        Random rand = new Random(seed);

        this.U = new double[ratings.length][datamodel.getNumberOfUsers()][numFactors];
        for (int r = 0; r < ratings.length; r++) {
            for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
                for (int k = 0; k < numFactors; k++) {
                    this.U[r][u][k] = rand.nextDouble();
                }
            }
        }


        this.V = new double[ratings.length][datamodel.getNumberOfItems()][numFactors];
        for (int r = 0; r < ratings.length; r++) {
            for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
                for (int k = 0; k < numFactors; k++) {
                    this.V[r][i][k] = rand.nextDouble();
                }
            }
        }
    }

    /**
     * Get the number of factors of the model
     *
     * @return Number of factors
     */
    public int getNumFactors() {
        return numFactors;
    }

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
            for (int r = 0; r < this.ratings.length; r++) {
                Parallelizer.exec(this.datamodel.getUsers(), new UpdateUsersFactors(U[r], V[r], ratings[r]));
                Parallelizer.exec(this.datamodel.getItems(), new UpdateItemsFactors(U[r], V[r], ratings[r]));
            }

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
        double dot = Maths.logistic(Maths.dotProduct(this.U[r][userIndex], this.V[r][itemIndex]));

        double sum = 0;
        for (int i = 0; i < this.ratings.length; i++) {
            sum += Maths.logistic(Maths.dotProduct(this.U[i][userIndex], this.V[i][itemIndex]));
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
        StringBuilder str = new StringBuilder("BeMF(")
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

        private final double rating;

        private final double[][] U;

        private final double[][] V;

        public UpdateUsersFactors(double[][] U, double[][] V, double rating) {
            this.U = U;
            this.V = V;
            this.rating = rating;
        }

        @Override
        public void beforeRun() { }

        @Override
        public void run(User user) {
            int userIndex = user.getUserIndex();

            double[] gradient = new double[numFactors];

            for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                boolean oneHot = user.getRatingAt(pos) == rating;

                int itemIndex = user.getItemAt(pos);
                double dot = Maths.dotProduct(U[userIndex], V[itemIndex]);

                for (int k = 0; k < numFactors; k++) {
                    if (oneHot) {
                        gradient[k] += (1 - Maths.logistic(dot)) * V[itemIndex][k];
                    } else {
                        gradient[k] -= Maths.logistic(dot) * V[itemIndex][k];
                    }
                }
            }

            for (int k = 0; k < numFactors; k++) {
                U[userIndex][k] += learningRate * (gradient[k] - regularization * U[userIndex][k]);
            }
        }

        @Override
        public void afterRun() { }
    }

    /**
     * Auxiliary inner class to parallelize item factors computation
     */
    private class UpdateItemsFactors implements Partible<Item> {

        private final  double rating;

        private final  double[][] U;

        private final  double[][] V;

        public UpdateItemsFactors(double[][] U, double[][] V, double rating) {
            this.U = U;
            this.V = V;
            this.rating = rating;
        }

        @Override
        public void beforeRun() { }

        @Override
        public void run(Item item) {
            int itemIndex = item.getItemIndex();

            double[] gradient = new double[numFactors];

            for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
                boolean oneHot = item.getRatingAt(pos) == rating;

                int userIndex = item.getUserAt(pos);
                double dot = Maths.dotProduct(U[userIndex], V[itemIndex]);

                for (int k = 0; k < numFactors; k++) {
                    if (oneHot) {
                        gradient[k] += (1 - Maths.logistic(dot)) * U[userIndex][k];
                    } else {
                        gradient[k] -= Maths.logistic(dot) * U[userIndex][k];
                    }
                }
            }

            for (int k = 0; k < numFactors; k++) {
                V[itemIndex][k] += learningRate * (gradient[k] - regularization * V[itemIndex][k]);
            }
        }

        @Override
        public void afterRun() { }
    }
}
