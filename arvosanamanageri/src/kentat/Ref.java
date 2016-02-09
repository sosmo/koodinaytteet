package kentat;

import java.io.Serializable;

/**
 * Viite, joka sisältää tiedon järjestysnumerosta ja kohteen tyypistä.
 * 
 * @author Sampo Osmonen
 */
public class Ref implements Comparable<Ref>, Serializable {

	private Class type;
	private Integer id = -1;


	/**
	 * @param type Viitteen kohteen luokka.
	 * @param id Viitteen numero.
	 */
	public Ref(Class<?> type, Integer id) {
		this.type = type;
		this.id = id;
	}


	/**
	 * @return Viitteen numero.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Viitteen kohteen luokka.
	 */
	public Class<?> getType() {
		return type;
	}

	@Override
	public int compareTo(Ref other) {
		if (getId().equals(other.getId())) {
			return 0;
		}
		if (getId() > other.getId()) {
			return 1;
		}
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Ref)) {
			return false;
		}
		Ref other = (Ref)o;
		if (other.getId().equals(getId()) && other.getType().equals(getType())) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return id.toString();
	}

}
