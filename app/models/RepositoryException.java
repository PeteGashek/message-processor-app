package models;

/**
 * Custom exception subclass to indicate an error in the
 * {@link MessageRepository}.
 * 
 * @author zstorok
 *
 */
public class RepositoryException extends Exception {

	public RepositoryException() {
		super();
	}

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(Throwable cause) {
		super(cause);
	}

	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
