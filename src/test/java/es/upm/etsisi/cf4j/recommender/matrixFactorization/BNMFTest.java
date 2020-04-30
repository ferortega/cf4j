package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BNMFTest {

  private static final int seed = 69;
  private static final int numFactors = 2;
  private static final int numIters = 1;

  private static final int testUserId = 1;
  private static final int testItemId = 1;

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void bnmfTest() {
    BNMF bnmf = new BNMF(datamodel, numFactors, numIters, 0.2, 1, seed);
    bnmf.fit();

    assertEquals(3.0, bnmf.predict(testUserId, testItemId));
    assertEquals(
        bnmf.predict(testUserId, testItemId),
        bnmf.predict(datamodel.getTestUser(testUserId))[testItemId]);

    assertEquals(numFactors, bnmf.getNumFactors());
    assertEquals(numIters, bnmf.getNumIters());

    assertEquals(0.2, bnmf.getAlpha());
    assertEquals(1, bnmf.getBeta());
    assertEquals(4.0, bnmf.getR());
  }
}
