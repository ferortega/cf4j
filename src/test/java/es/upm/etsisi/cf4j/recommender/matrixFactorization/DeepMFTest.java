package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeepMFTest {

  private static final int seed = 42;
  private static final int[] numFactors = {2, 3};
  private static final int[] numIters = {1, 1};
  private static final double[] learningRate = {0.01, 0.01};
  private static final double[] regularization = {0.01, 0.01};

  private static final int testUserId = 1;
  private static final int testItemId = 1;

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void deepmfTest() {
    DeepMF deepmf = new DeepMF(datamodel, numFactors, numIters, learningRate, regularization, seed);
    deepmf.fit();

    assertEquals(1.5383572632417226, deepmf.predict(testUserId, testItemId));
  }
}
