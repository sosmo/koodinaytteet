package arvosanamanageri;

import gui.AddExam;
import gui.AddGrade;
import gui.AddStudent;
import gui.DataComboBoxModel;
import gui.DataTableModel;
import gui.Main;
import gui.OpenGrade;
import gui.StudentFrame;

import java.awt.Dialog.ModalityType;
import java.awt.Point;
import java.awt.Window;
import java.io.File;
import java.text.ParseException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kentat.FN;
import kentat.Ref;
import sekalaiset.DeepCopy;
import sekalaiset.StorageException;
import sekalaiset.Taulukot;
import tietorakenne.CombinedUnit;
import tietorakenne.Constants;
import tietorakenne.DataManager;
import tietorakenne.Exam;
import tietorakenne.Grade;
import tietorakenne.IUnit;
import tietorakenne.Student;
import tietorakenne.Students;
import tietorakenne.Subject;

/**
 * GUI-controller
 * 
 * @author Sampo Osmonen
 */
public class Controller {

	private final DataManager manager = new DataManager();

	// Onko tallentamattomia muutoksia.
	private boolean modified = false;

	protected final JFileChooser fc = new JFileChooser();

	// Ikkuna, jolle ilmoitetaan tiedostorakenteen muutoksesta.
	private JFrame notify;


