package es.upm.etsisi.cf4j.recommender.knn;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.types.MockDataSet;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.UserSimilarityMetricMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserKNNTest {

    final private static int numberOfNeighbors = 2;

    final private static int testUserId = 1;
    final private static int testItemId = 1;

    private static DataModel datamodel;

    @BeforeAll
    static void initAll() {
        datamodel = new DataModel(new MockDataSet());
    }

    @Test
    void userKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new UserSimilarityMetricMock(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(2.249999999999999, uKNN.predict(testUserId,testItemId));
        assertEquals(uKNN.predict(testUserId,testItemId), uKNN.predict(datamodel.getTestUser(testUserId))[testItemId]);

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new UserSimilarityMetricMock(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(3.1, uKNN.predict(testUserId,testItemId));
        assertEquals(uKNN.predict(testUserId,testItemId), uKNN.predict(datamodel.getTestUser(testUserId))[testItemId]);

        //Mean
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new UserSimilarityMetricMock(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(3.1, uKNN.predict(testUserId,testItemId));
        assertEquals(uKNN.predict(testUserId,testItemId), uKNN.predict(datamodel.getTestUser(testUserId))[testItemId]);

    }

}
