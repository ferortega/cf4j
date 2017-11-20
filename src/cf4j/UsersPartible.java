package cf4j;

/**
 * <p>This interface must be implemented if you want to process a Partible over
 * the users set.</p>
 * @see Partible
 * @author Fernando Ortega
 */
public interface UsersPartible extends Partible {

	/**
	 * Is execute once for each user.
	 * @param userIndex Index of the user.
	 */
	public void run (int userIndex);
}
