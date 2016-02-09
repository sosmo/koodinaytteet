package tietorakenne;

import java.io.Serializable;
import java.text.ParseException;

import kentat.FN;
import kentat.Ref;

/**
 * Rajapinta datayksikölle, joka sisältää tietokenttiä.
 * 
 * @author Sampo Osmonen
 */
public interface IUnit extends Serializable {
	
	/**
	 * Palauttaa yksikön "pääviiteavaimen" numeron.
	 *
	 * @return Yksikön viitenumero.
	 */
	Ref getRef();
	
	/**
	 * @return Yksikön kenttien määrä.
	 */
	int fieldCount();
	
	/**
	 * @return Yksikön viitekenttien määrä.
	 */
	int refFieldCount();
	
	/**
	 * @return Yksikön datakenttien määrä.
	 */
	int dataFieldCount();
	
	/**
	 * Hae nimeä vastaava kenttä.
	 * 
	 * @param name Haluttu kentän nimi.
	 * @return Nimeä vastaavan kentön sisältö.
	 */
	Comparable<?> getField(FN name);
	
	/**
	 * Hae nimeä vastaava kenttä haluttuna tyyppinä.
	 * 
	 * @param name Haluttu kentän nimi.
	 * @param targetClass Tyyppi, jona kentän sisältö palautetaan.
	 * @return Nimeä vastaavan kentön sisältö.
	 */
	<T> T getField(FN name, Class<T> targetClass);
	
	/**
	 * Parsii yksikön kenttien tiedot määrittelyjärjestyksessä tabulaattorein erotetusta jonosta. Käytetään kenttien lukemiseen tekstitiedostosta.
	 * 
	 * @param line Merkkijono, joka sisältää yksikön id:n ja kenttien tiedot siinä järjestyksessä kuin ne on luokassa esitelty.
	 * @throws ParseException Jos jokin tiedoista ei täytä kentille asetettuja eheysvaatimuksia.
	 */
	void parse(String line) throws ParseException;
	
	/**
	 * Parsi kaikki datakentät määrittelyjärjestyksessä taulukosta.
	 *
	 * @param data Datakenttien uudet parsittavat sisällöt siinä järjestyksessä kuin ne on luokassa esitelty.
	 * @throws ParseException Jos jokin tiedoista ei täytä datakentille asetettuja eheysvaatimuksia.
	 */
	void parseDataFields(String...data) throws ParseException;
	
	/**
	 * Parsi haluttu datakenttä merkkijonosta.
	 * 
	 * @param name Haluttu kentän nimi.
	 * @param data Parsittava merkkijono.
	 * @throws ParseException Jos merkkijono ei täytä eheysvaatimuksia.
	 */
	void parseField(FN name, String data) throws ParseException;
	
	@Override
	String toString();
	
	/**
	 * Hae nimeä vastaava viitekenttä.
	 * 
	 * @param name Haluttu kentän nimi.
	 * @return Nimeä vastaavan kentän viite.
	 */
	Ref getRefField(FN name);
	
	FN[] getDataFieldNames();
	
	/**
	 * @return
	 */
	FN[] getRefFieldNames();
	
	String getFieldAsString(FN name);
	
	void setField(FN name, Comparable<?> value);
	
	Comparable<?>[] getDataFields();
	
	Ref[] getRefFields();
	
	boolean equals(Object o);
	
}
