package kentat;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iteraattori taulukolle, joka sisältää AUnit-olioita. Taulukko käydään läpi järjestyksessä 0. indeksistä alkaen.
 * 
 * @author Sampo Osmonen
 */
public class FieldIterator implements Iterator<AField<?>>, Serializable {

	private AField<?>[] kentat;
	private int i = 0;


	/**
	 * Muodostaja.
	 * 
	 * @param kentat Kentät, joita iteraattori käy läpi.
	 */
	public FieldIterator(AField<?>[] kentat) {
		this.kentat = kentat;
	}


	@Override
	public boolean hasNext() {
		return this.i < this.kentat.length;
	}

	@Override
	public AField<?> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return this.kentat[this.i++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
