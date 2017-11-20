package cf4j.model.matrixFactorization;

import cf4j.Item;
import cf4j.ItemsPartible;
import cf4j.Kernel;
import cf4j.Processor;
import cf4j.User;
import cf4j.UsersPartible;
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
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 */
	public Pmf (int numFactors, int numIters)	{
		this(numFactors, numIters, DEFAULT_LAMBDA, DEFAULT_GAMMA, true);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 */
	public Pmf (int numFactors, int numIters, double lambda) {
		this(numFactors, numIters, lambda, DEFAULT_GAMMA, true);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param gamma Learning rate parameter
	 */
	public Pmf (int numFactors, int numIters, double lambda, double gamma) {
		this(numFactors, numIters, lambda, gamma, true);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param biases Enable/disable biases in the model
	 */
	public Pmf (int numFactors, int numIters, double lambda, boolean biases) {
		this(numFactors, numIters, lambda, DEFAULT_GAMMA, biases);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param gamma Learning rate parameter
	 * @param biases Enable/disable biases in the model
	 */
	public Pmf (int numFactors, int numIters, double lambda, double gamma, boolean biases) {

		this.numFactors = numFactors;
		this.numIters = numIters;
		this.lambda = lambda;
		this.gamma = gamma;
		this.biases = biases;

		// Users initialization
		for (int u = 0; u < Kernel.gi().getNumberOfUsers(); u++) {
			this.setUserFactors(u, this.random(this.numFactors, -1, 1));
		}

		// Items initialization
		for (int i = 0; i < Kernel.gi().getNumberOfItems(); i++) {
			this.setItemFactors(i, this.random(this.numFactors, -1, 1));
		}

		// Initialize bias if needed
		if (this.biases) {

			// Users bias initialization
			for (int u = 0; u < Kernel.gi().getNumberOfUsers(); u++) {
				this.setUserBias(u, this.random(-1, 1));
			}

			// Items bias initialization
			for (int i = 0; i < Kernel.gi().getNumberOfItems(); i++) {
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

			// ALS: fix q_i and update p_u -> fix p_u and update q_i
			Processor.getInstance().usersProcess(new UpdateUsersFactors(), false);
			Processor.getInstance().itemsProcess(new UpdateItemsFactors(), false);

			if ((iter % 10) == 0) System.out.print(".");
			if ((iter % 100) == 0) System.out.println(iter + " iterations");
		}
	}

	/**
	 * Get user factors
	 * @param userIndex User index
	 * @return User factors
	 */
	public double [] getUserFactors (int userIndex) {
		User user = Kernel.gi().getUsers()[userIndex];
		return (double []) user.get(USER_FACTORS_KEY);
	}

	/**
	 * Set user factors
	 * @param userIndex User index
	 * @param factors User factors
	 */
	private void setUserFactors (int userIndex, double [] factors) {
		User user = Kernel.gi().getUsers()[userIndex];
		user.put(USER_FACTORS_KEY, factors);
	}

	/**
	 * Get item factors
	 * @param itemIndex Item index
	 * @return Item factors
	 */
	public double [] getItemFactors (int itemIndex) {
		Item item = Kernel.gi().getItems()[itemIndex];
		return (double []) item.get(ITEM_FACTORS_KEY);
	}

	/**
	 * Set item factors
	 * @param itemIndex Item index
	 * @param factors Item factors
	 */
	private void setItemFactors (int itemIndex, double [] factors) {
		Item item = Kernel.gi().getItems()[itemIndex];
		item.put(ITEM_FACTORS_KEY, factors);
	}

	/**
	 * Get user bias (if enabled)
	 * @param userIndex User index
	 * @return User bias or null
	 */
	public double getUserBias (int userIndex) {
		User user = Kernel.gi().getUsers()[userIndex];
		return (Double) user.get(USER_BIAS_KEY);
	}

	/**
	 * Set user bias
	 * @param userIndex User index
	 * @param bias User bias
	 */
	private void setUserBias (int userIndex, double bias) 	{
		User user = Kernel.gi().getUsers()[userIndex];
		user.put(USER_BIAS_KEY, bias);
	}

	/**
	 * Get item bias (if needed)
	 * @param itemIndex Item index
	 * @return Item bias
	 */
	public double getItemBias (int itemIndex) {
		Item item = Kernel.gi().getItems()[itemIndex];
		return (Double) item.get(ITEM_BIAS_KEY);
	}

	/**
	 * Set item bias
	 * @param itemIndex Item index
	 * @param bias Item bias
	 */
	private void setItemBias (int itemIndex, double bias) {
		Item item = Kernel.gi().getItems()[itemIndex];
		item.put(ITEM_BIAS_KEY, bias);
	}

	/**
	 * Computes a rating prediction
	 * @param userIndex User index
	 * @param itemIndex Item index
	 * @return Prediction
	 */
	public double getPrediction (int userIndex, int itemIndex) {
		double [] factors_u = this.getUserFactors(userIndex);
		double [] factors_i = this.getItemFactors(itemIndex);

		if (this.biases) {
			double average = Kernel.gi().getRatingAverage();

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
	private class UpdateUsersFactors implements UsersPartible {

		@Override
		public void beforeRun() { }

		@Override
		public void run (int userIndex) {

			User user = Kernel.gi().getUsers()[userIndex];

			int itemIndex = 0;

			for (int j = 0; j < user.getNumberOfRatings(); j++) {

				while (Kernel.gi().getItems()[itemIndex].getItemCode() < user.getItems()[j]) itemIndex++;

				// Get error
				double error = user.getRatings()[j] - Pmf.this.getPrediction(userIndex, itemIndex);

				// Update p_u
				double [] p_u = Pmf.this.getUserFactors(userIndex);
				double [] q_i = Pmf.this.getItemFactors(itemIndex);

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
	private class UpdateItemsFactors implements ItemsPartible {

		@Override
		public void beforeRun() { }

		@Override
		public void afterRun() { }

		@Override
		public void run(int itemIndex) {

			Item item = Kernel.gi().getItems()[itemIndex];

			int userIndex = 0;

			for (int v = 0; v < item.getNumberOfRatings(); v++)
			{
				while (Kernel.gi().getUsers()[userIndex].getUserCode() < item.getUsers()[v]) userIndex++;

				// Get error
				double error = item.getRatings()[v] - Pmf.this.getPrediction(userIndex, itemIndex);

				// Update q_i
				double [] q_i = Pmf.this.getItemFactors(itemIndex);
				double [] p_u = Pmf.this.getUserFactors(userIndex);

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
	private double [] random (int size, double min, double max) {
		double [] d = new double [size];
		for (int i = 0; i < size; i++) d[i] = this.random(min, max);
		return d;
	}
}
