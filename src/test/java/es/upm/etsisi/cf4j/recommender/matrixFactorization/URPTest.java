package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.types.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class URPTest {

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
    void urpTest(){
        URP urp = new URP(datamodel,numFactors,new double[]{0.1,0.2},numIters,seed);
        urp.fit();

        assertEquals(0.1, urp.predict(testUserId,testItemId));
        assertEquals(urp.predict(testUserId,testItemId), urp.predict(datamodel.getTestUser(testUserId))[testItemId]);

        assertEquals(urp.getNumFactors(),numFactors);
        assertEquals(urp.getNumIters(),numIters);
        assertEquals(urp.getRatings()[0],0.1);
    }
}