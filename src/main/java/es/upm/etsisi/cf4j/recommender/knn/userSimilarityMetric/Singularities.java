package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Ortega, F., &amp; Hernando, A.
 * (2012). A collaborative filtering similarity measure based on singularities, Information
 * Processing and Management, 48 (2), 204-217.
 */
public class Singularities extends UserSimilarityMetric {

  /** Maximum difference between the ratings */
  private double maxDiff;

  /** Relevant ratings set */
  private HashSet<Double> relevantRatings;

  /** Not relevant ratings set */
  private HashSet<Double> notRelevantRatings;

  /** Singularity of the relevant ratings */
  private double[] singularityOfRelevantRatings;

  /** Singularity of the not relevant ratings */
  private double[] singularityOfNotRelevantRatings;

  /**
   * Constructor of the similarity metric
   *
   * @param relevantRatings Relevant ratings array
   * @param notRelevantRatings Not relevant ratings array
   */
  public Singularities(double[] relevantRatings, double[] notRelevantRatings) {
    this.relevantRatings = new HashSet<>();
    for (double r : relevantRatings) {
      this.relevantRatings.add(r);
    }

    this.notRelevantRatings = new HashSet<>();
    for (double r : notRelevantRatings) {
      this.notRelevantRatings.add(r);
    }
  }

  @Override
  public void beforeRun() {
    super.beforeRun();

    this.maxDiff = super.datamodel.getMaxRating() - super.datamodel.getMinRating();

    int numUsers = super.datamodel.getNumberOfUsers();
    int numItems = super.datamodel.getNumberOfItems();

    // To store items singularity
    this.singularityOfRelevantRatings = new double[numItems];
    this.singularityOfNotRelevantRatings = new double[numItems];

    for (int i = 0; i < numItems; i++) {
      Item item = super.datamodel.getItem(i);

      int numberOfRelevantRatings = 0;
      int numberOfNotRelevantRatings = 0;

      for (int j = 0; j < item.getNumberOfRatings(); j++) {
        double rating = item.getRatingAt(j);
        if (relevantRatings.contains(rating)) numberOfRelevantRatings++;
        if (notRelevantRatings.contains(rating)) numberOfNotRelevantRatings++;
      }

      this.singularityOfRelevantRatings[i] = 1d - numberOfRelevantRatings / (double) numUsers;
      this.singularityOfNotRelevantRatings[i] = 1d - numberOfNotRelevantRatings / (double) numUsers;
    }
  }

  @Override
  public double similarity(User user, User otherUser) {

    // Compute the metric
    //  (a) Both users have rated as relevant
    //  (b) Both users has rated as no relevant
    //  (c) One user has rated relevant and the other one has rated no relevant
    double metric_a = 0d, metric_b = 0d, metric_c = 0d;
    int items_a = 0, items_b = 0, items_c = 0;

    int i = 0, j = 0, common = 0;
    while (i < user.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
      if (user.getItemAt(i) < otherUser.getItemAt(j)) {
        i++;
      } else if (user.getItemAt(i) > otherUser.getItemAt(j)) {
        j++;
      } else {

        // Get the ratings
        int itemIndex = user.getItemAt(i);
        double activeUserRating = user.getRatingAt(i);
        double targetUserRating = otherUser.getRatingAt(j);

        // Both user have rated relevant
        if (this.relevantRatings.contains(activeUserRating)
            && this.relevantRatings.contains(targetUserRating)) {
          items_a++;

          double sing_p = this.singularityOfRelevantRatings[itemIndex];

          double diff = (activeUserRating - targetUserRating) / this.maxDiff;
          metric_a += (1d - diff * diff) * sing_p * sing_p;

          // Both users have rated no relevant
        } else if (this.notRelevantRatings.contains(activeUserRating)
            && this.notRelevantRatings.contains(targetUserRating)) {
          items_b++;

          double sing_n = this.singularityOfNotRelevantRatings[itemIndex];

          double diff = (activeUserRating - targetUserRating) / this.maxDiff;
          metric_b += (1d - diff * diff) * sing_n * sing_n;

          //  One user has rated relevant and the other one has rated no relevant
        } else {
          items_c++;

          double sing_p = this.singularityOfRelevantRatings[itemIndex];
          double sing_n = this.singularityOfNotRelevantRatings[itemIndex];

          double diff = (activeUserRating - targetUserRating) / this.maxDiff;
          metric_c += (1d - diff * diff) * sing_p * sing_n;
        }

        common++;
        i++;
        j++;
      }
    }

    // If there is not items in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Normalization
    if (items_a != 0) metric_a = metric_a / (double) items_a;
    if (items_b != 0) metric_b = metric_b / (double) items_b;
    if (items_c != 0) metric_c = metric_c / (double) items_c;

    // Return similarity
    return (metric_a + metric_b + metric_c) / 3d;
  }

  @Override
  public String toString() {
    return super.toString()
        + "(relevantRatings="
        + this.relevantRatings
        + ", notRelevantRatings="
        + this.notRelevantRatings
        + ")";
  }
}
