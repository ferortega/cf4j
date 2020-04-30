package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SVDPlusPlusTest {

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
  void svdPlusPlusTest() {
    SVDPlusPlus svdPlusPlus = new SVDPlusPlus(datamodel, numFactors, numIters, seed);
    svdPlusPlus.fit();

    assertEquals(4.382725933596731, svdPlusPlus.predict(testUserId, testItemId));
    assertEquals(
        svdPlusPlus.predict(testUserId, testItemId),
        svdPlusPlus.predict(datamodel.getTestUser(testUserId))[testItemId]);

    assertEquals(numFactors, svdPlusPlus.getNumFactors());
    assertEquals(numIters, svdPlusPlus.getNumIters());

    assertEquals(0.001, svdPlusPlus.getGamma());
    assertEquals(0.01, svdPlusPlus.getLambda());
  }
}
