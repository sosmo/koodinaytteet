package kentat;

import java.text.ParseException;

/**
 * Double-kenttä
 * 
 * @author Sampo Osmonen
 */
public class DoubleField extends AField<Double> {

	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 */
	public DoubleField(FN name, String description) {
		super(name, description);
	}


	@Override
	public void parse(String jono) throws ParseException {
		jono = jono.trim();
		String syote = jono.replaceAll(",", ".");
		try {
			setValue(Double.parseDouble(syote));
		}
		catch (NumberFormatException e) {
			throw new ParseException("Syöte ei ole luku (" + jono + ")", 0);
		}
		if (getValue() % 0.25 != 0) {
			throw new ParseException("Syötteeksi kelpaavat vain koepisteet kokonaislukuina tai kouluarvosanoja vastaavat desimaaliluvut (9.25, 9.5 jne)", 0);
		}
	}

}
