package es.upm.etsisi.cf4j.data;

import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;

class RandomSplitDataSetTest {

  private static final String filename = "src/test/resources/MockDataset.data";
  private static final long seed = 246;
  private static final double testUsersPercentage = 0.5;
  private static final double testItemsPercentage = 0.5;

  private static RandomSplitDataSet dataSet;

  @BeforeAll
  static void initAll() throws IOException {
    dataSet = new RandomSplitDataSet(filename, testUsersPercentage, testItemsPercentage, seed);
  }

  @Test
  void trainingIterator() {
    Iterator<DataSetEntry> trainingItr = dataSet.getRatingsIterator();
    assertEquals(new DataSetEntry("Laurie", "Potatoad", 3.1), trainingItr.next());
    assertEquals(new DataSetEntry("Laurie", "Yeah,IsWired", 3.2), trainingItr.next());
    assertEquals(new DataSetEntry("Laurie", "Milk", 3.3), trainingItr.next());
    assertEquals(new DataSetEntry("Mike", "Milk", 4.1), trainingItr.next());
    assertEquals(new DataSetEntry("Mike", "WiredThing", 0.0), trainingItr.next());
    assertEquals(new DataSetEntry("Tim", "Milk", 1.1), trainingItr.next());
    assertEquals(new DataSetEntry("Tim", "Potatoad", 1.2), trainingItr.next());
    assertEquals(new DataSetEntry("Tim", "WiredThing", 1.3), trainingItr.next());
    assertEquals(new DataSetEntry("Tim", "Yeah,IsWired", 1.4), trainingItr.next());
    assertEquals(new DataSetEntry("Kim", "Milk", 2.1), trainingItr.next());
    assertEquals(new DataSetEntry("Kim", "WiredThing", 2.3), trainingItr.next());
  }

  @Test
  void numTrainingRatings() {
    assertEquals(11, dataSet.getNumberOfRatings());
  }

  @Test
  void testIterator() {
    Iterator<DataSetEntry> testItr = dataSet.getTestRatingsIterator();
    assertEquals(new DataSetEntry("Mike", "Potatoad", 4.3), testItr.next());
    assertEquals(new DataSetEntry("Mike", "Yeah,IsWired", 4.4), testItr.next());
    assertEquals(new DataSetEntry("Kim", "Potatoad", 2.2), testItr.next());
    assertEquals(new DataSetEntry("Kim", "Yeah,IsWired", 2.4), testItr.next());
  }

  @Test
  void numTestRatings() {
    assertEquals(4, dataSet.getNumberOfTestRatings());
  }
}
