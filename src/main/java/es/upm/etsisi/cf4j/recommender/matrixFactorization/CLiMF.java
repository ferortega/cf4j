package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements Shi, Y., Karatzoglou, A., Baltrunas, L., Larson, M., Oliver, N., &amp; Hanjalic, A.
 * (2012, September). CLiMF: learning to maximize reciprocal rank with collaborative less-is-more
 * filtering. In Proceedings of the sixth ACM conference on Recommender systems (pp. 139-146).
 */
public class CLiMF extends Recommender {

  protected static final double DEFAULT_GAMMA = 1E-5;
  protected static final double DEFAULT_LAMBDA = 1E-4;

  /** Number of latent factors */
  protected int numFactors;

  /** Learning rate */
  protected double gamma;

  /** Regularization */
  protected double lambda;

  /** Number of iterations */
  protected int numIters;

  /**
   * Threshold to binarize rating matrix. Any rating greater or equal than this threshold will be
   * used during the training process.
   */
  protected double threshold;

  /** Users' latent factors */
  protected double[][] U;

  /** Items's latent factors */
  protected double[][] V;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numFactors</b>: int value with the number of latent factors.
   *   <li><b>numIters:</b>: int value with the number of iterations.
   *   <li><b>threshold</b>: double value representing the rating value that binaries the matrix.
   *   <li><b><em>gamma</em></b> (optional): double value with the learning rate hyper-parameter. If
   *       missing, it is set to 1E-5.
   *   <li><b><em>lambda</em></b> (optional): double value with the regularization hyper-parameter.
   *       If missing, it is set to 1E-4.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public CLiMF(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int) params.get("numFactors"),
        params.containsKey("gamma") ? (double) params.get("gamma") : DEFAULT_GAMMA,
        params.containsKey("lambda") ? (double) params.get("lambda") : DEFAULT_LAMBDA,
        (int) params.get("numIters"),
        (double) params.get("threshold"),
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   */
  public CLiMF(DataModel datamodel, int numFactors, int numIters) {
    this(datamodel, numFactors, numIters, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   * @param seed Seed for random numbers generation
   */
  public CLiMF(DataModel datamodel, int numFactors, int numIters, long seed) {
    this(datamodel, numFactors, 1E-5, 1E-4, numIters, datamodel.getMinRating(), seed);
  }

  /**
   * Model constructor
   *
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
   *
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
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param gamma Learning rate
   * @param lambda Regularization
   * @param numIters Number of iterations
   * @param threshold Threshold to binarize rating matrix. Any rating greater or equal than this
   *     threshold will be used during the training process.
   */
  public CLiMF(
      DataModel datamodel,
      int numFactors,
      double gamma,
      double lambda,
      int numIters,
      double threshold) {
    this(datamodel, numFactors, gamma, lambda, numIters, threshold, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param gamma Learning rate
   * @param lambda Regularization
   * @param numIters Number of iterations
   * @param threshold Threshold to binarize rating matrix. Any rating greater or equal than this
   *     threshold will be used during the training process.
   * @param seed Seed for random numbers generation
   */
  public CLiMF(
      DataModel datamodel,
      int numFactors,
      double gamma,
      double lambda,
      int numIters,
      double threshold,
      long seed) {
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
    System.out.println("\nFitting " + this.toString());

    for (int iter = 1; iter <= this.numIters; iter++) {
      Parallelizer.exec(super.datamodel.getUsers(), new UpdateModel());
      if ((iter % 10) == 0) System.out.print(".");
      if ((iter % 100) == 0) System.out.println(iter + " iterations");
    }
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    return Maths.dotProduct(this.U[userIndex], this.V[itemIndex]);
  }

  @Override
  public String toString() {
    return "CLiMF("
        + "numFactors="
        + this.numFactors
        + "; "
        + "numIters="
        + this.numIters
        + "; "
        + "gamma="
        + this.gamma
        + "; "
        + "lambda="
        + this.lambda
        + "; "
        + "threshold="
        + this.threshold
        + ")";
  }

  /**
   * Returns the gradient value of the logistic function
   *
   * @param x Value for which gradient value of logistic function must be computed
   * @return Gradient value of logistic function of x
   */
  private static double logisticGradientValue(double x) {
    return Maths.logistic(x) * Maths.logistic(-x);
  }

  /** Auxiliary inner class to parallelize model update */
  private class UpdateModel implements Partible<User> {
    private final double[][] usersGradients;
    private final Map<Integer, double[][]> itemsGradients;

    public UpdateModel() {
      this.usersGradients = new double[datamodel.getNumberOfUsers()][numFactors];
      this.itemsGradients = new ConcurrentHashMap<>();
    }

    @Override
    public void beforeRun() {}

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
            userGradients[f] += Maths.logistic(-jPred) * V[j][f];
            ratedItemsGradients[jPos][f] = Maths.logistic(-jPred) * U[userIndex][f];

            for (int kPos = 0; kPos < user.getNumberOfRatings(); kPos++) {
              double kRating = user.getRatingAt(kPos);
              if (jPos != kPos && kRating >= threshold) {
                int k = user.getItemAt(kPos);
                double kPred = predict(userIndex, k);

                double diff = kPred - jPred;

                userGradients[f] +=
                    logisticGradientValue(diff)
                        * (V[j][f] - V[k][f])
                        / (1.0 - Maths.logistic(diff));
                ratedItemsGradients[jPos][f] +=
                    logisticGradientValue(-diff)
                        * ((1.0 / (1.0 - Maths.logistic(diff)))
                            - (1.0 / (1.0 - Maths.logistic(-diff))))
                        * U[userIndex][f];
              }
            }
          }
        }
      }

      this.usersGradients[userIndex] = userGradients;
      this.itemsGradients.put(userIndex, ratedItemsGradients);
    }

    @Override
    public void afterRun() {
      for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
        User user = datamodel.getUser(userIndex);

        double[][] ratedItemsGradients = this.itemsGradients.get(userIndex);

        for (int f = 0; f < numFactors; f++) {
          U[userIndex][f] += gamma * (this.usersGradients[userIndex][f] - lambda * U[userIndex][f]);

          for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
            int itemIndex = user.getItemAt(pos);
            V[itemIndex][f] += gamma * (ratedItemsGradients[pos][f] - lambda * V[itemIndex][f]);
          }
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
    return this.numFactors;
  }

  /**
   * Get the number of iterations
   *
   * @return Number of iterations
   */
  public int getNumIters() {
    return this.numIters;
  }

  /**
   * Get the regularization parameter of the model
   *
   * @return Lambda
   */
  public double getLambda() {
    return this.lambda;
  }

  /**
   * Get the learning rate parameter of the model
   *
   * @return Gamma
   */
  public double getGamma() {
    return this.gamma;
  }
}
