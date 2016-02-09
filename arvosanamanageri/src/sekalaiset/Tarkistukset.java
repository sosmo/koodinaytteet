package sekalaiset;

/**
 * Luokka, joka sisältää erilaisia tarkistusaliohjelmia
 * 
 * @author Sampo Osmonen
 */
public class Tarkistukset {

	/**
	 * Numerot.
	 */
	public static final String NUMEROT = "0123456789";

	/**
	 * Tarkistaa, että jono sisältää vain annettuja merkkejä.
	 * 
	 * @param jono tutkittava jono
	 * @param merkit merkit, joita jono saa sisältää
	 * @return true, jos sisältää vain merkkejä, muuten false
	 * @example
	 * <pre name="test">
	 *  sisaltaaVain("09328", "0123456789") === true;
	 *  sisaltaaVain("9f98", "0123456789") === false;
	 *  sisaltaaVain("90 12-", "0123456789") === false;
	 * </pre>
	 */
	public static boolean sisaltaaVain(String jono, String merkit) {
		char[] merkitArray = merkit.toCharArray();
		for (int i = 0; i < jono.length(); i++) {
			boolean loytyi = false;
			for (int j = 0; j < merkitArray.length; j++) {
				if (jono.charAt(i) == merkitArray[j]) {
					loytyi = true;
					break;
				}
			}
			if (!loytyi) {
				return false;
			}
		}
		return true;
	}

}
