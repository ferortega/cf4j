package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.types.MockDataSet;
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

        assertEquals(4.562892956151807, svdPlusPlus.predict(testUserId,testItemId));
        assertEquals(svdPlusPlus.predict(testUserId,testItemId), svdPlusPlus.predict(datamodel.getTestUser(testUserId))[testItemId]);

        //assertEquals(svdPlusPlus.getNumFactors(),numFactors);
        //assertEquals(svdPlusPlus.getNumIters(),numIters);
    }
}