package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;

import java.util.Random;

/**
 * Implements Mnih, A., &amp; Salakhutdinov, R. R. (2008). Probabilistic matrix factorization. In Advances in neural
 * information processing systems (pp. 1257-1264).
 * @author Fernando Ortega
 */
public class Pmf extends Recommender {

	private final static double DEFAULT_GAMMA = 0.01;
	private final static double DEFAULT_LAMBDA = 0.1;

	/**
	 * User factors
	 */
	private double[][] p;

	/**
	 * Item factors
	 */
	private double[][] q;

	/**
	 * Learning rate
	 */
	private double gamma;

	/**
	 * Regularization parameter
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
	 * Model constructor
	 * @param datamodel DataModel instance
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 */
	public Pmf(DataModel datamodel, int numFactors, int numIters)	{
		this(datamodel, numFactors, numIters, DEFAULT_LAMBDA);
	}

	/**
	 * Model constructor
	 * @param datamodel DataModel instance
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param seed Seed for random numbers generation
	 */
	public Pmf(DataModel datamodel, int numFactors, int numIters, long seed)	{
		this(datamodel, numFactors, numIters, DEFAULT_LAMBDA, DEFAULT_GAMMA, seed);
	}

	/**
	 * Model constructor
	 * @param datamodel
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 */
	public Pmf(DataModel datamodel, int numFactors, int numIters, double lambda) {
		this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, System.currentTimeMillis());
	}

	/**
	 * Model constructor
	 * @param datamodel DataModel instance
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param seed Seed for random numbers generation
	 */
	public Pmf(DataModel datamodel, int numFactors, int numIters, double lambda, long seed) {
		this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, seed);
	}

	/**
	 * Model constructor
	 * @param datamodel DataModel instance
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param lambda Regularization parameter
	 * @param gamma Learning rate parameter
	 * @param seed Seed for random numbers generation
	 */
	public Pmf(DataModel datamodel, int numFactors, int numIters, double lambda, double gamma, long seed) {
		super(datamodel);

		this.numFactors = numFactors;
		this.numIters = numIters;
		this.lambda = lambda;
		this.gamma = gamma;

		Random rand = new Random(seed);

		// Users initialization
		this.p = new double[datamodel.getNumberOfUsers()][numFactors];
		for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
			for (int k = 0; k < numFactors; k++) {
				this.p[u][k] = rand.nextDouble() * 2 - 1;
			}
		}

		// Items initialization
		this.q = new double[datamodel.getNumberOfItems()][numFactors];
		for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
			for (int k = 0; k < numFactors; k++) {
				this.q[i][k] = rand.nextDouble() * 2 - 1;
			}
		}
	}

	/**
	 * Get the number of factors of the model
	 * @return Number of factors
	 */
	public int getNumFactors() {
		return this.numFactors;
	}

	/**
	 * Get the number of iterations
	 * @return Number of iterations
	 */
	public int getNumIters() {
		return this.numIters;
	}

	/**
	 * Get the regularization parameter of the model
	 * @return Lambda
	 */
	public double getLambda() {
		return this.lambda;
	}

	/**
	 * Get the learning rate parameter of the model
	 * @return Gamma
	 */
	public double getGamma() {
		return this.gamma;
	}

	@Override
	public void fit() {

		System.out.println("\nProcessing PMF...");

		for (int iter = 1; iter <= this.numIters; iter++) {

			// ALS: fix q_i and update p_u -> fix p_u and update q_i
			Parallelizer.exec(this.datamodel.getUsers(), new UpdateUsersFactors());
			Parallelizer.exec(this.datamodel.getItems(), new UpdateItemsFactors());

			if ((iter % 10) == 0) System.out.print(".");
			if ((iter % 100) == 0) System.out.println(iter + " iterations");
		}
	}

	@Override
	public double predict(int userIndex, int itemIndex) {
		return Methods.dotProduct(this.p[userIndex], this.q[itemIndex]);
	}

	/**
	 * Auxiliary inner class to parallelize user factors computation
	 */
	private class UpdateUsersFactors implements Partible<User> {

		@Override
		public void beforeRun() { }

		@Override
		public void run(User user) {
			int userIndex = user.getUserIndex();

			for (int j = 0; j < user.getNumberOfRatings(); j++) {

				int itemIndex = user.getItemAt(j);

				double error = user.getRatingAt(j) - predict(userIndex, itemIndex);

				for (int k = 0; k < numFactors; k++)	{
					p[userIndex][k] += gamma * (error * q[itemIndex][k] - lambda * p[userIndex][k]);
				}
			}
		}

		@Override
		public void afterRun() { }
	}

	/**
	 * Auxiliary inner class to parallelize item factors computation
	 */
	private class UpdateItemsFactors implements Partible<Item> {

		@Override
		public void beforeRun() { }

		@Override
		public void run(Item item) {
			int itemIndex = item.getItemIndex();

			for (int v = 0; v < item.getNumberOfRatings(); v++) {

				int userIndex = item.getUserAt(v);

				// Get error
				double error = item.getRatingAt(v) - predict(userIndex, itemIndex);

				for (int k = 0; k < numFactors; k++) {
					q[itemIndex][k] += gamma * (error * p[userIndex][k] - lambda * q[itemIndex][k]);
				}
			}
		}

		@Override
		public void afterRun() { }
	}
}
