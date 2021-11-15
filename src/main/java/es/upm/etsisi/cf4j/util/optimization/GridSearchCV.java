package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to performs a grid search over a Recommender instance. The Recommender class used
 * during the grid search must contains a constructor with the signature
 * Recommender::&lt;init&gt;(DataModel, Map&lt;String, Object&gt;) that initializes the Recommender
 * using the attributes defined in the Map object. The parameters used in the search process, i.e.
 * the development set, must be defined in a ParamsGrid instance. The random search is executed in
 * such a way that it minimizes (by default) or maximizes a QualityMeasure by splitting the train
 * set of the dataset in validations sets using cross validation. If the QualityMeasure requires
 * parameters to work, it must contains a constructor with the signature
 * QualityMeasure::&lt;init&gt;(Recommender, Map&lt;String, Object&gt;) that initializes the
 * QualityMeasure using the attributes defined in the Map object.
 */
public class GridSearchCV extends RandomSearchCV {

    /**
     * GridSearchCV constructor
     *
     * @param datamodel DataModel instance
     * @param grid ParamsGrid instance containing the development set
     * @param recommenderClass Recommender class to be evaluated. This class must contains a
     *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
     *     Object&gt;)
     * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
     *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
     *     Map&lt;String, Object&gt;)
     * @param cv Number of folds for the cross validation
     */
    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, cv, grid.getDevelopmentSetSize());
    }

    /**
     * GridSearchCV constructor
     *
     * @param datamodel DataModel instance
     * @param grid ParamsGrid instance containing the development set
     * @param recommenderClass Recommender class to be evaluated. This class must contains a
     *     constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String,
     *     Object&gt;)
     * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class
     *     must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender,
     *     Map&lt;String, Object&gt;)
     * @param cv Number of folds for the cross validation
     * @param seed Random seed for random numbers generation
     */
    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv, long seed) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, cv, grid.getDevelopmentSetSize(), seed);
    }

    /**
     * GridSearchCV constructor
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
     * @param cv Number of folds for the cross validation
     */
    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, grid.getDevelopmentSetSize());
    }

    /**
     * GridSearchCV constructor
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
     * @param cv Number of folds for the cross validation
     * @param seed Random seed for random numbers generation
     */
    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv, long seed) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, grid.getDevelopmentSetSize(), seed);
    }
}
