package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;
import org.apache.commons.math3.special.Gamma;

import java.util.Random;

/**
 * Implements Gopalan, P., Hofman, J. M., &amp; Blei, D. M. (2015, July). Scalable Recommendation with Hierarchical
 * Poisson Factorization. In UAI (pp. 326-335).
 */
public class Hpf extends Recommender {

    /**
     * Number of latent factors
     */
    private int numFactors;

    /**
     * Number of iterations
     */
    private int numIters;

    // Model hyperparameters
    private double a;
    private double aPrime;
    private double c;
    private double cPrime;
    private double bPrime;
    private double dPrime;

    // Model parameters
    private double[][] gamma;
    private double[][] gammaShp;
    private double[][] gammaRte;
    private double kappaShp;
    private double[] kappaRte;
    private double[][] lambda;
    private double[][] lambdaShp;
    private double[][] lambdaRte;
    private double tauShp;
    private double[] tauRte;

    /**
     * Models constructor
     * @param datamodel DataModel instace
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     */
    public Hpf(DataModel datamodel, int numFactors, int numIters) {
        this(datamodel, numFactors, numIters, System.currentTimeMillis());
    }

    /**
     * Models constructor
     * @param datamodel DataModel instace
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     * @param seed Seed for random numbers generation
     */
    public Hpf(DataModel datamodel, int numFactors, int numIters, long seed) {
        this(datamodel, numFactors, numIters, 0.3, 0.3, 1.0, 0.3, 0.3, 1.0, seed);
    }

    /**
     * Models constructor
     * @param datamodel DataModel instace
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     * @param a Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param aPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param bPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param c Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param cPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param dPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     */
    public Hpf (DataModel datamodel, int numFactors, int numIters, double a, double aPrime,  double bPrime, double c, double cPrime, double dPrime) {
        this(datamodel, numFactors, numIters, a, aPrime, bPrime, c, cPrime, dPrime, System.currentTimeMillis());
    }

    /**
     * Models constructor
     * @param datamodel DataModel instace
     * @param numFactors Number of latent factors
     * @param numIters Number of iterations
     * @param a Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param aPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param bPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param c Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param cPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param dPrime Model hyper-parameter. Read the paper for more informacion related to this hyper-parameter.
     * @param seed Seed for random numbers generation
     */
    public Hpf (DataModel datamodel, int numFactors, int numIters, double a, double aPrime, double bPrime, double c, double cPrime, double dPrime, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;

        this.a = a;
        this.aPrime = aPrime;
        this.bPrime = bPrime;

        this.c = c;
        this.cPrime = cPrime;
        this.dPrime = dPrime;

        this.kappaShp = aPrime + numFactors * a;
        this.tauShp = cPrime + numFactors * c;

        int numUsers = datamodel.getNumberOfUsers();
        int numItems = datamodel.getNumberOfItems();

        Random generator = new Random(seed);

        this.gamma = new double[numUsers][numFactors];
        this.gammaShp = new double[numUsers][numFactors];
        this.gammaRte = new double[numUsers][numFactors];
        this.kappaRte = new double[numUsers];

        for (int u = 0; u < numUsers; u++) {
            this.kappaRte[u] = generator.nextDouble();
            for (int f = 0; f < numFactors; f++) {
                this.gammaShp[u][f] = generator.nextDouble();
                this.gammaRte[u][f] = generator.nextDouble();
            }
        }

        this.lambda = new double[numItems][numFactors];
        this.lambdaShp = new double[numItems][numFactors];
        this.lambdaRte = new double[numItems][numFactors];
        this.tauRte = new double[numItems];

        for (int i = 0; i < numItems; i++) {
            this.tauRte[i] = generator.nextDouble();
            for (int f = 0; f < numFactors; f++) {
                this.lambdaShp[i][f] = generator.nextDouble();
                this.lambdaRte[i][f] = generator.nextDouble();
            }
        }
    }

