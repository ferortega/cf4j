package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class DataModelTrainTestFilesTest {

    final private static String trainingFilename = "src/test/resources/trainingPartDataset.data";
    final private static String testFilename = "src/test/resources/testPartDataset.data";

    private static DataModel dataModel;

    @BeforeAll
    static void initAll() throws IOException {
        dataModel = new DataModel(new TrainTestFilesDataSet(trainingFilename, testFilename));
    }

    @Test
    void checkingContents () {
        //Users
        assertEquals(dataModel.getNumberOfUsers(),  4);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Tim")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Kim")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Laurie")).getNumberOfRatings(), 3);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Mike")).getNumberOfRatings(), 4);
        //Items
        assertEquals(dataModel.getNumberOfItems(),  4);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("Milk")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("Potatoad")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("WiredThing")).getNumberOfRatings(), 3);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("Yeah,IsWired")).getNumberOfRatings(), 4);
        //TestUsers
        assertEquals(dataModel.getNumberOfTestUsers(),  2);
        assertEquals(dataModel.getTestUser(dataModel.findTestUserIndex("Tim")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getTestUser(dataModel.findTestUserIndex("Kim")).getNumberOfRatings(), 2);
        //TestItems
        assertEquals(dataModel.getNumberOfTestItems(),  2);
        assertEquals(dataModel.getTestItem(dataModel.findTestItemIndex("Milk")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getTestItem(dataModel.findTestItemIndex("Potatoad")).getNumberOfRatings(), 2);
    }

    @Test
    //Note, elements should be located following insertion order (taking into account that the test ones are inserted first)
    void checkingElementOrders () {
        //Users
        assertEquals(dataModel.findUserIndex("Tim"), 0);
        assertEquals(dataModel.findUserIndex("Kim"), 1);
        assertEquals(dataModel.findUserIndex("Laurie"), 2);
        assertEquals(dataModel.findUserIndex("Mike"), 3);
        //Items
        assertEquals(dataModel.findItemIndex("Milk"), 0);
        assertEquals(dataModel.findItemIndex("Potatoad"), 1);
        assertEquals(dataModel.findItemIndex("Yeah,IsWired"), 2);
        assertEquals(dataModel.findItemIndex("WiredThing"), 3);
        //TestUsers
        assertEquals(dataModel.findTestUserIndex("Tim"), 0);
        assertEquals(dataModel.findTestUserIndex("Kim"), 1);
        //TestItems
        assertEquals(dataModel.findTestItemIndex("Milk"), 0);
        assertEquals(dataModel.findTestItemIndex("Potatoad"), 1);
    }
}