package cf4j.algorithms.model.predictions;

import cf4j.algorithms.TestPredictions;
import cf4j.algorithms.model.matrixFactorization.FactorizationModel;
import cf4j.data.DataModel;
import cf4j.data.TestUser;

/**
 * Compute users predictions using a factorization model
 *
 * @see FactorizationModel
 *
 * @author Fernando Ortega
 */
public class FactorizationPrediction extends TestPredictions {

	protected FactorizationModel model;

	public FactorizationPrediction (FactorizationModel model) {
		super();
		this.model = model;
	}

	@Override
	public double predict(TestUser testUser, int itemCode) {
		int itemIndex = DataModel.getInstance().getItemIndex(itemCode);
		int userIndex = testUser.getUserIndex();
		return model.getPrediction(userIndex, itemIndex);
	}
}
