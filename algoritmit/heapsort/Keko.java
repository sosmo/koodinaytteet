import java.util.Arrays;
import java.util.Random;

/**
 * Kekolajittelu kokonaislukutaulukoille.
 *
 * @author Sampo Osmonen
 */
public class Keko {

	/**
	 * @param a Lajiteltava taulukko, ekassa indeksissä oleva alkio jätetään huomiotta.
	 * @param n Taulukon koko (0. alkio lasketaan mukaan vaikka sitä ei lajitella)
	 */
	public static void heapSort(int[] a, int n) {
		muutaKeoksi(a, n);
		for (int i = n-1; i > 1; i--) {
			// Siirrä suurin alkio keon alusta keon taakse ja vaihda keon viimeisenä ollut lehtisolmu sen tilalle.
			swap(a, 1, i);
			// Korjaa keko vaihdon jälkeen.
			siirraAlas(a, 1, i);
		}
	}

	/**
	 * @param a Maksimikeoksi muunnettava taulukko, ekassa indeksissä oleva alkio jätetään huomiotta.
	 * @param n Taulukon koko (0. alkio lasketaan mukaan vaikka sitä ei lajitella)
	 */
	public static void muutaKeoksi(int[] a, int n) {
		// n/2 riittää, koska viimeiset solmut joilla on lapsia on ekassa puolikkaassa
		for (int i = n/2; i > 0; i--) {
			siirraAlas(a, i, n);
		}
	}

	/**
	 * Korjaa maksimikekoa vaihtamalla i-indeksissä ollutta solmua lapsen kanssa "alas" kohti lehtisolmuja niin kauan kuin se on pienempi kuin suurempi lapsi
	 *
	 * @param a Lajiteltava taulukko, ekassa indeksissä oleva alkio jätetään huomiotta.
	 * @param i Aloitusindeksi, jossa olevaa solmua aletaan "laskea".
	 * @param n Taulukon koko (0. alkio lasketaan mukaan vaikka sitä ei lajitella)
	 */
	public static void siirraAlas(int[] a, int i, int n) {
		// i-solmun ekan lapsen paikka
		int j = i*2;
		int alkio = a[i];
		while (j < n) {
			// jos lapsen sisar on suurempi, vaihdetaan ennemmin se vanhemman kanssa
			if (j+1 < n && a[j+1] > a[j]) {
				j++;
			}
			// lapsi pienempi -> löytyi paikka alkiolle
			if (a[j] <= alkio) {
				break;
			}
			a[i] = a[j];
			i = j;
			// j arvoksi seuraavan paikan eka lapsi
			j *= 2;
		}
		// sijoitetaan alkio löytyneelle paikalle
		a[i] = alkio;
	}

	public static void swap(int[] a, int i, int j){
		int temp = a[i];
		a[i] = a[j];
		a[j]=temp;
	}


	public static void main(String[] args){
		Random r = new Random();

		for (int x=0;x<5;x++){

			int[] a = new int[21];
			for (int i = 1; i < 21; i++) {
				a[i] = r.nextInt(20);
			}
			System.out.println("Taulukko:");
			System.out.println(Arrays.toString(a));

			heapSort(a, 21);

			System.out.println("\nLajiteltuna:");
			System.out.println(Arrays.toString(a));

			boolean ok=true;
			for (int i = 1; i < a.length; i++){
				if (a[i-1]>a[i])
					ok=false;
			}
			System.out.println("\nJärjestyksessä: " + ok);

			System.out.println("\n-------------------------\n");
		}

	}


}
