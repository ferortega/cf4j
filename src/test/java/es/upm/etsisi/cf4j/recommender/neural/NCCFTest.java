package es.upm.etsisi.cf4j.recommender.neural;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NCCFTest {

    private static final int seed = 69;
    private static final int numEpochs = 1;

    private static DataModel datamodel;

    @BeforeAll
    static void initAll() {
        datamodel = new DataModel(new MockDataSet());
    }

    @Test
    void biasedmfTest() {
        NCCF nccf = new NCCF(datamodel, numEpochs, seed);
        nccf.fit();

        assertEquals(0.25135916471481323, nccf.predict(0, 0));
        assertEquals(numEpochs, nccf.getNumEpochs());
    }
}
