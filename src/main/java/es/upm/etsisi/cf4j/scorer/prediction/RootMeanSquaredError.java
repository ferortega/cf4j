package es.upm.etsisi.cf4j.scorer.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class RootMeanSquaredError extends MeanSquaredError {

    /**
     * Creates a new quality measure
     *
     * @param recommender Recommender instance for which the quality measure are going to be computed
     */
    public RootMeanSquaredError(Recommender recommender) {
        super(recommender);
    }

    @Override
    protected double getUserScore(TestUser testUser) {
        double[] predictions = recommender.predict(testUser);

        double sum = 0d;
        int count = 0;

        for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
            if (!Double.isNaN(predictions[pos])) {
                sum += Math.pow(predictions[pos] - testUser.getTestRatingAt(pos), 2);
                count++;
            }
        }

        return Math.sqrt(super.getUserScore(testUser));
    }
}
