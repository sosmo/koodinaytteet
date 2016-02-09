package kentat;

import java.text.ParseException;

/**
 * Kokonaislukukenttä.
 * 
 * @author Sampo Osmonen
 */
public class IntField extends AField<Integer> {

	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 */
	public IntField(FN name, String description) {
		super(name, description);
	}


	/**
	 * Asetetaan kentän arvo merkkijonosta.  Jos jono
	 * kunnollinen, palautetaan null.  Jos jono ei
	 * kunnollinen int-syöte, palautetaan virheilmoitus ja
	 * kentän alkuperäinen arvo jää voimaan.
	 * @param jono kentään asetettava arvo mekrkijonona
	 * @return null jos hyvä arvo, muuten virhe.
	 * @example
	 * <pre name="test">
	 * IntKentta kentta = new IntKentta("määrä");
	 * kentta.aseta("12") === null; kentta.getValue() === 12;
	 * kentta.aseta("k") === "Ei kokonaisluku (k)"; kentta.getValue() === 12;
	 * </pre>
	 */
	@Override
	public void parse(String jono) throws ParseException {
		jono = jono.trim();
		try {
			setValue(Integer.parseInt(jono));
		}
		catch (NumberFormatException e) {
			throw new ParseException("Ei kokonaisluku (" + jono + ")", 0);
		}
	}

}
