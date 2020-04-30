package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;

import java.util.Map;
import java.util.Random;

/**
 * Implements Koren, Y., Bell, R., &amp; Volinsky, C. (2009). Matrix factorization techniques for
 * recommender systems. Computer, (8), 30-37.
 */
public class BiasedMF extends Recommender {

  protected static final double DEFAULT_GAMMA = 0.01;
  protected static final double DEFAULT_LAMBDA = 0.1;

  /** User factors */
  protected final double[][] p;

  /** Item factors */
  protected final double[][] q;

  /** User bias */
  protected final double[] bu;

  /** Item bias */
  protected final double[] bi;

  /** Learning rate */
  protected final double gamma;

  /** Regularization parameter */
  protected final double lambda;

  /** Number of latent factors */
  protected final int numFactors;

  /** Number of iterations */
  protected final int numIters;

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
  public BiasedMF(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int) params.get("numFactors"),
        (int) params.get("numIters"),
        params.containsKey("lambda") ? (double) params.get("lambda") : DEFAULT_LAMBDA,
        params.containsKey("gamma") ? (double) params.get("gamma") : DEFAULT_GAMMA,
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors
   * @param numIters Number of iterations
   */
  public BiasedMF(DataModel datamodel, int numFactors, int numIters) {
    this(datamodel, numFactors, numIters, DEFAULT_LAMBDA);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors
   * @param numIters Number of iterations
   * @param seed Seed for random numbers generation
   */
  public BiasedMF(DataModel datamodel, int numFactors, int numIters, long seed) {
    this(datamodel, numFactors, numIters, DEFAULT_LAMBDA, DEFAULT_GAMMA, seed);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors
   * @param numIters Number of iterations
   * @param lambda Regularization parameter
   */
  public BiasedMF(DataModel datamodel, int numFactors, int numIters, double lambda) {
    this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors
   * @param numIters Number of iterations
   * @param lambda Regularization parameter
   * @param seed Seed for random numbers generation
   */
  public BiasedMF(DataModel datamodel, int numFactors, int numIters, double lambda, long seed) {
    this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, seed);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors
   * @param numIters Number of iterations
   * @param lambda Regularization parameter
   * @param gamma Learning rate parameter
   * @param seed Seed for random numbers generation
   */
  public BiasedMF(
      DataModel datamodel, int numFactors, int numIters, double lambda, double gamma, long seed) {
    super(datamodel);

    this.numFactors = numFactors;
    this.numIters = numIters;
    this.lambda = lambda;
    this.gamma = gamma;

    Random rand = new Random(seed);

    // Users initialization
    this.p = new double[datamodel.getNumberOfUsers()][numFactors];
    this.bu = new double[datamodel.getNumberOfUsers()];
    for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
      this.bu[u] = rand.nextDouble() * 2 - 1;
      for (int k = 0; k < numFactors; k++) {
        this.p[u][k] = rand.nextDouble() * 2 - 1;
      }
    }

    // Items initialization
    this.q = new double[datamodel.getNumberOfItems()][numFactors];
    this.bi = new double[datamodel.getNumberOfItems()];
    for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
      this.bi[i] = rand.nextDouble() * 2 - 1;
      for (int k = 0; k < numFactors; k++) {
        this.q[i][k] = rand.nextDouble() * 2 - 1;
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

  @Override
  public void fit() {

    System.out.println("\nFitting " + this.toString());

    for (int iter = 1; iter <= this.numIters; iter++) {

      Parallelizer.exec(super.datamodel.getUsers(), new UpdateUsersFactors());
      Parallelizer.exec(super.datamodel.getItems(), new UpdateItemsFactors());

      if ((iter % 10) == 0) System.out.print(".");
      if ((iter % 100) == 0) System.out.println(iter + " iterations");
    }
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    double[] pu = this.p[userIndex];
    double[] qi = this.q[itemIndex];
    return datamodel.getRatingAverage()
        + this.bu[userIndex]
        + this.bi[itemIndex]
        + Maths.dotProduct(pu, qi);
  }

  @Override
  public String toString() {
    return "BiasedMF("
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

  /** Auxiliary inner class to parallelize user factors computation */
  private class UpdateUsersFactors implements Partible<User> {

    @Override
    public void beforeRun() {}

    @Override
    public void run(User user) {
      int userIndex = user.getUserIndex();
      for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
        int itemIndex = user.getItemAt(pos);
        double error = user.getRatingAt(pos) - predict(userIndex, itemIndex);

        bu[userIndex] += gamma * (error - lambda * bu[userIndex]);

        for (int k = 0; k < numFactors; k++) {
          p[userIndex][k] += gamma * (error * q[itemIndex][k] - lambda * p[userIndex][k]);
        }
      }
    }

    @Override
    public void afterRun() {}
  }

  /** Auxiliary inner class to parallelize item factors computation */
  private class UpdateItemsFactors implements Partible<Item> {

    @Override
    public void beforeRun() {}

    @Override
    public void run(Item item) {
      int itemIndex = item.getItemIndex();
      for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
        int userIndex = item.getUserAt(pos);
        double error = item.getRatingAt(pos) - predict(userIndex, itemIndex);

        bi[itemIndex] += gamma * (error - lambda * bi[itemIndex]);

        for (int k = 0; k < numFactors; k++) {
          q[itemIndex][k] += gamma * (error * p[userIndex][k] - lambda * q[itemIndex][k]);
        }
      }
    }

    @Override
    public void afterRun() {}
  }
}
