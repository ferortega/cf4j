package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * Utility class to performs a random search over a Recommender instance. The Recommender class used
 * during the random search must contains a constructor with the signature
 * Recommender::&lt;init&gt;(DataModel, Map&lt;String, Object&gt;) that initializes the Recommender
 * using the attributes defined in the Map object. The parameters used in the search process, i.e.
 * the development set, must be defined in a ParamsGrid instance. The random search is executed in
 * such a way that it minimizes (by default) or maximizes a QualityMeasure instance over the test
 * set of the DataModel instance. If the QualityMeasure requires parameters to work, it must
 * contains a constructor with the signature QualityMeasure::&lt;init&gt;(Recommender,
 * Map&lt;String, Object&gt;) that initializes the QualityMeasure using the attributes defined in
 * the Map object. The search is performed by selecting numIters parameters of the development set.
 */
public class RandomSearch {

  /** DataModel instance */
  private final DataModel datamodel;

  /** ParamsGrid instance containing the development set */
  private final ParamsGrid grid;

  /** Recommender class to be evaluated */
  private final Class<? extends Recommender> recommenderClass;

  /** QualityMeasure classes used to evaluate the Recommender */
  private final Class<? extends QualityMeasure>[] qualityMeasuresClasses;

  /** Maps objects containing the quality measure parameters names (keys) and values (value) */
  private final Map<String, Object>[] qualityMeasuresParams;

  /** Number of samples of the development set to be evaluated */
  private final int numIters;

  /** Random seed for random numbers generation */
  private final long seed;

  /** String prefix to be shown during fut */
  private final String progressPrefix;

