package tietorakenne;

import java.io.File;
import java.util.List;

import kentat.FN;
import kentat.Ref;
import sekalaiset.StorageException;

/**
 * Rajapinta luokalle, joka sisältää samantyyppisiä datayksikköjä.
 * 
 * @author Sampo Osmonen
 * @param <T> Sisällytettävien yksikköjen tyyppi.
 */
public interface IUnits<T extends IUnit> {

	/**
	 * @return Yksiköiden määrä.
	 */
	int getCount();

	/**
	 * @param t Lisättävä yksikkö.
	 */
	void add(T t);

	/**
	 * @return Lista kaikista sisällytetyistä yksiköistä.
	 */
	List<T> getAll();
	
	/**
	 * Hae kaikki yksiköt, joiden haluttu kenttä on sama kuin annettu olio.
	 * 
	 * @param data Olio, jota verrataan yksikön halutun kentän sisältöön.
	 * @param field Kenttä, jonka sisältöä verrataan olioon.
	 * @return Lista ehtoja vastaavista yksiköistä.
	 */
	List<T> getAll(Object data, FN field);
	
	/**
	 * Hae kaikki yksiköt, joiden halutun kentän merkkijonoesitys vastaa kokonaan tai osittain annettua jonoa.
	 * 
	 * @param search Jono, jota verrataan yksikön halutun kentän sisältöön.
	 * @param field Kenttä, jonka merkkijonoesitystä verrataan olioon.
	 * @return Lista ehtoja vastaavista yksiköistä.
	 */
	List<T> getMatches(String search, FN field);

	/**
	 * Palauttaa ensimmäisen yksikön, jonka viite vastaa haluttua.
	 *
	 * @param ref Viite, jota vastaavaa yksikköä haetaan
	 * @return Hakua vastaava yksikkö
	 */
	T get(Ref ref);

	/**
	 * Poista viitettä vastaava yksikkö.
	 *
	 * @param ref Haettava viite, jota vastaava yksikkö poistetaan.
	 * @return Poistettu yksikkö.
	 */
	T remove(Ref ref);

	/**
	 * Lue vastaava tiedosto halutusta kansiosta.
	 * 
	 * @param dir Kansio, josta tiedosto luetaan.
	 * @throws StorageException Jos lukeminen epäonnistuu.
	 */
	void lue(File dir) throws StorageException;

	/**
	 * Tallenna yksiköt vastaavaan tiedostoon halutun kansion sisälle.
	 * 
	 * @param dir Kansio, johon yksiköt tallennetaan tiedostoon.
	 * @throws StorageException Jos tallentaminen epäonnistuu.
	 */
	void save(File dir) throws StorageException;

}
