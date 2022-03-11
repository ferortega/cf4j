package es.upm.etsisi.cf4j.recommender.neural;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Arrays;
import java.util.Map;

/**
 * Implements He, X., Liao, L., Zhang, H., Nie, L., Hu, X., &amp; Chua, T. S. (2017, April). Neural
 * collaborative filtering. In Proceedings of the 26th international conference on world wide web
 * (pp. 173-182).
 */
public class MLP extends Recommender {

  /** Neural network * */
  private final ComputationGraph network;

  /** Number of epochs * */
  private final int numEpochs;

  /** Learning rate */
  protected final double learningRate;

  /** Number of factors */
  protected final int numFactors;

  /** Array of layers neurons */
  protected final int[] layers;

  /**
   * Model constructor from a Map containing the model's hyper-parameters values. Map object must
   * contains the following keys:
   *
   * <ul>
   *   <li><b>numFactors</b> (optional): int value with the number of factors. If missing, default
   *       10 latent factors are used.
   *   <li><b>numEpochs</b>: int value with the number of epochs.
   *   <li><b>learningRate</b>: double value with the learning rate.
   *   <li><b>layers</b> (optional): Array of layers neurons. If missing, default [20, 10] Array is
   *       used.
   *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
   *       random value is used.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Model's hyper-parameters values
   */
  public MLP(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        params.containsKey("numFactors") ? (int) params.get("numFactors") : 10,
        (int) params.get("numEpochs"),
        (double) params.get("learningRate"),
        params.containsKey("layers") ? (int[]) params.get("layers") : new int[] {20, 10},
        params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors. 10 by default
   * @param numEpochs Number of epochs
   * @param learningRate Learning rate
   */
  public MLP(DataModel datamodel, int numFactors, int numEpochs, double learningRate) {
    this(
        datamodel,
        numFactors,
        numEpochs,
        learningRate,
        new int[] {20, 10},
        System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numFactors Number of factors. 10 by default
   * @param numEpochs Number of epochs
   * @param learningRate Learning rate
   * @param seed Seed for random numbers generation
   */
  public MLP(DataModel datamodel, int numFactors, int numEpochs, double learningRate, long seed) {
    this(datamodel, numFactors, numEpochs, learningRate, new int[] {20, 10}, seed);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numEpochs Number of epochs. 10 by default
   * @param learningRate Learning rate
   * @param numFactors Number of factors
   * @param layers Array of layers neurons. [20, 10] by default
   */
  public MLP(
      DataModel datamodel, int numFactors, int numEpochs, double learningRate, int[] layers) {
    this(datamodel, numFactors, numEpochs, learningRate, layers, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numEpochs Number of epochs
   * @param learningRate Learning rate
   */
  public MLP(DataModel datamodel, int numEpochs, double learningRate) {
    this(datamodel, 10, numEpochs, learningRate, new int[] {20, 10}, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numEpochs Number of epochs
   * @param learningRate Learning rate
   * @param seed Seed for random numbers generation
   */
  public MLP(DataModel datamodel, int numEpochs, double learningRate, long seed) {
    this(datamodel, 10, numEpochs, learningRate, new int[] {20, 10}, seed);
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param learningRate Learning rate
   * @param layers Array of layers neurons. [20, 10] by default
   */
  public MLP(DataModel datamodel, int numEpochs, double learningRate, int[] layers) {
    this(datamodel, 10, numEpochs, learningRate, layers, System.currentTimeMillis());
  }

  /**
   * Model constructor
   *
   * @param datamodel DataModel instance
   * @param numEpochs Number of epochs
   * @param learningRate Learning rate
   * @param numFactors Number of factors
   * @param layers Array of layers neurons. [20, 10] by default.
   * @param seed Seed for random numbers generation
   */
  public MLP(
      DataModel datamodel,
      int numFactors,
      int numEpochs,
      double learningRate,
      int[] layers,
      long seed) {
    super(datamodel);

    this.numEpochs = numEpochs;
    this.learningRate = learningRate;
    this.numFactors = numFactors;
    this.layers = layers;

    ComputationGraphConfiguration.GraphBuilder builder =
        new NeuralNetConfiguration.Builder()
            .seed(seed)
            .updater(new Adam(learningRate))
            .graphBuilder()
            .addInputs("user", "item")
            .addLayer(
                "userEmbeddingLayer",
                new EmbeddingLayer.Builder()
                    .nIn(super.getDataModel().getNumberOfUsers())
                    .nOut(this.numFactors)
                    .build(),
                "user")
            .addLayer(
                "itemEmbeddingLayer",
                new EmbeddingLayer.Builder()
                    .nIn(super.getDataModel().getNumberOfItems())
                    .nOut(this.numFactors)
                    .build(),
                "item")
            .addVertex("concat", new MergeVertex(), "userEmbeddingLayer", "itemEmbeddingLayer");
    int i = 0;
    for (; i < this.layers.length; i++) {
      if (i == 0)
        builder.addLayer(
            "hiddenLayer" + i,
            new DenseLayer.Builder().nIn(this.numFactors * 2).nOut(layers[i]).build(),
            "concat");
      else
        builder.addLayer(
            "hiddenLayer" + i,
            new DenseLayer.Builder().nIn(layers[i - 1]).nOut(layers[i]).build(),
            "hiddenLayer" + (i - 1));
    }

    builder
        .addLayer(
            "out",
            new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .nIn(layers[i - 1])
                .nOut(1)
                .activation(Activation.IDENTITY)
                .build(),
            "hiddenLayer" + (i - 1))
        .setOutputs("out")
        .build();

    this.network = new ComputationGraph(builder.build());
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

    int i = 0;
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
   * Returns the prediction of a rating of a certain user for a certain item, through these
   * predictions the metrics of MAE, MSE and RMSE can be obtained.
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
   *
   * @return Number of epochs.
   */
  public int getNumEpochs() {
    return this.numEpochs;
  }

  /**
   * Returns the number of latent factors.
   *
   * @return Number of latent factors.
   */
  public int getNumFactors() {
    return this.numFactors;
  }

  /**
   * Returns learning rate.
   *
   * @return learning rate.
   */
  public double getLearningRate() {
    return this.learningRate;
  }

  /**
   * Returns net layers.
   *
   * @return net layers.
   */
  public int[] getLayers() {
    return this.layers;
  }

  @Override
  public String toString() {
    StringBuilder str =
        new StringBuilder("MLP(")
            .append("numEpochs=")
            .append(this.numEpochs)
            .append("; ")
            .append("learningRate=")
            .append(this.learningRate)
            .append("; ")
            .append("numFactors=")
            .append(this.numFactors)
            .append("; ")
            .append("layers=")
            .append(Arrays.toString(layers))
            .append(")");
    return str.toString();
  }
}
