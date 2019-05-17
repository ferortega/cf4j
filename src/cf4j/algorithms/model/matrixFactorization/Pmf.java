package cf4j.algorithms.model.matrixFactorization;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.User;
import cf4j.process.PartibleThreads;
import cf4j.process.Processor;
import cf4j.utils.Methods;

/**
 * Implements Probabilist Matrix Factorization: Koren, Y., Bell, R., &amp; Volinsky, C. (2009). 
 * Matrix factorization techniques for recommender systems. Computer, (8), 30-37.
 *
 * @author Fernando Ortega
 */
public class Pmf implements FactorizationModel {

	private final static String USER_BIAS_KEY = "pmf-user-bias";
	private final static String USER_FACTORS_KEY = "pmf-user-factors";

	private final static String ITEM_BIAS_KEY = "pmf-item-bias";
	private final static String ITEM_FACTORS_KEY = "pmf-item-factors";

	private final static double DEFAULT_GAMMA = 0.01;
	private final static double DEFAULT_LAMBDA = 0.1;

	/**
	 * Learning rate: 0.01 by default
	 */
	private double gamma;

	/**
	 * Regularization parameter: 0.1 by default
	 */
	private double lambda;

	/**
	 * Number of latent factors
	 */
	private int numFactors;

	/**
	 * Number of iterations
	 */
	private int numIters;

	/**
	 * Enable biases
	 */
	private boolean biases;

