package es.upm.etsisi.cf4j.recommender.neural;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.DropoutLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.cpu.nativecpu.NDArray;

import java.util.Map;

/**
 * Implements short method of Bobadilla, J., Ortega, F., Gutiérrez, A., &amp; Alonso, S. (2020).
 * Classification-based Deep Neural Network Architecture for Collaborative Filtering Recommender
 * Systems. International Journal of Interactive Multimedia &amp; Artificial Intelligence, 6(1).
 */
public class NCCF extends Recommender {

  /** Neural network * */
  private MultiLayerNetwork network;

  /** Number of epochs * */
  private int numEpochs;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numEpochs</b>: int value with the number of epochs.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public NCCF(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int) params.get("numEpochs"),
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numEpochs Number of epochs
   */
  public NCCF(DataModel datamodel, int numEpochs) {
    this(datamodel, numEpochs, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numEpochs Number of epochs
   * @param seed Seed for random numbers generation
   */
  public NCCF(DataModel datamodel, int numEpochs, long seed) {
    super(datamodel);

    this.numEpochs = numEpochs;

    MultiLayerConfiguration conf =
        new NeuralNetConfiguration.Builder()
            .seed(seed)
            .updater(new Adam())
            .list()
            .layer(
                new DenseLayer.Builder()
                    .nIn(datamodel.getNumberOfItems())
                    .nOut(200)
                    .activation(Activation.RELU)
                    .build())
            .layer(new DenseLayer.Builder().nIn(200).nOut(200).activation(Activation.RELU).build())
            .layer(new DropoutLayer(0.2))
            .layer(new DenseLayer.Builder().nIn(200).nOut(100).activation(Activation.RELU).build())
            .layer(new DropoutLayer(0.2))
            .layer(
                new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                    .nIn(100)
                    .nOut(datamodel.getNumberOfItems())
                    .activation(Activation.SOFTMAX)
                    .build())
            .build();

    this.network = new MultiLayerNetwork(conf);
    this.network.init();
  }

  @Override
  public void fit() {

    System.out.println("\nFitting " + this.toString());

    double[][] X =
        new double[super.datamodel.getNumberOfRatings()][super.datamodel.getNumberOfItems()];
    double[][] y =
        new double[super.datamodel.getNumberOfRatings()][super.datamodel.getNumberOfItems()];

    int i = 0;
    for (User user : super.datamodel.getUsers()) {
      double[] userMask = new double[super.datamodel.getNumberOfItems()];

      for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
        int itemIndex = user.getItemAt(pos);
        userMask[itemIndex] = 1;
      }

      for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
        int itemIndex = user.getItemAt(pos);
        X[i] = userMask.clone();
        X[i][itemIndex] = 0;
        y[i][itemIndex] = 1;
        i++;
      }
    }

    for (int epoch = 1; epoch <= this.numEpochs; epoch++) {
      this.network.fit(new NDArray(X), new NDArray(y));

      if ((epoch % 5) == 0) System.out.print(".");
      if ((epoch % 50) == 0) System.out.println(epoch + " iterations");
    }
  }

  /**
   * Computes the probability that an item will be of interest to the user. This prediction can not
   * be used as the predicted rating, so prediction quality measures such as MAE or MSE can not be
   * used to evaluate this recommender.
   *
   * @param userIndex Index of the user in the array of Users of the DataModel instance
   * @param itemIndex Index of the item in the array of Items of the DataModel instance
   * @return Prediction
   */
  public double predict(int userIndex, int itemIndex) {
    User user = super.datamodel.getUser(userIndex);

    double[][] X = new double[1][super.datamodel.getNumberOfItems()];
    for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
      int i = user.getItemAt(pos);
      X[0][i] = 1;
    }

    INDArray y = this.network.output(new NDArray(X));

    return y.getDouble(itemIndex);
  }

  /**
   * Returns the number of epochs.
   * @return Number of epochs.
   */
  public int getNumEpochs() {
    return numEpochs;
  }

  @Override
  public String toString() {
    return "NCCF(" + "numEpochs=" + this.numEpochs + ")";
  }
}
