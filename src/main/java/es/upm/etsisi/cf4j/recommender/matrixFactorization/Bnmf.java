package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;
import org.apache.commons.math3.special.Gamma;


/**
 * Implements Probabilist Matrix Factorization: Hernando, A., Bobadilla, J., &amp; Ortega, F. (2016).
 * A non negative matrix factorization for collaborative filtering recommender systems on a
 * Bayesian probabilistic model. Knowledge-Based Systems, 97, 188-202.
 *
 * @author Fernando Ortega
 */
public class Bnmf extends Recommender {

	private final static double DEFAULT_R = 4;

	/**
	 * User factors
	 */
	private double[][] a;

	/**
	 * Item factors
	 */
	private double[][] b;


	private double[][] gamma;


	private double[][] epsilonPlus;

	private double[][] epsilonMinus;


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
	public Bnmf(DataModel dataModel, int numFactors, int numIters, double alpha, double beta) {
		this(dataModel, numFactors, numIters, alpha, beta, DEFAULT_R, (long) (Math.random() * 1E10));
	}

	public Bnmf(DataModel dataModel, int numFactors, int numIters, double alpha, double beta, long seed) {
		this(dataModel, numFactors, numIters, alpha, beta, DEFAULT_R, seed);
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
	public Bnmf(DataModel datamodel, int numFactors, int numIters, double alpha, double beta, double r, long seed) {
		super(datamodel);

		this.numFactors = numFactors;
		this.numIters = numIters;
		this.alpha = alpha;
		this.beta = beta;
		this.r = r;

		Random rand = new Random(seed);

		// Users initialization
		this.gamma = new double[datamodel.getNumberOfUsers()][numFactors];
		for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
			for (int k = 0; k < numFactors; k++) {
				this.gamma[u][k] = rand.nextDouble();
			}
		}

		// Items initialization
		this.epsilonPlus = new double[datamodel.getNumberOfItems()][numFactors];
		this.epsilonMinus = new double[datamodel.getNumberOfItems()][numFactors];
		for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
			for (int k = 0; k < numFactors; k++) {
				this.epsilonPlus[i][k] = rand.nextDouble();
				this.epsilonMinus[i][k] = rand.nextDouble();
			}
		}
	}

	/**
	 * Get the number of topics of the model
	 * @return Number of topics
	 */
	public int getNumberOfTopics() {
		return this.numFactors;
	}

	/**
	 * Estimate the latent model factors
	 */
	public void fit() {
		System.out.println("\nProcessing BNMF...");

		for (int iter = 1; iter <= this.numIters; iter++) {

			Parallelizer.exec(datamodel.getItems(), new UpdateModel());

			if ((iter % 10) == 0) System.out.print(".");
			if ((iter % 100) == 0) System.out.println(iter + " iterations");
		}

		// set user factors
		this.a = new double[this.datamodel.getNumberOfUsers()][this.numFactors];
		for (int userIndex = 0; userIndex < this.datamodel.getNumberOfUsers(); userIndex++) {
			double sum = 0;
			for (int k = 0; k < this.numFactors; k++) {
				sum += this.gamma[userIndex][k];
			}

			for (int k = 0; k < this.numFactors; k++) {
				this.a[userIndex][k] = this.gamma[userIndex][k] / sum;
			}
		}

		// set item factors
		this.b = new double[this.datamodel.getNumberOfItems()][this.numFactors];
		for (int itemIndex = 0; itemIndex < this.datamodel.getNumberOfItems(); itemIndex++) {
			for (int k = 0; k < this.numFactors; k++) {
				this.b[itemIndex][k] = this.epsilonPlus[itemIndex][k] / (this.epsilonPlus[itemIndex][k] + this.epsilonMinus[itemIndex][k]);
			}
		}

	}

	/**
	 * Computes a rating prediction
	 * @param userIndex User index
	 * @param itemIndex Item index
	 * @return Prediction
	 */
	public double predict(int userIndex, int itemIndex) {
		double prediction = Methods.dotProduct(this.a[userIndex], this.b[itemIndex]);
		return prediction * (datamodel.getMaxRating() - datamodel.getMinRating()) + datamodel.getMinRating(); // TODO review this
	}

	/**
	 * Auxiliary inner class to parallelize model update
	 * @author Fernando Ortega
	 */
	private class UpdateModel implements Partible<Item> {

		private final int NUM_LOCKS = 100;

		private ReentrantLock [] locks;

		private double [][] gamma;

		private double [][] epsilonPlus;

		private double [][] epsilonMinus;

		public UpdateModel() {

			// Locks avoid problem while users gammas are updated in different threads
			this.locks = new ReentrantLock [NUM_LOCKS];
			for (int i = 0; i < NUM_LOCKS; i++) {
				this.locks[i] = new ReentrantLock();
			}

			this.gamma = new double[datamodel.getNumberOfUsers()][numFactors];
			this.epsilonPlus = new double[datamodel.getNumberOfItems()][numFactors];
			this.epsilonMinus = new double[datamodel.getNumberOfItems()][numFactors];
		}

		@Override
		public void beforeRun() {

			// Init gamma
			for (int i = 0; i < this.gamma.length; i++) {
				for (int j = 0; j < this.gamma[i].length; j++) {
					this.gamma[i][j] = Bnmf.this.alpha;
				}
			}

			// Init E+ & E-
			for (int i = 0; i < this.epsilonPlus.length; i++) {
				for (int j = 0; j < this.epsilonPlus[i].length; j++) {
					this.epsilonPlus[i][j] = Bnmf.this.beta;
					this.epsilonMinus[i][j] = Bnmf.this.beta;
				}
			}
		}

		@Override
		public void run(Item item) {
			int itemIndex = item.getIndex();

			for (int u = 0; u < item.getNumberOfRatings(); u++) {

				int userIndex = item.getUserAt(u);

				double [] lambda = new double [Bnmf.this.numFactors];

				double rating = (item.getRatingAt(u) - datamodel.getMinRating()) / (datamodel.getMaxRatring() - dataModel.getMinRating());

				double sum = 0;

				// Compute lambda
				for (int k = 0; k < Bnmf.this.numFactors; k++) {
					lambda[k] = Math.exp(
						Gamma.digamma(Bnmf.this.gamma[userIndex][k]) +
						Bnmf.this.r * rating * Gamma.digamma(Bnmf.this.epsilonPlus[itemIndex][k]) +
						Bnmf.this.r * (1 - rating) * Gamma.digamma(Bnmf.this.epsilonMinus[itemIndex][k]) -
						Bnmf.this.r * Gamma.digamma(Bnmf.this.epsilonPlus[itemIndex][k] + Bnmf.this.epsilonMinus[itemIndex][k])
					);

					sum += lambda[k];
				}

				// Update model
				for (int k = 0; k < Bnmf.this.numFactors; k++) {

					double l = lambda[k] / sum;

					// Update E+ & E-
					this.epsilonPlus[itemIndex][k] += l * Bnmf.this.r * rating;
					this.epsilonMinus[itemIndex][k] += l * Bnmf.this.r * (1 - rating);

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
			Bnmf.this.gamma = this.gamma;
			Bnmf.this.epsilonPlus = this.epsilonPlus;
			Bnmf.this.epsilonMinus = this.epsilonMinus;
		}
	}
}
