package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SVDPlusPlusTest {

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
    void svdPlusPlusTest(){
        SVDPlusPlus svdPlusPlus = new SVDPlusPlus(datamodel,numFactors,numIters,seed);
        svdPlusPlus.fit();

        assertEquals(4.308595462865821, svdPlusPlus.predict(testUserId,testItemId));
        assertEquals(svdPlusPlus.predict(testUserId,testItemId), svdPlusPlus.predict(datamodel.getTestUser(testUserId))[testItemId]);

        assertEquals(numFactors, svdPlusPlus.getNumFactors());
        assertEquals(numIters, svdPlusPlus.getNumIters());
    }
}