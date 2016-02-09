package kentat;

import java.text.ParseException;

import sekalaiset.Date;
import sekalaiset.PvmKasittely;

/**
 * Päivämääräkenttä.
 * 
 * @author Sampo Osmonen
 */
public class DateField extends AField<Date> {

	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 */
	public DateField(FN name, String description) {
		super(name, description);
	}


	@Override
	public void parse(String jono) throws ParseException{
		int[] pvm;
		pvm = PvmKasittely.erotaPvm(jono);
		Date date = new Date(pvm[0], pvm[1], pvm[2]);
		setValue(date);
	}

}
