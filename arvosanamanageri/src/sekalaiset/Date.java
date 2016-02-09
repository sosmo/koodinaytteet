package sekalaiset;

import java.io.Serializable;

/**
 * Oma helppo pvm-luokka
 * 
 * @author Sampo Osmonen
 */
public class Date implements Comparable<Date>, Serializable {
	
	int day, month, year;
	
	public Date(int day, int month, int year) {
		this.day=day;
		this.month=month;
		this.year=year;
	}
	
	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	@Override
	public String toString() {
		return sekalaiset.PvmKasittely.dateToString(this);
	}
	
	@Override
	public int compareTo(Date other) {
		if (getYear() > other.getYear())
			return 1;
		if (getYear() < other.getYear())
			return -1;
		if (getMonth() > other.getMonth())
			return 1;
		if (getMonth() < other.getMonth())
			return -1;
		if (getDay() > other.getDay())
			return 1;
		if (getDay() < other.getDay())
			return -1;
		return 0;
	}

}
