package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeMFTest {

  private static final int seed = 43;
  private static final int numFactors = 2;
  private static final int numIters = 1;
  private static final double learningRate = 0.01;
  private static final double regularization = 0.08;
  private static final double[] ratings = {1, 2, 3, 4, 5};

  private static final int testUserId = 1;
  private static final int testItemId = 1;

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void bemfTest() {
    BeMF bemf = new BeMF(datamodel, numFactors, numIters, learningRate, regularization, ratings, seed);
    bemf.fit();

    assertEquals(3.0, bemf.predict(testUserId, testItemId));
    assertEquals(0.22521728438775332, bemf.predictProba(testUserId, testItemId));

    assertEquals(numFactors, bemf.getNumFactors());
    assertEquals(numIters, bemf.getNumIters());
    assertEquals(learningRate, bemf.getLearningRate());
    assertEquals(regularization, bemf.getRegularization());
    assertEquals(ratings, bemf.getRatings());
  }
}
