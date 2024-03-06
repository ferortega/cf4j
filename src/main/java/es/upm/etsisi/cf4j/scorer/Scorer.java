package es.upm.etsisi.cf4j.scorer;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;

import java.util.Arrays;

/**
 * Abstract class used to simplify the evaluation of collaborative filtering based recommendation
 * models. To define a new score, getScore(TestUser testUser, double[] predictions) must
 * be encoded.
 */
public abstract class Scorer {

  /** Recommender instance for which the quality measure are going to be computed */
  protected Recommender recommender;

  /** Stores the score of each test user */
  private double[] usersScores;

  /**
   * Creates a new quality measure
   *
   * @param recommender Recommender instance for which the quality measure are going to be computed
   */
  public Scorer(Recommender recommender) {
    this.recommender = recommender;
  }

  public Scorer fit() {
    Parallelizer.exec(recommender.getDataModel().getTestUsers(), new EvaluateTestUsers());
    return this;
  }

  protected abstract double getUserScore(TestUser testUser);


  public double getScore() {
    return this.getScoreMean();
  }

  public double getScoreMean() {
    return Maths.arrayAverage(usersScores);
  }

  public double getScoreStandardDeviation() {
    return Maths.arrayStandardDeviation(usersScores);
  }

  public double get95ConfidenceIntervalMargin() {
    return getConfidenceIntervalMargin(1.96);
  }

  public double get99ConfidenceIntervalMargin() {
    return getConfidenceIntervalMargin(2.58);
  }

  private double getConfidenceIntervalMargin(double coef) {
    double standardDeviation = this.getScoreStandardDeviation();
    long sampleSize = Arrays.stream(this.usersScores).filter(score -> !Double.isNaN(score)).count();
    double standardError = standardDeviation / Math.sqrt(sampleSize);
    return coef * standardError;
  }

  /** Private inner class used to parallelize the computation of the quality measures */
  private class EvaluateTestUsers implements Partible<TestUser> {

    @Override
    public void beforeRun() {
      usersScores = new double[recommender.getDataModel().getNumberOfTestUsers()];
    }

    @Override
    public void run(TestUser testUser) {
      int testUserIndex = testUser.getTestUserIndex();
      usersScores[testUserIndex] = getUserScore(testUser);
    }

    @Override
    public void afterRun() {}
  }
}
