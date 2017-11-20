package cf4j.model.predictions;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.TestUsersPartible;
import cf4j.model.matrixFactorization.FactorizationModel;

/**
 * Compute users predictions using a factorization model
 *
 * @see FactorizationModel
 *
 * @author Fernando Ortega
 */
public class FactorizationPrediction implements TestUsersPartible {

	protected FactorizationModel model;

	public FactorizationPrediction (FactorizationModel model) {
		this.model = model;
	}

	@Override
	public void beforeRun() { }

	@Override
	public void run (int testUserIndex) {
		TestUser user = Kernel.gi().getTestUsers()[testUserIndex];

		double [] predictions = new double [user.getNumberOfTestRatings()];

		for (int i = 0; i < user.getNumberOfTestRatings(); i++) {
			int itemCode = user.getTestItems()[i];
			int itemIndex = Kernel.gi().getItemIndex(itemCode);

			int userIndex = user.getUserIndex();

			predictions[i] = model.getPrediction(userIndex, itemIndex);
		}

		user.setPredictions(predictions);
	}

	@Override
	public void afterRun() { }
}
