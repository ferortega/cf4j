package cf4j.qualityMeasures;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;
import cf4j.process.PartibleThreads;

/**
 * Abstract class to compute quality measures for test of users. To encode
 * a new quality measure the constructor must be redefined giving a name to
 * the quality measure and the method getMeasure() must be implemented.
 * 
 * @author Fernando Ortega
 */
public abstract class QualityMeasure extends PartibleThreads {

	private String qualityMeasureName;
	
	public QualityMeasure (DataModel dataModel, String qualityMeasureName) {
		super(dataModel);
		this.qualityMeasureName = qualityMeasureName;
	}

	@Override
	public int getTotalIndexes () { return dataModel.getNumberOfTestUsers(); }
		
	@Override
	public void beforeRun() { }
	
	/**
	 * This method must return the quality measure for the test user. If
	 * the quality measure can not be computed for that user, Double.NaN
	 * must be returned.
	 * @param testUser User for compute quality measure
	 * @return Quality measure value or Double.NaN
	 */
	public abstract double getMeasure (TestUser testUser);

	@Override
	public void run (int testUserIndex) {
		TestUser testUser = dataModel.getTestUserAt(testUserIndex);
		double measure = this.getMeasure(testUser);
		testUser.getStoredData().setDouble(qualityMeasureName, measure);
	}

	@Override
	public void afterRun() {

		double summation = 0.0f;
		int numValues = 0;
		for (int i = 0 ; i < dataModel.getNumberOfUsers(); i++){
			User user = dataModel.getTestUserAt(i);
			double userValue = user.getStoredData().getDouble(qualityMeasureName);
			if (!Double.isNaN(userValue)) {
				summation += userValue;
				numValues++;
			}
		}
		if (numValues > 0) {
			dataModel.getDataBank().setDouble(qualityMeasureName,(summation / numValues));
		} else {
			dataModel.getDataBank().setDouble(qualityMeasureName, Double.NaN);
		}
	}
}
