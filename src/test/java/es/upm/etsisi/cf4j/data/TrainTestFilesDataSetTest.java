package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class TrainTestFilesDataSetTest {

  private static final String trainingFilename = "src/test/resources/trainingPartDataset.data";
  private static final String testFilename = "src/test/resources/testPartDataset.data";

  private static final String serializedFilename = "src/test/resources/dataset.save";
  private static final String serializedResultString =
      "\n"
          + "Number of users: 4\n"
          + "Number of test users: 2\n"
          + "Number of items: 4\n"
          + "Number of test items: 2\n"
          + "Number of ratings: 11\n"
          + "Min rating: 0.0\n"
          + "Max rating: 4.4\n"
          + "Average rating: 2.709090909090909\n"
          + "Number of test ratings: 4\n"
          + "Min test rating: 1.1\n"
          + "Max test rating: 2.2\n"
          + "Average test rating: 1.6500000000000001";

  private static DataSet dataSet;

  @BeforeAll
  static void initAll() throws IOException {
    dataSet = new TrainTestFilesDataSet(trainingFilename, testFilename);
  }

  @Test
  void trainingIterator() {
    Iterator<DataSetEntry> trainingItr = dataSet.getRatingsIterator();
    assertEquals(trainingItr.next(), new DataSetEntry("Laurie", "Potatoad", 3.1));
    assertEquals(trainingItr.next(), new DataSetEntry("Laurie", "Yeah,IsWired", 3.2));
    assertEquals(trainingItr.next(), new DataSetEntry("Laurie", "Milk", 3.3));
    assertEquals(trainingItr.next(), new DataSetEntry("Mike", "Milk", 4.1));
    assertEquals(trainingItr.next(), new DataSetEntry("Mike", "WiredThing", 0.0));
    assertEquals(trainingItr.next(), new DataSetEntry("Mike", "Potatoad", 4.3));
    assertEquals(trainingItr.next(), new DataSetEntry("Mike", "Yeah,IsWired", 4.4));
    assertEquals(trainingItr.next(), new DataSetEntry("Tim", "WiredThing", 1.3));
    assertEquals(trainingItr.next(), new DataSetEntry("Tim", "Yeah,IsWired", 1.4));
    assertEquals(trainingItr.next(), new DataSetEntry("Kim", "WiredThing", 2.3));
    assertEquals(trainingItr.next(), new DataSetEntry("Kim", "Yeah,IsWired", 2.4));
  }

  @Test
  void numTrainingRatings() {
    assertEquals(11, dataSet.getNumberOfRatings());
  }

  @Test
  void testIterator() {
    Iterator<DataSetEntry> testItr = dataSet.getTestRatingsIterator();
    assertEquals(testItr.next(), new DataSetEntry("Tim", "Milk", 1.1));
    assertEquals(testItr.next(), new DataSetEntry("Tim", "Potatoad", 1.2));
    assertEquals(testItr.next(), new DataSetEntry("Kim", "Milk", 2.1));
    assertEquals(testItr.next(), new DataSetEntry("Kim", "Potatoad", 2.2));
  }

  @Test
  void numTestRatings() {
    assertEquals(4, dataSet.getNumberOfTestRatings());
  }
}
