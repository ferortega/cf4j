package cf4j.model.matrixFactorization;

/**
 * <p>Interface for matrix factorization model based collaborative filtering. It has two
 * methods:</p> 
 * <ol>
 * 		<li>train: computes factorization process.</li>
 * 		<li>getPrediction: get rating prediction of an user to an item.</li>
 * </ol>
 * 
 * @author Fernando Ortega
 *
 */
public interface FactorizationModel {
	
	/**
	 * Runs matrix factorization process. Parallelization must be implemented
	 * inside this method.
	 */
	public void train ();
	
	/**
	 * Get rating prediction of an user to an item
	 * @param userIndex User index
	 * @param itemIndex Item index
	 * @return Rating prediction
	 */
	public double getPrediction (int userIndex, int itemIndex);
}
