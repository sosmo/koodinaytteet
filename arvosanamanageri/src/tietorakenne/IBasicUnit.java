package tietorakenne;

import java.text.ParseException;

import kentat.FN;
import kentat.Ref;

public interface IBasicUnit {

	Ref getRef();

	Comparable getField(FN name);

	<T> T getField(FN name, Class<T> targetClass);

	void parse(String rivi) throws ParseException;

	void parseDataFields(String...data) throws ParseException;

	void parseField(FN nimi, String tiedot) throws ParseException;

	String toString();

	Ref getRefField(FN name);

	FN[] getDataFieldNames();

	FN[] getRefFieldNames();

	String getFieldAsString(FN name);

	void setField(FN name, Comparable value);

}
