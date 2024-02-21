package es.upm.etsisi.cf4j.metrics.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.metrics.Score;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class PredictionCoverage extends Score {

    /**
     * Creates a new quality measure
     *
     * @param recommender Recommender instance for which the quality measure are going to be computed
     */
    public PredictionCoverage(Recommender recommender) {
        super(recommender);
    }

    @Override
    protected double getUserScore(TestUser testUser) {
        double[] predictions = recommender.predict(testUser);

        int count = 0;

        for (double prediction : predictions) {
            if (!Double.isNaN(prediction)) {
                count++;
            }
        }

        return (double) count / (double) testUser.getNumberOfTestRatings();
    }
}
