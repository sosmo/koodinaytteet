package sekalaiset;

import java.lang.reflect.Array;

/**
 * Metodeja taulukkojen käsittelyyn.
 * 
 * @author Sampo Osmonen
 */
public class Taulukot {
	
	/**
	 * Yhdistä saman tyyppisiä taulukoita yhteen taulukkoon.
	 * 
	 * @param targetClass Yhdistetyn taulukon luokka.
	 * @param array Ensimmäinen taulukko. Null-arvoja ei sallita.
	 * @param arrays Valinnaiset muut taulukot. Null-arvoja ei sallita.
	 * @return Taulukkojen yhdistelmä, jossa ovat kaikki eri taulukkojen alkiot parametrien mukaisessa järjestyksessä.
	 * @example
	 * <pre name="test">
	 *  #import java.util.Arrays;
	 *  Integer[] a1 = {1, 2, 3, 9};
	 *  Integer[] a2 = {};
	 *  Integer[] a3 = {4, 5, 2};
	 *  Integer[] expect;
	 *  Integer[] result;
	 *  expect = new Integer[] {1, 2, 3, 9, 4, 5, 2};
	 *  result = Taulukot.combine(Integer.class, a1, a2, a3);
	 *  Arrays.equals(result, expect) === true;
	 *  expect = new Integer[] {1, 2, 3, 9};
	 *  result = Taulukot.combine(Integer.class, a1);
	 *  Arrays.equals(result, expect) === true;
	 */
	public static <T> T[] combine(Class<T> targetClass, T[] array, T[]... arrays) {
		int length = array.length;
		for (int i = 0; i < arrays.length; i++) {
			length += arrays[i].length;
		}
		T[] result = (T[])Array.newInstance(targetClass, length);
		int iCurrent = 0;
		for (int i = 0; i < array.length; i++) {
			result[iCurrent++] = array[i];
		}
		for (int i = 0; i < arrays.length; i++) {
			for (int j = 0; j < arrays[i].length; j++) {
				result[iCurrent++] = arrays[i][j];
			}
		}
		return result;
	}
	
	/**
	 * Yhdistä saman tyyppisiä taulukoita yhteen taulukkoon.
	 * 
	 * @param array Ensimmäinen taulukko. Null-arvoja ei sallita.
	 * @param arrays Valinnaiset muut taulukot. Null-arvoja ei sallita.
	 * @return Taulukkojen yhdistelmä, jossa ovat kaikki eri taulukkojen alkiot parametrien mukaisessa järjestyksessä.
	 * @example
	 * <pre name="test">
	 *  #import java.util.Arrays;
	 *  Integer[] a1 = {1, 2, 3, 9};
	 *  Integer[] a2 = {};
	 *  Integer[] a3 = {4, 5, 2};
	 *  Object[] expect;
	 *  Object[] result;
	 *  expect = new Object[] {1, 2, 3, 9, 4, 5, 2};
	 *  result = Taulukot.combine(a1, a2, a3);
	 *  Arrays.equals(result, expect) === true;
	 *  expect = new Object[] {1, 2, 3, 9};
	 *  result = Taulukot.combine(a1);
	 *  Arrays.equals(result, expect) === true;
	 */
	public static Object[] combine(Object[] array, Object[]... arrays) {
		int length = array.length;
		for (int i = 0; i < arrays.length; i++) {
			length += arrays[i].length;
		}
		Object[] result = new Object[length];
		int iCurrent = 0;
		for (int i = 0; i < array.length; i++) {
			result[iCurrent++] = array[i];
		}
		for (int i = 0; i < arrays.length; i++) {
			for (int j = 0; j < arrays[i].length; j++) {
				result[iCurrent++] = arrays[i][j];
			}
		}
		return result;
	}
	
}
