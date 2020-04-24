package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.types.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HPFTest {

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
    void hpfTest() {
        HPF hpf = new HPF(datamodel,numFactors,numIters,seed);
        hpf.fit();

        assertEquals(0.07013102405389338, hpf.predict(testUserId,testItemId));
        assertEquals(hpf.predict(testUserId,testItemId), hpf.predict(datamodel.getTestUser(testUserId))[testItemId]);

        assertEquals(hpf.getNumFactors(),numFactors);
        assertEquals(hpf.getNumIters(),numIters);
    }

}