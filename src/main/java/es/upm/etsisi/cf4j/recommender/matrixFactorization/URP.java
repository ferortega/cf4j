package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;

import org.apache.commons.math3.special.Gamma;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements Marlin, B. M. (2004). Modeling user rating profiles for collaborative filtering. In
 * Advances in neural information processing systems (pp. 627-634).
 */
public class URP extends Recommender {

  protected static final double DEFAULT_H = 0.1;
  protected static final double EPSILON = 1E-2;

  /** Number of iterations */
  protected final int numIters;

  /** Number of latent factors */
  protected final int numFactors;

  /** Heuristic factor to control number of iterations during E-Step */
  protected final double H;

  /** Plausible ratings (must be sorted in ascending order) */
  protected final double[] ratings;

  /** Gamma parameter */
  protected final double[][] gamma;

  /** Beta parameter */
  protected final double[][][] beta;

  /** Alpha parameter */
  protected final double[] alpha;

  /** Phi parameter */
  protected final Map<Integer, double[][]> phi;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numFactors</b>: int value with the number of latent factors.
   *   <li><b>ratings</b>: double array with the plausible ratings of the DataModel.
   *   <li><b>numIters:</b>: int value with the number of iterations.
   *   <li><b><em>H</em></b> (optional): double value that represents the heuristic factor to
   *       control the number of iterations during E-Step. The number of iterations is defined by H
   *       * number_of_user_ratings.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public URP(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int) params.get("numFactors"),
        (double[]) params.get("ratings"),
        (int) params.get("numIters"),
        params.containsKey("H") ? (double) params.get("H") : DEFAULT_H,
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param ratings Plausible ratings (must be sorted in ascending order)
   * @param numIters Number of iterations
   */
  public URP(DataModel datamodel, int numFactors, double[] ratings, int numIters) {
    this(datamodel, numFactors, ratings, numIters, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param ratings Plausible ratings (must be sorted in ascending order)
   * @param numIters Number of iterations
   * @param seed Seed for random numbers generation
   */
  public URP(DataModel datamodel, int numFactors, double[] ratings, int numIters, long seed) {
    this(datamodel, numFactors, ratings, numIters, DEFAULT_H, seed);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param ratings Plausible ratings (must be sorted in ascending order)
   * @param numIters Number of iterations
   * @param H Heuristic factor to control number of iterations during E-Step. The number of
   *     iterations is defined by H * number_of_user_ratings
   */
  public URP(DataModel datamodel, int numFactors, double[] ratings, int numIters, double H) {
    this(datamodel, numFactors, ratings, numIters, H, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param ratings Plausible ratings (must be sorted in ascending order)
   * @param numIters Number of iterations
   * @param H Heuristic factor to control number of iterations during E-Step. The number of
   *     iterations is defined by H * number_of_user_ratings
   * @param seed Seed for random numbers generation
   */
  public URP(
      DataModel datamodel, int numFactors, double[] ratings, int numIters, double H, long seed) {
    super(datamodel);

    this.numFactors = numFactors;
    this.numIters = numIters;
    this.ratings = ratings;
    this.H = H;

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

    this.phi = new ConcurrentHashMap<>();
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
   * Get the plausible ratings
   *
   * @return Plausible ratings
   */
  public double[] getRatings() {
    return ratings;
  }

  /**
   * Get the H value
   *
   * @return H value
   */
  public double getH() {
    return H;
  }

  @Override
  public void fit() {
    System.out.println("\nFitting " + this.toString());

    for (int iter = 1; iter <= this.numIters; iter++) {
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

      if ((iter % 10) == 0) System.out.print(".");
      if ((iter % 100) == 0) System.out.println(iter + " iterations");
    }
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    double[] pd = this.getPredictionProbabilityDistribution(userIndex, itemIndex);

    double acc = 0;
    int v = -1;

    do {
      v++;
      acc += pd[v];
    } while (acc < 0.5);

    return this.ratings[v];
  }

  /**
   * Returns the probability distribution of a prediction. Each position of the returned array
   * corresponds with the probability that the user (defined by userIndex) rates the item (defined
   * by itemIndex) with the rating value that is in the same position in the array returned by
   * getRatings()
   *
   * @param userIndex User
   * @param itemIndex Item
   * @return Probability distribution of the prediction
   */
  public double[] getPredictionProbabilityDistribution(int userIndex, int itemIndex) {
    double sumGamma = 0;

    for (int j = 0; j < this.numFactors; j++) {
      sumGamma += this.gamma[userIndex][j];
    }

    double[] probabilities = new double[this.ratings.length];
    double sumProbabilities = 0;

    for (int v = 0; v < this.ratings.length; v++) {
      for (int z = 0; z < this.numFactors; z++) {
        probabilities[v] += this.beta[itemIndex][v][z] * this.gamma[userIndex][z] / sumGamma;
      }
      sumProbabilities += probabilities[v];
    }

    for (int v = 0; v < this.ratings.length; v++) {
      probabilities[v] /= sumProbabilities;
    }

    return probabilities;
  }

  @Override
  public String toString() {
    return "URP("
        + "numFactors="
        + this.numFactors
        + "; "
        + "numIters="
        + this.numIters
        + "; "
        + "ratings="
        + Arrays.toString(this.ratings)
        + "; "
        + "H="
        + this.H
        + ")";
  }

  /**
   * Computes the inverse of the PSI function. Source:
   * http://bariskurt.com/calculating-the-inverse-of-digamma-function/
   *
   * @param value value
   * @return PSI^-1(value)
   */
  private double inversePsi(double value) {
    double inv = (value >= -2.22) ? Math.exp(value) + 0.5 : -1.0 / (value - Gamma.digamma(1));

    for (int i = 0; i < 3; i++) {
      inv -= (Gamma.digamma(inv) - value) / Gamma.trigamma(inv);
    }

    return inv;
  }

  /** Auxiliary inner class to parallelize user factors computation */
  private class UpdatePhiGamma implements Partible<User> {

    @Override
    public void beforeRun() {}

    @Override
    public void run(User user) {
      int userIndex = user.getUserIndex();

      double[][] userPhi = new double[user.getNumberOfRatings()][numFactors];

      for (int h = 0; h < Math.max(1, H * user.getNumberOfRatings()); h++) {

        // update phi

        double gs = 0; // gamma sum
        for (int z = 0; z < numFactors; z++) {
          gs += gamma[userIndex][z];
        }

        for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
          int itemIndex = user.getItemAt(pos);

          double rating = user.getRatingAt(pos);
          int v = Arrays.binarySearch(ratings, rating);

          for (int z = 0; z < URP.this.numFactors; z++) {
            userPhi[pos][z] =
                Math.exp(Gamma.digamma(gamma[userIndex][z]) - Gamma.digamma(gs))
                    * beta[itemIndex][v][z];
          }
        }

        // update gamma

        for (int z = 0; z < numFactors; z++) {
          gamma[userIndex][z] = URP.this.alpha[z];
          for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
            gamma[userIndex][z] += userPhi[pos][z];
          }
        }
      }

      phi.put(userIndex, userPhi);
    }

    @Override
    public void afterRun() {}
  }

  /** Auxiliary inner class to parallelize item factors computation */
  private class UpdateBeta implements Partible<Item> {

    @Override
    public void beforeRun() {}

    @Override
    public void run(Item item) {
      int itemIndex = item.getItemIndex();

      beta[itemIndex] = new double[ratings.length][numFactors]; // reset beta

      for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
        int userIndex = item.getUserAt(pos);
        User user = datamodel.getUser(userIndex);

        int p = user.findItem(itemIndex);
        double[][] userPhi = phi.get(userIndex);

        double rating = item.getRatingAt(pos);
        int v = Arrays.binarySearch(ratings, rating);

        for (int z = 0; z < numFactors; z++) {
          beta[itemIndex][v][z] += userPhi[p][z];
        }
      }
    }

    @Override
    public void afterRun() {}
  }
}
