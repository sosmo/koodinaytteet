package kentat;

/**
 * Poikkeus, joka voidaan heittää kenttien käsittelyyn liittyvissä ongelmatilanteissa.
 * 
 * @author Sampo Osmonen
 */
public class FieldException extends RuntimeException {

	public FieldException() {
		super();
	}

	public FieldException(String message) {
		super(message);
	}

	public FieldException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
