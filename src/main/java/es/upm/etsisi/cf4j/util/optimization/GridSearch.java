package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.apache.commons.math3.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * Utility class to performs a grid search over a Recommender instance. The Recommender class used
 * during the grid search must contains a constructor with the signature
 * Recommender::&lt;init&gt;(DataModel, Map&lt;String, Object&gt;) that initializes the Recommender
 * using the attributes defined in the Map object. The parameters used in the search process, i.e.
 * the development set, must be defined in a ParamsGrid instance. The grid search is executed in
 * such a way that it minimizes (by default) or maximizes a QualityMeasure instance. If the
 * QualityMeasure requires parameters to work, it must contains a constructor with the signature
 * QualityMeasure::&lt;init&gt;(Recommender, Map&lt;String, Object&gt;) that initializes the
 * QualityMeasure using the attributes defined in the Map object.
 */
public class GridSearch {

  /** DataModel instance */
  private DataModel datamodel;

  /** ParamsGrid instance containing the development set */
  private ParamsGrid grid;

  /** Recommender class to be evaluated */
  private Class<? extends Recommender> recommenderClass;

  /** QualityMeasure class used to evaluate the Recommender */
  private Class<? extends QualityMeasure> qualityMeasureClass;

  /** Map object containing the quality measure parameters names (keys) and values (value) */
  private Map<String, Object> qualityMeasureParams;

  /** List to store grid search results */
  private List<Pair<String, Double>> results;

  /**
   * GridSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated
   * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender
   */
  public GridSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass) {
    this(datamodel, grid, recommenderClass, qualityMeasureClass, null);
  }

  /**
   * GridSearch constructor
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
   */
  public GridSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends QualityMeasure> qualityMeasureClass,
      Map<String, Object> qualityMeasureParams) {
    this.datamodel = datamodel;
    this.grid = grid;
    this.recommenderClass = recommenderClass;
    this.qualityMeasureClass = qualityMeasureClass;
    this.qualityMeasureParams = qualityMeasureParams;

    this.results = new ArrayList<>();
  }

  /** Performs grid search */
  public void fit() {

    Iterator<Map<String, Object>> iter = grid.getDevelopmentSetIterator();

    while (iter.hasNext()) {
      Map<String, Object> params = iter.next();

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
        results.add(new Pair<>(params.toString(), score));
      }
    }
  }

  /**
   * Prints the results of the grid search. By default, the quality measure is better the lower its
   * value.
   */
  public void printResults() {
    this.printResults("0.000000", this.results.size(), true);
  }

  /**
   * Prints the results of the grid search
   *
   * @param topN Number of entries of the development set to be shown as the top ones
   */
  public void printResults(int topN) {
    this.printResults("0.000000", topN, true);
  }

  /**
   * Prints the results of the grid search. By default, the quality measure is better the lower its
   * value.
   *
   * @param numberFormat Number format for the quality measure values
   */
  public void printResults(String numberFormat) {
    this.printResults(numberFormat, this.results.size(), true);
  }

  /**
   * Prints the results of the grid search
   *
   * @param lowerIsBetter True if the quality measure is better the lower its value. False
   *     otherwise.
   */
  public void printResults(boolean lowerIsBetter) {
    this.printResults("0.000000", this.results.size(), lowerIsBetter);
  }

  /**
   * Prints the results of the grid search
   *
   * @param numberFormat Number format for the quality measure values
   * @param topN Number of entries of the development set to be shown as the top ones
   */
  public void printResults(String numberFormat, int topN) {
    this.printResults(numberFormat, topN, true);
  }

  /**
   * Prints the results of the grid search
   *
   * @param topN Number of entries of the development set to be shown as the top ones
   * @param lowerIsBetter True if the quality measure is better the lower its value. False
   *     otherwise.
   */
  public void printResults(int topN, boolean lowerIsBetter) {
    this.printResults("0.000000", topN, lowerIsBetter);
  }

  /**
   * Prints the results of the grid search
   *
   * @param numberFormat Number format for the quality measure values
   * @param lowerIsBetter True if the quality measure is better the lower its value. False
   *     otherwise.
   */
  public void printResults(String numberFormat, boolean lowerIsBetter) {
    this.printResults(numberFormat, this.results.size(), lowerIsBetter);
  }

  /**
   * Prints the results of the grid search
   *
   * @param numberFormat Number format for the quality measure values
   * @param topN Number of entries of the development set to be shown as the top ones
   * @param lowerIsBetter True if the quality measure is better the lower its value. False
   *     otherwise.
   */
  public void printResults(String numberFormat, int topN, boolean lowerIsBetter) {

    // Sort results
    Comparator<Pair<String, Double>> comparator =
        Comparator.comparing(
            Pair::getValue,
            (d1, d2) -> {
              if (Double.isNaN(d1) && Double.isNaN(d2)) {
                return 0;
              } else if (Double.isNaN(d1)) {
                return 1;
              } else if (Double.isNaN(d2)) {
                return -1;
              } else {
                return Double.compare(d1, d2);
              }
            });

    if (!lowerIsBetter) {
      comparator = comparator.reversed();
    }

    this.results.sort(comparator);

    // Prepare printable results
    StringBuilder sb = new StringBuilder();
    DecimalFormat df = new DecimalFormat(numberFormat, new DecimalFormatSymbols(Locale.US));

    sb.append("Tuning parameters for ")
        .append(recommenderClass.getSimpleName())
        .append(" recommender:\n\n");

    sb.append("Best parameters set found on development set:\n\n")
        .append(this.results.get(0).getKey())
        .append("\n\n");

    sb.append(this.qualityMeasureClass.getSimpleName());
    if (this.qualityMeasureParams != null) {
      sb.append(this.qualityMeasureParams.toString());
    }
    sb.append(" scores on development set:\n\n");

    for (int i = 0; i < Math.min(topN, this.results.size()); i++) {
      Pair<String, Double> result = this.results.get(i);

      String value = "";

      if (!Double.isNaN(result.getValue())) {
        value = df.format(result.getValue());
      } else {
        value = "NaN";
        for (int s = 0; s < numberFormat.length() - "NaN".length(); s++) {
          value += " ";
        }
      }

      sb.append(value).append(" for ").append(result.getKey()).append("\n");
    }

    // Print results
    System.out.println("\n" + sb.toString());
  }
}
