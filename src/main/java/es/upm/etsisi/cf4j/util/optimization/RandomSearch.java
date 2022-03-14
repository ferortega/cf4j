package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
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

  /** QualityMeasure class used to evaluate the Recommender */
  private final Class<? extends QualityMeasure> qualityMeasureClass;

  /** Map object containing the quality measure parameters names (keys) and values (value) */
  private final Map<String, Object> qualityMeasureParams;

  /** Number of samples of the development set to be evaluated */
  private final int numIters;

  /**
   * Boolean value that takes true if the quality measure is better the lower its value. False
   * otherwise
   */
  private final boolean lowerIsBetter;

  /** Random seed for random numbers generation */
  private final long seed;

  /** Map to store grid search results */
  private final Map<Map<String, Object>, Double> results;

  /**
   * RandomSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
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
        true,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int numIters,
      long seed) {
    this(datamodel, grid, recommenderClass, qualityMeasureClass, null, numIters, true, seed);
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
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
        true,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
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
        coverage,
        true,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
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
        coverage,
        true,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
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
    this(datamodel, grid, recommenderClass, qualityMeasureClass, null, coverage, true, seed);
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
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
        true,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int numIters,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        numIters,
        lowerIsBetter,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int numIters,
      boolean lowerIsBetter,
      long seed) {
    this(datamodel, grid, recommenderClass, qualityMeasureClass, null, numIters, seed);
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int numIters,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        numIters,
        lowerIsBetter,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      double coverage,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        coverage,
        lowerIsBetter,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      double coverage,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        coverage,
        lowerIsBetter,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      double coverage,
      boolean lowerIsBetter,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        coverage,
        lowerIsBetter,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      double coverage,
      boolean lowerIsBetter,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        (int) (coverage * grid.getDevelopmentSetSize()),
        lowerIsBetter,
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
   *     must contains a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   * @param seed Random seed for random numbers generation
   */
  public RandomSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int numIters,
      boolean lowerIsBetter,
      long seed) {
    this.datamodel = datamodel;
    this.grid = grid;
    this.recommenderClass = recommenderClass;
    this.qualityMeasureClass = qualityMeasureClass;
    this.qualityMeasureParams = qualityMeasureParams;
    this.numIters = Math.min(numIters, grid.getDevelopmentSetSize());
    this.lowerIsBetter = lowerIsBetter;
    this.seed = seed;
    this.results = new HashMap<>();
  }

  /** Performs grid search */
  public void fit() {

    Iterator<Map<String, Object>> iter = grid.getDevelopmentSetIterator(true, seed);

    int i = 0;
    while (i < this.numIters && iter.hasNext()) {
      Map<String, Object> params = iter.next();
      i++;

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

      QualityMeasure qm = null;

      try {
        if (this.qualityMeasureParams == null || this.qualityMeasureParams.isEmpty()) {
          qm = this.qualityMeasureClass.getConstructor(Recommender.class).newInstance(recommender);
        } else {
          qm =
              this.qualityMeasureClass
                  .getConstructor(Recommender.class, Map.class)
                  .newInstance(recommender, this.qualityMeasureParams);
        }
      } catch (NoSuchMethodException e) {
        System.err.println(
            this.qualityMeasureClass.getCanonicalName()
                + " does not seem to contain a constructor to be used in a grid search.");
        e.printStackTrace();
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
        System.err.println(
            "A problem has occurred during the "
                + this.qualityMeasureClass.getCanonicalName()
                + " instantiation.");
        e.printStackTrace();
      }

      if (qm != null) {
        double score = qm.getScore();
        results.put(params, score);
      }
    }
  }

  /**
   * Get the best result params. By default, the quality measure is better the lower its value.
   *
   * @return Map with best params
   */
  public Map<String, Object> getBestParams() {
    Map<String, Object> bestParams = null;
    Double bestScore = (this.lowerIsBetter) ? Double.MAX_VALUE : Double.MIN_VALUE;
    for (Map<String, Object> params : this.results.keySet()) {
      double error = this.results.get(params);
      if ((this.lowerIsBetter && error < bestScore) || (!this.lowerIsBetter && error > bestScore)) {
        bestScore = error;
        bestParams = params;
      }
    }
    return bestParams;
  }

  /**
   * Get the best result score. By default, the quality measure is better the lower its value.
   *
   * @return double value with best score
   */
  public double getBestScore() {
    double bestScore = results.get(getBestParams());
    return bestScore;
  }

  /**
   * Prints the results of the random search. By default, the quality measure is better the lower
   * its value.
   */
  public void printResults() {
    this.printResults("0.000000", this.results.size());
  }

  /**
   * Prints the results of the random search
   *
   * @param topN Number of entries of the development set to be shown as the top ones
   */
  public void printResults(int topN) {
    this.printResults("0.000000", topN);
  }

  /**
   * Prints the results of the random search. By default, the quality measure is better the lower
   * its value.
   *
   * @param numberFormat Number format for the quality measure values
   */
  public void printResults(String numberFormat) {
    this.printResults(numberFormat, this.results.size());
  }

  /**
   * Prints the results of the random search
   *
   * @param numberFormat Number format for the quality measure values
   * @param topN Number of entries of the development set to be shown as the top ones
   */
  public void printResults(String numberFormat, int topN) {

    List<Pair<Map<String, Object>, Double>> resultsList = new ArrayList<>();
    for (Map<String, Object> params : this.results.keySet()) {
      double error = this.results.get(params);
      resultsList.add(new Pair<>(params, error));
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

    sb.append(this.qualityMeasureClass.getSimpleName());
    if (this.qualityMeasureParams != null) {
      sb.append(this.qualityMeasureParams.toString());
    }
    sb.append(" scores on development set:\n\n");

    for (int i = 0; i < Math.min(topN, this.results.size()); i++) {
      Pair<Map<String, Object>, Double> result = resultsList.get(i);

      StringBuilder value = new StringBuilder();

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

    String measure = this.qualityMeasureClass.getSimpleName().toLowerCase();

    PrintWriter writer = new PrintWriter(f);

    String[] paramsName = grid.getParamsName();

    if (includeHeader) {

      writer.print(measure);

      for (String name : paramsName) {
        writer.print(separator);
        writer.print(name);
      }

      writer.println();
    }

    for (Map<String, Object> params : this.results.keySet()) {
      double error = this.results.get(params);

      writer.print(error);

      for (String key : paramsName) {
        writer.print(separator);
        writer.print(params.get(key));
      }

      writer.println();
    }

    writer.close();
  }

  public Map<Map<String, Object>, Double> getResults() {
    return results;
  }
}