  /** Map to store grid search results */
  private final Map<Map<String, Object>, Double[]> results;

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int numIters,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        new Class[] {qualityMeasureClass},
        new Map[] {qualityMeasureParams},
        numIters,
        seed);
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        null,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        null,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        null,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param qualityMeasuresParams Maps objects containing the quality measure parameters names
   *     (keys) and values (value)
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        qualityMeasuresParams,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param qualityMeasuresParams Maps objects containing the quality measure parameters names
   *     (keys) and values (value)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        qualityMeasuresParams,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param qualityMeasuresParams Maps objects containing the quality measure parameters names
   *     (keys) and values (value)
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        qualityMeasuresParams,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param qualityMeasuresParams Maps objects containing the quality measure parameters names
   *     (keys) and values (value)
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      int numIters,
      long seed) {
    this(datamodel, grid, recommenderClass, qualityMeasuresClasses, qualityMeasuresParams, numIters, seed, "");
  }

  /**
   * RandomSearch constructor to be used inside es.upm.etsisi.cf4j.util.optimization package
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param qualityMeasuresParams Maps objects containing the quality measure parameters names
   *     (keys) and values (value)
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   * @param progressPrefix String prefix to be printed during fit
   */
  protected RandomSearch(
          DataModel datamodel,
          ParamsGrid grid,
          Class<? extends Recommender> recommenderClass,
          Class<? extends QualityMeasure>[] qualityMeasuresClasses,
          Map<String, Object>[] qualityMeasuresParams,
          int numIters,
          long seed,
          String progressPrefix) {
    this.datamodel = datamodel;
    this.grid = grid;
    this.recommenderClass = recommenderClass;
    this.qualityMeasuresClasses = qualityMeasuresClasses;
    this.qualityMeasuresParams = qualityMeasuresParams;
    this.numIters = Math.min(numIters, grid.getDevelopmentSetSize());
    this.seed = seed;
    this.results = new HashMap<>();
    this.progressPrefix = progressPrefix;
  }

  /** Performs grid search */
  public void fit() {

    Iterator<Map<String, Object>> iter = grid.getDevelopmentSetIterator(true, seed);

    Long init = System.currentTimeMillis();

    int i = 0;
    while (i < this.numIters && iter.hasNext()) {
      Map<String, Object> params = iter.next();
      i++;

      System.out.print("\n\n");

      if (!this.progressPrefix.isEmpty()) {
        System.out.print(this.progressPrefix + ". ");
      }

      System.out.print("Iter " + i + " of " + this.numIters);

      String completePercent = new DecimalFormat("0.00").format(100.0 * i / this.numIters);
      System.out.print(" (" + completePercent + "%). ");

      long now = System.currentTimeMillis();
      long elapsedTime = now - init;
      System.out.print("Elapsed time: " + DurationFormatUtils.formatDurationWords(elapsedTime, true, false));

      if (i > 1) {
        long eta = (this.numIters - i + 1) * elapsedTime / (i - 1);
        System.out.print(". ETA: " + DurationFormatUtils.formatDurationWords(eta, true, false));
      }

      System.out.println();

      Recommender recommender = null;

      try {
        recommender =
            this.recommenderClass
                .getConstructor(DataModel.class, Map.class)
                .newInstance(this.datamodel, params);
      } catch (NoSuchMethodException e) {
        System.err.println(
            this.recommenderClass.getCanonicalName()
                + " does not seem to contain a constructor to be used in a grid search.");
        e.printStackTrace();
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
        System.err.println(
            "A problem has occurred during the "
                + this.recommenderClass.getCanonicalName()
                + " instantiation.");
        e.printStackTrace();
      }

      if (recommender != null) {
        recommender.fit();
      }

      Double[] scores = new Double[this.qualityMeasuresClasses.length];

      for (int q = 0; q < this.qualityMeasuresClasses.length; q++) {
        QualityMeasure qm = null;

        Class<? extends QualityMeasure> qualityMeasureClass = this.qualityMeasuresClasses[q];
        Map<String, Object> qualityMeasureParams =
            this.qualityMeasuresParams == null ? null : this.qualityMeasuresParams[q];

        try {
          if (qualityMeasureParams == null || qualityMeasureParams.isEmpty()) {
            qm = qualityMeasureClass.getConstructor(Recommender.class).newInstance(recommender);
          } else {
            qm =
                qualityMeasureClass
                    .getConstructor(Recommender.class, Map.class)
                    .newInstance(recommender, qualityMeasureParams);
          }
        } catch (NoSuchMethodException e) {
          System.err.println(
              qualityMeasureClass.getCanonicalName()
                  + " does not seem to contain a constructor to be used in a grid search.");
          e.printStackTrace();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
          System.err.println(
              "A problem has occurred during the "
                  + qualityMeasureClass.getCanonicalName()
                  + " instantiation.");
          e.printStackTrace();
        }

        scores[q] = qm != null ? qm.getScore() : null;
      }

      results.put(params, scores);
    }
  }

  /**
   * Get the best result parameters.
   *
   * @return Map with best params
   */
  public Map<String, Object> getBestParams() {
    return this.getBestParams(true);
  }

  /**
   * Get the best result parameters.
   *
   * @param lowerIsBetter Boolean value that takes true if the quality measure is better the lower
   *     its value. False otherwise.
   * @return Map with best params
   */
  public Map<String, Object> getBestParams(boolean lowerIsBetter) {
    return this.getBestParams(0, lowerIsBetter);
  }

  /**
   * Get the best result parameters.
   *
   * @param index Index of the quality measure for which the best parameters are to be obtained.
   * @param lowerIsBetter Boolean value that takes true if the quality measure is better the lower
   *     its value. False otherwise.
   * @return Map with best params
   */
  public Map<String, Object> getBestParams(int index, boolean lowerIsBetter) {
    Map<String, Object> bestParams = null;
    Double bestScore = (lowerIsBetter) ? Double.MAX_VALUE : Double.MIN_VALUE;
    for (Map<String, Object> params : this.results.keySet()) {
      Double[] scores = this.results.get(params);
      Double score = scores[index];
      if ((lowerIsBetter && score < bestScore) || (!lowerIsBetter && score > bestScore)) {
        bestScore = score;
        bestParams = params;
      }
    }
    return bestParams;
  }

  /**
   * Get the best result score.
   *
   * @return double value with best score.
   */
  public double getBestScore() {
    return this.getBestScore(true);
  }

  /**
   * Get the best result score.
   *
   * @param lowerIsBetter Boolean value that takes true if the quality measure is better the lower
   *     its value. False otherwise.
   * @return double value with best score.
   */
  public double getBestScore(boolean lowerIsBetter) {
    return this.getBestScore(0, lowerIsBetter);
  }

  /**
   * Get the best result score.
   *
   * @param index Index of the quality measure for which the best score is to be obtained
   * @param lowerIsBetter Boolean value that takes true if the quality measure is better the lower
   *     its value. False otherwise.
   * @return double value with best score.
   */
  public double getBestScore(int index, boolean lowerIsBetter) {
    Map<String, Object> params = getBestParams(index, lowerIsBetter);
    Double[] scores = results.get(params);
    return scores[index];
  }

  /**
   * Prints the results of the random search. By default, the quality measure is better the lower
   * its value.
   *
   * @param topN Number of entries of the development set to be shown as the top ones
   */
  public void printResults(int topN) {
    this.printResults(topN, true);
  }

  /**
   * Prints the results of the random search.
   *
   * @param topN Number of entries of the development set to be shown as the top ones
   * @param lowerIsBetter Boolean value that takes true if the quality measure is better the lower
   *     its value. False otherwise.
   */
  public void printResults(int topN, boolean lowerIsBetter) {
    this.printResults(topN, 0, lowerIsBetter);
  }

  /**
   * Prints the results of the random search.
   *
   * @param topN Number of entries of the development set to be shown as the top ones
   * @param index Index of the quality measure for which the best results are to be printed
   * @param lowerIsBetter Boolean value that takes true if the quality measure is better the lower
   *     its value. False otherwise.
   */
  public void printResults(int topN, int index, boolean lowerIsBetter) {
    this.printResults("0.000000", topN, index, lowerIsBetter);
  }

  /**
   * Prints the results of the random search.
   *
   * @param numberFormat Number format for the quality measure values
   * @param topN Number of entries of the development set to be shown as the top ones
   * @param index Index of the quality measure for which the best results are to be printed
   * @param lowerIsBetter Boolean value that takes true if the quality measure is better the lower
   *     its value. False otherwise.
   */
  public void printResults(String numberFormat, int topN, int index, boolean lowerIsBetter) {

    List<Pair<Map<String, Object>, Double>> resultsList = new ArrayList<>();
    for (Map<String, Object> params : this.results.keySet()) {
      Double[] scores = this.results.get(params);
      Double score = scores[index];
      resultsList.add(new Pair<>(params, score));
    }

    // Sort results
    Comparator<Pair<Map<String, Object>, Double>> comparator =
        Comparator.comparing(
            Pair::getValue,
            (d1, d2) -> {
              if (Double.isNaN(d1) && Double.isNaN(d2)) {
                return 0;
              } else if (Double.isNaN(d1)) {
                return -1;
              } else if (Double.isNaN(d2)) {
                return 1;
              } else {
                return Double.compare(d1, d2);
              }
            });

    if (!lowerIsBetter) {
      comparator = comparator.reversed();
    }

    resultsList.sort(comparator);

    // Prepare printable results
    StringBuilder sb = new StringBuilder();
    DecimalFormat df = new DecimalFormat(numberFormat, new DecimalFormatSymbols(Locale.US));

    sb.append("Tuning parameters for ")
        .append(recommenderClass.getSimpleName())
        .append(" recommender:\n\n");

    sb.append("Best parameters set found on development set:\n\n")
        .append(resultsList.get(0).getKey())
        .append("\n\n");

    sb.append("Top ")
        .append(topN)
        .append(" ")
        .append(this.qualityMeasuresClasses[index].getSimpleName());

    if (this.qualityMeasuresParams != null && this.qualityMeasuresParams[index] != null) {
      sb.append(this.qualityMeasuresParams[index].toString());
    }

    sb.append(" scores on development set:\n\n");

    for (int i = 0; i < Math.min(topN, this.results.size()); i++) {
      Pair<Map<String, Object>, Double> result = resultsList.get(i);

      StringBuilder value;

      if (!Double.isNaN(result.getValue())) {
        value = new StringBuilder(df.format(result.getValue()));
      } else {
        value = new StringBuilder("NaN");
        for (int s = 0; s < numberFormat.length() - "NaN".length(); s++) {
          value.append(" ");
        }
      }

      sb.append(value).append(" for ").append(result.getKey()).append("\n");
    }

    // Print results
    System.out.println("\n" + sb.toString());
  }

  /**
   * Exports results of RandomSerach in csv format
   *
   * @param filename File name
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename) throws IOException {
    exportResults(filename, true);
  }

  /**
   * Exports results of RandomSerach in csv format
   *
   * @param filename File name
   * @param includeHeader Include CSV header line. By default: true
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, boolean includeHeader) throws IOException {
    exportResults(filename, ",", includeHeader);
  }

  /**
   * Exports results of RandomSerach in csv format
   *
   * @param filename File name
   * @param separator CSV separator field. By default: colon character (,)
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, String separator) throws IOException {
    exportResults(filename, separator, true);
  }

  /**
   * Exports results of RandomSerach in csv format
   *
   * @param filename File name
   * @param separator CSV separator field. By default: colon character (,)
   * @param includeHeader Include CSV header line. By default: true
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, String separator, boolean includeHeader)
      throws IOException {
    File f = new File(filename);
    File parent = f.getAbsoluteFile().getParentFile();
    if (!parent.exists() && !parent.mkdirs()) {
      throw new IOException("Unable to create directory " + parent);
    }

    PrintWriter writer = new PrintWriter(f);

    String[] paramsName = grid.getParamsName();

    if (includeHeader) {
      for (int q = 0; q < this.qualityMeasuresClasses.length; q++) {
        String qualityMeasureName = this.qualityMeasuresClasses[q].getSimpleName().toLowerCase();
        writer.print(qualityMeasureName);
        if (q < this.qualityMeasuresClasses.length - 1) {
          writer.print(separator);
        }
      }

      for (String name : paramsName) {
        writer.print(separator);
        writer.print(name);
      }

      writer.println();
    }

    for (Map<String, Object> params : this.results.keySet()) {
      Double[] scores = this.results.get(params);
      for (int q = 0; q < scores.length; q++) {
        writer.print(scores[q].doubleValue());
        if (q < this.qualityMeasuresClasses.length - 1) {
          writer.print(separator);
        }
      }

      for (String key : paramsName) {
        writer.print(separator);
        writer.print(params.get(key));
      }

      writer.println();
    }

    writer.close();
  }

  /**
   * This method is required for cross validation (CV). Final users should not used it.
   *
   * @return Map that storage search results.
   */
  protected Map<Map<String, Object>, Double[]> getResults() {
    return results;
  }
}
