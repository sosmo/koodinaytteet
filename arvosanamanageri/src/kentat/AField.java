package kentat;

import java.text.ParseException;

/**
 * Abstrakti luokka kenttiä varten. Kenttä sisältää arvon, ja tiedon siitä, minkälaista arvoa se edustaa.
 * 
 * @author Sampo Osmonen
 */
public abstract class AField<T extends Comparable<?>> implements IField<T> {

	private final FN name;
	private T value = null;
	private final String description;


	/**
	 * @param name Kentän tunnistava nimi.
	 * @param description Kuvaus.
	 */
	public AField(FN name, String description) {
		this.name = name;
		this.description = description;
	}


	/**
	 * @return Kentän arvo alkuperäisessä muodossaan.
	 */
	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * @return nimi
	 */
	public FN getName() {
		return name;
	}

	/**
	 * @return kuvaus
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public abstract void parse(String jono) throws ParseException;
	
	protected static void blockEmpty(String str) throws ParseException {
		if (str.equals("")) {
			throw new ParseException("Kenttä ei voi olla tyhjä", 0);
		}
	}

	/**
	 * @return Kentän arvo merkkijonona.
	 */
	@Override
	public String toString() {
		if (getValue() == null) {
			return "";
		}
		return getValue().toString();
	}

}
