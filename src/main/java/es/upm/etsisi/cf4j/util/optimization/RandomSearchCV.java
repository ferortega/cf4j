package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.*;
import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Stream;

/**
 * Utility class to performs a random search over a Recommender instance. The Recommender class used
 * during the random search must contains a constructor with the signature
 * Recommender::&lt;init&gt;(DataModel, Map&lt;String, Object&gt;) that initializes the Recommender
 * using the attributes defined in the Map object. The parameters used in the search process, i.e.
 * the development set, must be defined in a ParamsGrid instance. The random search is executed in
 * such a way that it minimizes (by default) or maximizes a QualityMeasure by splitting the train
 * set of the dataset in validations sets using cross validation. If the QualityMeasure requires
 * parameters to work, it must contains a constructor with the signature
 * QualityMeasure::&lt;init&gt;(Recommender, Map&lt;String, Object&gt;) that initializes the
 * QualityMeasure using the attributes defined in the Map object. The search is performed by
 * selecting numIters parameters of the development set.
 */
public class RandomSearchCV {

  /** DataModel instance */
  private final DataModel datamodel;

  /** ParamsGrid instance containing the development set */
  private final ParamsGrid grid;

  /** Recommender class to be evaluated */
  private final Class<? extends Recommender> recommenderClass;

  /** QualityMeasure classes used to evaluate the Recommender */
  private final Class<? extends QualityMeasure>[] qualityMeasuresClasses;

  /** Maps objects containing the quality measures parameters names (keys) and values (value) */
  private final Map<String, Object>[] qualityMeasuresParams;

  /** Number of folds for the cross validation */
  private final int cv;

  /** Random seed for random numbers generation * */
  private final long seed;

  /** Number of samples of the development set to be evaluated */
  private final int numIters;

  /** Map to store grid search results */
  private final Map<Map<String, Object>, Double[][]> results;

  /**
   * RandomSearchCV constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearchCV constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        cv,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        cv,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int cv,
      int numIters,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        new Class[] {qualityMeasureClass},
        new Map[] {qualityMeasureParams},
        cv,
        numIters,
        seed);
  }

  /**
   * RandomSearchCV constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        null,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        null,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearchCV constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasuresClasses QualityMeasure classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        null,
        cv,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        qualityMeasuresParams,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        qualityMeasuresParams,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        seed);
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasuresClasses,
        qualityMeasuresParams,
        cv,
        numIters,
        System.currentTimeMillis());
  }

  /**
   * RandomSearchCV constructor
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
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure>[] qualityMeasuresClasses,
      Map<String, Object>[] qualityMeasuresParams,
      int cv,
      int numIters,
      long seed) {
    this.datamodel = datamodel;
    this.grid = grid;
    this.cv = cv;
    this.recommenderClass = recommenderClass;
    this.qualityMeasuresClasses = qualityMeasuresClasses;
    this.qualityMeasuresParams = qualityMeasuresParams;
    this.numIters = numIters;
    this.seed = seed;
    this.results = new HashMap<>();
  }

  /** Performs the search */
  public void fit() {
    List<DataSetEntry> ratings = new ArrayList<>();

    for (User user : datamodel.getUsers()) {
      String userId = user.getId();

      for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
        int itemIndex = user.getItemAt(pos);
        Item item = datamodel.getItem(itemIndex);
        String itemId = item.getId();

        double rating = user.getRatingAt(pos);

        DataSetEntry entry = new DataSetEntry(userId, itemId, rating);
        ratings.add(entry);
      }
    }

    Collections.shuffle(ratings, new Random(seed));

    int foldSize = ratings.size() / cv;

