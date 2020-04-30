package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class implements the DataSet interface by loading training and test ratings from separated
 * text files. Each line of the ratings files must have the following format:
 *
 * <pre>&lt;userId&gt;&lt;separator&gt;&lt;itemId&gt;&lt;separator&gt;&lt;rating&gt;</pre>
 *
 * <p>Where &lt;separator&gt; is an special character that delimits ratings fields (semicolon by
 * default).
 */
public class TrainTestFilesDataSet implements DataSet {

  protected static final String DEFAULT_SEPARATOR = ";";

  /** Raw stored ratings */
  protected ArrayList<DataSetEntry> ratings;

  /** Raw stored test ratings */
  protected ArrayList<DataSetEntry> testRatings;

  /**
   * Generates a DataSet form training and test ratings files. Semicolon is used as separator.
   *
   * @param trainingFileName File with the (training) ratings.
   * @param testFileName File with the test ratings.
   * @throws IOException When the file is not accessible by the system with read permissions.
   */
  public TrainTestFilesDataSet(String trainingFileName, String testFileName) throws IOException {
    this(trainingFileName, testFileName, DEFAULT_SEPARATOR);
  }

  /**
   * Generates a DataSet form training and test ratings files.
   *
   * @param trainingFileName File with the (training) ratings.
   * @param testFileName File with the test ratings.
   * @param separator Separator char between ratings fields.
   * @throws IOException When the file is not accessible by the system with read permissions.
   */
  public TrainTestFilesDataSet(String trainingFileName, String testFileName, String separator)
      throws IOException {

    // Load traning file

    System.out.println("\nLoading " + trainingFileName + "...");

    BufferedReader datasetFile = new BufferedReader(new FileReader(new File(trainingFileName)));
    this.ratings = new ArrayList<>();

    String line;
    int numLines = 0;
    while ((line = datasetFile.readLine()) != null) {
      numLines++;
      if (numLines % 1000000 == 0) System.out.print(".");
      if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

      String[] s = line.split(separator);
      String userId = s[0];
      String itemId = s[1];
      double rating = Double.parseDouble(s[2]);

      this.ratings.add(new DataSetEntry(userId, itemId, rating));
    }

    datasetFile.close();

    // Load test file

    System.out.println("\nLoading " + testFileName + "...");

    BufferedReader testDatasetFile = new BufferedReader(new FileReader(new File(testFileName)));
    this.testRatings = new ArrayList<>();

    numLines = 0;
    while ((line = testDatasetFile.readLine()) != null) {
      numLines++;
      if (numLines % 1000000 == 0) System.out.print(".");
      if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

      String[] s = line.split(separator);
      String userId = s[0];
      String itemId = s[1];
      double rating = Double.parseDouble(s[2]);

      this.testRatings.add(new DataSetEntry(userId, itemId, rating));
    }

    testDatasetFile.close();
  }

  @Override
  public Iterator<DataSetEntry> getRatingsIterator() {
    return ratings.iterator();
  }

  @Override
  public Iterator<DataSetEntry> getTestRatingsIterator() {
    return testRatings.iterator();
  }

  @Override
  public int getNumberOfRatings() {
    return ratings.size();
  }

  @Override
  public int getNumberOfTestRatings() {
    return testRatings.size();
  }
}
