package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class DataModelTrainTestFilesTest {

    final private static String trainingFilename = "src/test/resources/trainingPartDataset.data";
    final private static String testFilename = "src/test/resources/testPartDataset.data";

    final private static String serializedFilename = "src/test/resources/dataset.save";
    final private static String serializedResultString = "\n" +
            "Number of users: 4\n" +
            "Number of test users: 2\n" +
            "Number of items: 4\n" +
            "Number of test items: 2\n" +
            "Number of ratings: 11\n" +
            "Number of test ratings: 4\n" +
            "Min rating: 0.0\n" +
            "Max rating: 4.4\n" +
            "Average rating: 2.709090909090909\n" +
            "Number of test ratings: 4\n" +
            "Min test rating: 1.1\n" +
            "Max test rating: 2.2\n" +
            "Average test rating: 1.6500000000000001";

    private static DataModel dataModel;

    @BeforeAll
    static void initAll() throws IOException {
        dataModel = new DataModel(new TrainTestFilesDataSet(trainingFilename, testFilename));
    }

    @Test

    void findIndexes () {
        //Note: elements should be located following insertion order.
        //(taking into account that the test ones are inserted first)
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

    @Test
    void numberOfRatings () {
        //Users
        assertEquals(dataModel.getNumberOfUsers(),  4);
        assertEquals(dataModel.getNumberOfItems(), dataModel.getUsers().length);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Tim")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Kim")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Laurie")).getNumberOfRatings(), 3);
        assertEquals(dataModel.getUser(dataModel.findUserIndex("Mike")).getNumberOfRatings(), 4);
        //Items
        assertEquals(dataModel.getNumberOfItems(),  4);
        assertEquals(dataModel.getNumberOfItems(), dataModel.getItems().length);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("Milk")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("Potatoad")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("WiredThing")).getNumberOfRatings(), 3);
        assertEquals(dataModel.getItem(dataModel.findItemIndex("Yeah,IsWired")).getNumberOfRatings(), 4);
        //TestUsers
        assertEquals(dataModel.getNumberOfTestUsers(),  2);
        assertEquals(dataModel.getNumberOfTestUsers(), dataModel.getTestUsers().length);
        assertEquals(dataModel.getTestUser(dataModel.findTestUserIndex("Tim")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getTestUser(dataModel.findTestUserIndex("Kim")).getNumberOfRatings(), 2);
        //TestItems
        assertEquals(dataModel.getNumberOfTestItems(),  2);
        assertEquals(dataModel.getNumberOfTestItems(), dataModel.getTestItems().length);
        assertEquals(dataModel.getTestItem(dataModel.findTestItemIndex("Milk")).getNumberOfRatings(), 2);
        assertEquals(dataModel.getTestItem(dataModel.findTestItemIndex("Potatoad")).getNumberOfRatings(), 2);
    }

    @Test
    void generalMetrics () {
        assertEquals(dataModel.getMinRating(),0);
        assertEquals(dataModel.getMaxRating(),4.4);
        assertTrue(Math.abs(dataModel.getRatingAverage() - 2.709090909090909) <= Math.ulp(2.709090909090909));

        assertEquals(dataModel.getMinTestRating(),1.1);
        assertEquals(dataModel.getMaxTestRating(),2.2);
        assertTrue(Math.abs(dataModel.getTestRatingAverage() - 1.65) <= Math.ulp(1.65) );
    }

    @Test
    void serializeMethods () throws IOException, ClassNotFoundException{
        dataModel.save(serializedFilename);
        DataModel auxDataModel = DataModel.load(serializedFilename);
        assertEquals( auxDataModel.toString(), serializedResultString);
    }
}