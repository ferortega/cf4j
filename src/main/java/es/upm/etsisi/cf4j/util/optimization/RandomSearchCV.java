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

  /** QualityMeasure class used to evaluate the Recommender */
  private final Class<? extends QualityMeasure> qualityMeasureClass;

  /** Map object containing the quality measure parameters names (keys) and values (value) */
  private final Map<String, Object> qualityMeasureParams;

  /** Number of folds for the cross validation */
  private final int cv;

  /** Random seed for random numbers generation * */
  private final long seed;

  /** Number of samples of the development set to be evaluated */
  private final int numIters;

  /**
   * Boolean value that takes true if the quality measure is better the lower its value. False
   * otherwise
   */
  private final boolean lowerIsBetter;

  /** Map to store grid search results */
  private final Map<Map<String, Object>, double[]> results;

  /**
   * RandomSearchCV constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
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
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        true,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
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
        true,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
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
        true,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
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
        true,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
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
        cv,
        numIters,
        true,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      int numIters,
      long seed) {
    this(datamodel, grid, recommenderClass, qualityMeasureClass, null, cv, numIters, true, seed);
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
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
        true,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      double coverage,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        lowerIsBetter,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      double coverage,
      boolean lowerIsBetter,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        lowerIsBetter,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int cv,
      double coverage,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        lowerIsBetter,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param coverage Percentage of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
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
      boolean lowerIsBetter,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        cv,
        (int) (coverage * grid.getDevelopmentSetSize()),
        lowerIsBetter,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      int numIters,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        cv,
        numIters,
        lowerIsBetter,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   * @param seed Random seed for random numbers generation
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      int cv,
      int numIters,
      boolean lowerIsBetter,
      long seed) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        null,
        cv,
        numIters,
        lowerIsBetter,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
   */
  public RandomSearchCV(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams,
      int cv,
      int numIters,
      boolean lowerIsBetter) {
    this(
        datamodel,
        grid,
        recommenderClass,
        qualityMeasureClass,
        qualityMeasureParams,
        cv,
        numIters,
        lowerIsBetter,
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
   *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param qualityMeasureParams Map object containing the quality measure parameters names (keys)
   *     and values (value)
   * @param cv Number of fold for the cross validation
   * @param numIters Number of samples of the development set to be evaluated
   * @param lowerIsBetter True if the quality measure is better the lower its value, false
   *     otherwise. True by default.
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
      boolean lowerIsBetter,
      long seed) {
    this.datamodel = datamodel;
    this.grid = grid;
    this.cv = cv;
    this.recommenderClass = recommenderClass;
    this.qualityMeasureClass = qualityMeasureClass;
    this.qualityMeasureParams = qualityMeasureParams;
    this.numIters = numIters;
    this.lowerIsBetter = lowerIsBetter;
    this.seed = seed;
    this.results = new HashMap<>();
  }

  /** Performs grid search */
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
              qualityMeasureClass,
              qualityMeasureParams,
              numIters,
              seed);
      randomSearch.fit();

      Map<Map<String, Object>, Double> randomSearchResults = randomSearch.getResults();
      for (Map<String, Object> params : randomSearchResults.keySet()) {
        double[] errors = (fold == 0) ? new double[cv] : this.results.get(params);
        errors[fold] = randomSearchResults.get(params);
        this.results.put(params, errors);
      }
    }
  }

  /** Get the best result params. By default, the quality measure is better the lower its value. */
  public Map<String, Object> getBestParams() {
    Map<String, Object> bestParams = null;
    Double bestScore = (this.lowerIsBetter) ? Double.MAX_VALUE : Double.MIN_VALUE;

    for (Map<String, Object> params : this.results.keySet()) {
      double[] errors = this.results.get(params);
      double averageError = Maths.arrayAverage(errors);
      if ((this.lowerIsBetter && averageError < bestScore)
          || (!this.lowerIsBetter && averageError > bestScore)) {
        bestScore = averageError;
        bestParams = params;
      }
    }

    return bestParams;
  }

  /** Get the best result score. By default, the quality measure is better the lower its value. */
  public Double getBestScore() {
    double bestScore = Maths.arrayAverage(results.get(getBestParams()));
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

    List<Pair<Map<String, Object>, Double>> cvResults = new ArrayList<>();
    for (Map<String, Object> params : this.results.keySet()) {
      double[] errors = this.results.get(params);
      double averageError = Maths.arrayAverage(errors);
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

    if (!this.lowerIsBetter) {
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

    sb.append(this.qualityMeasureClass.getSimpleName());
    if (this.qualityMeasureParams != null) {
      sb.append(this.qualityMeasureParams.toString());
    }
    sb.append(" scores on development set:\n\n");

    for (int i = 0; i < Math.min(topN, cvResults.size()); i++) {
      Pair<Map<String, Object>, Double> result = cvResults.get(i);

      StringBuilder value = new StringBuilder();

      if (!Double.isNaN(result.getValue())) {
        value = new StringBuilder(df.format(result.getValue()));
      } else {
        value = new StringBuilder("NaN");
        for (int s = 0; s < numberFormat.length() - "NaN".length(); s++) {
          value.append(" ");
        }
      }

      sb.append(this.qualityMeasureClass.getSimpleName().toLowerCase())
          .append('=')
          .append(Arrays.toString(this.results.get(result.getKey())))
          .append(", avg=")
          .append(value)
          .append(", std=")
          .append(df.format(Maths.arrayStandardDeviation(this.results.get(result.getKey()))))
          .append(" for ")
          .append(result.getKey())
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

    String measure = this.qualityMeasureClass.getSimpleName().toLowerCase();

    PrintWriter writer = new PrintWriter(f);

    if (includeHeader) {
      for (int fold = 0; fold < this.cv; fold++) {
        writer.print(measure);
        writer.print("_fold_");
        writer.print(fold);
        writer.print(separator);
      }

      writer.print(measure);
      writer.print("_avg");
      writer.print(separator);

      writer.print(measure);
      writer.print("_std");
      writer.print(separator);

      writer.println("params");
    }

    for (Map<String, Object> params : this.results.keySet()) {
      double[] errors = this.results.get(params);
      double avg = Maths.arrayAverage(errors);
      double std = Maths.arrayStandardDeviation(errors);

      for (int fold = 0; fold < this.cv; fold++) {
        writer.print(errors[fold]);
        writer.print(separator);
      }

      writer.print(avg);
      writer.print(separator);

      writer.print(std);
      writer.print(separator);

      if (separator.equals(",")) {
        writer.print("\"");
      }
      writer.print(params);

      if (separator.equals(",")) {
        writer.print("\"");
      }

      writer.println();
    }

    writer.close();
  }
}
