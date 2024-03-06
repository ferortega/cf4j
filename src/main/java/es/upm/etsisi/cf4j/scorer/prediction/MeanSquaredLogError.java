package es.upm.etsisi.cf4j.scorer.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.scorer.Scorer;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class MeanSquaredLogError extends Scorer {

    /**
     * Creates a new quality measure
     *
     * @param recommender Recommender instance for which the quality measure are going to be computed
     */
    public MeanSquaredLogError(Recommender recommender) {
        super(recommender);
    }

    @Override
    protected double getUserScore(TestUser testUser) {
        double[] predictions = recommender.predict(testUser);

        double sum = 0d;
        int count = 0;

        for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
            if (!Double.isNaN(predictions[pos])) {
                double diff = Math.log(1 + testUser.getTestRatingAt(pos)) - Math.log(1 + predictions[pos]);
                sum += diff * diff;
                count++;
            }
        }

        return (count == 0) ? Double.NaN : (sum / count);
    }
}
