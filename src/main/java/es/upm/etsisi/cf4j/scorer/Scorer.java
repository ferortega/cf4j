package es.upm.etsisi.cf4j.scorer;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * Abstract class used to simplify the evaluation of collaborative filtering based recommendation
 * models. To define a new score, getScore(TestUser testUser, double[] predictions) must
 * be encoded.
 */
public abstract class Scorer {

  /** Recommender instance for which the quality measure are going to be computed */
  protected Recommender recommender;

  /** Stores de global score of the quality measures */
  private double score;

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

  public void fit() {
    Parallelizer.exec(recommender.getDataModel().getTestUsers(), new EvaluateUsers());
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
    double mean = this.getScoreMean();
    double standardDeviation = this.getScoreStandardDeviation();
    long sampleSize = Arrays.stream(this.usersScores).filter(score -> !Double.isNaN(score)).count();
    double standardError = standardDeviation / Math.sqrt(sampleSize);
    return coef * standardError;
  }

  /**
   * Exports results of Scorer in csv format
   *
   * @param filename File name
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename) throws IOException {
    exportResults(filename, true);
  }

  /**
   * Exports results of Scorer in csv format
   *
   * @param filename File name
   * @param includeHeader Include CSV header line. By default: true
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, boolean includeHeader) throws IOException {
    exportResults(filename, ",", includeHeader);
  }

  /**
   * Exports results of Scorer in csv format
   *
   * @param filename File name
   * @param separator CSV separator field. By default: colon character (,)
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, String separator) throws IOException {
    exportResults(filename, separator, true);
  }

  /**
   * Exports results of Scorer in csv format
   *
   * @param filename File name
   * @param separator CSV separator field. By default: colon character (,)
   * @param includeHeader Include CSV header line. By default: true
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, String separator, boolean includeHeader) throws IOException {
    File f = new File(filename);
    File parent = f.getAbsoluteFile().getParentFile();
    if (!parent.exists() && !parent.mkdirs()) {
      throw new IOException("Unable to create directory " + parent);
    }

    PrintWriter writer = new PrintWriter(f);

    if (includeHeader) {
      writer.print("testUserIndex");
      writer.print(separator);
      writer.print("userId");
      writer.print(separator);

      String scorerName = this.getClass().getSimpleName().toLowerCase();
      writer.print(scorerName);

      writer.println();
    }

    for (int testUserIndex = 0; testUserIndex < usersScores.length; testUserIndex++) {
      writer.print(testUserIndex);
      writer.print(separator);

      TestUser testUser = recommender.getDataModel().getTestUser(testUserIndex);
      String userId = testUser.getId();
      writer.print(userId);
      writer.print(separator);

      writer.print(usersScores[testUserIndex]);

      writer.println();
    }

    writer.close();
  }

  /** Private inner class used to parallelize the computation of the quality measures */
  private class EvaluateUsers implements Partible<TestUser> {

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
