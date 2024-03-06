package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.scorer.Scorer;
import es.upm.etsisi.cf4j.recommender.Recommender;

import java.util.Map;

/**
 * Utility class to performs a grid search over a Recommender instance. The Recommender class used
 * during the grid search must contains a constructor with the signature
 * Recommender::&lt;init&gt;(DataModel, Map&lt;String, Object&gt;) that initializes the Recommender
 * using the attributes defined in the Map object. The parameters used in the search process, i.e.
 * the development set, must be defined in a ParamsGrid instance. The random search is executed in
 * such a way that it minimizes (by default) or maximizes a QualityMeasure instance over the test
 * set of the DataModel instance. If the QualityMeasure requires parameters to work, it must
 * contains a constructor with the signature QualityMeasure::&lt;init&gt;(Recommender,
 * Map&lt;String, Object&gt;) that initializes the QualityMeasure using the attributes defined in
 * the Map object.
 */
public class GridSearch extends RandomSearch {

  /**
   * GridSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated
   * @param scorerClass Scorer class used to evaluate the Recommender
   */
  public GridSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass) {
    super(datamodel, grid, recommenderClass, scorerClass, grid.getDevelopmentSetSize());
  }

  /**
   * GridSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param scorerClass Scorer class used to evaluate the Recommender. This class
   *     must contains a constructor with the signature Scorer.&lt;init&gt;(Recommender,
   *     Map&lt;String, Object&gt;)
   * @param scorerParams Map object containing the scorer parameters names (keys)
   *     and values (value)
   */
  public GridSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer> scorerClass,
      Map<String, Object> scorerParams) {
    super(
        datamodel,
        grid,
        recommenderClass,
        scorerClass,
        scorerParams,
        grid.getDevelopmentSetSize());
  }

  /**
   * GridSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Score.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   */
  public GridSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses) {
    super(datamodel, grid, recommenderClass, scorerClasses, grid.getDevelopmentSetSize());
  }

  /**
   * GridSearch constructor
   *
   * @param datamodel DataModel instance
   * @param grid ParamsGrid instance containing the development set
   * @param recommenderClass Recommender class to be evaluated. This class must contains a
   *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
   *     Object&gt;)
   * @param scorerClasses Scorer classes used to evaluate the Recommender. These
   *     classes must contain a constructor with the signature
   *     Score.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
   * @param scorersParams Maps objects containing the scorer parameters names
   *     (keys) and values (value)
   */
  public GridSearch(
      DataModel datamodel,
      ParamsGrid grid,
      Class<? extends Recommender> recommenderClass,
      Class<? extends Scorer>[] scorerClasses,
      Map<String, Object>[] scorersParams) {
    super(
        datamodel,
        grid,
        recommenderClass,
        scorerClasses,
        scorersParams,
        grid.getDevelopmentSetSize());
  }
}
