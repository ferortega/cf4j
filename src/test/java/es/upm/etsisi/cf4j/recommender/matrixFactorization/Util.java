package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Util {
    static void checkDoubleArray(double[] actualValues, double[] expectedValues) {
        for (int i = 0; i < actualValues.length; i++) {
            assertEquals(actualValues[i], expectedValues[i]);
        }
    }

}
