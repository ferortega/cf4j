package es.upm.etsisi.cf4j.recommender.neural;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex;
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

import java.util.Map;


public class NeuMF extends Recommender {

    /** Neural network * */
    private final ComputationGraph network;

    /** Number of epochs * */
    private final int numEpochs;

    /** Number of latent factors */
    protected final int numFactors;

    /** Learning Rate */
    protected final double learningRate;

    /** Array of layers neurons */
    protected final int[] layers;

    /**
     * Model constructor from a Map containing the model's hyper-parameters values. Map object must
     * contains the following keys:
     *
     * <ul>
     *   <li><b>numFactors</b>: int value with the number of latent factors.
     *   <li><b>numEpochs</b>: int value with the number of epochs.
     *   <li><b>learningRate</b>: double value with the learning rate.
     *   <li><b>layers</b>: Array of layers neurons.
     *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
     *       random value is used.
     * </ul>
     *
     * @param datamodel DataModel instance
     * @param params Model's hyper-parameters values
     */
    public NeuMF(DataModel datamodel, Map<String, Object> params) {
        this(
                datamodel,
                (int) params.get("numFactors"),
                (int) params.get("numEpochs"),
                (double) params.get("learningRate"),
                (int[]) params.get("layers"),
                params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numEpochs Number of epochs
     * @param learningRate Learning rate
     * @param layers Array of layers neurons
     */
    public NeuMF(DataModel datamodel, int numFactors, int numEpochs, double learningRate, int[] layers) {this(datamodel, numFactors, numEpochs, learningRate, layers, System.currentTimeMillis());}

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numEpochs Number of epochs
     * @param learningRate Learning rate
     * @param layers Array of layers neurons
     * @param seed Seed for random numbers generation
     */
    public NeuMF(DataModel datamodel, int numFactors, int numEpochs, double learningRate, int[] layers, long seed) {
        super(datamodel);

        this.numEpochs = numEpochs;
        this.numFactors = numFactors;
        this.learningRate = learningRate;
        this.layers = layers;

        ComputationGraphConfiguration.GraphBuilder builder = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam(learningRate))
                .graphBuilder()
                .addInputs("input1", "input2")
                .addLayer("L1mf", new EmbeddingLayer.Builder()
                        .nIn(super.getDataModel().getNumberOfUsers())
                        .nOut(this.numFactors)
                        .build(), "input1")
                .addLayer("L2mf", new EmbeddingLayer.Builder()
                        .nIn(super.getDataModel().getNumberOfItems())
                        .nOut(this.numFactors)
                        .build(), "input2")
                .addLayer("L1mlp", new EmbeddingLayer.Builder()
                        .nIn(super.getDataModel().getNumberOfUsers())
                        .nOut(layers[0]/2)
                        .build(), "input1")
                .addLayer("L2mlp", new EmbeddingLayer.Builder()
                        .nIn(super.getDataModel().getNumberOfItems())
                        .nOut(layers[0]/2)
                        .build(), "input2")
                .addVertex("mul",new ElementWiseVertex(ElementWiseVertex.Op.Product),"L1mf","L2mf")
                .addVertex("concat",new MergeVertex(),"L1mlp","L2mlp");
        int i = 0;
        for(;i<this.layers.length;i++){
            if(i == 0)
                builder.addLayer("hiddenLayer"+i, new DenseLayer.Builder()
                        .nIn(layers[0])
                        .nOut(layers[i])
                        .build(), "concat");
            else
                builder.addLayer("hiddenLayer"+i, new DenseLayer.Builder()
                        .nIn(layers[i-1])
                        .nOut(layers[i])
                        .build(), "hiddenLayer"+(i-1));
        }

        builder.addVertex("finalConcat",new MergeVertex(),"mul","hiddenLayer"+(i-1))
                .addLayer("out", new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .nIn(layers[i-1]+this.numFactors)
                .nOut(1)
                .activation(Activation.IDENTITY)
                .build(), "finalConcat")
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
        String layerPrint = "[";
        for(int i = 0; i<layers.length;i++)
            layerPrint += " "+layers[i]+" ";
        layerPrint+="]";

        StringBuilder sbuilder = new StringBuilder("MLP(" + "numEpochs=" + this.numEpochs + " numFactors="+this.numFactors+" learningRate="+this.learningRate+" layers="+layerPrint+")");
        return sbuilder.toString();
    }
}

