package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CLiTest {

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
  void climfTest() {
    CLiMF climf = new CLiMF(datamodel, numFactors, numIters, seed);
    climf.fit();

    assertEquals(0.8124318087775715, climf.predict(testUserId, testItemId));
    assertEquals(
        climf.predict(testUserId, testItemId),
        climf.predict(datamodel.getTestUser(testUserId))[testItemId]);

    assertEquals(numFactors, climf.getNumFactors());
    assertEquals(numIters, climf.getNumIters());

    assertEquals(1.0E-5, climf.getGamma());
    assertEquals(1.0E-4, climf.getLambda());
  }
}
