package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BiasedMFTest {

    final private static int seed = 69;
    final private static int numFactors = 2;
    final private static int numIters = 1;

    final private static int testUserId = 1;
    final private static int testItemId = 1;

    private static DataModel datamodel;

    @BeforeAll
    static void initAll() {
        datamodel = new DataModel(new MockDataSet());
    }

    @Test
    void biasedmfTest() {
        BiasedMF biasedmf = new BiasedMF(datamodel,numFactors,numIters,seed);
        biasedmf.fit();

        assertEquals(2.5873137380617095, biasedmf.predict(testUserId,testItemId));
        assertEquals(biasedmf.predict(testUserId,testItemId), biasedmf.predict(datamodel.getTestUser(testUserId))[testItemId]);

        assertEquals(biasedmf.getNumFactors(),numFactors);
        assertEquals(biasedmf.getNumIters(),numIters);
    }

}