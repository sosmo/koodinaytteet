package tietorakenne;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kentat.FN;
import kentat.Ref;
import sekalaiset.StorageException;

/**
 * Luokka arvosanamanagerin tietokantatoimintojen keskittämiseen ja yhteistyöhön.
 *
 * @author Sampo Osmonen
 */
public class DataManager {

	private Grades grades = new Grades();
	private Exams exams = new Exams();
	private Students students = new Students();
	private Subjects subjects = new Subjects();

	// Kansio, johon oletuksena tallennetaan.
	private File dir = null;
	// Onko tallentamattomia muutoksia.
	private boolean isChanged = false;


	/**
	 * @return True, jos tallentamattomia muutoksia.
	 */
	public boolean isChanged() {
		return isChanged;
	}

	/**
	 * @return Kansio, jota käytetään tallentamiseen
	 */
	public File getDirectory() {
		return dir;
	}

	/**
	 * @param dir Aseta kansio, jota käytetään tallentamiseen
	 * @throws StorageException 
	 */
	public void setDirectory(File dir) throws StorageException {
		if (!dir.exists()) {
			dir.mkdir();
		}
		else if (!dir.isDirectory()) {
			throw new StorageException("Saman niminen tiedosto on jo olemassa (" + dir + ")");
		}
		this.dir = dir;
	}

	/**
	 * @return alkioiden lukumäärä
	 */
	public int examsCount() {
		return exams.getCount();
	}

	/**
	 * @return alkioiden lukumäärä
	 */
	public int subjectsCount() {
		return subjects.getCount();
	}

	/**
	 * @return alkioiden lukumäärä
	 */
	public int studentsCount() {
		return students.getCount();
	}

	/**
	 * @return alkioiden lukumäärä
	 */
	public int gradesCount() {
		return grades.getCount();
	}


	public List<Subject> getSubjects() {
		return subjects.getAll();
	}

	public List<Exam> getExams() {
		return exams.getAll();
	}

	public List<Student> getStudents() {
		return students.getAll();
	}

	public List<Grade> getGrades() {
		return grades.getAll();
	}



	/**
	 * Hae yksiköt, jotka vastaavat osin tai kokonaan hakua.
	 * 
	 * @param haku Haettava jono.
	 * @param hakukohde Kenttä, johon jonoa verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Student> searchStudents(String haku, FN hakukohde) {
		List<Student> opiskelijat = this.students.getMatches(haku, hakukohde);
		return Students.sortByNameAscending(opiskelijat);
	}

	/**
	 * Hae yksiköt, jotka vastaavat osin tai kokonaan hakua.
	 * 
	 * @param haku Haettava jono.
	 * @param hakukohde Kenttä, johon jonoa verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Exam> searchExams(String haku, FN hakukohde) {
		return exams.getMatches(haku, hakukohde);
	}

	/**
	 * Hae yksiköt, jotka vastaavat osin tai kokonaan hakua.
	 * 
	 * @param haku Haettava jono.
	 * @param hakukohde Kenttä, johon jonoa verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Subject> searchSubjects(String haku, FN hakukohde) {
		return subjects.getMatches(haku, hakukohde);
	}

	/**
	 * Hae yksiköt, jotka vastaavat osin tai kokonaan hakua.
	 * 
	 * @param haku Haettava jono.
	 * @param hakukohde Kenttä, johon jonoa verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Grade> searchGrades(String haku, FN hakukohde) {
		return grades.getMatches(haku, hakukohde);
	}


	/**
	 * Hae yksiköt, joiden kentät vastaavat osin tai kokonaan hakuja. Hakutermien ja hakukohteiden on oltava samassa järjestyksessä.
	 * 
	 * @param haku Haettavat jonot.
	 * @param hakukohde Kentät, joihon jonoja verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Student> searchStudents(String[] haku, FN[] hakukohde) {
		List<Student> opiskelijat = this.students.getMatches(haku, hakukohde);
		return Students.sortByNameAscending(opiskelijat);
	}

	/**
	 * Hae yksiköt, joiden kentät vastaavat osin tai kokonaan hakuja. Hakutermien ja hakukohteiden on oltava samassa järjestyksessä.
	 * 
	 * @param haku Haettavat jonot.
	 * @param hakukohde Kentät, joihon jonoja verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Exam> searchExams(String[] haku, FN[] hakukohde) {
		return exams.getMatches(haku, hakukohde);
	}

	/**
	 * Hae yksiköt, joiden kentät vastaavat osin tai kokonaan hakuja. Hakutermien ja hakukohteiden on oltava samassa järjestyksessä.
	 * 
	 * @param haku Haettavat jonot.
	 * @param hakukohde Kentät, joihon jonoja verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Subject> searchSubjects(String[] haku, FN[] hakukohde) {
		return subjects.getMatches(haku, hakukohde);
	}

	/**
	 * Hae yksiköt, joiden kentät vastaavat osin tai kokonaan hakuja. Hakutermien ja hakukohteiden on oltava samassa järjestyksessä.
	 * 
	 * @param haku Haettavat jonot.
	 * @param hakukohde Kentät, joihon jonoja verrataan.
	 * @return Vastaavat yksiköt.
	 */
	public List<Grade> searchGrades(String[] haku, FN[] hakukohde) {
		return grades.getMatches(haku, hakukohde);
	}