	/**
	 * Enable dataModel
	 */
	private DataModel dataModel;

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 */
	public Pmf (DataModel dataModel, int numFactors, int numIters)	{
		this(dataModel, numFactors, numIters, DEFAULT_LAMBDA, DEFAULT_GAMMA, true);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 */
	public Pmf (DataModel dataModel,int numFactors, int numIters, double lambda) {
		this(dataModel, numFactors, numIters, lambda, DEFAULT_GAMMA, true);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param gamma Learning rate parameter
	 */
	public Pmf (DataModel dataModel, int numFactors, int numIters, double lambda, double gamma) {
		this(dataModel, numFactors, numIters, lambda, gamma, true);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param biases Enable/disable biases in the model
	 */
	public Pmf (DataModel dataModel, int numFactors, int numIters, double lambda, boolean biases) {
		this(dataModel, numFactors, numIters, lambda, DEFAULT_GAMMA, biases);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param gamma Learning rate parameter
	 * @param biases Enable/disable biases in the model
	 */
	public Pmf (DataModel dataModel, int numFactors, int numIters, double lambda, double gamma, boolean biases) {

		this.numFactors = numFactors;
		this.numIters = numIters;
		this.lambda = lambda;
		this.gamma = gamma;
		this.biases = biases;
		this.dataModel = dataModel;

		// Users initialization
		for (int u = 0; u < dataModel.getNumberOfUsers(); u++) {
			this.setUserFactors(u, this.random(this.numFactors, -1, 1));
		}

		// Items initialization
		for (int i = 0; i < dataModel.getNumberOfItems(); i++) {
			this.setItemFactors(i, this.random(this.numFactors, -1, 1));
		}

		// Initialize bias if needed
		if (this.biases) {

			// Users bias initialization
			for (int u = 0; u < dataModel.getNumberOfUsers(); u++) {
				this.setUserBias(u, this.random(-1, 1));
			}

			// Items bias initialization
			for (int i = 0; i < dataModel.getNumberOfItems(); i++) {
				this.setItemBias(i, this.random(-1, 1));
			}
		}
	}

	/**
	 * Get the number of topics of the model
	 * @return Number of topics
	 */
	public int getNumberOfTopics () {
		return this.numFactors;
	}

	/**
	 * Get the regularization parameter of the model
	 * @return Lambda
	 */
	public double getLambda () {
		return this.lambda;
	}

	/**
	 * Get the learning rate parameter of the model
	 * @return Gamma
	 */
	public double getGamma () {
		return this.gamma;
	}

	/**
	 * Estimate the latent model factors
	 */
	public void train () {

		System.out.println("\nProcessing PMF...");

		for (int iter = 1; iter <= this.numIters; iter++) {

			Processor porcessor = new Processor(false);

			// ALS: fix q_i and update p_u -> fix p_u and update q_i
			porcessor.process(new UpdateUsersFactors(dataModel));
			porcessor.process(new UpdateItemsFactors(dataModel));

			if ((iter % 10) == 0) System.out.print(".");
			if ((iter % 100) == 0) System.out.println(iter + " iterations");
		}
	}

	/**
	 * Get user factors
	 * @param userIndex User index
	 * @return User factors
	 */
	public Double [] getUserFactors (int userIndex) {
		User user = dataModel.getUserAt(userIndex);
		return user.getStoredData().getDoubleArray(USER_FACTORS_KEY);
	}

	/**
	 * Set user factors
	 * @param userIndex User index
	 * @param factors User factors
	 */
	private void setUserFactors (int userIndex, Double [] factors) {
		User user = this.dataModel.getUserAt(userIndex);
		user.getStoredData().setDoubleArray(USER_FACTORS_KEY, factors);
	}

	/**
	 * Get item factors
	 * @param itemIndex Item index
	 * @return Item factors
	 */
	public Double [] getItemFactors (int itemIndex) {
		Item item = this.dataModel.getItemAt(itemIndex);
		return (Double []) item.getStoredData().getDoubleArray(ITEM_FACTORS_KEY);
	}

	/**
	 * Set item factors
	 * @param itemIndex Item index
	 * @param factors Item factors
	 */
	private void setItemFactors (int itemIndex, Double [] factors) {
		Item item = this.dataModel.getItemAt(itemIndex);
		item.getStoredData().setDoubleArray(ITEM_FACTORS_KEY, factors);
	}

	/**
	 * Get user bias (if enabled)
	 * @param userIndex User index
	 * @return User bias or null
	 */
	public double getUserBias (int userIndex) {
		User user = this.dataModel.getUserAt(userIndex);
		return user.getStoredData().getDouble(USER_BIAS_KEY);
	}

	/**
	 * Set user bias
	 * @param userIndex User index
	 * @param bias User bias
	 */
	private void setUserBias (int userIndex, double bias) 	{
		User user = this.dataModel.getUserAt(userIndex);
		user.getStoredData().setDouble(USER_BIAS_KEY, bias);
	}

	/**
	 * Get item bias (if needed)
	 * @param itemIndex Item index
	 * @return Item bias
	 */
	public double getItemBias (int itemIndex) {
		Item item = this.dataModel.getItemAt(itemIndex);
		return item.getStoredData().getDouble(ITEM_BIAS_KEY);
	}

	/**
	 * Set item bias
	 * @param itemIndex Item index
	 * @param bias Item bias
	 */
	private void setItemBias (int itemIndex, double bias) {
		Item item = this.dataModel.getItemAt(itemIndex);
		item.getStoredData().setDouble(ITEM_BIAS_KEY, bias);
	}

	/**
	 * Computes a rating prediction
	 * @param userIndex User index
	 * @param itemIndex Item index
	 * @return Prediction
	 */
	public double getPrediction (int userIndex, int itemIndex) {
		Double [] factors_u = this.getUserFactors(userIndex);
		Double [] factors_i = this.getItemFactors(itemIndex);

		if (this.biases) {
			double average = this.dataModel.getDataBank().getDouble(DataModel.AVERAGERATING_KEY);

			double bias_u = this.getUserBias(userIndex);
			double bias_i = this.getItemBias(itemIndex);

			return average + bias_u + bias_i + Methods.dotProduct(factors_u, factors_i);
		}
		else {
			return Methods.dotProduct(factors_u, factors_i);
		}
	}

	/**
	 * Auxiliary inner class to parallelize user factors computation
	 * @author Fernando Ortega
	 */
	private class UpdateUsersFactors extends PartibleThreads {

		public UpdateUsersFactors(DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public int getTotalIndexes (){
			return this.dataModel.getNumberOfUsers();
		}

		@Override
		public void beforeRun() { }

		@Override
		public void run (int userIndex) {

			User user = dataModel.getUserAt(userIndex);

			int itemIndex = 0;

			for (int j = 0; j < user.getNumberOfRatings(); j++) {

				while (dataModel.getItemAt(itemIndex).getItemCode().compareTo(user.getItems().get(j)) < 0) itemIndex++;

				// Get error
				double error = user.getRatingAt(j) - Pmf.this.getPrediction(userIndex, itemIndex);

				// Update p_u
				Double [] p_u = Pmf.this.getUserFactors(userIndex);
				Double [] q_i = Pmf.this.getItemFactors(itemIndex);

				for (int k = 0; k < Pmf.this.numFactors; k++)	{
					p_u[k] += Pmf.this.gamma * (error * q_i[k] - Pmf.this.lambda * p_u[k]);
				}

				//Pmf.this.setUserFactors(userIndex, p_u);

				// Update biases if needed
				if (Pmf.this.biases) {
					double b_u = Pmf.this.getUserBias(userIndex);

					b_u += Pmf.this.gamma * (error - Pmf.this.lambda * b_u);

					Pmf.this.setUserBias(userIndex, b_u);
				}
			}
		}

		@Override
		public void afterRun() { }
	}

	/**
	 * Auxiliary inner class to parallelize item factors computation
	 * @author Fernando Ortega
	 */
	private class UpdateItemsFactors extends PartibleThreads {

		public UpdateItemsFactors(DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public int getTotalIndexes (){
			return this.dataModel.getNumberOfUsers();
		}

		@Override
		public void beforeRun() { }

		@Override
		public void afterRun() { }

		@Override
		public void run(int itemIndex) {

			Item item = this.dataModel.getItemAt(itemIndex);

			int userIndex = 0;

			for (int v = 0; v < item.getNumberOfRatings(); v++)
			{
				while (this.dataModel.getUserAt(userIndex).getUserCode().compareTo(item.getUserAt(v)) < 0) userIndex++; //TODO: Check, could be reversed

				// Get error
				double error = item.getRatingAt(v) - Pmf.this.getPrediction(userIndex, itemIndex);

				// Update q_i
				Double [] q_i = Pmf.this.getItemFactors(itemIndex);
				Double [] p_u = Pmf.this.getUserFactors(userIndex);

				for (int k = 0; k < Pmf.this.numFactors; k++) {
					q_i[k] += Pmf.this.gamma * (error * p_u[k] - Pmf.this.lambda * q_i[k]);
				}

				//Pmf.this.setItemFactors(itemIndex, q_i);

				// Update b_i if needed
				if (Pmf.this.biases) {
					double b_i = Pmf.this.getItemBias(itemIndex);

					b_i += Pmf.this.gamma * (error - Pmf.this.lambda * b_i);

					Pmf.this.setItemBias(itemIndex, b_i);
				}
			}
		}
	}


	/**
	 * Get a random number between min and max
	 * @param min Minimum random value
	 * @param max Maximum random value
	 * @return Random value between min and max
	 */
	private double random (double min, double max) {
		return Math.random() * (max - min) + min;
	}

	/**
	 * Get an array of random numbers
	 * @param size Array length
	 * @param min Minimum random value
	 * @param max Maximum random value
	 * @return Array of randoms
	 */
	private Double [] random (int size, double min, double max) {
		Double [] d = new Double [size];
		for (int i = 0; i < size; i++) d[i] = this.random(min, max);
		return d;
	}
}
