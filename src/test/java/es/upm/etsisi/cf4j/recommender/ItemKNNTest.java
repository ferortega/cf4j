package es.upm.etsisi.cf4j.recommender;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.types.MockDataSet;
import es.upm.etsisi.cf4j.recommender.knn.ItemKNN;
import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemKNNTest {

    final private static int numberOfNeighbors = 2;

    final private static int testUserId = 1;
    final private static int testItemId = 1;

    private static DataModel datamodel;

    @BeforeAll
    static void initAll() {
        datamodel = new DataModel(new MockDataSet());
    }

    @Test
    void itemAdjustedCosineKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new AdjustedCosine(),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //Mean
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new AdjustedCosine(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemCorrelationKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Correlation(),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Correlation(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemCorrelationConstrainedKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new CorrelationConstrained(0.5),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new CorrelationConstrained(0.5),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemJaccardKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Jaccard(),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Jaccard(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemCosineKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Cosine(),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Cosine(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemJMSDKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new JMSD(),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new JMSD(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemMSDKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new MSD(),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new MSD(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemPIPKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new PIP(),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new PIP(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemSingularitiesKNNTest() {

        double[] relevantRatings = {3, 4, 5};
        double[] notRelevantRatings = {1, 2};

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Singularities(relevantRatings, notRelevantRatings),ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new Singularities(relevantRatings, notRelevantRatings),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

    @Test
    void itemSpearmanRankKNNTest() {

        //WEIGHTED_MEAN
        ItemKNN iKNN = new ItemKNN(datamodel,numberOfNeighbors,new SpearmanRank(), ItemKNN.AggregationApproach.WEIGHTED_MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

        //MEAN
        iKNN = new ItemKNN(datamodel,numberOfNeighbors,new SpearmanRank(),ItemKNN.AggregationApproach.MEAN);
        iKNN.fit();
        assertEquals(iKNN.predict(testUserId,testItemId),2.4);
        assertEquals(iKNN.predict(datamodel.getTestUser(testUserId))[testItemId],iKNN.predict(testUserId,testItemId));

    }

}
