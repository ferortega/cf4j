package es.upm.etsisi.cf4j.metrics.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.metrics.Score;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class R2Score extends Score {

    /**
     * Creates a new quality measure
     *
     * @param recommender Recommender instance for which the quality measure are going to be computed
     */
    public R2Score(Recommender recommender) {
        super(recommender);
    }

    @Override
    protected double getUserScore(TestUser testUser) {
        double[] predictions = recommender.predict(testUser);

        double num = 0.0;
        double den = 0.0;

        int count = 0;

        for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
            if (!Double.isNaN(predictions[pos])) {
                num += Math.pow(testUser.getTestRatingAt(pos) - predictions[pos], 2);
                den += Math.pow(testUser.getTestRatingAt(pos) - testUser.getTestRatingAverage(), 2);
                count++;
            }
        }

        if (count < 2 || den == 0) {
            return Double.NaN;
        } else {
            return 1 - num / den;
        }
    }
}
