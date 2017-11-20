package cf4j.qualityMeasures;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.TestUsersPartible;

/**
 * Abstract class to compute quality measures for test of users. To encode
 * a new quality measure the constructor must be redefined giving a name to
 * the quality measure and the method getMeasure() must be implemented.
 * 
 * @author Fernando Ortega
 */
public abstract class QualityMeasure implements TestUsersPartible {

	private String qualityMeasureName;
	
	public QualityMeasure (String qualityMeasureName) {
		this.qualityMeasureName = qualityMeasureName;
	}
		
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
		TestUser testUser = Kernel.gi().getTestUsers()[testUserIndex];
		double measure = this.getMeasure(testUser);
		testUser.put(qualityMeasureName, measure);
	}

	@Override
	public void afterRun() {
		Kernel.getInstance().putUsersAverage(qualityMeasureName);
	}
}
