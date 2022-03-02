package es.upm.etsisi.cf4j.recommender.neural;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Map;

/**
 * Implements He, Xiangnan & Liao, Lizi &amp; Zhang, Hanwang. (2017). Neural Collaborative
 * Filtering. Proceedings of the 26th International Conference on World Wide Web.
 */
public class GMF extends Recommender {

  /** Neural network * */
  private final ComputationGraph network;

  /** Number of epochs * */
  private final int numEpochs;

  /** Number of latent factors */
  protected final int numFactors;

  /** Learning Rate */
  protected final double learningRate;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numFactors</b>: int value with the number of latent factors.
   *   <li><b>numEpochs</b>: int value with the number of epochs.
   *   <li><b>learningRate</b>: double value with the learning rate.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public GMF(DataModel datamodel, Map<String, Object> params) {
    this(
            datamodel,
            (int) params.get("numFactors"),
            (int) params.get("numEpochs"),
            (double) params.get("learningRate"),
            params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors
   * @param numEpochs Number of epochs
   * @param learningRate Learning rate
   */
  public GMF(DataModel datamodel, int numFactors, int numEpochs, double learningRate) {this(datamodel, numFactors, numEpochs, learningRate, System.currentTimeMillis());}

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors
   * @param numEpochs Number of epochs
   * @param learningRate Learning rate
   * @param seed Seed for random numbers generation
   */
  public GMF(DataModel datamodel, int numFactors, int numEpochs, double learningRate, long seed) {
    super(datamodel);

    this.numEpochs = numEpochs;
    this.numFactors = numFactors;
    this.learningRate = learningRate;

    ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .updater(new Adam(learningRate))
            .graphBuilder()
            .addInputs("user", "item")
            .addLayer("userEmbeddingLayer", new EmbeddingLayer.Builder()
                    .nIn(super.getDataModel().getNumberOfUsers())
                    .nOut(this.numFactors)
                    .build(), "user")
            .addLayer("itemEmbeddingLayer", new EmbeddingLayer.Builder()
                    .nIn(super.getDataModel().getNumberOfItems())
                    .nOut(this.numFactors)
                    .build(), "item")
            .addVertex("product",new ElementWiseVertex(ElementWiseVertex.Op.Product),"userEmbeddingLayer","itemEmbeddingLayer")
            .addLayer("out", new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                    .nIn(this.numFactors)
                    .nOut(1)
                    .activation(Activation.IDENTITY)
                    .build(), "product")
            .setOutputs("out")
            .build();

    this.network = new ComputationGraph(conf);
    this.network.init();
  }

  @Override
  public void fit() {
    System.out.println("\nFitting " + this.toString());

    NDArray[] X = new NDArray[2];
    NDArray[] y = new NDArray[1];

    double[][] users = new double[super.getDataModel().getNumberOfRatings()][1];
    double[][] items = new double[super.getDataModel().getNumberOfRatings()][1];
    double[][] ratings = new double[super.getDataModel().getNumberOfRatings()][1];

    int i=0;
    for (User user : super.datamodel.getUsers()) {
      for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
        int itemIndex = user.getItemAt(pos);

        users[i][0] = user.getUserIndex();
        items[i][0] = itemIndex;
        ratings[i][0] = user.getRatingAt(pos);
        i++;
      }
    }

    X[0] = new NDArray(users);
    X[1] = new NDArray(items);
    y[0] = new NDArray(ratings);

    for (int epoch = 1; epoch <= this.numEpochs; epoch++) {
      this.network.fit(X, y);
      if ((epoch % 5) == 0) System.out.print(".");
      if ((epoch % 50) == 0) System.out.println(epoch + " iterations");
    }
  }


  /**
   * Returns the prediction of a rating of a certain user for a certain item,
   * through these predictions the metrics of MAE, MSE and RMSE can be obtained.
   *
   * @param userIndex Index of the user in the array of Users of the DataModel instance
   * @param itemIndex Index of the item in the array of Items of the DataModel instance
   * @return Prediction
   */
  public double predict(int userIndex, int itemIndex) {

    NDArray[] X = new NDArray[2];

    double[][] aux = new double[1][1];

    aux[0][0] = userIndex;
    X[0] = new NDArray(aux);
    aux[0][0] = itemIndex;
    X[1] = new NDArray(aux);

    INDArray[] y = this.network.output(X);

    return y[0].toDoubleVector()[0];
  }

  /**
   * Returns the number of epochs.
   * @return Number of epochs.
   */
  public int getNumEpochs() {
    return this.numEpochs;
  }

  @Override
  public String toString() {
    StringBuilder sbuilder = new StringBuilder("GMF(" + "numEpochs=" + this.numEpochs + " numFactors="+this.numFactors+" learningRate="+this.learningRate+")");
    return sbuilder.toString();
  }
}
