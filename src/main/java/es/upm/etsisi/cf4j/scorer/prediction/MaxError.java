package es.upm.etsisi.cf4j.scorer.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.scorer.Scorer;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class MaxError extends Scorer {

    /**
     * Creates a new quality measure
     *
     * @param recommender Recommender instance for which the quality measure are going to be computed
     */
    public MaxError(Recommender recommender) {
        super(recommender);
    }

    @Override
    protected double getUserScore(TestUser testUser) {
        double[] predictions = recommender.predict(testUser);

        double max = Double.NEGATIVE_INFINITY;

        for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
            if (!Double.isNaN(predictions[pos])) {
                double error = Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
                if (error > max) {
                    max = error;
                }
            }
        }

        return (Double.isInfinite(max)) ? Double.NaN : max;
    }
}
