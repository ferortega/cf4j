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

    private final int cv;

    private final long seed;

    private final int numIters;

    private final Map<String, double[]> results;

    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv, double coverage) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, cv, (int) (coverage * grid.getDevelopmentSetSize()), System.currentTimeMillis());
    }

    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv, double coverage, long seed) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, null, cv, (int) (coverage * grid.getDevelopmentSetSize()), seed);
    }

    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv, double coverage) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, (int) (coverage * grid.getDevelopmentSetSize()), System.currentTimeMillis());
    }

    public RandomSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv, double coverage, long seed) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, (int) (coverage * grid.getDevelopmentSetSize()), seed);
    }

    public RandomSearchCV(
            DataModel datamodel,
            ParamsGrid grid,
            Class<? extends Recommender> recommenderClass,
            Class<? extends QualityMeasure> qualityMeasureClass,
            int cv,
            int numIters) {
        this(datamodel, grid, recommenderClass, qualityMeasureClass, cv, numIters, System.currentTimeMillis());
    }

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
