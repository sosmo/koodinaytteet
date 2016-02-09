package kentat;

import java.text.ParseException;

/**
 * Kenttä, joka tallettaa viitteitä Unitteihin.
 * 
 * @author Sampo Osmonen
 */
public class RefField extends AField<Ref> {
	
	private final Class<?> type;
	
	
	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 * @param type Viitteen luokka.
	 */
	public RefField(FN name, String description, Class<?> type) {
		super(name, description);
		this.type = type;
	}
	
	
	@Override
	public void parse(String jono) throws ParseException {
		jono = jono.trim();
		try {
			Integer id = Integer.parseInt(jono);
			Ref ref = new Ref(type, id);
			setValue(ref);
		}
		catch (NumberFormatException e) {
			throw new ParseException("Ei kokonaisluku (" + jono + ")", 0);
		}
	}
	
	@Override
	public String toString() {
		return getValue().getId().toString();
	}
	
}