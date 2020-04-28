package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosineTest {

    private static DataModel datamodel;

    @BeforeAll
    static void initAll() {
        datamodel = new DataModel(new MockDataSet());
    }

    @Test
    void similarity() {
        Cosine sim = new Cosine();
        sim.setDatamodel(datamodel);
        sim.beforeRun();
        assertEquals(0.9838699100999074,sim.similarity(datamodel.getUser(0),datamodel.getUser(1)));
        assertEquals(0.9899494936611664,sim.similarity(datamodel.getUser(0),datamodel.getUser(3)));
        assertEquals(0.9486832980505138,sim.similarity(datamodel.getUser(1),datamodel.getUser(3)));
        assertTrue(sim.similarity(datamodel.getUser(0),datamodel.getUser(1))<sim.similarity(datamodel.getUser(0),datamodel.getUser(3)));
        assertTrue(sim.similarity(datamodel.getUser(0),datamodel.getUser(1))>sim.similarity(datamodel.getUser(1),datamodel.getUser(3)));
        assertTrue(sim.similarity(datamodel.getUser(1),datamodel.getUser(3))< sim.similarity(datamodel.getUser(0),datamodel.getUser(3)));
        sim.afterRun();
    }
}