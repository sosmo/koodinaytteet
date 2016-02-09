package tietorakenne;

import kentat.DateField;
import kentat.FN;
import kentat.AField;
import kentat.RefField;
import sekalaiset.Date;
import sekalaiset.PvmKasittely;

/**
 * Käsittelee yhtä koetta
 * 
 * @author Sampo Osmonen
 */
public class Exam extends AUnit {

	String pvm = "HABABA";


	public Exam() {
		super(new RefField[] {
				new RefField(FN.EXAM_SUBJECT_REF, "aine", Subject.class)
		}, new AField[] {
				new DateField(FN.EXAM_DATE, "päivämäärä")
		});
	}


	public String pvmString() {
		return PvmKasittely.dateToString(getField(FN.EXAM_DATE, Date.class));  // TODO
	}

	@Override
	public String toString() {
		return getFieldAsString(FN.EXAM_DATE);
	}

}
