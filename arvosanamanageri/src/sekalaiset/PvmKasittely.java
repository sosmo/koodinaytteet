package sekalaiset;

import java.io.Serializable;
import java.text.ParseException;

import sekalaiset.Date;

import java.util.regex.Pattern;

/**
 * Staattisia metodeja pvm-käsittelyyn
 * 
 * @author Sampo Osmonen
 */
public class PvmKasittely implements Serializable {

	/**
	 * Erottelee syötetystä päivämäärä-jonosta päivät, kuukaudet ja vuoden omiksi jonoikseen.
	 * 
	 * @param pvm Pvm muodossa pp.kk.vvvv
	 * @return Pvm taulukkona {pp, kk, vvvv}
	 * @throws ParseException Jos parsinta ei onnistu
	 */
	public static int[] erotaPvm(String pvm) throws ParseException {
		String erotin = null;
		for (int i = 0; i < pvm.length(); i++) {
			if (!Character.isDigit(pvm.charAt(i))) {
				erotin = Character.toString(pvm.charAt(i));
			}
		}
		if (erotin == null) {
			throw new ParseException("Päivämäärän osia ei ole erotettu kunnolla", 0);
		}
		String[] pvmStr = pvm.split(Pattern.quote(erotin));
		if (pvmStr.length != 3) {
			throw new ParseException("Päivämäärässä pitää olla kolme osaa, pv, kk, vv", 0);
		}
		int[] pvmInt = new int[3];
		try {
			for (int i = 0; i < pvmStr.length; i++) {
				pvmInt[i] = Integer.parseInt(pvmStr[i]);
			}
		}
		catch (NumberFormatException e) {
			throw new ParseException("Päivämäärässä saa olla vain erottimet ja numeroita", 0);
		}
		if (!PvmSisaltoTarkistus.sisaltoOk(pvmInt)) {
			throw new ParseException("Päivämäärä ei ole kelvollinen päivämäärä", 0);
		}
		return pvmInt;
	}

	/**
	 * Palauttaa kokeen päivämäärän jonona.
	 * 
	 * @param pvm Pvm
	 * @return pvm jonona muodossa "pp.kk.vvvv"
	 */
	public static String dateToString(Date pvm) {
		return pvm.getDay() + "." + pvm.getMonth() + "." + pvm.getYear();
	}
	
}
