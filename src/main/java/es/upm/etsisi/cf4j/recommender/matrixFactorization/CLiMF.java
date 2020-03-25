package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Maths;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implements Shi, Y., Karatzoglou, A., Baltrunas, L., Larson, M., Oliver, N., &amp; Hanjalic, A. (2012, September).
 * CLiMF: learning to maximize reciprocal rank with collaborative less-is-more filtering. In Proceedings of the sixth
 * ACM conference on Recommender systems (pp. 139-146).
 */
public class CLiMF extends Recommender {

    /**
     * Number of latent factors
     */
    protected int numFactors;

    /**
     * Learning rate
     */
    protected double gamma;

    /**
     * Regularization
     */
    protected double lambda;

    /**
     * Number of iterations
     */
    protected int numIters;

    /**
     * Threshold to binarize rating matrix. Any rating greater or equal than this threshold will be used during the
     * training process.
     */
    protected double threshold;

    /**
     * Users' latent factors
     */
    protected double[][] U;

    /**
     * Items's latent factors
     */
    protected double[][] V;

    /**
     * Model constructor
     * @param datamodel DataModel instance
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     * @param threshold Threshold to binarize rating matrix. Any rating greater or equal than this
     *     threshold will be used during the training process.
     * @param seed Seed for random numbers generation
     */
    public CLiMF(DataModel datamodel, int numFactors, int numIters, double threshold, long seed) {
        this(datamodel, numFactors, 1E-5, 1E-4, numIters, threshold, seed);
    }

    /**
     * Model constructor
     * @param datamodel DataModel instance
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     * @param threshold Threshold to binarize rating matrix. Any rating greater or equal than this
     *     threshold will be used during the training process.
     */
    public CLiMF(DataModel datamodel, int numFactors, int numIters, double threshold) {
        this(datamodel, numFactors, 1E-5, 1E-4, numIters, threshold);
    }

    /**
     * Model constructor
     * @param datamodel DataModel instance
     * @param numFactors Number of latent factors
     * @param gamma Learning rate
     * @param lambda Regularization
     * @param numIters Number of iterations
     * @param threshold Threshold to binarize rating matrix. Any rating greater or equal than this
     *     threshold will be used during the training process.
     */
    public CLiMF(DataModel datamodel, int numFactors, double gamma, double lambda, int numIters, double threshold) {
        this(datamodel, numFactors, gamma, lambda, numIters, threshold, System.currentTimeMillis());
    }

  /**
   * Model constructor
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param gamma Learning rate
   * @param lambda Regularization
   * @param numIters Number of iterations
   * @param threshold Threshold to binarize rating matrix. Any rating greater or equal than this
   *     threshold will be used during the training process.
   * @param seed Seed for random numbers generation
   */
  public CLiMF(DataModel datamodel, int numFactors, double gamma, double lambda, int numIters, double threshold, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.gamma = gamma;
        this.lambda = lambda;
        this.numIters = numIters;
        this.threshold = threshold;

        int numUsers = datamodel.getNumberOfUsers();
        int numItems = datamodel.getNumberOfItems();

        Random generator = new Random(seed);

        this.U = new double[numUsers][numFactors];
        for (int u = 0; u < numUsers; u++) {
            for (int f = 0; f < numFactors; f++) {
                this.U[u][f] = generator.nextDouble();
            }
        }

        this.V = new double[numItems][numFactors];
        for (int i = 0; i < numItems; i++) {
            for (int f = 0; f < numFactors; f++) {
                this.V[i][f] = generator.nextDouble();
            }
        }
    }

    @Override
    public void fit() {
        for (int iter = 1; iter <= this.numIters; iter++) {
            Parallelizer.exec(super.datamodel.getUsers(), new UpdateModel());
        }
    }

    @Override
    public double predict(int userIndex, int itemIndex) {
        return Maths.dotProduct(this.U[userIndex], this.V[itemIndex]);
    }

    /**
     * Returns logistic function value
     * @param x Value for which logistic function must be computed
     * @return Logistic function of x
     */
    private static double logistic(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Returns the gradient value of the logistic function
     * @param x Value for which gradient value of logistic function must be computed
     * @return Gradient value of logistic function of x
     */
    private static double logisticGradientValue(double x) {
        return logistic(x) * logistic(-x);
    }

    /**
     * Auxiliary inner class to parallelize model update
     */
    private class UpdateModel implements Partible<User> {

        private final int NUM_LOCKS = 100;

        private ReentrantLock[] locks;

        private double[][] usersGradients;
        private double[][] itemsGradients;

        public UpdateModel () {

            // Locks avoid problem while items' V are updated in different threads
            this.locks = new ReentrantLock [NUM_LOCKS];
            for (int i = 0; i < NUM_LOCKS; i++) {
                this.locks[i] = new ReentrantLock();
            }

            this.usersGradients = new double[datamodel.getNumberOfUsers()][numFactors];
            this.itemsGradients = new double[datamodel.getNumberOfItems()][numFactors];
        }

        @Override
        public void beforeRun() { }

        @Override
        public void run(User user) {
            int userIndex = user.getUserIndex();

            double[] userGradients = new double[numFactors];
            double[][] ratedItemsGradients = new double[user.getNumberOfRatings()][numFactors];

            for (int jPos = 0; jPos < user.getNumberOfRatings(); jPos++) {
                double jRating = user.getRatingAt(jPos);
                if (jRating >= threshold) {
                    int j = user.getItemAt(jPos);
                    double jPred = predict(userIndex, j);

                    for (int f = 0; f < numFactors; f++) {
                        userGradients[f] += logistic(-jPred) * V[j][f];
                        ratedItemsGradients[jPos][f] = logistic(-jPred) * U[userIndex][f];

                        for (int kPos = 0; kPos < user.getNumberOfRatings(); kPos++) {
                            double kRating = user.getRatingAt(kPos);
                            if (jPos != kPos && kRating >= threshold) {
                                int k = user.getItemAt(kPos);
                                double kPred = predict(userIndex, k);

                                double diff = kPred - jPred;

                                userGradients[f] += logisticGradientValue(diff) * (V[j][f] - V[k][f]) / (1.0 - logistic(diff));
                                ratedItemsGradients[jPos][f] += logisticGradientValue(-diff) * ((1.0 / (1.0 - logistic(diff))) - (1.0 / (1.0 - logistic(-diff)))) * U[userIndex][f];
                            }
                        }
                    }
                }
            }

            for (int f = 0; f < numFactors; f++) {
                this.usersGradients[userIndex][f] = userGradients[f] - lambda * U[userIndex][f];
            }

            for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                int itemIndex = user.getItemAt(pos);
                int lockIndex = itemIndex % this.locks.length;
                this.locks[lockIndex].lock();
                for (int f = 0; f < numFactors; f++) {
                    this.itemsGradients[itemIndex][f] += ratedItemsGradients[pos][f] - lambda * V[itemIndex][f];
                }
                this.locks[lockIndex].unlock();
            }
        }

        @Override
        public void afterRun() {
            for (int f = 0; f < numFactors; f++) {
                for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
                    U[userIndex][f] += gamma * this.usersGradients[userIndex][f];
                }

                for (int itemIndex = 0; itemIndex < datamodel.getNumberOfItems(); itemIndex++) {
                    V[itemIndex][f] += gamma * this.itemsGradients[itemIndex][f];
                }
            }
        }
    }
}