    for (int fold = 0; fold < cv; fold++) {
      int from = foldSize * fold;
      int to = foldSize * (fold + 1);

      // special case for number of ratings not divisible by cv
      if (fold == this.cv - 1 && ratings.size() % this.cv != 0) {
        to = ratings.size();
      }

      List<DataSetEntry> trainRatings = new ArrayList<>(ratings.subList(0, from));
      List<DataSetEntry> testRatings = ratings.subList(from, to);
      trainRatings.addAll(ratings.subList(to, ratings.size()));

      DataSet validationDataset = new ManualDataSet(trainRatings, testRatings);
      DataModel validationDatamodel = new DataModel(validationDataset);

      RandomSearch randomSearch =
          new RandomSearch(
              validationDatamodel,
              grid,
              recommenderClass,
              qualityMeasuresClasses,
              qualityMeasuresParams,
              numIters,
              seed,
              "Fold " + (fold+1) + " of " + this.cv);
      randomSearch.fit();

      Map<Map<String, Object>, Double[]> randomSearchResults = randomSearch.getResults();
      for (Map<String, Object> params : randomSearchResults.keySet()) {
        Double[][] scores =
            (fold == 0) ? new Double[qualityMeasuresClasses.length][cv] : this.results.get(params);

        Double[] readomSearchScores = randomSearchResults.get(params);
        for (int q = 0; q < readomSearchScores.length; q++) {
          scores[q][fold] = readomSearchScores[q];
        }

        this.results.put(params, scores);
      }
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
    double bestScore = (lowerIsBetter) ? Double.MAX_VALUE : Double.MIN_VALUE;

    for (Map<String, Object> params : this.results.keySet()) {
      Double[][] scores = this.results.get(params);
      double averageError =
          Maths.arrayAverage(Stream.of(scores[index]).mapToDouble(Double::doubleValue).toArray());
      if ((lowerIsBetter && averageError < bestScore)
          || (!lowerIsBetter && averageError > bestScore)) {
        bestScore = averageError;
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
    Double[][] scores = this.results.get(params);
    return Maths.arrayAverage(Stream.of(scores[index]).mapToDouble(Double::doubleValue).toArray());
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

    List<Pair<Map<String, Object>, Double>> cvResults = new ArrayList<>();
    for (Map<String, Object> params : this.results.keySet()) {
      Double[][] scores = this.results.get(params);
      double averageError =
          Maths.arrayAverage(Stream.of(scores[index]).mapToDouble(Double::doubleValue).toArray());
      cvResults.add(new Pair<>(params, averageError));
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

    cvResults.sort(comparator);

    // Prepare printable results
    StringBuilder sb = new StringBuilder();
    DecimalFormat df = new DecimalFormat(numberFormat, new DecimalFormatSymbols(Locale.US));

    sb.append("Tuning parameters for ")
        .append(recommenderClass.getSimpleName())
        .append(" recommender:\n\n");

    sb.append("Best parameters set found on development set:\n\n")
        .append(cvResults.get(0).getKey())
        .append("\n\n");

    sb.append("Top ")
        .append(topN)
        .append(" ")
        .append(this.qualityMeasuresClasses[index].getSimpleName());

    if (this.qualityMeasuresParams != null && this.qualityMeasuresParams[index] != null) {
      sb.append(this.qualityMeasuresParams[index].toString());
    }

    sb.append(" scores on development set:\n\n");

    for (int i = 0; i < Math.min(topN, cvResults.size()); i++) {
      Pair<Map<String, Object>, Double> result = cvResults.get(i);

      Map<String, Object> params = result.getKey();
      Double[][] scores = this.results.get(params);
      Double averageScore = result.getValue();

      sb.append(this.qualityMeasuresClasses[index].getSimpleName().toLowerCase())
          .append('=')
          .append(Arrays.toString(scores[index]))
          .append(", avg=");

      if (!Double.isNaN(averageScore)) {
        sb.append(df.format(averageScore));
      } else {
        sb.append("NaN");
        for (int s = 0; s < numberFormat.length() - "NaN".length(); s++) {
          sb.append(" ");
        }
      }

      sb.append(", std=")
          .append(
              df.format(
                  Maths.arrayStandardDeviation(
                      Stream.of(scores[index]).mapToDouble(Double::doubleValue).toArray())))
          .append(" for ")
          .append(params)
          .append("\n");
    }

    // Print results
    System.out.println("\n" + sb.toString());
  }

  /**
   * Exports results of RandomSerachCV in csv format
   *
   * @param filename File name
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename) throws IOException {
    exportResults(filename, true);
  }

  /**
   * Exports results of RandomSerachCV in csv format
   *
   * @param filename File name
   * @param includeHeader Include CSV header line. By default: true
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, boolean includeHeader) throws IOException {
    exportResults(filename, ",", includeHeader);
  }

  /**
   * Exports results of RandomSerachCV in csv format
   *
   * @param filename File name
   * @param separator CSV separator field. By default: colon character (,)
   * @throws IOException When file is not found or is locked.
   */
  public void exportResults(String filename, String separator) throws IOException {
    exportResults(filename, separator, true);
  }

  /**
   * Exports results of RandomSerachCV in csv format
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

        for (int fold = 0; fold < this.cv; fold++) {
          writer.print(qualityMeasureName);
          writer.print("_fold_");
          writer.print(fold);
          writer.print(separator);
        }

        writer.print(qualityMeasureName);
        writer.print("_avg");
        writer.print(separator);

        writer.print(qualityMeasureName);
        writer.print("_std");

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
      for (int q = 0; q < this.qualityMeasuresClasses.length; q++) {
        Double[][] scores = this.results.get(params);

        double avg =
            Maths.arrayAverage(Stream.of(scores[q]).mapToDouble(Double::doubleValue).toArray());
        double std =
            Maths.arrayStandardDeviation(
                Stream.of(scores[q]).mapToDouble(Double::doubleValue).toArray());

        for (int fold = 0; fold < this.cv; fold++) {
          writer.print(scores[q][fold]);
          writer.print(separator);
        }

        writer.print(avg);
        writer.print(separator);

        writer.print(std);

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
}
