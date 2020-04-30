package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import org.apache.commons.math3.special.Gamma;

import java.util.Map;
import java.util.Random;

/**
 * Implements Gopalan, P., Hofman, J. M., &amp; Blei, D. M. (2015, July). Scalable Recommendation
 * with Hierarchical Poisson Factorization. In UAI (pp. 326-335).
 */
public class HPF extends Recommender {

  protected static final double DEFAULT_A = 0.3;
  protected static final double DEFAULT_A_PRIME = 0.3;
  protected static final double DEFAULT_B_PRIME = 1.0;
  protected static final double DEFAULT_C = 0.3;
  protected static final double DEFAULT_C_PRIME = 0.3;
  protected static final double DEFAULT_D_PRIME = 1.0;

  /** Number of latent factors */
  protected final int numFactors;

  /** Number of iterations */
  protected final int numIters;

  // Model hyperparameters
  protected final double a;
  protected final double aPrime;
  protected final double bPrime;
  protected final double c;
  protected final double cPrime;
  protected final double dPrime;

  // Model parameters
  protected final double[][] gamma;
  protected final double[][] gammaShp;
  protected final double[][] gammaRte;
  protected final double kappaShp;
  protected final double[] kappaRte;
  protected final double[][] lambda;
  protected final double[][] lambdaShp;
  protected final double[][] lambdaRte;
  protected final double tauShp;
  protected final double[] tauRte;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numFactors</b>: int value with the number of latent factors.
   *   <li><b>numIters:</b>: int value with the number of iterations.
   *   <li><b><em>a</em></b> (optional): double value with the a hyper-parameter. If missing, it is
   *       set to 0.3.
   *   <li><b><em>aPrime</em></b> (optional): double value with the a' hyper-parameter. If missing,
   *       it is set to 0.3.
   *   <li><b><em>bPrime</em></b> (optional): double value with the b' hyper-parameter. If missing,
   *       it is set to 1.0.
   *   <li><b><em>c</em></b> (optional): double value with the c hyper-parameter. If missing, it is
   *       set to 0.3.
   *   <li><b><em>cPrime</em></b> (optional): double value with the c' hyper-parameter. If missing,
   *       it is set to 0.3.
   *   <li><b><em>dPrime</em></b> (optional): double value with the d' hyper-parameter. If missing,
   *       it is set to 1.0.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public HPF(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int) params.get("numFactors"),
        (int) params.get("numIters"),
        params.containsKey("a") ? (double) params.get("a") : DEFAULT_A,
        params.containsKey("aPrime") ? (double) params.get("aPrime") : DEFAULT_A_PRIME,
        params.containsKey("bPrime") ? (double) params.get("bPrime") : DEFAULT_B_PRIME,
        params.containsKey("c") ? (double) params.get("c") : DEFAULT_C,
        params.containsKey("cPrime") ? (double) params.get("cPrime") : DEFAULT_C_PRIME,
        params.containsKey("dPrime") ? (double) params.get("dPrime") : DEFAULT_D_PRIME,
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Models constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   */
  public HPF(DataModel datamodel, int numFactors, int numIters) {
    this(datamodel, numFactors, numIters, System.currentTimeMillis());
  }

  /**
   * Models constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   * @param seed Seed for random numbers generation
   */
  public HPF(DataModel datamodel, int numFactors, int numIters, long seed) {
    this(
        datamodel,
        numFactors,
        numIters,
        DEFAULT_A,
        DEFAULT_A_PRIME,
        DEFAULT_B_PRIME,
        DEFAULT_C,
        DEFAULT_C_PRIME,
        DEFAULT_D_PRIME,
        seed);
  }

  /**
   * Models constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   * @param a Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param aPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param bPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param c Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param cPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param dPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   */
  public HPF(
      DataModel datamodel,
      int numFactors,
      int numIters,
      double a,
      double aPrime,
      double bPrime,
      double c,
      double cPrime,
      double dPrime) {
    this(
        datamodel,
        numFactors,
        numIters,
        a,
        aPrime,
        bPrime,
        c,
        cPrime,
        dPrime,
        System.currentTimeMillis());
  }

  /**
   * Models constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of latent factors
   * @param numIters Number of iterations
   * @param a Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param aPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param bPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param c Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param cPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param dPrime Model hyper-parameter. Read the paper for more information related to this
   *     hyper-parameter.
   * @param seed Seed for random numbers generation
   */
  public HPF(
      DataModel datamodel,
      int numFactors,
      int numIters,
      double a,
      double aPrime,
      double bPrime,
      double c,
      double cPrime,
      double dPrime,
      long seed) {
    super(datamodel);

    this.numFactors = numFactors;
    this.numIters = numIters;

    this.a = a;
    this.aPrime = aPrime;
    this.bPrime = bPrime;

    this.c = c;
    this.cPrime = cPrime;
    this.dPrime = dPrime;

    this.kappaShp = aPrime + numFactors * a;
    this.tauShp = cPrime + numFactors * c;

    int numUsers = datamodel.getNumberOfUsers();
    int numItems = datamodel.getNumberOfItems();

    Random generator = new Random(seed);

    this.gamma = new double[numUsers][numFactors];
    this.gammaShp = new double[numUsers][numFactors];
    this.gammaRte = new double[numUsers][numFactors];
    this.kappaRte = new double[numUsers];

    for (int u = 0; u < numUsers; u++) {
      this.kappaRte[u] = generator.nextDouble();
      for (int f = 0; f < numFactors; f++) {
        this.gammaShp[u][f] = generator.nextDouble();
        this.gammaRte[u][f] = generator.nextDouble();
      }
    }

    this.lambda = new double[numItems][numFactors];
    this.lambdaShp = new double[numItems][numFactors];
    this.lambdaRte = new double[numItems][numFactors];
    this.tauRte = new double[numItems];

    for (int i = 0; i < numItems; i++) {
      this.tauRte[i] = generator.nextDouble();
      for (int f = 0; f < numFactors; f++) {
        this.lambdaShp[i][f] = generator.nextDouble();
        this.lambdaRte[i][f] = generator.nextDouble();
      }
    }
  }

