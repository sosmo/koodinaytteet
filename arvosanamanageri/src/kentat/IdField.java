package kentat;

import java.text.ParseException;

import sekalaiset.StringOperation;
import sekalaiset.HetuTarkistus;

/**
 * Henkilötunnuskenttä.
 * 
 * @author Sampo Osmonen
 */
public class IdField extends AField<String> {

	private StringOperation parseOperation = null;
	

	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 */
	public IdField(FN name, String description) {
		super(name, description);
	}

	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 * @param parseOperation Funktio, joka jonolle suoritetaan ennen tallennusta.
	 */
	public IdField(FN name, String description, StringOperation parseOperation) {
		super(name, description);
		this.parseOperation = parseOperation;
	}


	@Override
	public void parse(String jono) throws ParseException {
		jono = jono.trim();
		if (parseOperation != null) {
			jono = parseOperation.f(jono);
		}
		String errorString = HetuTarkistus.tarkista(jono);
		if (errorString != null) {
			throw new ParseException("Tunnus on virheellinen (" + errorString + ")", 0);
		}
		setValue(jono);
	}

}
