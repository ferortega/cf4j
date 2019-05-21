package cf4j.algorithms.model.matrixFactorization;

import java.util.concurrent.locks.ReentrantLock;

import cf4j.process.ItemPartible;
import org.apache.commons.math3.special.Gamma;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.User;
import cf4j.process.Processor;
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
	 * DataModel where operate
	 */
	private DataModel dataModel;

	/**
	 * Model constructor
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param alpha This parameter is related to the possibility of obtaining overlapping
	 * 	groups of users sharing the same tastes
	 * @param beta Amount of evidence that the algorithm requires to deduce that a group
	 * 	of users likes an item
	 */
	public Bmf (DataModel dataModel, int numFactors, int numIters, double alpha, double beta) {
		this(dataModel, numFactors, numIters, alpha, beta, DEFAULT_R);
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
	public Bmf (DataModel dataModel, int numFactors, int numIters, double alpha, double beta, double r) {

		this.dataModel = dataModel;
		this.numFactors = numFactors;
		this.numIters = numIters;
		this.alpha = alpha;
		this.beta = beta;
		this.r = r;

		// Users initialization
		for (int u = 0; u < dataModel.getNumberOfUsers(); u++) {
			this.setUserGamma(u, this.random(this.numFactors));
		}

		// Items initialization
		for (int i = 0; i < dataModel.getNumberOfItems(); i++) {
			this.setItemEPlus(i, this.random(this.numFactors));
			this.setItemEMinus(i, this.random(this.numFactors));
		}

		this.dataModel.calculateMetrics();
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

			Processor.getInstance().parallelExec(new UpdateModel(this.dataModel), false);

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
		Double [] gamma = this.getUserGamma(userIndex);

		double sum = 0;
		for (double g : gamma) sum += g;

		Double [] a = new Double [this.numFactors];
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
	public Double [] getItemFactors (int itemIndex) {
		Double [] ePlus = this.getItemEPlus(itemIndex);
		Double [] eMinus = this.getItemEMinus(itemIndex);

		Double [] b = new Double [this.numFactors];
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
	public Double [] getUserGamma (int userIndex) {
		User user = this.dataModel.getUserAt(userIndex);
		return user.getDataBank().getDoubleArray(USER_GAMMA_KEY);
	}

	/**
	 * Set user gamma
	 * @param userIndex User index
	 * @param gamma User gamma
	 */
	private void setUserGamma (int userIndex, Double [] gamma) 	{
		User user = this.dataModel.getUserAt(userIndex);
		user.getDataBank().setDoubleArray(USER_GAMMA_KEY, gamma);
	}

	/**
	 * Get item E+
	 * @param itemIndex Item index
	 * @return Item E+
	 */
	public Double [] getItemEPlus (int itemIndex) {
		Item item = this.dataModel.getItemAt(itemIndex);
		return item.getDataBank().getDoubleArray(ITEM_E_PLUS_KEY).clone();
	}

	/**
	 * Set item E+
	 * @param itemIndex Item index
	 * @param ePlus Item E+
	 */
	private void setItemEPlus (int itemIndex, Double [] ePlus) {
		Item item = this.dataModel.getItemAt(itemIndex);
		item.getDataBank().setDoubleArray(ITEM_E_PLUS_KEY, ePlus);
	}

	/**
	 * Get item E-
	 * @param itemIndex Item index
	 * @return Item E-
	 */
	public Double [] getItemEMinus (int itemIndex) {
		Item item = this.dataModel.getItemAt(itemIndex);
		return item.getDataBank().getDoubleArray(ITEM_E_MINUS_KEY).clone();
	}

	/**
	 * Set item E-
	 * @param itemIndex Item index
	 * @param eMinus Item E-
	 */
	private void setItemEMinus (int itemIndex, Double [] eMinus) 	{
		Item item = this.dataModel.getItemAt(itemIndex);
		item.getDataBank().setDoubleArray(ITEM_E_MINUS_KEY, eMinus);
	}

	/**
	 * Computes a rating prediction
	 * @param userIndex User index
	 * @param itemIndex Item index
	 * @return Prediction
	 */
	public double getPrediction (int userIndex, int itemIndex) {
		Double [] a = this.getUserFactors(userIndex);
		Double [] b = this.getItemFactors(itemIndex);
		double prediction = Methods.dotProduct(a, b);

		double max = this.dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY);
		double min = this.dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY);

		return prediction * (max - min) + min;
	}

	/**
	 * Auxiliary inner class to parallelize model update
	 * @author Fernando Ortega
	 */
	private class UpdateModel extends ItemPartible {

		private final int NUM_LOCKS = 100;

		private ReentrantLock [] locks;

		private Double [][] gamma;

		private Double [][] ePlus;

		private Double [][] eMinus;

		public UpdateModel (DataModel dataModel) {
			super(dataModel);

			// Locks avoid problem while users gammas are updated in different threads
			this.locks = new ReentrantLock [NUM_LOCKS];
			for (int i = 0; i < NUM_LOCKS; i++) {
				this.locks[i] = new ReentrantLock();
			}

			int numUsers = dataModel.getNumberOfUsers();
			int numItems = dataModel.getNumberOfItems();
			int numFactors = Bmf.this.numFactors;

			this.gamma = new Double [numUsers][numFactors];
			this.ePlus = new Double [numItems][numFactors];
			this.eMinus = new Double [numItems][numFactors];
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

			this.dataModel.calculateMetrics();
		}

		@Override
		public void run (int itemIndex) {

			Item item = this.dataModel.getItemAt(itemIndex);

			Double [] ePlus = Bmf.this.getItemEPlus(itemIndex);
			Double [] eMinus = Bmf.this.getItemEMinus(itemIndex);

			int userIndex = 0;

			for (int u = 0; u < item.getNumberOfRatings(); u++) {

				// Arrays of ref codes are sorted
				while (this.dataModel.getUserAt(userIndex).getUserCode().compareTo(item.getUserAt(u)) < 0) userIndex++; //TODO: Check, could be reversed.

				Double [] gamma = Bmf.this.getUserGamma(userIndex);

				double [] lambda = new double [Bmf.this.numFactors];

				double rating = (item.getRatingAt(u) - this.dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY))
						/ (this.dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY) - this.dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY));

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
			for (int userIndex = 0; userIndex < this.dataModel.getNumberOfUsers(); userIndex++) {
				Bmf.this.setUserGamma(userIndex, this.gamma[userIndex]);
			}

			for (int itemIndex = 0; itemIndex < this.dataModel.getNumberOfItems(); itemIndex++) {
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
	private Double [] random (int size) {
		Double [] d = new Double [size];
		for (int i = 0; i < size; i++) d[i] = Math.random();
		return d;
	}
}