	/**
	 * Vakiokonstruktori
	 */
	public Controller() {
		fc.setCurrentDirectory(new File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}


	/**
	 * Kysy vahvistusta käyttäjältä, jos tallentamattomia muutoksia.
	 * 
	 * @return True, jos käyttäjä palaa tallentamaan, false jos muutokset hylätään tai niitä ei ole.
	 */
	public boolean confirmIfUnsavedChanges() {
		if (needsSaving()) {
			int ans = JOptionPane.showConfirmDialog(null, "Sinulla on tallentamattomia muutoksia, jotka menetät jos poistut tästä. Jatketaanko?", "Poistu", JOptionPane.YES_NO_OPTION);
			if (ans == JOptionPane.NO_OPTION) {
				return true;
			}
			discardSave();
		}
		return false;
	}


	/**
	 * Kysy vahvistusta käyttäjältä tallennettaessa.
	 * 
	 * @return True, jos tallennetaan, muuten false.
	 */
	private static boolean confirmSave() {
		int ans = JOptionPane.showConfirmDialog(null, "Tallennetaanko muutokset?", "Tallenna", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.NO_OPTION) {
			return false;
		}
		return true;
	}

	/**
	 * Kysy vahvistusta käyttäjältä poistettaessa.
	 * 
	 * @return True, jos poistetaan, muuten false.
	 */
	private static boolean confirmRemove() {
		int ans = JOptionPane.showConfirmDialog(null, "Toimintoa ei voi peruuttaa. Jatketaanko?", "Poista koe", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}

	private static void showParseError(ParseException e) {
		JOptionPane.showMessageDialog(null, "Antamasi tiedot ovat virheellisessä muodossa!" + Constants.NL + "Virheen syy: " + e.getMessage() + ".", "Virhe", JOptionPane.WARNING_MESSAGE);
	}


	/**
	 * Ilmoita controllerille, jos tietorakenteeseen tulee muutoksia.
	 */
	public void registerChanges() {
		modified = true;
	}


	private boolean needsSaving() {
		return modified;
	}


	/**
	 * Unohda odottavat muutokset tietorakenteeseen.
	 */
	private void discardSave() {
		modified = false;
	}

	/**
	 * Huolehdi tallentamisen jälkeisistä muutoksista.
	 */
	private void save() {
		modified = false;
	}


	/**
	 * Aseta tämänhetkinen ikkuna, jolle ilmoitetaan muutoksista tietorakenteeseen.
	 * 
	 * @param source ikkuna
	 */
	private void setSource(JFrame source) {
		notify = source;
	}

	/**
	 * Ilmoita asetetulle ikkunalle muutoksista tietorakenteeseen.
	 */
	private void notifySource() {
		if (notify instanceof Main) {
			((Main)notify).update();
		}
	}




	/**
	 * Selvitä datayksikön viitekentän kohde.
	 * 
	 * @param unit Käsiteltävä yksikkö.
	 * @param field Käsiteltävä kenttä.
	 * @return Yksikkö, johon kenttä viittaa.
	 */
	public IUnit getRefTarget(IUnit unit, FN field) {
		Object value = unit.getField(field);
		if (value instanceof Ref) {
			Ref ref = (Ref)value;
			IUnit result = getRefTarget(ref);
			return result;
		}
		throw new IllegalArgumentException("Annettu kenttä (" + field + ") ei sisällä viitettä");
	}

	/**
	 * Selvitä viitteen kohde.
	 * 
	 * @param ref viite
	 * @return Yksikkö, johon viite viittaa.
	 */
	public IUnit getRefTarget(Ref ref) {
		return manager.getRefTarget(ref);
	}




	/**
	 * @param wholeName Haluttu kokonimi järjestyksessä etunimet-sukunimi tai sukunimi-etunimet.
	 * @param personalId Haluttu hetu.
	 * @return Oppilas-taulukon malli, jossa on kaikki tietorakenteen parametreja osittain vastaavat yksiköt.
	 */
	public DataTableModel<Student> getStudentTableModel(String wholeName, String personalId) {
		FN[] fields = {FN.STUDENT_FIRSTNAME, FN.STUDENT_LASTNAME, FN.STUDENT_ID};
		String[] fieldNames = {"etunimi", "sukunimi", "henkilötunnus"};
		List<Student> studentsMatchingId = manager.searchStudents(personalId, FN.STUDENT_ID);
		List<Student> students = Students.filterByProperty(studentsMatchingId, wholeName, FN.STUDENT_WHOLENAME);
		DataTableModel<Student> model = new DataTableModel<>(students, fields, fieldNames);
		return model;
	}

	/**
	 * @param selectedSubject Haluttu aine.
	 * @param date Haluttu pvm.
	 * @return Koe-taulukon malli, jossa on kaikki tietorakenteen parametreja osittain vastaavat yksiköt.
	 */
	public DataTableModel<CombinedUnit> getExamTableModel(Subject selectedSubject, String date) {
		FN[] fields = {FN.SUBJECT_NAME, FN.EXAM_DATE};
		String[] fieldNames = {"aine", "päivämäärä"};
		String subjectId;
		if (selectedSubject == null) {
			subjectId = "";
		}
		else {
			subjectId = String.valueOf(selectedSubject.getRef());
		}
		String[] search = {subjectId, date};
		List<Exam> exams = manager.searchExams(search, new FN[] {FN.EXAM_SUBJECT_REF, FN.EXAM_DATE});
		List<CombinedUnit> data = manager.joinRefs(exams, FN.EXAM_SUBJECT_REF);
		DataTableModel<CombinedUnit> model = new DataTableModel<>(data, fields, fieldNames);
		return model;
	}

	/**
	 * @param exam Haluttu koe.
	 * @return Tulos-taulukon malli, jossa on kaikki tietorakenteen parametreja osittain vastaavat yksiköt.
	 */
	public DataTableModel<CombinedUnit> getGradeTableModel(Exam exam) {
		FN[] fields = {FN.STUDENT_FIRSTNAME, FN.STUDENT_LASTNAME, FN.STUDENT_ID, FN.GRADE_GRADE};
		Ref ref = exam.getRef();
		List<Grade> grades = manager.getGrades(ref, FN.GRADE_EXAM_REF);
		List<CombinedUnit> gradesAndStudents = manager.joinRefs(grades, FN.GRADE_STUDENT_REF);
		String[] fieldNames = {"etunimi", "sukunimi", "henkilötunnus", "arvosana"};

		return new DataTableModel<>(gradesAndStudents, fields, fieldNames);
	}


	/**
	 * @return Aine-valitsimen malli, jossa on kaikki tietorakenteen aineet.
	 */
	public DataComboBoxModel<Subject> getSubjectModel() {
		List<Subject> subjects = manager.getSubjects();
		Object[] arr = {"Valitse aine..."};
		Object[] temp = subjects.toArray();
		Object[] objects = Taulukot.combine(arr, temp);
		return new DataComboBoxModel<>(objects);
	}

	/**
	 * @return Kaikki tietorakenteen aineet.
	 */
	public List<Subject> getSubjects() {
		return manager.getSubjects();
	}




	/**
	 * Poista koe.
	 * 
	 * @param unit Poistettava koe.
	 * @return True, jos poistettiin, muuten false.
	 */
	public boolean remove(Exam unit) {
		Ref ref = unit.getRef();
		if (confirmRemove()) {
			manager.poistaKoe(ref);
			notifySource();
			return true;
		}
		return false;
	}

	/**
	 * Poista oppilas.
	 * 
	 * @param unit Poistettava oppilas.
	 * @return True, jos poistettiin, muuten false.
	 */
	public boolean remove(Student unit) {
		Ref ref = unit.getRef();
		if (confirmRemove()) {
			manager.poistaOpiskelija(ref);
			notifySource();
			return true;
		}
		return false;
	}

	/**
	 * Poista arvosana.
	 * 
	 * @param unit Poistettava arvosana.
	 * @return True, jos poistettiin, muuten false.
	 */
	public boolean remove(Grade unit) {
		Ref ref = unit.getRef();
		if (confirmRemove()) {
			manager.poistaTulos(ref);
			notifySource();
			return true;
		}
		return false;
	}




	/**
	 * Keskitä ikkuna toiseen nähden.
	 * 
	 * @param child Keskitettävä ikkuna.
	 * @param parent Ikkuna, jonka suhteen keskitetään.
	 */
	public static void centerChild(Window child, Window parent) {
		Point parentCorner = parent.getLocation();
		double x = parentCorner.getX();
		double y = parentCorner.getY();
		x += (parent.getWidth() - child.getWidth()) / 2;
		y += (parent.getHeight() - child.getHeight()) / 2;
		Point newLocation = new Point((int)x, (int)y);

		child.setLocation(newLocation);
	}



	/**
	 * Avaa dialogi uuden oppilaan luomiseksi.
	 * 
	 * @param source Ikkuna, josta dialogi avattiin.
	 */
	public void newStudent(JFrame source) {
		setSource(source);

		Student student = new Student();

		AddStudent frame = new AddStudent(this, student, ModalityType.DOCUMENT_MODAL);

		centerChild(frame, source);

		frame.setModal(true);
		frame.setVisible(true);
	}

	/**
	 * Avaa dialogi uuden kokeen luomiseksi.
	 * 
	 * @param source Ikkuna, josta dialogi avattiin.
	 */
	public void newExam(JFrame source) {
		setSource(source);

		Exam exam = new Exam();

		AddExam frame = new AddExam(this, exam, ModalityType.DOCUMENT_MODAL);

		centerChild(frame, source);

		frame.setModal(true);
		frame.setVisible(true);
	}

	/**
	 * Avaa dialogi uuden arvosanan luomiseksi.
	 * 
	 * @param exam Koe, johon arvosana kuuluu.
	 * @param source Ikkuna, josta dialogi avattiin.
	 */
	public void newGrade(Exam exam, JFrame source) {
		setSource(source);

		AddGrade frame = new AddGrade(this, exam, ModalityType.DOCUMENT_MODAL);

		centerChild(frame, source);

		frame.setModal(true);
		frame.setVisible(true);
	}




	/**
	 * Avaa arvosana dialogissa.
	 * 
	 * @param grade Avattava arvosana.
	 * @param exam Arvosanaan liittyvä koe.
	 * @param source Ikkuna, josta dialogi avattiin.
	 */
	public void openGrade(Grade grade, Exam exam, JFrame source) {
		setSource(source);

		OpenGrade frame = new OpenGrade(this, grade, exam, ModalityType.DOCUMENT_MODAL);

		centerChild(frame, source);

		frame.setModal(true);
		frame.setVisible(true);
	}

	/**
	 * Avaa oppilas dialogissa.
	 * 
	 * @param student Avattava oppilas.
	 * @param source Ikkuna, josta dialogi avattiin.
	 */
	public void openStudent(Student student, JFrame source) {
		setSource(source);

		StudentFrame frame = new StudentFrame(this, student, ModalityType.DOCUMENT_MODAL);

		centerChild(frame, source);

		frame.setModal(true);
		frame.setVisible(true);
	}




	/**
	 * Luo syväkopio yksiköstä muokkausta varten.
	 * @param unit Kopioitava yksikkö.
	 * @return Syväkopio yksiköstä.
	 */
	@SuppressWarnings("unchecked")
	private static <T extends IUnit> T getEditable(T unit) {
		return (T)DeepCopy.copy(unit);
	}



	private void finishSave() {
		save();
		notifySource();
	}

	

	private File getFileDialogSelection() {
		File dir = fc.getSelectedFile();
		fc.setCurrentDirectory(dir.getParentFile());
		return dir;
	}
	
	
	/**
	 * Avaa datakansio dialogissa.
	 * 
	 * @return Avattu kansio, null jos ei avattu mitään.
	 */
	public File openDir() {
		if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File dir = getFileDialogSelection();
		read(dir);
		return dir;
	}

	/**
	 * Tallenna uusi datakansio dialogissa.
	 * 
	 * @return Tallennettu kansio, null jos ei mitään.
	 */
	public File saveNewDir() {
		if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File dir = getFileDialogSelection();
		saveNew(dir);
		return dir;
	}

	/**
	 * Tallenna datakansio (dialogissa, jos 1. tallennus).
	 * 
	 * @return Tallennettu kansio, null jos ei mitään.
	 */
	public File saveCurrentDir() {
		File current = manager.getDirectory();
		if (current == null) {
			if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
				return null;
			}
			File dir = getFileDialogSelection();
			saveToStorage(dir);
			current = dir;
		}
		else {
			saveToStorage();
		}
		return current;
	}



	/**
	 * Lue datakansio. Raportoi virheet dialogilla.
	 * 
	 * @param file Luettava kansio.
	 */
	public void read(File file) {
		try {
			manager.lue(file);
		}
		catch (StorageException e) {
			JOptionPane.showMessageDialog(null, "Kansion lukeminen ei onnistunut! Tarkista, että olet valinnut oikean kansion." + Constants.NL + "Virheen syy: " + e.getMessage(), "Virhe luettaessa", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Tallenna datakansioon. Raportoi virheet dialogilla.
	 */
	public void saveToStorage() {
		try {
			manager.tallenna();
		}
		catch (StorageException e) {
			JOptionPane.showMessageDialog(null, "Tallentaminen ei onnistunut!" + Constants.NL + "Virheen syy: " + e.getMessage(), "Virhe tallennettaessa", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Tallenna datakansioon. Raportoi virheet dialogilla.
	 * 
	 * @param dir Haluttu kansio.
	 */
	public void saveToStorage(File dir) {
		try {
			manager.setDirectory(dir);
		}
		catch (StorageException e) {
			JOptionPane.showMessageDialog(null, "Tallentaminen ei onnistunut!" + Constants.NL + "Virheen syy: " + e.getMessage(), "Virhe tallennettaessa", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		saveToStorage();
	}

	/**
	 * Luo uusi datakansio. Vahvistaa ei-tyhjään kansioon tallentamisen dialogilla. Raportoi virheet dialogilla.
	 * 
	 * @param file Luotava kansio.
	 */
	public void saveNew(File file) {
		if (file.isDirectory() && file.listFiles().length > 0) {
			int answer = JOptionPane.showConfirmDialog(null, "Kansio sisältää jo tiedostoja. Jos jatkat, niiden yli kirjoitetaan. Jatketaanko?", "Jatketaanko?", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				return;
			}
		}
		try {
			manager.tallennaUusi(file);
		}
		catch (StorageException e) {
			JOptionPane.showMessageDialog(null, "Tallentaminen ei onnistunut!" + Constants.NL + "Virheen syy: " + e.getMessage(), "Virhe tallennettaessa", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}



	/**
	 * Luo paramatrien pohjalta uusi tai tallenna vanha oppilas tietorakenteeseen.
	 * 
	 * @param unit Yksikkö, joka tallennetaan.
	 * @param first Etunimet.
	 * @param last Sukunimi.
	 * @param id Hetu.
	 * @return True, jos tallennettiin, muute false.
	 */
	public boolean saveStudent(Student unit, String first, String last, String id) {
		boolean replace = false;
		if (unit.getRef() != null) {
			if (!confirmSave()) {
				return false;
			}
			replace = true;
		}
		Student edit = unit;
		if (replace) {
			edit = getEditable(edit);
		}
		try {
			edit.parseField(FN.STUDENT_FIRSTNAME, first);
			edit.parseField(FN.STUDENT_LASTNAME, last);
			edit.parseField(FN.STUDENT_ID, id);
		}
		catch (ParseException e) {
			showParseError(e);
			return false;
		}
		if (replace) {
			manager.remove(unit);
		}
		manager.add(edit);

		finishSave();

		return true;
	}

	/**
	 * Luo paramatrien pohjalta uusi tai tallenna vanha arvosana tietorakenteeseen.
	 * 
	 * @param unit Yksikkö, joka tallennetaan.
	 * @param exam Koe, johon arvosana kuuluu.
	 * @param gradeNum Arvosana numerona.
	 * @param student Oppilas, joka arvosanaan liittyy.
	 * @return True, jos tallennettiin, muute false.
	 */
	public boolean saveGrade(Grade unit, Exam exam, String gradeNum, Student student) {
		boolean replace = false;
		if (unit.getRef() != null) {
			if (!confirmSave()) {
				return false;
			}
			replace = true;
		}
		Grade edit = unit;
		if (replace) {
			edit = getEditable(edit);
		}
		try {
			edit.parseField(FN.GRADE_GRADE, gradeNum);
			edit.setField(FN.GRADE_STUDENT_REF, student.getRef());
			edit.setField(FN.GRADE_EXAM_REF, exam.getRef());
		}
		catch (ParseException e) {
			showParseError(e);
			return false;
		}
		if (replace) {
			manager.remove(unit);
		}
		manager.add(edit);

		finishSave();

		return true;
	}

	/**
	 * Luo paramatrien pohjalta uusi tai tallenna vanha koe tietorakenteeseen.
	 * 
	 * @param unit Yksikkö, joka tallennetaan.
	 * @param date Pvm.
	 * @return True, jos tallennettiin, muute false.
	 */
	public boolean saveExam(Exam unit, String date) {
		return saveExam(unit, date, null);
	}

	/**
	 * Luo paramatrien pohjalta uusi tai tallenna vanha koe tietorakenteeseen.
	 * 
	 * @param unit Yksikkö, joka tallennetaan.
	 * @param date Pvm.
	 * @param subject Aine.
	 * @return True, jos tallennettiin, muute false.
	 */
	public boolean saveExam(Exam unit, String date, String subject) {
		boolean replace = false;
		if (unit.getRef() != null) {
			if (!confirmSave()) {
				return false;
			}
			replace = true;
		}
		Exam edit = unit;
		if (replace) {
			edit = getEditable(edit);
		}
		try {
			edit.parseField(FN.EXAM_DATE, date);

			if (subject != null) {
				List<Subject> match = manager.getSubjects(subject, FN.SUBJECT_NAME);
				Subject s;
				if (match.size() < 1) {
					s = new Subject();
					s.parseField(FN.SUBJECT_NAME, subject);
					manager.add(s);
				}
				else {
					s = match.get(0);
				}
				Ref subjectRef = s.getRef();

				edit.setField(FN.EXAM_SUBJECT_REF, subjectRef);
			}
		}
		catch (ParseException e) {
			showParseError(e);
			return false;
		}
		if (replace) {
			manager.remove(unit);
		}
		manager.add(edit);

		finishSave();

		return true;
	}

}
