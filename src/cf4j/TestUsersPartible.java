package cf4j;

/**
 * <p>This interface must be implemented if you want to process a Partible over
 * the test users set.</p>
 * @see Partible
 * @author Fernando Ortega
 */
public interface TestUsersPartible extends Partible {

	/**
	 * Is execute once for each test user.
	 * @param testUserIndex Index of the test user.
	 */
	public void run (int testUserIndex);
}
