package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class URPTest {

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
  void urpTest() {
    URP urp =
        new URP(datamodel, numFactors, new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, numIters, seed);
    urp.fit();

    assertEquals(1.0, urp.predict(testUserId, testItemId));
    assertEquals(
        urp.predict(testUserId, testItemId),
        urp.predict(datamodel.getTestUser(testUserId))[testItemId]);

    assertEquals(numFactors, urp.getNumFactors());
    assertEquals(numIters, urp.getNumIters());
    assertEquals(1.0, urp.getRatings()[0]);
    assertEquals(2.0, urp.getRatings()[1]);
    assertEquals(3.0, urp.getRatings()[2]);
    assertEquals(4.0, urp.getRatings()[3]);
    assertEquals(5.0, urp.getRatings()[4]);
    assertEquals(0.1, urp.getH());
  }
}
