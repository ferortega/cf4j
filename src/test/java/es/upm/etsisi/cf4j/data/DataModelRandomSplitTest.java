package es.upm.etsisi.cf4j.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DataModelRandomSplitTest {

    final private static String filename = "src/main/resources/datasets/ml100k.data";
    final private static long seed = 1337;
    final private static double testUsersPercentage = 0.2;
    final private static double testItemsPercentage = 0.2;

    private static DataModel dataModel;

    @BeforeAll
    static void initAll() throws IOException {
        dataModel = new DataModel(new RandomSplitDataSet(filename, testUsersPercentage,testItemsPercentage,"\t", seed));
    }

    @Test
    void checkingIfTestIndexesAreTheSameOfTrainingIndexes () {
        for (int i = 0; i < dataModel.getNumberOfTestUsers(); ++i){
            TestUser testUser = dataModel.getTestUser(i);
            assertEquals(testUser.getUserIndex(),testUser.getTestUserIndex());
        }

        for (int i = 0; i < dataModel.getNumberOfTestItems(); ++i){
            TestItem testItem = dataModel.getTestItem(i);
            assertEquals(testItem.getItemIndex(),testItem.getTestItemIndex());
        }
    }
}