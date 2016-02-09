package tietorakenne;

import kentat.FN;
import kentat.AField;
import kentat.RefField;
import kentat.StringField;


/**
 * Käsittelee yhtä ainetta
 * 
 * @author Sampo Osmonen
 */
public class Subject extends AUnit implements Comparable<Subject> {
	
	public Subject() {
		super(new RefField[] {
		}, new AField[] {
				new StringField(FN.SUBJECT_NAME, "aine", x -> x.toLowerCase())
		});
	}

	
	@Override
	public int compareTo(Subject aine) {
		return getField(FN.SUBJECT_NAME, String.class).compareToIgnoreCase(aine.getField(FN.SUBJECT_NAME, String.class));
	}

	@Override
	public String toString() {
		return getFieldAsString(FN.SUBJECT_NAME);
	}

}
