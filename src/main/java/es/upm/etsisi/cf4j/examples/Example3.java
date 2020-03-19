package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasures.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.Pmf;

/**
 * Compare MAE and Precision of PMF and BMF.
 * @author Fernando Ortega
 */
public class Example3 {

	public static void main (String [] args) {

		DataSet ml1m = new RandomSplitDataSet("src/main/resources/datasets/ml1m.dat", 0.2, 0.2, "::", 43);
		DataModel datamodel = new DataModel(ml1m);

		Recommender pmf = new Pmf(datamodel, 10, 50, 43);
		pmf.fit();

		QualityMeasure mae = new MAE(pmf);
		System.out.println("\nMAE: " + mae.getScore());
	}
}
