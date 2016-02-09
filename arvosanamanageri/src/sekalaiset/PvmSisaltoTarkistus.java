package sekalaiset;

/**
 * Luokka, josta löytyy aliohjelmat päivämäärän sisällön järkevyyden tarkistukseen.
 * 
 * @author Sampo Osmonen
 */
public class PvmSisaltoTarkistus {

	/** kuukausien pituudet, normivuosi ja karkausvuosi */
	public static final int KPITUUDET[][] = {
		// 1  2  3  4  5  6  7  8  9 10 11 12
		{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 },
		{ 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }
	};
	
	private static final int MINIMIVUOSI = 1850;

	/**
	 * Tarkistaa, että päivämäärä on sisällöllisesti mahdollinen. Huomioi myös karkausvuodet ja pienimmän sallitun vuosiluvun (muuttuja minimiVuosi).
	 *
	 * @param pvm tarkistettava päivämäärä taulukossa muodossa {päivä, kuukausi, vuosi}
	 * @return true, jos päivämäärä kelpaa, muuten false
	 * @example
	 * HUOM! Comtest ei suostu tekemään validia testitiedostoa, vaan pitää itse lisätä "public class PvmSisaltoTarkistusTest {" testitiedostoon
	 * <pre name="test">
	 *  sisaltoOk(new int[] {5, 5, 2001}) === true;
	 *  sisaltoOk(new int[] {32, 5, 2001}) === false;
	 *  sisaltoOk(new int[] {12, 1, 1995}) === true;
	 *  sisaltoOk(new int[] {12, 13, 1995}) === false;
	 *  sisaltoOk(new int[] {12, -3, 1995}) === false;
	 *  sisaltoOk(new int[] {-12, 1, 1995}) === false;
	 *  sisaltoOk(new int[] {12, 1, -1995}) === false;
	 *  sisaltoOk(new int[] {0, 0, 1995}) === false;
	 *  sisaltoOk(new int[] {29, 2, 2001}) === false;
	 *  sisaltoOk(new int[] {29, 2, 2000}) === true;
	 *  sisaltoOk(new int[] {12, 1, 190}) === false;
	 * </pre>
	 */
	public static boolean sisaltoOk(int[] pvm) {
		int pv = pvm[0];
		int kk = pvm[1];
		int vv = pvm[2];

		// tarkistetaan rajat ajankohdille
		if (pv <= 0 || kk > 12 || kk <= 0 || vv < MINIMIVUOSI) {
			return false;
		}
		// tarkistetaan mahtuuko päivä kuukauteen
		int karkaus = karkausvuosi(vv);
		if ((karkaus == 0 && KPITUUDET[0][kk-1] >= pv) || (karkaus == 1 && KPITUUDET[1][kk-1] >= pv)) {
			return true;
		}
		return false;
	}

	/**
	 * Palautetaan tieto siitä onko tutkittava vuosi karkausvuosi vai ei
	 *
	 * @author Vesa Lappalainen
	 * http://kurssit.it.jyu.fi/TIEP111/2014/demovast/src/demo/d2/LisaaPvm.java
	 *
	 * @param vv tutkittava vuosi
	 * @return 1 jos on karkausvuosi ja 0 jos ei ole
	 * @example
	 * <pre name="test">
	 *   karkausvuosi(1900) === 0
	 *   karkausvuosi(1900) === 0
	 *   karkausvuosi(1901) === 0
	 *   karkausvuosi(1996) === 1
	 *   karkausvuosi(2000) === 1
	 *   karkausvuosi(2001) === 0
	 *   karkausvuosi(2004) === 1
	 * </pre>
	 */
	public static int karkausvuosi(int vv) {
		if ( vv % 400 == 0 ) return 1;
		if ( vv % 100 == 0 ) return 0;
		if ( vv % 4 == 0 ) return 1;
		return 0;
	}

}
