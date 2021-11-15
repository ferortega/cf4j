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


    double[] expectedGamma = {2.179465296606575, 0.22053470339342526};
    Util.checkDoubleArray(expectedGamma, bnmf.getGamma(1));
    double[] expectedEpsilonPlus = {1.020495014935493, 1.979504985064507};
    Util.checkDoubleArray(expectedEpsilonPlus, bnmf.getEpsilonPlus(1));
    double[] expectedEpsilonMinus = {1.0681052697863767, 7.931894730213624};
    Util.checkDoubleArray(expectedEpsilonMinus, bnmf.getEpsilonMinus(1));
    double[] expectedUserFactors = {0.9081105402527395, 0.09188945974726052};
    Util.checkDoubleArray(expectedUserFactors, bnmf.getUserFactors(1));
    double[] expectedItemFactors = {0.48860235364345367, 0.19972002360203053};
    Util.checkDoubleArray(expectedItemFactors, bnmf.getItemFactors(1));
  }
}
