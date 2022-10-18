package es.upm.etsisi.cf4j.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

/**
 * This class allows final users to work with benchmark DataModel instances. These instances have
 * been split into train and test subsets.
 *
 * <b>ALERT</b>: due to security changes on the server hosting the BenchmarkDataModels, these will
 * no longer be available for versions lower than 2.3.0. If you need to continue using the
 * BenchmarkDataModels, please upgrade to version 2.3.0 or higher.
 */
public class BenchmarkDataModels {

  /**
   * Loads a DataModel instance of MovieLens 100K dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 943
   *   <li>Number of test users: 280
   *   <li>Number of items: 1,682
   *   <li>Number of test items: 448
   *   <li>Number of ratings: 92,026
   *   <li>Number of test ratings: 7,974
   *   <li>Min rating: 1.0
   *   <li>Max rating: 5.0
   * </ul>
   *
   * Visit <a
   * href="https://grouplens.org/datasets/movielens/">https://grouplens.org/datasets/movielens/</a>
   * for more information.
   *
   * @return A DataModel instance with MovieLens 100K dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel MovieLens100K() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/ml100k.cf4j");
  }

  /**
   * Loads a DataModel instance of MovieLens 1M dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 6,040
   *   <li>Number of test users: 1,758
   *   <li>Number of items: 3,706
   *   <li>Number of test items: 1,032
   *   <li>Number of ratings: 911,031
   *   <li>Number of test ratings: 89,178
   *   <li>Min rating: 1.0
   *   <li>Max rating: 5.0
   * </ul>
   *
   * Visit <a
   * href="https://grouplens.org/datasets/movielens/">https://grouplens.org/datasets/movielens/</a>
   * for more information.
   *
   * @return A DataModel instance with MovieLens 1M dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel MovieLens1M() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/ml1m.cf4j");
  }

  /**
   * Loads a DataModel instance of MovieLens 1M dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 69,878
   *   <li>Number of test users: 20,851
   *   <li>Number of items: 10,677
   *   <li>Number of test items: 3,117
   *   <li>Number of ratings: 9,104,681
   *   <li>Number of test ratings: 895,373
   *   <li>Min rating: 0.5
   *   <li>Max rating: 5.0
   * </ul>
   *
   * Visit <a
   * href="https://grouplens.org/datasets/movielens/">https://grouplens.org/datasets/movielens/</a>
   * for more information.
   *
   * @return A DataModel instance with MovieLens 1M dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel MovieLens10M() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/ml10m.cf4j");
  }

  /**
   * Loads a DataModel instance of FilmTrust dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 1,508
   *   <li>Number of test users: 420
   *   <li>Number of items: 2,071
   *   <li>Number of test items: 325
   *   <li>Number of ratings: 32,675
   *   <li>Number of test ratings: 2,819
   *   <li>Min rating: 0.5
   *   <li>Max rating: 4.0
   * </ul>
   *
   * @return A DataModel instance with FilmTrust dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel FilmTrust() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/ft.cf4j");
  }

  /**
   * Loads a DataModel instance of BookCrossing dataset. Only explicit ratings has been included.
   * The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 77,805
   *   <li>Number of test users: 11,426
   *   <li>Number of items: 185,973
   *   <li>Number of test items: 25,697
   *   <li>Number of ratings: 390,351
   *   <li>Number of test ratings: 43,320
   *   <li>Min rating: 1.0
   *   <li>Max rating: 10.0
   * </ul>
   *
   * Visit <a
   * href="http://www2.informatik.uni-freiburg.de/~cziegler/BX/">http://www2.informatik.uni-freiburg.de/~cziegler/BX/</a>
   * for more information.
   *
   * @return A DataModel instance with BookCroosing dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel BookCrossing() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/bx.cf4j");
  }

  /**
   * Loads a DataModel instance of LibimSeTi dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 135,359
   *   <li>Number of test users: 40,940
   *   <li>Number of items: 168,791
   *   <li>Number of test items: 40,532
   *   <li>Number of ratings: 15,846,347
   *   <li>Number of test ratings: 1,512,999
   *   <li>Min rating: 1.0
   *   <li>Max rating: 10.0
   * </ul>
   *
   * Visit <a
   * href="http://www.occamslab.com/petricek/data/">http://www.occamslab.com/petricek/data/</a> for
   * more information.
   *
   * @return A DataModel instance with LibimSeTi dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel LibimSeTi() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/libimseti.cf4j");
  }

  /**
   * Loads a DataModel instance of MyAnimeList dataset. Only explicit ratings has been included. The
   * DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 69,600
   *   <li>Number of test users: 19,179
   *   <li>Number of items: 9,927
   *   <li>Number of test items: 2,692
   *   <li>Number of ratings: 5,788,207
   *   <li>Number of test ratings: 549,027
   *   <li>Min rating: 1.0
   *   <li>Max rating: 10.0
   * </ul>
   *
   * Visit <a
   * href="https://www.kaggle.com/CooperUnion/anime-recommendations-database/data">https://www.kaggle.com/CooperUnion/anime-recommendations-database/data</a>
   * for more information.
   *
   * @return A DataModel instance with LibimSeTi dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel MyAnimeList() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/myanimelist.cf4j");
  }

  /**
   * Loads a DataModel instance of Jester (Dataset 3) dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 54,905
   *   <li>Number of test users: 16,465
   *   <li>Number of items: 140
   *   <li>Number of test items: 37
   *   <li>Number of ratings: 1,662,713
   *   <li>Number of test ratings: 179,657
   *   <li>Min rating: -10.0
   *   <li>Max rating: 10.0
   * </ul>
   *
   * Visit <a
   * href="http://eigentaste.berkeley.edu/dataset/">http://eigentaste.berkeley.edu/dataset/</a> for
   * more information.
   *
   * @return A DataModel instance with Jester dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel Jester() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/jester.cf4j");
  }

  /**
   * Loads a DataModel instance of Netflix Prize dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 480,189
   *   <li>Number of test users: 23,012
   *   <li>Number of items: 17,770
   *   <li>Number of test items: 1,750
   *   <li>Number of ratings: 9,9945,049
   *   <li>Number of test ratings: 535,458
   *   <li>Min rating: 1.0
   *   <li>Max rating: 5.0
   * </ul>
   *
   * Visit <a
   * href="https://www.kaggle.com/netflix-inc/netflix-prize-data">https://www.kaggle.com/netflix-inc/netflix-prize-data</a>
   * for more information.
   *
   * @return A DataModel instance with Netflix Prize dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel NetflixPrize() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/netflix.cf4j");
  }

  /**
   * Loads a DataModel instance of BoardGameGeek dataset. The DataModel contains:
   *
   * <ul>
   *   <li>Number of users: 411,375
   *   <li>Number of test users: 57,459
   *   <li>Number of items: 21,925
   *   <li>Number of test items: 4,372
   *   <li>Number of ratings: 18,273,394
   *   <li>Number of test ratings: 63,6134
   *   <li>Min rating: 1.0
   *   <li>Max rating: 10.0
   * </ul>
   *
   * <p>Visit <a
   * href="https://www.kaggle.com/datasets/threnjen/board-games-database-from-boardgamegeek">https://www.kaggle.com/datasets/threnjen/board-games-database-from-boardgamegeek</a>
   * for more information. The duplicated ratings have been removed keeping only the last one defined.
   *
   * @return A DataModel instance with BoardGameGeek dataset.
   * @throws IOException When the DataModel can not be loaded.
   */
  public static DataModel BoardGameGeek() throws IOException {
    return loadRemoteDataModel("https://cf4j.etsisi.upm.es/datamodels/bgg.cf4j");
  }

  /**
   * Loads a DataModel instance from a remote URL
   *
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
      System.err.println(
          "A problem has occurred while loading the DataModel. If the problem persists in a few minutes, open an issue in CF4J GitHub's repository.");
      e.printStackTrace();
    } finally{
      in.close();
    }
    return datamodel;
  }
}
