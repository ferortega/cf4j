package es.upm.etsisi.cf4j.metrics.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.metrics.Score;
import es.upm.etsisi.cf4j.recommender.Recommender;

import java.util.Map;

public class PerfectPredictions extends Score {

    /** Threshold value to measure if a prediction is perfect or not */
    private final double threshold;

    /**
     * Constructor from a Map object with the quality measure parameters. Map object must contains the
     * following keys:
     *
     * <ul>
     *   <li><b>threshold</b>: double value that defines the allowed threshold to measure if a
     *       prediction is perfect or not.
     * </ul>
     *
     * @param recommender Recommender instance for which the Perfect score are going to be computed
     * @param params Quality measure's parameters
     */
    public PerfectPredictions(Recommender recommender, Map<String, Object> params) {
        this(recommender, (double) params.get("threshold"));
    }

    /**
     * Constructor of the class which basically calls the father's one
     *
     * @param recommender Recommender instance for which the perfect score are going to be computed
     * @param threshold Threshold value to measure if a prediction is perfect or not
     */
    public PerfectPredictions(Recommender recommender, double threshold) {
        super(recommender);
        this.threshold = threshold;
    }

    @Override
    protected double getUserScore(TestUser testUser) {
        double[] predictions = recommender.predict(testUser);

        int hits = 0;
        int total = 0;

        for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
            if (!Double.isNaN(predictions[pos])) {
                double diff = Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
                if (diff <= threshold) {
                    hits++;
                }
                total++;
            }
        }

        return (total == 0) ? Double.NaN : (double) hits / (double) total;
    }
}
