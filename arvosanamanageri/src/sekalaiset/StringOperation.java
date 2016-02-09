package sekalaiset;

import java.io.Serializable;

/**
 * Funktiorajapinta merkkijonojen käsittelylle.
 * 
 * @author Sampo Osmonen
 */
@FunctionalInterface
public interface StringOperation extends Serializable {

	/**
	 * @param x Käsiteltävä jono
	 * @return Käsitelty jono
	 */
	public String f(String x);

}