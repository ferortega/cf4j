package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BiasedMFTest {

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
  void biasedmfTest() {
    BiasedMF biasedmf = new BiasedMF(datamodel, numFactors, numIters, seed);
    biasedmf.fit();

    assertEquals(2.6843560265981696, biasedmf.predict(testUserId, testItemId));
    assertEquals(
        biasedmf.predict(testUserId, testItemId),
        biasedmf.predict(datamodel.getTestUser(testUserId))[testItemId]);

    assertEquals(numFactors, biasedmf.getNumFactors());
    assertEquals(numIters, biasedmf.getNumIters());

    assertEquals(0.01, biasedmf.getGamma());
    assertEquals(0.1, biasedmf.getLambda());
  }
}