	/**
	 * Hae yksiköt, joiden kenttä on sama kuin annettu haku.
	 *
	 * @param data Objekti, johon kenttää verrataan.
	 * @param kentta Verrattava kenttä.
	 * @return Yksiköt, joiden kenttä vastasi hakua.
	 */
	public List<Student> getStudents(Object data, FN kentta) {
		return students.getAll(data, kentta);
	}

	/**
	 * Hae yksiköt, joiden kenttä on sama kuin annettu haku.
	 *
	 * @param data Objekti, johon kenttää verrataan.
	 * @param kentta Verrattava kenttä.
	 * @return Yksiköt, joiden kenttä vastasi hakua.
	 */
	public List<Exam> getExams(Object data, FN kentta) {
		return exams.getAll(data, kentta);
	}

	/**
	 * Hae yksiköt, joiden kenttä on sama kuin annettu haku.
	 *
	 * @param data Objekti, johon kenttää verrataan.
	 * @param kentta Verrattava kenttä.
	 * @return Yksiköt, joiden kenttä vastasi hakua.
	 */
	public List<Subject> getSubjects(Object data, FN kentta) {
		return subjects.getAll(data, kentta);
	}

	/**
	 * Hae yksiköt, joiden kenttä on sama kuin annettu haku.
	 *
	 * @param data Objekti, johon kenttää verrataan.
	 * @param kentta Verrattava kenttä.
	 * @return Yksiköt, joiden kenttä vastasi hakua.
	 */
	public List<Grade> getGrades(Object data, FN kentta) {
		return grades.getAll(data, kentta);
	}


	/**
	 * Hae yksikkö, johon viite viittaa.
	 *
	 * @param id Verrattava viite.
	 * @return Yksikkö, johon viite viittaa.
	 */
	public Student getOpiskelija(Ref id) {
		return students.get(id);
	}

	/**
	 * Hae yksikkö, johon viite viittaa.
	 *
	 * @param id Verrattava viite.
	 * @return Yksikkö, johon viite viittaa.
	 */
	public Exam getKoe(Ref id) {
		return exams.get(id);
	}

	/**
	 * Hae yksikkö, johon viite viittaa.
	 *
	 * @param id Verrattava viite.
	 * @return Yksikkö, johon viite viittaa.
	 */
	public Subject getAine(Ref id) {
		return subjects.get(id);
	}

	/**
	 * Hae yksikkö, johon viite viittaa.
	 *
	 * @param id Verrattava viite.
	 * @return Yksikkö, johon viite viittaa.
	 */
	public Grade getTulos(Ref id) {
		return grades.get(id);
	}


