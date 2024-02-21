package es.upm.etsisi.cf4j.metrics.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.metrics.Score;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class MeanAbsoluteError extends Score {

    /**
     * Creates a new quality measure
     *
     * @param recommender Recommender instance for which the quality measure are going to be computed
     */
    public MeanAbsoluteError(Recommender recommender) {
        super(recommender);
    }

    @Override
    protected double getUserScore(TestUser testUser) {
        double[] predictions = recommender.predict(testUser);

        double sum = 0d;
        int count = 0;

        for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
            if (!Double.isNaN(predictions[pos])) {
                sum += Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
                count++;
            }
        }

        return (count == 0) ? Double.NaN : (sum / count);
    }
}
