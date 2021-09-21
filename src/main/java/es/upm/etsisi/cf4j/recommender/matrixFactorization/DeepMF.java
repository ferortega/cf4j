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
 * Implements Lara-Cabrera, R., González-Prieto, Á., &amp; Ortega, F. (2020). Deep matrix
 * factorization approach for collaborative filtering recommender systems. Applied Sciences, 10(14),
 * 4926.
 */
public class DeepMF extends Recommender {

  /** User factors */
  double[][] userFactors;

  /** Item factors */
  double[][] itemFactors;

  /** Learning rate */
  private double[] learningRate;

  /** Regularization parameter */
  private double[] regularization;

  /** Number of iterations */
  private int[] numIters;

  /** Number of latent factors */
  private int[] numFactors;

  /** Random seed * */
  private long seed;

  /** Factorization depth * */
  private int depth;

  /** Higher factorization if exists * */
  private DeepMF parent;

  /** Deeper factorization if exists * */
  private DeepMF child;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numFactors</b>: int array with the number of latent factors of each factorization.
   *   <li><b>numIters:</b>: int array with the number of iterations of each factorization.
   *   <li><b>learingRate</b>: double array with the learning rate hyper-parameter of each
   *       factorization.
   *   <li><b>regularization</b>: double array with the regularization hyper-parameter of each
   *       factorization.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public DeepMF(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int[]) params.get("numFactors"),
        (int[]) params.get("numIters"),
        (double[]) params.get("learningRate"),
        (double[]) params.get("regularization"),
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors int array with the number of latent factors of each factorization
   * @param numIters int array with the number of iterations of each factorization
   * @param learningRate double array with the learning rate hyper-parameter of each factorization
   * @param regularization double array with the regularization hyper-parameter of each
   *     factorization
   */
  public DeepMF(
      DataModel datamodel,
      int[] numFactors,
      int[] numIters,
      double[] learningRate,
      double[] regularization) {
    this(
        datamodel,
        numFactors,
        numIters,
        learningRate,
        regularization,
        0,
        null,
        System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors int array with the number of latent factors of each factorization
   * @param numIters int array with the number of iterations of each factorization
   * @param learningRate double array with the learning rate hyper-parameter of each factorization
   * @param regularization double array with the regularization hyper-parameter of each
   *     factorization
   * @param seed Seed for random numbers generation
   */
  public DeepMF(
      DataModel datamodel,
      int[] numFactors,
      int[] numIters,
      double[] learningRate,
      double[] regularization,
      long seed) {
    this(datamodel, numFactors, numIters, learningRate, regularization, 0, null, seed);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors int array with the number of latent factors of each factorization
   * @param numIters int array with the number of iterations of each factorization
   * @param learningRate double array with the learning rate hyper-parameter of each factorization
   * @param regularization double array with the regularization hyper-parameter of each
   *     factorization
   * @param depth Depth level
   * @param parent Higher factorization
   * @param seed Seed for random numbers generation
   */
  private DeepMF(
      DataModel datamodel,
      int[] numFactors,
      int[] numIters,
      double[] learningRate,
      double[] regularization,
      int depth,
      DeepMF parent,
      long seed) {
    super(datamodel);

    this.numFactors = numFactors;
    this.depth = depth;

    this.numIters = numIters;
    this.learningRate = learningRate;
    this.regularization = regularization;

    this.parent = parent;
    this.child = null;

    this.seed = seed;
    Random rand = new Random(seed);

    int numUsers = datamodel.getNumberOfUsers();
    this.userFactors = new double[numUsers][this.getNumFactors()];

    for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
      for (int f = 0; f < this.getNumFactors(); f++) {
        this.userFactors[userIndex][f] = rand.nextDouble();
      }
    }

    int numItems = datamodel.getNumberOfItems();
    this.itemFactors = new double[numItems][this.getNumFactors()];

    for (int itemIndex = 0; itemIndex < datamodel.getNumberOfItems(); itemIndex++) {
      for (int f = 0; f < this.getNumFactors(); f++) {
        this.itemFactors[itemIndex][f] = rand.nextDouble();
      }
    }
  }

  @Override
  public void fit() {

    for (int iter = 1; iter <= this.getNumIters(); iter++) {
      Parallelizer.exec(datamodel.getUsers(), new UpdateUsersFactors());
      Parallelizer.exec(datamodel.getItems(), new UpdateItemsFactors());
    }

    if (this.depth < this.getMaxDepth() - 1) {
      this.child =
          new DeepMF(
              datamodel,
              this.numFactors,
              this.numIters,
              this.regularization,
              this.learningRate,
              this.depth + 1,
              this,
              seed);
      this.child.fit();
    }
  }

  /**
   * Get the number of factors of the model at current depth
   *
   * @return Number of factors
   */
  private int getNumFactors() {
    return this.numFactors[this.depth];
  }

  /**
   * Get the learning rate of the model at current depth
   *
   * @return Learning rate
   */
  private double getLearningRate() {
    return this.learningRate[this.depth];
  }

  /**
   * Get the number of iterations of the model at current depth
   *
   * @return Number of iterations
   */
  private int getNumIters() {
    return this.numIters[this.depth];
  }

  /**
   * Get the regularization of the model at current depth
   *
   * @return Regularization
   */
  private double getRegularization() {
    return this.regularization[this.depth];
  }

  /**
   * Get the maximum depth of the factorization process
   *
   * @return Maximum depth
   */
  private int getMaxDepth() {
    return this.numFactors.length;
  }

  /**
   * Check if current factorization has parent (i.e. current factorization is not the first
   * factorization).
   *
   * @return true if current factorization has parent; false otherwise
   */
  private boolean hasParent() {
    return this.parent != null;
  }

  /**
   * Check if current factorization has child (i.e. current factorization is not the last
   * factorization).
   *
   * @return true if current factorization has child; false otherwise
   */
  private boolean hasChild() {
    return this.child != null;
  }

  /**
   * Returns the value to be learned in the current factorization.
   *
   * @param userIndex User index
   * @param itemIndex Item index
   * @return Value to be learned
   */
  private double getValue(int userIndex, int itemIndex) {
    if (this.hasParent()) {
      double value = this.parent.getValue(userIndex, itemIndex);
      double estimation = this.parent.getEstimation(userIndex, itemIndex);
      return value - estimation; // error
    } else {
      User user = datamodel.getUser(userIndex);
      int pos = user.findItem(itemIndex);
      return user.getRatingAt(pos);
    }
  }

  /**
   * Returns the estimation of the current factorization.
   *
   * @param userIndex User index
   * @param itemIndex Item index
   * @return Estimation
   */
  private double getEstimation(int userIndex, int itemIndex) {
    return Maths.dotProduct(this.userFactors[userIndex], this.itemFactors[itemIndex]);
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    if (this.hasChild()) {
      return this.getEstimation(userIndex, itemIndex) + this.child.predict(userIndex, itemIndex);
    } else {
      return this.getEstimation(userIndex, itemIndex);
    }
  }

  @Override
  public String toString() {
    StringBuilder str =
        new StringBuilder("DeepMF(")
            .append("numFactors=")
            .append(Arrays.toString(this.numFactors))
            .append("; ")
            .append("numIters=")
            .append(Arrays.toString(this.numIters))
            .append("; ")
            .append("learningRate=")
            .append(Arrays.toString(this.learningRate))
            .append("; ")
            .append("regularization=")
            .append(Arrays.toString(this.regularization))
            .append(")");
    return str.toString();
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

        double value = getValue(userIndex, itemIndex);
        double estimation = getEstimation(userIndex, itemIndex);

        double error = value - estimation;

        for (int f = 0; f < DeepMF.this.getNumFactors(); f++) {
          userFactors[userIndex][f] +=
              getLearningRate()
                  * (error * itemFactors[itemIndex][f]
                      - getRegularization() * userFactors[userIndex][f]);
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

        double value = getValue(userIndex, itemIndex);
        double estimation = getEstimation(userIndex, itemIndex);

        double error = value - estimation;

        for (int f = 0; f < DeepMF.this.getNumFactors(); f++) {
          itemFactors[itemIndex][f] +=
              getLearningRate()
                  * (error * userFactors[userIndex][f]
                      - getRegularization() * itemFactors[itemIndex][f]);
        }
      }
    }

    @Override
    public void afterRun() {}
  }
}