  @Override
  public void fit() {
    System.out.println("\nFitting " + this.toString());

    for (int iter = 1; iter <= numIters; iter++) {
      Parallelizer.exec(super.datamodel.getUsers(), new UpdateUsersFactors());
      Parallelizer.exec(super.datamodel.getItems(), new UpdateItemsFactors());

      if ((iter % 10) == 0) System.out.print(".");
      if ((iter % 100) == 0) System.out.println(iter + " iterations");
    }
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    double dot = Maths.dotProduct(this.gamma[userIndex], this.lambda[itemIndex]);
    return 1 - Math.exp(-1 * dot);
  }

  @Override
  public String toString() {
    return "HPF("
        + "numFactors="
        + this.numFactors
        + "; "
        + "numIters="
        + this.numIters
        + "; "
        + "a="
        + this.a
        + "; "
        + "aPrime="
        + this.aPrime
        + "; "
        + "bPrime="
        + this.bPrime
        + "; "
        + "c="
        + this.c
        + "; "
        + "cPrime="
        + this.cPrime
        + "; "
        + "dPrime="
        + this.dPrime
        + ")";
  }

  /** Inner class to parallelize users' update */
  private class UpdateUsersFactors implements Partible<User> {

    @Override
    public void beforeRun() {}

    @Override
    public void run(User user) {
      int userIndex = user.getUserIndex();

      double[][] phi = new double[user.getNumberOfRatings()][numFactors];
      for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
        int itemIndex = user.getItemAt(pos);
        for (int k = 0; k < numFactors; k++) {
          phi[pos][k] =
              Math.exp(
                  Gamma.digamma(gammaShp[userIndex][k])
                      - Math.log(gammaRte[userIndex][k])
                      + Gamma.digamma(lambdaShp[itemIndex][k])
                      - Math.log(lambdaRte[itemIndex][k]));
        }
      }

      for (int k = 0; k < numFactors; k++) {
        gammaShp[userIndex][k] = a;
        gammaRte[userIndex][k] = kappaShp / kappaRte[userIndex];

        for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
          int itemIndex = user.getItemAt(pos);
          double rating = user.getRatingAt(pos);

          gammaShp[userIndex][k] += rating * phi[pos][k];
          gammaRte[userIndex][k] += lambdaShp[itemIndex][k] / lambdaRte[itemIndex][k];
        }
      }

      kappaRte[userIndex] = aPrime / bPrime;
      for (int k = 0; k < numFactors; k++) {
        kappaRte[userIndex] += gammaShp[userIndex][k] / gammaRte[userIndex][k];
      }
    }

    @Override
    public void afterRun() {
      for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
        for (int k = 0; k < numFactors; k++) {
          gamma[userIndex][k] = gammaShp[userIndex][k] / gammaRte[userIndex][k];
        }
      }
    }
  }

  /** Inner class to parallelize items' update */
  private class UpdateItemsFactors implements Partible<Item> {

    @Override
    public void beforeRun() {}

    @Override
    public void run(Item item) {
      int itemIndex = item.getItemIndex();

      double[][] phi = new double[item.getNumberOfRatings()][numFactors];
      for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
        int userIndex = item.getUserAt(pos);
        for (int k = 0; k < numFactors; k++) {
          phi[pos][k] =
              Math.exp(
                  Gamma.digamma(gammaShp[userIndex][k])
                      - Math.log(gammaRte[userIndex][k])
                      + Gamma.digamma(lambdaShp[itemIndex][k])
                      - Math.log(lambdaRte[itemIndex][k]));
        }
      }

      for (int k = 0; k < numFactors; k++) {
        lambdaShp[itemIndex][k] = c;
        lambdaRte[itemIndex][k] = tauShp / tauRte[itemIndex];

        for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
          int userIndex = item.getUserAt(pos);
          double rating = item.getRatingAt(pos);

          lambdaShp[itemIndex][k] += rating * phi[pos][k];
          lambdaRte[itemIndex][k] += gammaShp[userIndex][k] / gammaRte[userIndex][k];
        }
      }

      tauRte[itemIndex] = cPrime / dPrime;
      for (int k = 0; k < numFactors; k++) {
        tauRte[itemIndex] += lambdaShp[itemIndex][k] / lambdaRte[itemIndex][k];
      }
    }

    @Override
    public void afterRun() {
      for (int itemIndex = 0; itemIndex < datamodel.getNumberOfItems(); itemIndex++) {
        for (int k = 0; k < numFactors; k++) {
          lambda[itemIndex][k] = lambdaShp[itemIndex][k] / lambdaRte[itemIndex][k];
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
}
