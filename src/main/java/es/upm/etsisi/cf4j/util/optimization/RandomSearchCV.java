package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.*;
import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import es.upm.etsisi.cf4j.scorer.Scorer;
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

  /** Scorer classes used to evaluate the Recommender */
  private final Class<? extends Scorer>[] scorerClasses;

  /** Maps objects containing the scorers parameters names (keys) and values (value) */
  private final Map<String, Object>[] scorersParams;

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
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClass,
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
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClass,
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
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClass,
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
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param scorerParams Map object containing the scorer parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      Map<String, Object> scorerParams,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClass,
        scorerParams,
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
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param scorerParams Map object containing the scorer parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      Map<String, Object> scorerParams,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClass,
        scorerParams,
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
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param scorerParams Map object containing the scorer parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      Map<String, Object> scorerParams,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClass,
        scorerParams,
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
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contain a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param scorerParams Map object containing the scorer parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      Map<String, Object> scorerParams,
      int cv,
      int numIters,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        new Class[] {scorerClass},
        new Map[] {scorerParams},
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
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Scorer.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClasses,
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
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Scorer.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClasses,
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
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Scorer.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClasses,
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
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Scorer.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param scorersParams Maps objects containing the scorer parameters names
   *     (keys) and values (value)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      Map<String, Object>[] scorersParams,
      int cv,
      double coverage) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClasses,
        scorersParams,
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
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Scorer.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param scorersParams Maps objects containing the scorer parameters names
   *     (keys) and values (value)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      Map<String, Object>[] scorersParams,
      int cv,
      double coverage,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClasses,
        scorersParams,
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
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Scorer.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param scorersParams Maps objects containing the scorer parameters names
   *     (keys) and values (value)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      Map<String, Object>[] scorersParams,
      int cv,
      int numIters) {
    this(
        datamodel,
        grid,
        recommenderClass,
        scorerClasses,
        scorersParams,
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
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Scorer.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param scorersParams Maps objects containing the scorer parameters names
   *     (keys) and values (value)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      Map<String, Object>[] scorersParams,
      int cv,
      int numIters,
      long seed) {
    this.datamodel = datamodel;
    this.grid = grid;
    this.cv = cv;
    this.recommenderClass = recommenderClass;
    this.scorerClasses = scorerClasses;
    this.scorersParams = scorersParams;
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
              scorerClasses,
              scorersParams,
              numIters,
              seed,
              "Fold " + (fold+1) + " of " + this.cv);
      randomSearch.fit();

      Map<Map<String, Object>, Double[]> randomSearchResults = randomSearch.getResults();
      for (Map<String, Object> params : randomSearchResults.keySet()) {
        Double[][] scores =
            (fold == 0) ? new Double[scorerClasses.length][cv] : this.results.get(params);

        Double[] randomSearchScores = randomSearchResults.get(params);
        for (int s = 0; s < randomSearchScores.length; s++) {
          scores[s][fold] = randomSearchScores[s];
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
      if (!Double.isNaN(averageError) &&
              ((lowerIsBetter && averageError < bestScore) || (!lowerIsBetter && averageError > bestScore))) {
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

      if (!Double.isNaN(averageError)) {
        cvResults.add(new Pair<>(params, averageError));
      }
    }


    // Sort results
    Comparator<Pair<Map<String, Object>, Double>> comparator =
        Comparator.comparing(Pair::getValue, Comparator.comparingDouble(d -> d));

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
        .append(this.scorerClasses[index].getSimpleName());

    if (this.scorersParams != null && this.scorersParams[index] != null) {
      sb.append(this.scorersParams[index].toString());
    }

    sb.append(" scores on development set:\n\n");

    for (int i = 0; i < Math.min(topN, cvResults.size()); i++) {
      Pair<Map<String, Object>, Double> result = cvResults.get(i);

      Map<String, Object> params = result.getKey();
      Double[][] scores = this.results.get(params);
      Double averageScore = result.getValue();

      sb.append(this.scorerClasses[index].getSimpleName().toLowerCase())
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
      for (int s = 0; s < this.scorerClasses.length; s++) {
        String scorerName = this.scorerClasses[s].getSimpleName().toLowerCase();

        for (int fold = 0; fold < this.cv; fold++) {
          writer.print(scorerName);
          writer.print("_fold_");
          writer.print(fold);
          writer.print(separator);
        }

        writer.print(scorerName);
        writer.print("_avg");
        writer.print(separator);

        writer.print(scorerName);
        writer.print("_std");

        if (s < this.scorerClasses.length - 1) {
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
      for (int s = 0; s < this.scorerClasses.length; s++) {
        Double[][] scores = this.results.get(params);

        double avg =
            Maths.arrayAverage(Stream.of(scores[s]).mapToDouble(Double::doubleValue).toArray());
        double std =
            Maths.arrayStandardDeviation(
                Stream.of(scores[s]).mapToDouble(Double::doubleValue).toArray());

        for (int fold = 0; fold < this.cv; fold++) {
          writer.print(scores[s][fold]);
          writer.print(separator);
        }

        writer.print(avg);
        writer.print(separator);

        writer.print(std);

        if (s < this.scorerClasses.length - 1) {
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
