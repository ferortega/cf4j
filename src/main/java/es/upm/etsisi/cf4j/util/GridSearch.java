package es.upm.etsisi.cf4j.util;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.apache.commons.math3.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Performs a grid search.
 * @TODO complete
 */
public class GridSearch {

    /**
     * DataModel instance
     */
    private DataModel datamodel;

    /**
     * ParamsGrid instance containing the development set
     */
    private ParamsGrid grid;

    /**
     * Recommender class to be evaluated
     */
    private Class<? extends Recommender> recommenderClass;

    /**
     * QualityMeasure class used to evaluate the Recommender
     */
    private Class<? extends QualityMeasure> qualityMeasureClass;

    /**
     * Map object containing the quality measure parameters names (keys) and values (value)
     */
    private Map<String, Object> qualityMeasureParams;

    /**
     * List to store grid search results
     */
    private List<Pair<String, Double>> results;

    /**
     * GridSearch constructor
     * @param datamodel DataModel instance
     * @param grid ParamsGrid instance containing the development set
     * @param recommenderClass Recommender class to be evaluated
     * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender
     */
    public GridSearch(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, null);
    }

    /**
     * GridSearch constructor
     * @param datamodel DataModel instance
     * @param grid ParamsGrid instance containing the development set
     * @param recommenderClass Recommender class to be evaluated. This class must contains a constructor with the signature Recommender.&lt;init&gt;(DataModel, Map&lt;String, Object&gt;)
     * @param qualityMeasureClass QualityMeasure class used to evaluate the Recommender. This class must contains a constricutor with the signautre QualityMeasure.&lt;init&gt;(Recommender, Map&lt;String, Object&gt;)
     * @param qualityMeasureParams Map object containing the quality measure parameters names (keys) and values (value)
     */
    public GridSearch(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams) {
        this.datamodel = datamodel;
        this.grid = grid;
        this.recommenderClass = recommenderClass;
        this.qualityMeasureClass = qualityMeasureClass;
        this.qualityMeasureParams = qualityMeasureParams;

        this.results = new ArrayList<>();
    }

    /**
     * Performs grid search
     */
    public void fit() {

        Iterator<Map<String, Object>> iter = grid.getDevelopmentSetIterator();

        while(iter.hasNext()) {
            Map<String, Object> params = iter.next();
            
            Recommender recommender = null;

            try {
                recommender = this.recommenderClass.getConstructor(DataModel.class, Map.class).newInstance(this.datamodel, params);
            } catch (NoSuchMethodException e) {
                System.err.println(this.recommenderClass.getCanonicalName() + " does not seem to contain a constructor to be used in a grid search.");
                e.printStackTrace();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                System.err.println("A problem has occurred during the " + this.recommenderClass.getCanonicalName() + " instantiation.");
                e.printStackTrace();
            }

            recommender.fit();


            QualityMeasure qm = null;

            try {
                if (this.qualityMeasureParams == null || this.qualityMeasureParams.isEmpty()) {
                    qm = this.qualityMeasureClass.getConstructor(Recommender.class).newInstance(recommender);
                } else {
                    qm = this.qualityMeasureClass.getConstructor(Recommender.class, Map.class).newInstance(recommender, this.qualityMeasureParams);
                }
            } catch (NoSuchMethodException e) {
                System.err.println(this.qualityMeasureClass.getCanonicalName() + " does not seem to contain a constructor to be used in a grid search.");
                e.printStackTrace();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                System.err.println("A problem has occurred during the " + this.qualityMeasureClass.getCanonicalName() + " instantiation.");
                e.printStackTrace();
            }

            double score = qm.getScore();

            results.add(new Pair<>(params.toString(), score));
        }
    }

    /**
     * Prints the results of the grid search. By default, the quality measure is better the lower its value.
     */
    public void printResults() {
        this.printResults("0.000000", true);
    }

    /**
     * Prints the results of the grid search. By default, the quality measure is better the lower its value.
     * @param numberFormat Number format for the quality measure values
     */
    public void printResults(String numberFormat) {
        this.printResults(numberFormat, true);
    }

    /**
     * Prints the results of the grid search
     * @param lowerIsBetter True if the quality measure is better the lower its value. False otherwise.
     */
    public void printResults(boolean lowerIsBetter) {
        this.printResults("0.000000", lowerIsBetter);
    }

    /**
     * Prints the results of the grid search
     * @param numberFormat Number format for the quality measure values
     * @param lowerIsBetter True if the quality measure is better the lower its value. False otherwise.
     */
    public void printResults(String numberFormat, boolean lowerIsBetter) {

        // Sort results
        Comparator<Pair<String, Double>> comparator = Comparator.comparing(Pair::getValue, (v1, v2) -> {
            if (Double.isNaN(v1) || Double.isNaN(v2)) {
                return 1;
            } else if (v1 > v2) {
                return 1;
            } else if (v2 > v1) {
                return -1;
            } else {
                return 0;
            }
        });

        if (!lowerIsBetter) {
            comparator = comparator.reversed();
        }

        this.results.sort(comparator);

        // Prepare printable results
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat(numberFormat);

        sb.append("Tuning parameters for ").append(recommenderClass.getSimpleName()).append(" recommender:\n\n");

        sb.append("Best parameters set found on development set:\n\n").append(this.results.get(0).getKey()).append("\n\n");

        sb.append(this.qualityMeasureClass.getSimpleName());
        if (this.qualityMeasureParams != null) {
            sb.append(this.qualityMeasureParams.toString());
        }
        sb.append(" scores on development set:\n\n");

        for (Pair<String, Double> result : this.results) {

            String value = "";

            if (!Double.isNaN(result.getValue())) {
                value = df.format(result.getValue());
            } else {
                value = "NaN";
                for (int i = 0; i < numberFormat.length() - "NaN".length(); i++) {
                    value += " ";
                }
            }

            sb.append(value).append(" for ").append(result.getKey()).append("\n");
        }

        // Print results
        System.out.println("\n" + sb.toString());
    }
}
