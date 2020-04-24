package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BNMFTest {

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
    void bnmfTest() {
        BNMF bnmf = new BNMF(datamodel,numFactors,numIters,0.2,1,seed);
        bnmf.fit();

        assertEquals(3.0, bnmf.predict(testUserId,testItemId));
        assertEquals(bnmf.predict(testUserId,testItemId), bnmf.predict(datamodel.getTestUser(testUserId))[testItemId]);

        assertEquals(bnmf.getNumFactors(),numFactors);
        assertEquals(bnmf.getNumIters(),numIters);
    }

}