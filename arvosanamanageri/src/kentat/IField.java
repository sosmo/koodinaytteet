package kentat;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Rajapinta kenttiä varten. Kenttä sisältää arvon, ja tiedon siitä, minkälaista arvoa se edustaa.
 * 
 * @author Sampo Osmonen
 */
public interface IField<T extends Comparable> extends Serializable {

	/**
	 * @return Kentän arvo alkuperäisessä muodossaan.
	 */
	T getValue();
	
	/**
	 * Aseta kentän arvo.
	 * 
	 * @param t Kentän uusi arvo.
	 */
	void setValue(T t);
	
	/**
	 * @return Kentän arvo merkkijonona.
	 */
	@Override
	String toString();

	/**
	 * Parsi kentän arvo jonosta.
	 * 
	 * @param jono Parsittava.
	 * @throws ParseException Jos parsinta epäonnistuu.
	 */
	void parse(String jono) throws ParseException;
	
}
