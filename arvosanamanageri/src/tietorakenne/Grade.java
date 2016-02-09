package tietorakenne;

import kentat.DoubleField;
import kentat.FN;
import kentat.AField;
import kentat.RefField;

/**
 * Käsittelee yhtä tulosta
 * 
 * @author Sampo Osmonen
 */
public class Grade extends AUnit {

	public Grade() {
		super(new RefField[] {
				new RefField(FN.GRADE_EXAM_REF, "koe", Exam.class),
				new RefField(FN.GRADE_STUDENT_REF, "opiskelija", Student.class)
		}, new AField[] {
				new DoubleField(FN.GRADE_GRADE, "arvosana")
		});
	}
	

	@Override
	public String toString() {
		return getFieldAsString(FN.GRADE_GRADE);
	}

}
