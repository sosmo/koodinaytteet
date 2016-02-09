package sekalaiset;

/**
 * Oma poikkeusluokka tietorakenteessa tapahtuville virheille.
 *
 * @author Sampo Osmonen
 */
public class StorageException extends Exception {

	public StorageException() {
		super();
	}

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
