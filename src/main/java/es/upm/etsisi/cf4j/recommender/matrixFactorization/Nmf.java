package es.upm.etsisi.cf4j.recommender.matrixFactorization;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;

import java.util.Random;

public class Nmf extends Recommender {

	/**
	 * User factors
	 */
	private double[][] w;

	/**
	 * Item factors
	 */
	private double[][] h;

	private int numFactors;

	private int numIters;


	public Nmf(DataModel datamodel, int numFactors, int numIters) {
		this(datamodel, numFactors, numIters, (long) (Math.random() * 1E10))
	}

	public Nmf(DataModel datamodel, int numFactors, int numIters, long seed) {
		super(datamodel);

		this.numFactors = numFactors;
		this.numIters = numIters;

		Random rand = new Random(seed);

		// Users initialization
		this.w = new double[datamodel.getNumberOfUsers()][numFactors];
		for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
			for (int k = 0; k < numFactors; k++) {
				this.w[u][k] = 1 - rand.nextDouble();
			}
		}

		// Items initialization
		this.h = new double[datamodel.getNumberOfItems()][numFactors];
		for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
			for (int k = 0; k < numFactors; k++) {
				this.h[i][k] = 1 - rand.nextDouble();
			}
		}
	}


	public int getNumberOfTopics () {
		return this.numFactors;
	}

	public void fit() {

		System.out.println("\nProcessing NMF...");

		for (int iter = 1; iter <= this.numIters; iter++) {
			Parallelizer.exec(this.datamodel.getUsers(), new UpdateUsersFactors());
			Parallelizer.exec(this.datamodel.getItems(), new UpdateItemsFactors());
		}
	}

	public double predict(int userIndex, int itemIndex) {
		return Methods.dotProduct(this.w[userIndex], this.h[itemIndex]);
	}

	
	private class UpdateUsersFactors implements Partible<User> {

		@Override
		public void beforeRun() { }

		@Override
		public void run(User user) {
			int userIndex = user.getIndex();

			double [] wu = w[userIndex];

			double [] predictions = new double [user.getNumberOfRatings()];
			for (int i = 0; i < user.getNumberOfRatings(); i++) {
				int itemIndex = user.getItemAt(i);
				predictions[i] = predict(userIndex, itemIndex);
			}

			for (int k = 0; k < Nmf.this.numFactors; k++) {

				double sumRatings = 0;
				double sumPredictions = 0;

				for (int i = 0; i < user.getNumberOfRatings(); i++) {
					int itemIndex = user.getItemAt(i);
					double [] hi = h[itemIndex];
					sumRatings += hi[k] * user.getRatingAt(i);
					sumPredictions += hi[k] * predictions[i];
				}

				wu[k] = wu[k] * sumRatings / (sumPredictions + 1E-10);
			}
		}

		@Override
		public void afterRun() { }
	}

	
	private class UpdateItemsFactors implements Partible<Item> {

		@Override
		public void beforeRun() { }

		@Override
		public void run(Item item) {
			int itemIndex = item.getIndex();

			double [] hi = h[itemIndex];

			double [] predictions = new double [item.getNumberOfRatings()];
			for (int u = 0; u < item.getNumberOfRatings(); u++) {
				int userIndex = item.getUser(u);
				predictions[u] = predict(userIndex, itemIndex);
			}

			for (int k = 0; k < Nmf.this.numFactors; k++) {

				double sumRatings = 0;
				double sumPredictions = 0;

				for (int u = 0; u < item.getNumberOfRatings(); u++) {
					int userIndex = item.getUser(u);
					double [] wu = w[userIndex];
					sumRatings += wu[k] * item.getRating(u);
					sumPredictions += wu[k] * predictions[u];
				}

				hi[k] = hi[k] * sumRatings / (sumPredictions + 1E-10);
			}
		}

		@Override
		public void afterRun() { }
	}
}
