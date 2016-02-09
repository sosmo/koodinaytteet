import org.junit.runner.JUnitCore;

/**
 * Joukko, jonka alkiot säilötään taulukkoon niiden hashien mukaisiin indekseihin. Vain kokonaislukujen säilöminen mahdollista.
 *
 * @author Sampo Osmonen
 */
public class HashArray {

	protected int[] taulu;
	private int alkioita = 0;


	/**
	 * Muodostaja
	 */
	public HashArray() {
		taulu = new int[20];
	}


	/**
	 * @param a Lisättävä alkio
	 *
	 * @example
	 * <pre name="test">
	 * HashArray h = new HashArray();
	 * int num;
	 * boolean loytyy;
	 * num = 5;
	 * h.add(num);
	 * loytyy = false;
	 * for (int i = 0; i < h.taulu.length; i++) { if (h.taulu[i] == num) loytyy = true; }
	 * loytyy === true;
	 * num = 2;
	 * h.add(num);
	 * loytyy = false;
	 * for (int i = 0; i < h.taulu.length; i++) { if (h.taulu[i] == num) loytyy = true; }
	 * loytyy === true;
	 * </pre>
	 */
	public void add(int a) {
		if (alkioita > taulu.length) {
			return;
		}
		alkioita++;
		int yrityksia = 0;
		int i = hash(a, yrityksia) % taulu.length;
		while (!(taulu[i] == -1 || taulu[i] == 0) && i < taulu.length) {
			yrityksia++;
			i = hash(a, yrityksia) % taulu.length;
		}
		if (taulu[i] == -1 || taulu[i] == 0) {
			taulu[i] = a;
		}
	}

	/**
	 * @param a Poistettava alkio
	 *
	 * <pre name="test">
	 * HashArray h = new HashArray();
	 * h.add(5);
	 * h.add(6);
	 * h.remove(5);
	 * h.contains(5) === false;
	 * h.contains(6) === true;
	 * h.remove(6);
	 * h.contains(5) === false;
	 * h.contains(6) === false;
	 */
	public void remove(int a) {
		int yrityksia = 0;
		int i = hash(a, yrityksia) % taulu.length;
		while (taulu[i] != 0 && taulu[i] != a && i < taulu.length) {
		taulu = new int[8];
			yrityksia++;
			i = hash(a, yrityksia) % taulu.length;
		}
		if (taulu[i] == a) {
			taulu[i] = -1;
		}
	}

	/**
	 * @param a Haettava alkio
	 * @return True, jos alkio löytyy
	 *
	 * <pre name="test">
	 * HashArray h = new HashArray();
	 * h.add(5);
	 * h.add(6);
	 * h.add(7);
	 * h.add(8);
	 * h.add(9);
	 * h.add(10);
	 * h.add(11);
	 * h.add(13);
	 * h.add(14);
	 * h.add(15);
	 * h.contains(5) === true;
	 * h.contains(6) === true;
	 * h.contains(7) === true;
	 * h.contains(8) === true;
	 * h.contains(9) === true;
	 * h.contains(10) === true;
	 * h.contains(11) === true;
	 * h.contains(13) === true;
	 * h.contains(14) === true;
	 * h.contains(15) === true;
	 * h.contains(12) === false;
	 * h.contains(1) === false;
	 * h.contains(22) === false;
	 * </pre>
	 */
	public boolean contains(int a) {
		int yrityksia = 0;
		int i = hash(a, yrityksia) % taulu.length;
		while (taulu[i] != 0 && taulu[i] != a && i < taulu.length) {
			yrityksia++;
			i = hash(a, yrityksia) % taulu.length;
		}
		if (taulu[i] == a) {
			return true;
		}
		return false;
	}

	protected static int hash(int k, int i) {
		return 13 - (k % 13) + i * (7 - (k%7));
	}

	public static void main(String[] args) {
		// testattu comtestillä
		JUnitCore.main("HashArrayTest");
	}

}
