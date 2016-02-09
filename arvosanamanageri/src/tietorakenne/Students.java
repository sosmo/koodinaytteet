package tietorakenne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import kentat.FN;
import sekalaiset.StorageException;

/**
 * Luokka Student-alkioiden hallintaan.
 *
 * @author Sampo Osmonen
 */
public class Students extends AUnits<Student> {

	public Students() {
		super(Student.class, "opiskelijat");
	}


	/**
	 * Haetaan kaikki opiskelijat, joiden nimi vastaa osittain hakua.
	 *
	 * @param haku käytettävä hakusana
	 * @param hakukohde Minkä nimistä olion ominaisuutta haetaan. Jos nimi vastaa olion jonkin kentän nimeä, etsitään haku-jonon osaa kirjainkoko huomiotta jättäen vastaavista olioiden kentistä. Jos nimi ei vastaa mihinkään kenttään, se voi olla "erikoistapaus", eli vastata poikkeavilla säännöillä yhteen tai useampaan olion kenttään.
	 * @return tietorakenne, jossa viiteet opiskelijoihin, joiden halutut ominaisuudet vastasivat hakua
	 * @example
	 * <pre name="test">
	 //TODO
	 * </pre>
	 */
	public static List<Student> filterByProperty(List<Student> opiskelijat, String haku, FN hakukohde) {
		switch (hakukohde) {
		case STUDENT_WHOLENAME:
			List<Student> loydetyt = new ArrayList<>();
			for (Student opiskelija : opiskelijat) {
				String[] nimet = haku.toLowerCase().split(" ");
				String kokoNimi = opiskelija.kokoNimi().toLowerCase();
				boolean found = true;
				for (int i = 0; i < nimet.length; i++) {
					if (!kokoNimi.contains(nimet[i])) {
						found = false;
						break;
					}
				}
				if (found) {
					loydetyt.add(opiskelija);
				}
			}
			sortByNameAscending(loydetyt);
			return loydetyt;
		default:
			return opiskelijat;
		}

	}

	/**
	 * Lajittelee nousevaan järjestykseen annetun listan opiskelijoista, lajitteluperusteena sukunimi ja sitten etunimi.
	 *
	 * @param alkiot lajiteltava lista opiskelijoita
	 * @return annettu lista lajiteltuna
	 * @example
	 * <pre name="test">
	 *  Students op = new Students();
	 *  Student o = new Student();
	 *  o.maarita("2", "2", "3");
	 *  op.add(o);
	 *  Student o1 = new Student();
	 *  o1.maarita("1", "1", "3");
	 *  op.add(o1);
	 *  Student o2 = new Student();
	 *  o2.maarita("1", "2", "3");
	 *  op.add(o2);
	 *  op.annaOpiskelijat("", "nimi").toString() === "[1\t2\t2\t3, 2\t1\t1\t3, 3\t1\t2\t3]";
	 * </pre>
	 */
	public static List<Student> sortByNameAscending(List<Student> alkiot) {
		Collections.sort(alkiot, new Comparator<Student>() {
			@Override
			public int compare(Student eka, Student toka) {
				return eka.compareToIgnoreCase(toka);
			}
		});
		return alkiot;
	}

}
