package kentat;

import java.text.ParseException;

import sekalaiset.HetuTarkistus;

/**
 * Henkilötunnuskenttä.
 * 
 * @author Sampo Osmonen
 */
public class PersonalIdField extends AField<String> {

	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 */
	public PersonalIdField(FN name, String description) {
		super(name, description);
	}

	@Override
	public void parse(String jono) throws ParseException {
		setValue(jono.trim().toUpperCase());
		String virhe = HetuTarkistus.tarkista(getValue());
		if (virhe != null) {
			throw new ParseException(virhe, 0);
		}
	}
	
}
