package cf4j.model.matrixFactorization;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.math3.special.Gamma;

import cf4j.Item;
import cf4j.ItemsPartible;
import cf4j.Kernel;
import cf4j.Processor;
import cf4j.User;
import cf4j.utils.Methods;

/**
 * Implements Probabilist Matrix Factorization: Hernando, A., Bobadilla, J., 	&amp; Ortega, F. (2016).
 * A non negative matrix factorization for collaborative filtering recommender systems on a
 * Bayesian probabilistic model. Knowledge-Based Systems, 97, 188-202.
 *
 * @author Fernando Ortega
 */
public class Bmf implements FactorizationModel {

	private final static String USER_GAMMA_KEY = "bmf-user-gamma";
	private final static String ITEM_E_PLUS_KEY = "bmf-item-e+";
	private final static String ITEM_E_MINUS_KEY = "bmf-item-e-";

	private final static double DEFAULT_R = 4;

	/**
	 * This parameter is related to the possibility of obtaining overlapping
	 * groups of users sharing the same tastes.
	 */
	private double alpha;

	/**
	 * Amount of evidence that the algorithm requires to deduce that a group
	 * of users likes an item.
	 */
	private double beta;

	/**
	 * Parameter of the binomial distribution (fixed to 4)
	 */
	private double r;

	/**
	 * Number of factors
	 */
	private int numFactors;