	/**
	 * Hae yksikkö, johon viite viittaa.
	 * 
	 * @param ref Viite.
	 * @return Yksikkö, johon viite viittaa.
	 */
	public IUnit getRefTarget(Ref ref) {
		Class<?> type = ref.getType();
		if (type == Subject.class) {
			return getAine(ref);
		}
		if (type == Exam.class) {
			return getKoe(ref);
		}
		if (type == Grade.class) {
			return getTulos(ref);
		}
		if (type == Student.class) {
			return getOpiskelija(ref);
		}
		throw new IllegalArgumentException("Virheellinen tyyppi (" + type + ")");
	}

	/**
	 * Poista yksikkö, johon viite viittaa.
	 * 
	 * @param ref Viite.
	 * @return Poistettu yksikkö.
	 */
	public IUnit remove(Ref ref) {
		Class<?> type = ref.getType();
		if (type == Subject.class) {
			return subjects.remove(ref);
		}
		if (type == Exam.class) {
			return exams.remove(ref);
		}
		if (type == Grade.class) {
			return grades.remove(ref);
		}
		if (type == Student.class) {
			return students.remove(ref);
		}
		throw new IllegalArgumentException("Virheellinen tyyppi (" + type + ")");
	}

	/**
	 * Poista yksikkö. Jos yksikkö on yhdistetty useammasta, poista kaikki liittyvät yksiköt.
	 * 
	 * @param unit Poistettava yksikkö.
	 * @return Poistettu yksikkö.
	 */
	public IUnit remove(IUnit unit) {
		if (unit instanceof CombinedUnit) {
			IUnit[] contents = ((CombinedUnit)unit).getComponents();
			for (IUnit u : contents) {
				remove(u);
			}
			return unit;
		}
		return remove(unit.getRef());
	}

	/**
	 * Lisää yksikkö. Jos yksikkö on yhdistetty useammasta, lisää kaikki.
	 * 
	 * @param unit Lisättävä yksikkö.
	 */
	public void add(IUnit unit) {
		if (unit instanceof CombinedUnit) {
			IUnit[] contents = ((CombinedUnit)unit).getComponents();
			for (IUnit u : contents) {
				add(u);
			}
		}
		Class<? extends IUnit> type = unit.getClass();
		boolean added = false;
		if (type == Subject.class) {
			subjects.add((Subject)unit);
			added = true;
		}
		if (type == Exam.class) {
			exams.add((Exam)unit);
			added = true;
		}
		if (type == Grade.class) {
			grades.add((Grade)unit);
			added = true;
		}
		if (type == Student.class) {
			students.add((Student)unit);
			added = true;
		}
		if (!added) {
			throw new IllegalArgumentException("Virheellinen tyyppi (" + type + ")");
		}
		isChanged = true;
	}

	/**
	 * Korvaa viitteen osoittama yksikkö viitteen osoittamalla yksiköllä.
	 * 
	 * @param remove Viite poistettavaan yksikköön.
	 * @param add Viite korvaavaan yksikköön.
	 */
	public void replace(Ref remove, Ref add) {
		remove(remove);
		IUnit unit = getRefTarget(add);
		add(unit);
	}

	/**
	 * Korvaa yksikkö toisella.
	 * 
	 * @param remove Poistettava yksikkö.
	 * @param add Viite Korvaavaa yksikkö.
	 */
	public void replace(IUnit remove, IUnit add) {
		remove(remove);
		add(add);
	}

	/**
	 * Korvaa viitteen osoittama yksikkö toisella yksiköllä.
	 * 
	 * @param remove Viite poistettavaan yksikköön.
	 * @param add Viite Korvaavaa yksikkö.
	 */
	public void replace(Ref remove, IUnit add) {
		remove(remove);
		add(add);
	}


