package es.upm.etsisi.cf4j.recommender;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.TrainTestFilesDataSet;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RecommenderMFTest {

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
    void biasedmfTest() {
        BiasedMF biasedmf = new BiasedMF(datamodel,numFactors,numIters,seed);
        biasedmf.fit();

        assertEquals(biasedmf.predict(testUserId,testItemId),2.8580470651823022);
        assertEquals(biasedmf.predict(datamodel.getTestUser(testUserId))[testItemId],biasedmf.predict(testUserId,testItemId));

        assertEquals(biasedmf.getNumFactors(),numFactors);
        assertEquals(biasedmf.getNumIters(),numIters);
    }

    @Test
    void bnmfTest() {
        BNMF bnmf = new BNMF(datamodel,numFactors,numIters,0.5,seed);
        bnmf.fit();

        assertEquals(bnmf.predict(testUserId,testItemId),3.0);
        assertEquals(bnmf.predict(datamodel.getTestUser(testUserId))[testItemId],bnmf.predict(testUserId,testItemId));

        assertEquals(bnmf.getNumFactors(),numFactors);
        assertEquals(bnmf.getNumIters(),numIters);
    }

    @Test
    void climfTest() {
        CLiMF climf = new CLiMF(datamodel,numFactors,numIters,seed);
        climf.fit();

        assertEquals(climf.predict(testUserId,testItemId),0.8124426876014784);
        assertEquals(climf.predict(datamodel.getTestUser(testUserId))[testItemId],climf.predict(testUserId,testItemId));

        assertEquals(climf.getNumFactors(),numFactors);
        assertEquals(climf.getNumIters(),numIters);
    }

    @Test
    void hpfTest() {
        HPF hpf = new HPF(datamodel,numFactors,numIters,seed);
        hpf.fit();

        assertEquals(hpf.predict(testUserId,testItemId),0.07146102109130958);
        assertEquals(hpf.predict(datamodel.getTestUser(testUserId))[testItemId],hpf.predict(testUserId,testItemId));

        assertEquals(hpf.getNumFactors(),numFactors);
        assertEquals(hpf.getNumIters(),numIters);
    }

    @Test
    void nmfTest(){
        NMF nmf = new NMF(datamodel,numFactors,numIters,seed);
        nmf.fit();

        assertEquals(nmf.predict(testUserId,testItemId),5.190050793827944);
        assertEquals(nmf.predict(datamodel.getTestUser(testUserId))[testItemId],nmf.predict(testUserId,testItemId));

        assertEquals(nmf.getNumFactors(),numFactors);
        assertEquals(nmf.getNumIters(),numIters);
    }

    @Test
    void pmfTest(){
        PMF pmf = new PMF(datamodel,numFactors,numIters,seed);
        pmf.fit();

        assertEquals(pmf.predict(testUserId,testItemId),0.2974430922108766);
        assertEquals(pmf.predict(datamodel.getTestUser(testUserId))[testItemId],pmf.predict(testUserId,testItemId));

        assertEquals(pmf.getNumFactors(),numFactors);
        assertEquals(pmf.getNumIters(),numIters);
    }

    @Test
    void svdPlusPlusTest(){
        SVDPlusPlus svdPlusPlus = new SVDPlusPlus(datamodel,numFactors,numIters,seed);
        svdPlusPlus.fit();

        assertEquals(svdPlusPlus.predict(testUserId,testItemId),4.542345162515235);
        assertEquals(svdPlusPlus.predict(datamodel.getTestUser(testUserId))[testItemId],svdPlusPlus.predict(testUserId,testItemId));

        //assertEquals(svdPlusPlus.getNumFactors(),numFactors);
        //assertEquals(svdPlusPlus.getNumIters(),numIters);
    }

    @Test
    void urpTest(){
        URP urp = new URP(datamodel,numFactors,new double[]{0.1,0.2},numIters,seed);
        urp.fit();

        assertEquals(urp.predict(testUserId,testItemId),0.1);
        assertEquals(urp.predict(datamodel.getTestUser(testUserId))[testItemId],urp.predict(testUserId,testItemId));

        assertEquals(urp.getNumFactors(),numFactors);
        assertEquals(urp.getNumIters(),numIters);
        assertEquals(urp.getRatings()[0],0.1);
    }
}