	/**
	 * Number of iterations
	 */
	private int numIters;

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param alpha This parameter is related to the possibility of obtaining overlapping
	 * 	groups of users sharing the same tastes
	 * @param beta Amount of evidence that the algorithm requires to deduce that a group
	 * 	of users likes an item
	 */
	public Bmf (int numFactors, int numIters, double alpha, double beta) {
		this(numFactors, numIters, alpha, beta, DEFAULT_R);
	}

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param alpha This parameter is related to the possibility of obtaining overlapping
	 * 	groups of users sharing the same tastes
	 * @param beta Amount of evidence that the algorithm requires to deduce that a group
	 * 	of users likes an item
	 * @param r Parameter of the binomial distribution (fixed to 4)
	 */
	public Bmf (int numFactors, int numIters, double alpha, double beta, double r) {

		this.numFactors = numFactors;
		this.numIters = numIters;
		this.alpha = alpha;
		this.beta = beta;
		this.r = r;

		// Users initialization
		for (int u = 0; u < Kernel.gi().getNumberOfUsers(); u++) {
			this.setUserGamma(u, this.random(this.numFactors));
		}

		// Items initialization
		for (int i = 0; i < Kernel.gi().getNumberOfItems(); i++) {
			this.setItemEPlus(i, this.random(this.numFactors));
			this.setItemEMinus(i, this.random(this.numFactors));
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
	 * Estimate the latent model factors
	 */
	public void train () {
		System.out.println("\nProcessing BMF...");

		for (int iter = 1; iter <= this.numIters; iter++) {
			Processor.getInstance().itemsProcess(new UpdateModel(), false);

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
		double [] gamma = this.getUserGamma(userIndex);

		double sum = 0;
		for (double g : gamma) sum += g;

		double [] a = new double [this.numFactors];
		for (int k = 0; k < a.length; k++) {
			a[k] = gamma[k] / sum;
		}

		return a;
	}

	/**
	 * Get item factors
	 * @param itemIndex Item index
	 * @return Item factors
	 */
	public double [] getItemFactors (int itemIndex) {
		double [] ePlus = this.getItemEPlus(itemIndex);
		double [] eMinus = this.getItemEMinus(itemIndex);

		double [] b = new double [this.numFactors];
		for (int k = 0; k < b.length; k++) {
			b[k] = ePlus[k] / (ePlus[k] + eMinus[k]);
		}

		return b;
	}

	/**
	 * Get user gamma
	 * @param userIndex User index
	 * @return User gamma
	 */
	public double [] getUserGamma (int userIndex) {
		User user = Kernel.gi().getUsers()[userIndex];
		return (double []) user.get(USER_GAMMA_KEY);
	}

	/**
	 * Set user gamma
	 * @param userIndex User index
	 * @param gamma User gamma
	 */
	private void setUserGamma (int userIndex, double [] gamma) 	{
		User user = Kernel.gi().getUsers()[userIndex];
		user.put(USER_GAMMA_KEY, gamma);
	}

	/**
	 * Get item E+
	 * @param itemIndex Item index
	 * @return Item E+
	 */
	public double [] getItemEPlus (int itemIndex) {
		Item item = Kernel.gi().getItems()[itemIndex];
		return ((double []) item.get(ITEM_E_PLUS_KEY)).clone();
	}

	/**
	 * Set item E+
	 * @param itemIndex Item index
	 * @param ePlus Item E+
	 */
	private void setItemEPlus (int itemIndex, double [] ePlus) {
		Item item = Kernel.gi().getItems()[itemIndex];
		item.put(ITEM_E_PLUS_KEY, ePlus);
	}

	/**
	 * Get item E-
	 * @param itemIndex Item index
	 * @return Item E-
	 */
	public double [] getItemEMinus (int itemIndex) {
		Item item = Kernel.gi().getItems()[itemIndex];
		return ((double []) item.get(ITEM_E_MINUS_KEY)).clone();
	}

	/**
	 * Set item E-
	 * @param itemIndex Item index
	 * @param eMinus Item E-
	 */
	private void setItemEMinus (int itemIndex, double [] eMinus) 	{
		Item item = Kernel.gi().getItems()[itemIndex];
		item.put(ITEM_E_MINUS_KEY, eMinus);
	}

	/**
	 * Computes a rating prediction
	 * @param userIndex User index
	 * @param itemIndex Item index
	 * @return Prediction
	 */
	public double getPrediction (int userIndex, int itemIndex) {
		double [] a = this.getUserFactors(userIndex);
		double [] b = this.getItemFactors(itemIndex);
		double prediction = Methods.dotProduct(a, b);

		double max = Kernel.gi().getMaxRating();
		double min = Kernel.gi().getMinRating();

		return prediction * (max - min) + min;
	}

	/**
	 * Auxiliary inner class to parallelize model update
	 * @author Fernando Ortega
	 */
	private class UpdateModel implements ItemsPartible {

		private final int NUM_LOCKS = 100;

		private ReentrantLock [] locks;

		private double [][] gamma;

		private double [][] ePlus;

		private double [][] eMinus;

		public UpdateModel () {

			// Locks avoid problem while users gammas are updated in different threads
			this.locks = new ReentrantLock [NUM_LOCKS];
			for (int i = 0; i < NUM_LOCKS; i++) {
				this.locks[i] = new ReentrantLock();
			}

			int numUsers = Kernel.gi().getNumberOfUsers();
			int numItems = Kernel.gi().getNumberOfItems();
			int numFactors = Bmf.this.numFactors;

			this.gamma = new double [numUsers][numFactors];
			this.ePlus = new double [numItems][numFactors];
			this.eMinus = new double [numItems][numFactors];
		}

		@Override
		public void beforeRun () {

			// Init gamma
			for (int i = 0; i < this.gamma.length; i++) {
				for (int j = 0; j < this.gamma[i].length; j++) {
					this.gamma[i][j] = Bmf.this.alpha;
				}
			}

			// Init E+ & E-
			for (int i = 0; i < this.ePlus.length; i++) {
				for (int j = 0; j < this.ePlus[i].length; j++) {
					this.ePlus[i][j] = Bmf.this.beta;
					this.eMinus[i][j] = Bmf.this.beta;
				}
			}
		}

		@Override
		public void run (int itemIndex) {

			Item item = Kernel.gi().getItems()[itemIndex];

			double [] ePlus = Bmf.this.getItemEPlus(itemIndex);
			double [] eMinus = Bmf.this.getItemEMinus(itemIndex);

			int userIndex = 0;

			for (int u = 0; u < item.getNumberOfRatings(); u++) {

				// Arrays of ref codes are sorted
				while (Kernel.gi().getUsers()[userIndex].getUserCode() < item.getUsers()[u]) userIndex++;

				double [] gamma = Bmf.this.getUserGamma(userIndex);

				double [] lambda = new double [Bmf.this.numFactors];

				double rating = (item.getRatings()[u] - Kernel.gi().getMinRating())
						/ (Kernel.gi().getMaxRating() - Kernel.gi().getMinRating());

				double acc = 0;

				// Compute lambda
				for (int k = 0; k < Bmf.this.numFactors; k++) {
					lambda[k] = Math.exp(
						Gamma.digamma(gamma[k]) +
						Bmf.this.r * rating * Gamma.digamma(ePlus[k]) +
						Bmf.this.r * (1 - rating) * Gamma.digamma(eMinus[k]) -
						Bmf.this.r * Gamma.digamma(ePlus[k] + eMinus[k])
					);

					acc += lambda[k];
				}

				// Update model
				for (int k = 0; k < Bmf.this.numFactors; k++) {

					double l = lambda[k] / acc;

					// Update E+ & E-
					this.ePlus[itemIndex][k] += l * Bmf.this.r * rating;
					this.eMinus[itemIndex][k] += l * Bmf.this.r * (1 - rating);

					// Update gamma: user must be block to avoid concurrency problems
					int lockIndex = userIndex % this.locks.length;
					this.locks[lockIndex].lock();
					this.gamma[userIndex][k] += l;
					this.locks[lockIndex].unlock();
				}
			}
		}

		@Override
		public void afterRun() {
			for (int userIndex = 0; userIndex < Kernel.gi().getNumberOfUsers(); userIndex++) {
				Bmf.this.setUserGamma(userIndex, this.gamma[userIndex]);
			}

			for (int itemIndex = 0; itemIndex < Kernel.gi().getNumberOfItems(); itemIndex++) {
				Bmf.this.setItemEPlus(itemIndex, this.ePlus[itemIndex]);
				Bmf.this.setItemEMinus(itemIndex, this.eMinus[itemIndex]);
			}
		}
	}

	/**
	 * Get an array of random numbers between 0, 1)
	 * @param size Array length
	 * @return Array of random
	 */
	private double [] random (int size) {
		double [] d = new double [size];
		for (int i = 0; i < size; i++) d[i] = Math.random();
		return d;
	}
}
