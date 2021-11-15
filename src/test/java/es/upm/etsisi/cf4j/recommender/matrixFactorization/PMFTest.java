package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PMFTest {

  private static final int seed = 69;
  private static final int numFactors = 2;
  private static final int numIterations = 1;

  private static final int testUserId = 1;
  private static final int testItemId = 1;

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void pmfTest() {
    PMF pmf = new PMF(datamodel, numFactors, numIterations, seed);
    pmf.fit();

    assertEquals(0.3587833412033989, pmf.predict(testUserId, testItemId));
    assertEquals(
        pmf.predict(testUserId, testItemId),
        pmf.predict(datamodel.getTestUser(testUserId))[testItemId]);

    assertEquals(numFactors, pmf.getNumFactors());
    assertEquals(numIterations, pmf.getNumIters());

    assertEquals(0.01, pmf.getGamma());
    assertEquals(0.05, pmf.getLambda());

    double[] expectedUserFactors = {0.97463306383183, 0.04776056491364108};
    Util.checkDoubleArray(expectedUserFactors, pmf.getUserFactors(1));
    double[] expectedItemFactors = {0.3947872919102645, -0.5441603699614187};
    Util.checkDoubleArray(expectedItemFactors, pmf.getItemFactors(1));

  }
}
