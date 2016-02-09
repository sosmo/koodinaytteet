package tietorakenne;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.function.Function;

import kentat.FN;
import kentat.AField;
import kentat.IdField;
import kentat.RefField;
import kentat.StringField;
import sekalaiset.StringOperation;

/**
 * Käsittelee yhtä opiskelijaa
 *
 * @author Sampo Osmonen
 */
public class Student extends AUnit implements Comparable<Student> {
	
	public Student() {
		super(new RefField[] {
		}, new AField<?>[] {
				new StringField(FN.STUDENT_FIRSTNAME, "etunimi"),
				new StringField(FN.STUDENT_LASTNAME, "sukunimi"),
				new IdField(FN.STUDENT_ID, "henkilötunnus", (StringOperation & Serializable)x -> x.toUpperCase())
		});
	}


	/**
	 * Palauttaa opiskelijan koko nimen.
	 *
	 * @return opiskelijan nimi
	 */
	public String kokoNimi() {  // TODO
		return getFieldAsString(FN.STUDENT_LASTNAME) + " " + getFieldAsString(FN.STUDENT_FIRSTNAME);
	}
	
	public AField getFieldObject(FN name) {
		switch (name) {
		case STUDENT_WHOLENAME:
			AField f = new StringField(FN.STUDENT_WHOLENAME, "kokonimi");
			f.setValue(kokoNimi());
			return f;
		}
		return super.getFieldObject(name);
	}

	@Override
	public int compareTo(Student opiskelija) {
		return this.kokoNimi().compareTo(opiskelija.kokoNimi());
	}

	/**
	 * Vertaa kahta opiskelijaa ensin sukunimen, sitten etunimen perusteella.
	 *
	 * @param opiskelija opiskelija, johon nykyistä verrataan
	 * @return -1, jos nykyisen opiskelijan nimi on aakkosissa pienempi, 0, jos nimet samat, muuten 1
	 * @example
	 * <pre name="test">
	 *  Student erkki = new Student();
	 *  erkki.maarita("Erkki", "esimerkki", "121212");
	 *  Student pertti = new Student();
	 *  pertti.maarita("pertti", "esimerkki", "121212");
	 *  Student sami = new Student();
	 *  pertti.maarita("pertti", "erkkilä", "121212");
	 *  erkki.compareToIgnoreCase(pertti) < 0 === false;
	 *  erkki.compareToIgnoreCase(sami) > 0 === true;
	 *  erkki.compareToIgnoreCase(erkki) === 0;
	 * </pre>
	 */
	public int compareToIgnoreCase(Student opiskelija) {
		return this.kokoNimi().compareToIgnoreCase(opiskelija.kokoNimi());
	}


	@Override
	public String toString() {
		return kokoNimi();
	}
	
	public static void main(String args[]) {
		Student s =new Student();
		Exam x =new Exam();
		try {
		s.parseField(FN.STUDENT_FIRSTNAME, "m");
		s.parseField(FN.STUDENT_LASTNAME, "m");
		s.parseField(FN.STUDENT_ID, "m");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		ByteArrayOutputStream bos =new ByteArrayOutputStream();
		try {
			ObjectOutputStream oo=new ObjectOutputStream(bos);
			oo.writeObject(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