    @Override
    public void fit() {
        for (int iter = 1; iter <= numIters; iter++) {
            Parallelizer.exec(super.datamodel.getUsers(), new UpdateUsersFactors());
            Parallelizer.exec(super.datamodel.getItems(), new UpdateItemsFactors());
        }
    }

    @Override
    public double predict(int userIndex, int itemIndex) {
        double dot = Methods.dotProduct(this.gamma[userIndex], this.lambda[itemIndex]);
        double prob = 1 - Math.exp(-1 * dot);
        return prob;
    }

    /**
     * Inner class to parallelize users' update
     */
    private class UpdateUsersFactors implements Partible<User> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(User user) {
            int userIndex = user.getUserIndex();

            double[][] phi = new double[user.getNumberOfRatings()][numFactors];
            for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                int itemIndex = user.getItemAt(pos);
                for (int k = 0; k < numFactors; k++) {
                    phi[pos][k] = Math.exp(
                            Gamma.digamma(gammaShp[userIndex][k]) - Math.log(gammaRte[userIndex][k]) +
                            Gamma.digamma(lambdaShp[itemIndex][k]) - Math.log(lambdaRte[itemIndex][k])
                    );
                }
            }

            for (int k = 0; k < numFactors; k++) {
                gammaShp[userIndex][k] = a;
                gammaRte[userIndex][k] = kappaShp / kappaRte[userIndex];

                for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                    int itemIndex = user.getItemAt(pos);
                    double rating = user.getRatingAt(pos);

                    gammaShp[userIndex][k] += rating * phi[pos][k];
                    gammaRte[userIndex][k] += lambdaShp[itemIndex][k] / lambdaRte[itemIndex][k];
                }
            }

            kappaRte[userIndex] = cPrime / dPrime;
            for (int k = 0; k < numFactors; k++) {
                kappaRte[userIndex] += gammaShp[userIndex][k] / gammaRte[userIndex][k];
            }
        }

        @Override
        public void afterRun() {
            for (int userIndex = 0; userIndex < datamodel.getNumberOfUsers(); userIndex++) {
                for (int k = 0; k < numFactors; k++) {
                    gamma[userIndex][k] = gammaShp[userIndex][k] / gammaRte[userIndex][k];
                }
            }
        }
    }

    /**
     * Inner class to parallelize items' update
     */
    private class UpdateItemsFactors implements Partible<Item> {

        @Override
        public void beforeRun() { }

        @Override
        public void run(Item item) {
            int itemIndex = item.getItemIndex();

            double[][] phi = new double[item.getNumberOfRatings()][numFactors];
            for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
                int userIndex = item.getUserAt(pos);
                for (int k = 0; k < numFactors; k++) {
                    phi[pos][k] = Math.exp(
                            Gamma.digamma(gammaShp[userIndex][k]) - Math.log(gammaRte[userIndex][k]) +
                            Gamma.digamma(lambdaShp[itemIndex][k]) - Math.log(lambdaRte[itemIndex][k])
                    );
                }
            }

            for (int k = 0; k < numFactors; k++) {
                lambdaShp[itemIndex][k] = c;
                lambdaRte[itemIndex][k] = tauShp / tauRte[itemIndex];

                for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
                    int userIndex = item.getUserAt(pos);
                    double rating = item.getRatingAt(pos);

                    lambdaShp[itemIndex][k] += rating * phi[pos][k];
                    lambdaRte[itemIndex][k] += gammaShp[userIndex][k] / gammaRte[userIndex][k];
                }
            }

            tauRte[itemIndex] = cPrime / dPrime;
            for (int k = 0; k < numFactors; k++) {
                tauRte[itemIndex] += lambdaShp[itemIndex][k] / lambdaRte[itemIndex][k];
            }
        }

        @Override
        public void afterRun() {
            for (int itemIndex = 0; itemIndex < datamodel.getNumberOfItems(); itemIndex++) {
                for (int k = 0; k < numFactors; k++) {
                    lambda[itemIndex][k] = lambdaShp[itemIndex][k] / lambdaRte[itemIndex][k];
                }
            }
        }
    }
}
