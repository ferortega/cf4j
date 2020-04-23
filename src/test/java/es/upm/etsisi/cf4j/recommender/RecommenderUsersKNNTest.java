package es.upm.etsisi.cf4j.recommender;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.knn.UserKNN;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommenderUsersKNNTest {

    final private static int numberOfNeighbors = 2;

    final private static int testUserId = 1;
    final private static int testItemId = 1;

    private static DataModel datamodel;

    @BeforeAll
    static void initAll() {
        datamodel = new DataModel(new MockDataSet());
    }

    @Test
    void userAdjustedCosineKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new AdjustedCosine(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.4499999999999997);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new AdjustedCosine(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //Mean
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new AdjustedCosine(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userCJMSDKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new CJMSD(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),2.983457943925233);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new CJMSD(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.8334579439252336);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new CJMSD(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.7);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userCorrelationKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new Correlation(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.4499999999999997);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Correlation(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Correlation(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userCorrelationConstrainedKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new CorrelationConstrained(0.5),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.4499999999999997);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new CorrelationConstrained(0.5),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new CorrelationConstrained(0.5),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userJaccardKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new Jaccard(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.4499999999999997);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Jaccard(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Jaccard(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userCosineKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new Cosine(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),2.249999999999999);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Cosine(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.1);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Cosine(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.1);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userJMSDKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new JMSD(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.4499999999999997);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new JMSD(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new JMSD(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userMSDKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new MSD(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),2.249999999999999);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new MSD(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.1);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new MSD(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.1);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userPIPKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new PIP(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),2.9145948398733625);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new PIP(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.764594839873363);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new PIP(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.7);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userSingularitiesKNNTest() {

        double[] relevantRatings = {3, 4, 5};
        double[] notRelevantRatings = {1, 2};

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new Singularities(relevantRatings, notRelevantRatings),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),2.249999999999999);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Singularities(relevantRatings, notRelevantRatings),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.1);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new Singularities(relevantRatings, notRelevantRatings),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.1);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

    @Test
    void userSpearmanRankKNNTest() {

        //DEVIATION_FROM_MEAN
        UserKNN uKNN = new UserKNN(datamodel,numberOfNeighbors,new SpearmanRank(),UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),3.4499999999999997);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //WEIGHTED_MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new SpearmanRank(),UserKNN.AggregationApproach.WEIGHTED_MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

        //MEAN
        uKNN = new UserKNN(datamodel,numberOfNeighbors,new SpearmanRank(),UserKNN.AggregationApproach.MEAN);
        uKNN.fit();
        assertEquals(uKNN.predict(testUserId,testItemId),4.3);
        assertEquals(uKNN.predict(datamodel.getTestUser(testUserId))[testItemId],uKNN.predict(testUserId,testItemId));

    }

}
