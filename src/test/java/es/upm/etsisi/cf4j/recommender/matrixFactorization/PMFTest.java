package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PMFTest {

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
  void pmfTest() {
    PMF pmf = new PMF(datamodel, numFactors, numIters, seed);
    pmf.fit();

    assertEquals(0.3587833412033989, pmf.predict(testUserId, testItemId));
    assertEquals(
        pmf.predict(testUserId, testItemId),
        pmf.predict(datamodel.getTestUser(testUserId))[testItemId]);

    assertEquals(numFactors, pmf.getNumFactors());
    assertEquals(numIters, pmf.getNumIters());

    assertEquals(0.01, pmf.getGamma());
    assertEquals(0.05, pmf.getLambda());
  }
}
