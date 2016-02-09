package kentat;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Kokoelma, johon voi tallentaa AField-olioita taulukosta ja säilyttää niiden järjestyksen.
 * 
 * @author Sampo Osmonen
 */
public class FieldCollection implements Iterable<AField<?>>, Serializable {

	private AField<?>[] fields;
	
	
	/**
	 * Muodostaja
	 * 
	 * @param fields Kentät, jotka tallennetaan.
	 */
	public FieldCollection(AField<?>[] fields) {
		this.fields = fields;
	}
	
	
	/**
	 * Hae kenttä nimellä.
	 * 
	 * @param name Haettava kenttä.
	 * @return Vastaava kenttä tai null.
	 */
	public AField<?> get(FN name) {
		for (AField<?> field : this) {
			FN fieldRef = field.getName();
			if (fieldRef.equals(name)) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * @return Rakenteen sisältämien kenttien määrä.
	 */
	public int size() {
		return this.fields.length;
	}
	
	/**
	 * @return Rakenteen sisältämät kentät taulukkona.
	 */
	public AField<?>[] toArray() {
		Object[] temp = this.fields.clone();
		return (AField[])temp;
	}

	@Override
	public Iterator<AField<?>> iterator() {
		return new FieldIterator(this.fields);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(fields);
	}

}
