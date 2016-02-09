/**
 * Algoritmi, joka palauttaa optimaalisen valinnan tilanteessa, jossa eri kokoisia ja eriarvoisia resursseja pakataan rajattuun tilaan. Ongelma tunnetaan "kapsäkkiongelmana".
 *
 * @author Sampo Osmonen
 */
public class Laukkupulma {

	/**
	 * @param painot Pakattavien kappaleiden painot tietyssä järjestyksessä
	 * @param hyodyt Pakattavien kappaleiden hyötyarvot tietyssä järjestyksessä
	 * @param n Pakattavien kappaleiden määrä
	 * @param maxPaino Repun kantama maksimipaino
	 * @return Hyöty, jonka reppuun mahtuvista kappaleista voi maksimissaan saada
	 */
	public static int ratkoLaukkupulma(int[] painot, int[] hyodyt, int n, int maxPaino) {
		// tulostaulukko
		// taulukko alustuu nollilla
		// ulomman taulukon indekseinä mukaan otettavien i:n ensimmäisen kpl:n joukko. sisempiin taulukoihin eri painorajojen parhaat senhetkiset tulokset. sisempien taulukoiden indeksit vastaavat senhetkisiä painorajoja.
		int[][] t = new int[n+1][maxPaino+1];
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= maxPaino; j++) {
				int lisattavanPaino = painot[i-1];
				int painoraja = j;
				if (lisattavanPaino > painoraja) {
					t[i][j] = t[i-1][j];
				}
				else {
					int lisattavanEtu = hyodyt[i-1];
					int maxPainoJosLisataan = j-lisattavanPaino;
					int maxEtuJosLisataan = lisattavanEtu + t[i-1][maxPainoJosLisataan];
					t[i][j] = Math.max(t[i-1][j], maxEtuJosLisataan);
				}
			}
		}
		return t[n][maxPaino];
	}

	/**
	 * Yksinkertainen pikkutesti
	 *
	 * @param args ei
	 */
	public static void main(String[] args) {
		int maxPaino = 12;
		int[] painot = {6, 5, 5, 2};
		int[] hyodyt = {7, 5, 4, 4};
		// ratkaisu alkiot 2, 3, 4 => 13
		System.out.println("Odotus: 13; Tulos: " + ratkoLaukkupulma(painot, hyodyt, 4, maxPaino));
		maxPaino = 13;
		painot = new int[] {6, 5, 5, 2};
		hyodyt = new int[] {7, 6, 5, 3};
		// ratkaisu alkiot 1, 2, 4 => 16
		System.out.println("Odotus: 16; Tulos: " + ratkoLaukkupulma(painot, hyodyt, 4, maxPaino));
	}

}
