package kentat;

import java.text.ParseException;

import sekalaiset.StringOperation;

/**
 * String-kenttä.
 * 
 * @author Sampo Osmonen
 */
public class StringField extends AField<String> {

	private StringOperation parseOperation = null;


	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 */
	public StringField(FN name, String description) {
		super(name, description);
	}

	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 * @param parseOperation Funktio, joka jonolle suoritetaan ennen tallennusta.
	 */
	public StringField(FN name, String description, StringOperation parseOperation) {
		super(name, description);
		this.parseOperation = parseOperation;
	}


	@Override
	public void parse(String jono) throws ParseException {
		blockEmpty(jono);
		jono = jono.trim();
		if (parseOperation != null) {
			jono = this.parseOperation.f(jono);
		}
		setValue(jono);
	}

}
