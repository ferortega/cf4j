package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;

import java.util.Map;
import java.util.Random;

/**
 * Implements Koren, Y. (2008, August). Factorization meets the neighborhood: a multifaceted
 * collaborative filtering model. In Proceedings of the 14th ACM SIGKDD international conference on
 * Knowledge discovery and data mining (pp. 426-434).
 */
public class SVDPlusPlus extends Recommender {

  protected static final double DEFAULT_GAMMA = 0.001;
  protected static final double DEFAULT_LAMBDA = 0.01;

  /** Number of latent factors */
  protected final int numFactors;

  /** Number of iterations */
  protected final int numIters;

  /** Learning rate hyper-parameter */
  protected final double gamma;

  /** Regularization hyper-parameter */
  protected final double lambda;

  /** bu parameter */
  protected final double[] bu;

  /** bi parameter */
  protected final double[] bi;

  /** p parameter */
  protected final double[][] p;

  /** q parameter */
  protected final double[][] q;

  /** y parameter */
  protected final double[][] y;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numFactors</b>: int value with the number of latent factors.
   *   <li><b>numIters:</b>: int value with the number of iterations.
   *   <li><b><em>gamma</em></b> (optional): double value with the learning rate hyper-parameter. If
   *       missing, it is set to 0.01.
   *   <li><b><em>lambda</em></b> (optional): double value with the regularization hyper-parameter.
   *       If missing, it is set to 0.05.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public SVDPlusPlus(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int) params.get("numFactors"),
        (int) params.get("numIters"),
        params.containsKey("gamma") ? (double) params.get("gamma") : DEFAULT_GAMMA,
        params.containsKey("lambda") ? (double) params.get("lambda") : DEFAULT_LAMBDA,
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   */
  public SVDPlusPlus(DataModel datamodel, int numFactors, int numIters) {
    this(datamodel, numFactors, numIters, DEFAULT_GAMMA, DEFAULT_LAMBDA);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   * @param seed Seed for random numbers generation
   */
  public SVDPlusPlus(DataModel datamodel, int numFactors, int numIters, long seed) {
    this(datamodel, numFactors, numIters, DEFAULT_GAMMA, DEFAULT_LAMBDA, seed);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   * @param gamma Learning rate hyper-parameter
   * @param lambda Regularization hyper-parameter
   */
  public SVDPlusPlus(
      DataModel datamodel, int numFactors, int numIters, double gamma, double lambda) {
    this(datamodel, numFactors, numIters, gamma, lambda, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   * @param gamma Learning rate hyper-parameter
   * @param lambda Regularization hyper-parameter
   * @param seed Seed for random numbers generation
   */
  public SVDPlusPlus(
      DataModel datamodel, int numFactors, int numIters, double gamma, double lambda, long seed) {
    super(datamodel);

    this.numFactors = numFactors;
    this.numIters = numIters;
    this.gamma = gamma;
    this.lambda = lambda;

    int numUsers = datamodel.getNumberOfUsers();
    int numItems = datamodel.getNumberOfItems();

    Random generator = new Random(seed);

    this.bu = new double[numUsers];
    this.p = new double[numUsers][numFactors];
    for (int u = 0; u < numUsers; u++) {
      this.bu[u] = generator.nextDouble();
      for (int k = 0; k < numFactors; k++) {
        this.p[u][k] = generator.nextDouble();
      }
    }

    this.bi = new double[numItems];
    this.q = new double[numItems][numFactors];
    this.y = new double[numItems][numFactors];
    for (int i = 0; i < numItems; i++) {
      this.bi[i] = generator.nextDouble();
      for (int k = 0; k < numFactors; k++) {
        this.q[i][k] = generator.nextDouble();
        this.y[i][k] = generator.nextDouble();
      }
    }
  }

  @Override
  public void fit() {
    System.out.println("\nFitting " + this.toString());

    for (int iter = 1; iter <= this.numIters; iter++) {

      for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
        User user = datamodel.getUser(userIndex);

        double N = 1.0 / Math.sqrt(user.getNumberOfRatings());

        for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
          int itemIndex = user.getItemAt(pos);

          double rating = user.getRatingAt(pos);
          double prediction = this.predict(userIndex, itemIndex);

          double error = rating - prediction;

          this.bu[userIndex] += this.gamma * (error - this.lambda * this.bu[userIndex]);
          this.bi[itemIndex] += this.gamma * (error - this.lambda * this.bi[itemIndex]);

          double[] updatedP = this.p[userIndex].clone();
          double[] updatedQ = this.q[itemIndex].clone();
          double[] updatedY = this.y[itemIndex].clone();

          for (int k = 0; k < this.numFactors; k++) {

            for (int pos2 = 0; pos2 < user.getNumberOfRatings(); pos2++) {
              int itemIndex2 = user.getItemAt(pos2);
              updatedQ[k] += this.gamma * error * N * this.y[itemIndex2][k];
            }

            updatedQ[k] +=
                this.gamma * (error * this.p[userIndex][k] - this.lambda * this.q[itemIndex][k]);
            updatedY[k] +=
                this.gamma
                    * (error * N * this.q[itemIndex][k] - this.lambda * this.y[itemIndex][k]);
            updatedP[k] +=
                this.gamma * (error * this.q[itemIndex][k] - this.lambda * this.p[userIndex][k]);
          }

          this.p[userIndex] = updatedP;
          this.q[itemIndex] = updatedQ;
          this.y[itemIndex] = updatedY;
        }
      }

      if ((iter % 10) == 0) System.out.print(".");
      if ((iter % 100) == 0) System.out.println(iter + " iterations");
    }
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    User user = super.datamodel.getUser(userIndex);
    double N = 1.0 / Math.sqrt(user.getNumberOfRatings());

    double[] pu = this.p[userIndex].clone();
    for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
      int index = user.getItemAt(pos);
      for (int k = 0; k < this.numFactors; k++) {
        pu[k] += N * this.y[index][k];
      }
    }

    double[] qi = this.q[itemIndex];

    double dot = Maths.dotProduct(pu, qi);

    return super.datamodel.getRatingAverage() + this.bi[itemIndex] + this.bu[userIndex] + dot;
  }

  @Override
  public String toString() {
    return "SVDPlusPlus("
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
        + ")";
  }

  /**
   * Number of factors used in this recommender.
   *
   * @return Number of factors
   */
  public int getNumFactors() {
    return numFactors;
  }

  /**
   * Number of iterations used in this recommender.
   *
   * @return Number of factors
   */
  public int getNumIters() {
    return numIters;
  }

  /**
   * Getter of the gamma value.
   *
   * @return Gamma value
   */
  public double getGamma() {
    return gamma;
  }

  /**
   * Getter of the Lambda value.
   *
   * @return Lambda value
   */
  public double getLambda() {
    return lambda;
  }
}
