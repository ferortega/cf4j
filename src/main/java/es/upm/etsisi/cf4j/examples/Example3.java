package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Discovery;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Diversity;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Novelty;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.NMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

/**
 * Compare MAE and Precision of PMF and BMF.
 * @author Fernando Ortega
 */
public class Example3 {

	public static void main (String [] args) {

		DataSet ml1m = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", 43);
		DataModel datamodel = new DataModel(ml1m);

		Recommender pmf = new PMF(datamodel, 10, 50, 43);
		pmf.fit();

		QualityMeasure mae = new MAE(pmf);
		System.out.println("\nMAE: " + mae.getScore());

		QualityMeasure novelty = new Novelty(pmf, 10);
		System.out.println("\nNovelty: " + novelty.getScore());

		QualityMeasure discovery = new Discovery(pmf, 10);
		System.out.println("\nDiscovery: " + discovery.getScore());

		QualityMeasure diversity = new Diversity(pmf, 10);
		System.out.println("\nDiversity: " + diversity.getScore());

		Recommender nmf = new NMF(datamodel, 10, 50, 43);
		nmf.fit();

		novelty = new Novelty(nmf, 10);
		System.out.println("\nNovelty: " + novelty.getScore());

		discovery = new Discovery(nmf, 10);
		System.out.println("\nDiscovery: " + discovery.getScore());

		diversity = new Diversity(nmf, 10);
		System.out.println("\nDiversity: " + diversity.getScore());
	}
}