	/**
	 * "Pura" listan jokaisen yksikön haluttu viite ja palauta lista, jossa yksikköjen paikalla on vastaavat yksiköt, jotka sisältävät alkuperäisen "primääriyksikön" JA sen viitteen kohteen kentät.
	 * 
	 * @param primaries Lista "primääriyksiköistä", jotka käsitellään.
	 * @param key Viiteavain, jonka kohde yhdistetään luotavaan yhdistelmätyyppiin.
	 * @return Alkuperäisen listan järjestystä vastaava lista yhdistelmätyypeistä, joista jokainen sisältää alkuperäisen "primääriyksikön" JA sen viitteen kohteen kentät.
	 */
	public List<CombinedUnit> joinRefs(List<? extends IUnit> primaries, FN key) {
		List<CombinedUnit> combination = new ArrayList<>(primaries.size());
		for (IUnit unit: primaries) {
			Ref ref = unit.getRefField(key);
			IUnit secondary = getRefTarget(ref);
			CombinedUnit union = new CombinedUnit(unit, secondary);
			combination.add(union);
		}
		return combination;
	}


	/**
	 * Poista viitettä vastaava koe. Poistaa myös vastaavan aineen sekä arvosanat, jos niitä ei enää käytetä muualla.
	 *
	 * @param id viite, jota vastaava koe poistetaan
	 */
	public void poistaKoe(Ref id) {
		Exam poistettu = exams.remove(id);
		if (poistettu == null) {
			return;
		}
		Ref poistetunAineId = poistettu.getRefField(FN.EXAM_SUBJECT_REF);
		int aineestaKokeita = exams.getAll(poistetunAineId, FN.EXAM_SUBJECT_REF).size();
		if (aineestaKokeita == 0) {
			subjects.remove(poistetunAineId);
		}
		grades.removeAll(id, FN.GRADE_EXAM_REF);
		isChanged = true;
	}

	/**
	 * Poista arvosana halutulla id:llä.
	 *
	 * @param id viite, jota vastaava arvosana poistetaan
	 */
	public void poistaTulos(Ref id) {
		grades.remove(id);
		isChanged = true;
	}

	/**
	 * Poista opiskelija halutulla id:llä.
	 *
	 * @param id id, jota vastaava 1. tulos poistetaan
	 */
	public void poistaOpiskelija(Ref id) {
		students.remove(id);
		grades.removeAll(id, FN.GRADE_STUDENT_REF);
		isChanged = true;
	}


	/**
	 * Lue tekstitiedostot halutusta kansiosta.
	 * 
	 * @param dir Luettava kansio.
	 * @throws StorageException Jos kansion asettaminen tai tiedostojen lukeminen ei onnistu.
	 */
	public void lue(File dir) throws StorageException {
		setDirectory(dir);

		grades = new Grades();
		exams = new Exams();
		students = new Students();
		subjects = new Subjects();

		grades.lue(dir);
		exams.lue(dir);
		students.lue(dir);
		subjects.lue(dir);

		isChanged = false;
	}


	/**
	 * Tallenna nimellä, joka on viimeksi valittu.
	 *
	 * @throws StorageException Jos tallentaminen epäonnistuu.
	 */
	public void tallenna() throws StorageException {
		if (!isChanged()) {
			return;
		}
		if (dir == null) {
			throw new IllegalStateException("Tallennuskansio on asetettava ennen tallentamista");
		}
		grades.save(dir);
		exams.save(dir);
		students.save(dir);
		subjects.save(dir);

		isChanged = false;
	}

	/**
	 * Tallenna uusi kansio ja kirjoita siihen tyhjät tiedostot.
	 * 
	 * @param dir Tallennettava kansio.
	 * @throws StorageException Jos tallentaminen epäonnistuu.
	 */
	public void tallennaUusi(File dir) throws StorageException {
		setDirectory(dir);

		grades = new Grades();
		exams = new Exams();
		students = new Students();
		subjects = new Subjects();

		isChanged = true;
		tallenna();
	}

}
