package es.upm.etsisi.cf4j.recommender;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.TrainTestFilesDataSet;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.AdjustedCosine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommenderKNNTest {

    final private static String trainingFilename = "src/test/resources/trainingPartDataset.data";
    final private static String testFilename = "src/test/resources/testPartDataset.data";
    final private static int seed = 69;
    final private static int numFactors = 2;
    final private static int numIters = 2;

    final private static int testUserId = 1;
    final private static int testItemId = 1;

    private static DataModel datamodel;

    @BeforeAll
    static void initAll() throws IOException {
        datamodel = new DataModel(new TrainTestFilesDataSet(trainingFilename, testFilename));

    }

    @Test
    void userKNNTest() {
        //
    }

    @Test
    void itemKNNTest() {
        //
    }
}
