package es.upm.etsisi.cf4j.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

/**
 * This class allows final users to work with benchmark DataModel instances. These instances has been split into
 * train and test subsets.
 */
public class BenchmarkDataModels {

  /**
   * Loads a DataModel instance of MovieLens 100K dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 943</li>
   *   <li>Number of test users: 280</li>
   *   <li>Number of items: 1,682</li>
   *   <li>Number of test items: 448</li>
   *   <li>Number of ratings: 92,026</li>
   *   <li>Number of test ratings: 7,974</li>
   *   <li>Min rating: 1.0</li>
   *   <li>Max rating: 5.0</li>
   * </ul>
   *
   * Visit <a href="https://grouplens.org/datasets/movielens/">https://grouplens.org/datasets/movielens/</a>
   * for more information.
   *
   * @return A DataModel instance with MovieLens 100K dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel MovieLens100K() throws IOException {
        return loadRemoteDataModel("http://cf4j.etsisi.upm.es/datamodels/ml100k.cf4j");
    }

  /**
   * Loads a DataModel instance of MovieLens 1M dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 6,040</li>
   *   <li>Number of test users: 1,758</li>
   *   <li>Number of items: 3,706</li>
   *   <li>Number of test items: 1,032</li>
   *   <li>Number of ratings: 911,031</li>
   *   <li>Number of test ratings: 89,178</li>
   *   <li>Min rating: 1.0</li>
   *   <li>Max rating: 5.0</li>
   * </ul>
   *
   * Visit <a href="https://grouplens.org/datasets/movielens/">https://grouplens.org/datasets/movielens/</a>
   * for more information.
   *
   * @return A DataModel instance with MovieLens 1M dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel MovieLens1M() throws IOException {
        return loadRemoteDataModel("http://cf4j.etsisi.upm.es/datamodels/ml1m.cf4j");
    }

    /**
     * Loads a DataModel instance of MovieLens 1M dataset. The DataModel contains:
     *
     * <ul>
     *   <li>Number of users: 69,878</li>
     *   <li>Number of test users: 20,851</li>
     *   <li>Number of items: 10,677</li>
     *   <li>Number of test items: 3,117</li>
     *   <li>Number of ratings: 9,104,681</li>
     *   <li>Number of test ratings: 895,373</li>
     *   <li>Min rating: 0.5</li>
     *   <li>Max rating: 5.0</li>
     * </ul>
     *
     * Visit <a href="https://grouplens.org/datasets/movielens/">https://grouplens.org/datasets/movielens/</a>
     * for more information.
     *
     * @return A DataModel instance with MovieLens 1M dataset.
     * @throws IOException When the DataModel can not be loaded.
     */
    public static DataModel MovieLens10M() throws IOException {
        return loadRemoteDataModel("http://cf4j.etsisi.upm.es/datamodels/ml10m.cf4j");
    }

    /**
     * Loads a DataModel instance of FilmTrust dataset. The DataModel contains:
     *
     * <ul>
     *   <li>Number of users: 1,508</li>
     *   <li>Number of test users: 420</li>
     *   <li>Number of items: 2,071</li>
     *   <li>Number of test items: 325</li>
     *   <li>Number of ratings: 32,675</li>
     *   <li>Number of test ratings: 2,819</li>
     *   <li>Min rating: 0.5</li>
     *   <li>Max rating: 4.0</li>
     * </ul>
     *
     * @return A DataModel instance with FilmTrust dataset.
     * @throws IOException When the DataModel can not be loaded.
     */
    public static DataModel FilmTrust() throws IOException {
        return loadRemoteDataModel("http://cf4j.etsisi.upm.es/datamodels/ft.cf4j");
    }

    /**
     * Loads a DataModel instance of BookCrossing dataset. Only explicit ratings has been included. The DataModel
     * contains:
     *
     * <ul>
     *   <li>Number of users: 77,805</li>
     *   <li>Number of test users: 11,426</li>
     *   <li>Number of items: 185,973</li>
     *   <li>Number of test items: 25,697</li>
     *   <li>Number of ratings: 390,351</li>
     *   <li>Number of test ratings: 43,320</li>
     *   <li>Min rating: 1.0</li>
     *   <li>Max rating: 10.0</li>
     * </ul>
     *
     * Visit <a href="http://www2.informatik.uni-freiburg.de/~cziegler/BX/">http://www2.informatik.uni-freiburg.de/~cziegler/BX/</a>
     * for more information.
     *
     * @return A DataModel instance with BookCroosing dataset.
     * @throws IOException When the DataModel can not be loaded.
     */
    public static DataModel BookCrossing() throws IOException {
        return loadRemoteDataModel("http://cf4j.etsisi.upm.es/datamodels/bx.cf4j");
    }

    /**
     * Loads a DataModel instance from a remote URL
     * @param spec The String with the URL of the DataModel
     * @return DataModel instance
     * @throws IOException When the DataModel can not be loaded.
     */
    private static DataModel loadRemoteDataModel(String spec) throws IOException {
        URL url = new URL(spec);
        ObjectInputStream in = new ObjectInputStream(url.openStream());
        DataModel datamodel = null;
        try {
            datamodel = (DataModel) in.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("A problem has occurred while loading the DataModel. If the problem persists in a few minutes, open an issue in CF4J GitHub's repository.");
            e.printStackTrace();
        }
        return datamodel;
    }
}
