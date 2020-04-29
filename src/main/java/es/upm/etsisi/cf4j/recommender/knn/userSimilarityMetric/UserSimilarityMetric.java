package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;

/**
 * This class process the similarity measure between two users. To define your own similarity metric
 * implementation, you must extend this class and overrides the method similarity(User user, User
 * otherUser).
 *
 * <p>When the execution of the similarity metric is completed, the similarity of each user with
 * respect to another one can be retrieved using the getSimilarities(int userIndex) method.
 */
public abstract class UserSimilarityMetric implements Partible<User> {

  /** DataModel for which de similarities must be computed */
  protected DataModel datamodel;

  /** Matrix that contains the similarity between each pair of users */
  protected double[][] similarities;

  /**
   * Sets the DataModel for which the similarity are going to be computed
   *
   * @param datamodel DataModel instance
   */
  public void setDatamodel(DataModel datamodel) {
    this.datamodel = datamodel;
    this.similarities = new double[datamodel.getNumberOfUsers()][datamodel.getNumberOfUsers()];
  }

  /**
   * Returns the similarity array of an user. Each position of the array contains the similarity of
   * the user with the corresponding user at the same position in the array of Users of the
   * DataModel instance.
   *
   * @param userIndex Index of the user
   * @return Similarity of an user with other users of the DataModel instance
   */
  public double[] getSimilarities(int userIndex) {
    return this.similarities[userIndex];
  }

  /**
   * This method must returns the similarity between two users.
   *
   * <p>If two users do not have a similarity value, the method must return
   * Double.NEGATIVE_INFINITY.
   *
   * <p>The value returned by this method should be higher the greater the similarity between users.
   *
   * @param user A user
   * @param otherUser Other user
   * @return Similarity between user and otherUser
   */
  public abstract double similarity(User user, User otherUser);

  @Override
  public void beforeRun() {}

  @Override
  public void run(User user) {
    int userIndex = user.getUserIndex();

    for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
      User otherUser = datamodel.getUser(u);
      if (userIndex == otherUser.getUserIndex()) {
        similarities[userIndex][u] = Double.NEGATIVE_INFINITY;
      } else {
        similarities[userIndex][u] = this.similarity(user, otherUser);
      }
    }
  }

  @Override
  public void afterRun() {}

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
