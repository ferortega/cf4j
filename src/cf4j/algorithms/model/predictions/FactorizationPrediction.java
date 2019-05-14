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

	public FactorizationPrediction (DataModel dataModel,FactorizationModel model) {
		super(dataModel);
		this.model = model;
	}

	@Override
	public double predict(TestUser testUser, String itemCode) {
		int itemIndex = this.dataModel.getItemIndex(itemCode);
		int userIndex = this.dataModel.getUserIndex(testUser.getUserCode());
		return model.getPrediction(userIndex, itemIndex);
	}
}
