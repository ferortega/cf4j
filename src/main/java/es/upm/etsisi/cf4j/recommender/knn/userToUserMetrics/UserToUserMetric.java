package es.upm.etsisi.cf4j.recommender.knn.userToUserMetrics;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.process.Partible;

/**
 * <p>This class process the similarity measure between two users. If you want to define your own similarity
 * metric implementation, you must extend this class and implements the abstract method similarity (...).</p>
 * 
 * <p>When the execution of the similarity metric is complete, all the test users will have a double array
 * on his map saved with the key "itemToItemMetrics". The itemToItemMetrics can be retrieved using getSimilarities()
 * method of TestUser class. The positions of this array overlaps with the array returned by the method 
 * getUsers() of the Kernel class. For example, testUser.getSimilarities()[i] will contains the similarity 
 * between testUser and Kernel.getInstance().getUsers()[i] user.</p>
 * 
 * @author Fernando Ortega
 */
 public abstract class UserToUserMetric implements Partible<User> {

 	protected DataModel datamodel;
	protected double[][] similarities;

	public UserToUserMetric(DataModel datamodel, double[][] similarities) {
		this.datamodel = datamodel;
		this.similarities = similarities;
	}

	/**
	 * <p>This method must returns the similarity between two users.</p> 
	 * <p>If two users do not have a similarity value, the method must return Double.NEGATIVE_INIFINITY.</p>
	 * <p>The value returned by this method should be higher the higher the similarity between users.</p>
	 * @param activeUser Active user
	 * @param targetUser User with which the similarity is computed
	 * @return Similarity between activeUser and targetUser
	 */
	abstract public double similarity(User user, User otherUser);

	@Override
	public void beforeRun() { }

	@Override
	public void run(User user) {
		int userIndex = user.getUserIndex();

		for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
		    User otherUser = datamodel.getUser(u);
			if (userIndex == otherUser.getUserIndex()) {
				similarities[userIndex][u] = Double.NEGATIVE_INFINITY;
			} else { 
				similarities[userIndex][u] = this.similarity(user, otherUser);
			}
		}
	}
	
	@Override
	public void afterRun() { }
}
