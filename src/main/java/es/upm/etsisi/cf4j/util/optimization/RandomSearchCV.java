package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.*;
import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;
import org.apache.commons.math3.util.Pair;

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

    /** Random seed for random numbers generation **/
    private final long seed;

    /** Number of samples of the development set to be evaluated */
    private final int numIters;

    /** Map to store grid search results */
    private final Map<String, double[]> results;

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
    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv, double coverage) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, cv, (int) (coverage * grid.getDevelopmentSetSize()), System.currentTimeMillis());
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
    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv, double coverage, long seed) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, null, cv, (int) (coverage * grid.getDevelopmentSetSize()), seed);
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
    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv, double coverage) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, (int) (coverage * grid.getDevelopmentSetSize()), System.currentTimeMillis());
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
    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv, double coverage, long seed) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, (int) (coverage * grid.getDevelopmentSetSize()), seed);
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
        this(datamodel, grid, recommenderClass, qualityMeasureClass, cv, numIters, System.currentTimeMillis());
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
        this(datamodel, grid, recommenderClass, qualityMeasureClass, null, cv, numIters, seed);
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
      this(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, numIters, System.currentTimeMillis());
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
     * @param seed Random seed for random numbers generation
     */
    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv, int numIters, long seed) {
        this.datamodel = datamodel;
        this.grid = grid;
        this.cv = cv;
        this.recommenderClass = recommenderClass;
        this.qualityMeasureClass = qualityMeasureClass;
        this.qualityMeasureParams = qualityMeasureParams;
        this.numIters = numIters;
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

            List <DataSetEntry> testRatings = ratings.subList(from, to);

            List <DataSetEntry> trainRatings = new ArrayList<>(ratings);
            trainRatings.removeAll(testRatings);

            DataSet validationDataset = new ManualDataSet(trainRatings, testRatings);
            DataModel validationDatamodel = new DataModel(validationDataset);

            RandomSearch randomSearch = new RandomSearch(validationDatamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, numIters, seed);
            randomSearch.fit();

            List <Pair<String, Double>> randomSearchResults = randomSearch.getResults();
            for (Pair<String, Double> result : randomSearchResults) {
                double[] errors = (fold == 0) ? new double[cv] : this.results.get(result.getFirst());
                errors[fold] = result.getSecond();
                this.results.put(result.getFirst(), errors);
            }
        }
    }

    /**
     * Prints the results of the random search. By default, the quality measure is better the lower its
     * value.
     */
    public void printResults() {
        this.printResults("0.000000", this.results.size(), true);
    }

    /**
     * Prints the results of the random search
     *
     * @param topN Number of entries of the development set to be shown as the top ones
     */
    public void printResults(int topN) {
        this.printResults("0.000000", topN, true);
    }

    /**
     * Prints the results of the random search. By default, the quality measure is better the lower its
     * value.
     *
     * @param numberFormat Number format for the quality measure values
     */
    public void printResults(String numberFormat) {
        this.printResults(numberFormat, this.results.size(), true);
    }

    /**
     * Prints the results of the random search
     *
     * @param lowerIsBetter True if the quality measure is better the lower its value. False
     *     otherwise.
     */
    public void printResults(boolean lowerIsBetter) {
        this.printResults("0.000000", this.results.size(), lowerIsBetter);
    }

    /**
     * Prints the results of the random search
     *
     * @param numberFormat Number format for the quality measure values
     * @param topN Number of entries of the development set to be shown as the top ones
     */
    public void printResults(String numberFormat, int topN) {
        this.printResults(numberFormat, topN, true);
    }

    /**
     * Prints the results of the random search
     *
     * @param topN Number of entries of the development set to be shown as the top ones
     * @param lowerIsBetter True if the quality measure is better the lower its value. False
     *     otherwise.
     */
    public void printResults(int topN, boolean lowerIsBetter) {
        this.printResults("0.000000", topN, lowerIsBetter);
    }

    /**
     * Prints the results of the random search
     *
     * @param numberFormat Number format for the quality measure values
     * @param lowerIsBetter True if the quality measure is better the lower its value. False
     *     otherwise.
     */
    public void printResults(String numberFormat, boolean lowerIsBetter) {
        this.printResults(numberFormat, this.results.size(), lowerIsBetter);
    }

    /**
     * Prints the results of the random search
     *
     * @param numberFormat Number format for the quality measure values
     * @param topN Number of entries of the development set to be shown as the top ones
     * @param lowerIsBetter True if the quality measure is better the lower its value. False
     *     otherwise.
     */
    public void printResults(String numberFormat, int topN, boolean lowerIsBetter) {

        List<Pair<String, Double>> cvResults = new ArrayList<>();
        for (String params : this.results.keySet()) {
            double[] errors = this.results.get(params);
            double averageError = Maths.arrayAverage(errors);
            cvResults.add(new Pair<>(params, averageError));
        }

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
            Pair<String, Double> result = cvResults.get(i);

            StringBuilder value = new StringBuilder();

            if (!Double.isNaN(result.getValue())) {
                value = new StringBuilder(df.format(result.getValue()));
            } else {
                value = new StringBuilder("NaN");
                for (int s = 0; s < numberFormat.length() - "NaN".length(); s++) {
                    value.append(" ");
                }
            }

            sb.append(value)
                    .append(" ").append(Arrays.toString(this.results.get(result.getKey())))
                    .append(" for ").append(result.getKey()).append("\n");
        }

        // Print results
        System.out.println("\n" + sb.toString());
    }
}
