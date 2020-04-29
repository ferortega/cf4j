package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrecisionTest {
    private static TestUser user;
    private static final double[] perfectPrediction = new double[]{2.0,3.0,1.0,5.0,4.0,3.0};
    private static final double[] slightlyPrefectPrediction = new double[]{2.0,2.0,1.0,4.0,4.0,3.0};
    private static final double[] allMinimumPrediction = new double[]{1.0,1.0,1.0,1.0,1.0,1.0};
    private static final double[] allMaximumPrediction = new double[]{5.0,5.0,5.0,5.0,5.0,5.0};

    @BeforeAll
    static void initAll() {
        user = new TestUser("101", 101, 101);
        user.addTestRating(0,2.0);
        user.addTestRating(1,3.0);
        user.addTestRating(2,1.0);
        user.addTestRating(3,5.0);
        user.addTestRating(4,4.0);
        user.addTestRating(5,3.0);
    }

    @Test
    void getScore() {
        Precision metric = new Precision(null,6,4.0); //Recommender not needed;

        assertEquals(0.3333333333333333,metric.getScore(user, perfectPrediction));
        assertEquals(0.3333333333333333,metric.getScore(user, slightlyPrefectPrediction));
        assertEquals(0.3333333333333333,metric.getScore(user, allMinimumPrediction));
        assertEquals(0.3333333333333333,metric.getScore(user, allMaximumPrediction));
    }
}