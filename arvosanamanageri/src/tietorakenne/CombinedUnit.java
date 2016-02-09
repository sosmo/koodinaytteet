package tietorakenne;

import java.io.ObjectInputStream.GetField;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jgoodies.forms.layout.ConstantSize.Unit;

import kentat.FN;
import kentat.AField;
import kentat.FieldCollection;
import kentat.FieldException;
import kentat.Ref;
import kentat.RefField;
import sekalaiset.Taulukot;

public class CombinedUnit implements IUnit {

	private Map<Class<? extends IUnit>, IUnit> components = new HashMap<>();
	// Kenttien isäntäoliot
	private Map<FN, IUnit> refFields = new HashMap<>();
	private Map<FN, IUnit> dataFields = new HashMap<>();
	private Map<FN, IUnit> fields = new HashMap<>();


	public CombinedUnit(IUnit... sources) {
		if (sources.length < 2) {
			throw new IllegalArgumentException("Muodostajalle on annettava vähintään kaksi argumenttia");
		}
		for (IUnit s : sources) {
			putSource(s);
			buildRefFields(s, refFields);
			buildFields(s, dataFields);
		}
		fields.putAll(refFields);
		fields.putAll(dataFields);
	}
	
	private void putSource(IUnit source) {
		if (source instanceof CombinedUnit) {
			CombinedUnit comboSource = (CombinedUnit)source;
			components.putAll(comboSource.getComponentMap());
		}
		else {
			components.put(source.getClass(), source);
		}
	}

	private void buildRefFields(IUnit unit, Map<FN, IUnit> target) {
		FN[] refFields = unit.getRefFieldNames();
		for (FN fn : refFields) {
			target.put(fn, unit);
		}
	}

	private void buildFields(IUnit unit, Map<FN, IUnit> target) {
		FN[] dataFields = unit.getDataFieldNames();
		for (FN fn : dataFields) {
			target.put(fn, unit);
		}
	}
	
	public <T extends IUnit> T getComponent(Class<T> type) {
		IUnit result =  components.get(type);
		if (result == null) {
			throw new IllegalArgumentException("Virheellinen tyyppi (" + type + ")");
		}
		return type.cast(result);
	}
	
	public IUnit[] getComponents() {
		return components.values().toArray(new IUnit[0]);
	}
	
	protected Map<Class<? extends IUnit>, IUnit> getComponentMap() {
		return components;
	}
	


	public int fieldCount() {
		return fields.size();
	}

	public int refFieldCount() {
		return refFields.size();
	}

	public int dataFieldCount() {
		return dataFields.size();
	}

	public Comparable<?> getField(FN name) {
		IUnit unit = fields.get(name);
		if (unit == null) {
			throw new FieldException("Virheellinen kentännimi (" + name + ")");
		}
		Comparable field = unit.getField(name);
		return field;
	}

	public <T> T getField(FN name, Class<T> targetClass) {
		Object field = getField(name);
		try {
			return (T)field;
		}
		catch (ClassCastException e) {
			throw new ClassCastException("Anna parametrina validi luokka, jonka olioon haluat tallettaa kentän arvon (" + targetClass + " ei kelpaa): " + e.getMessage());
		}
	}

	public Ref getRefField(FN name) {
		Object field = getField(name);
		return (Ref)field;
	}

	public String getFieldAsString(FN name) {
		return getField(name).toString();
	}

	public void setField(FN name, Comparable value) {
		IUnit unit = fields.get(name);
		if (unit == null) {
			throw new FieldException("Virheellinen kentännimi (" + name + ")");
		}
		unit.setField(name, value);
	}

	private Comparable<?>[] getFields(Map<FN, IUnit> fields) {
		Comparable[] result = new Comparable[fields.size()];
		Set<FN> fieldNames = fields.keySet();
		int i = 0;
		for (FN field : fieldNames) {
			result[i++] = getField(field);
		}
		return result;
	}

	public Comparable<?>[] getDataFields() {
		return getFields(dataFields);
	}

	public Ref[] getRefFields() {
		Comparable[] fields = getFields(refFields);
		return Arrays.copyOf(fields, fields.length, Ref[].class);
	}

	public FN[] getDataFieldNames() {
		return dataFields.keySet().toArray(new FN[0]);
	}

	public FN[] getRefFieldNames() {
		return refFields.keySet().toArray(new FN[0]);
	}

	public void parseField(FN nimi, String tiedot) throws ParseException {
		fields.get(nimi).parseField(nimi, tiedot);
	}

	
	
	@Override
	public void parse(String rivi) throws ParseException {
		throw new UnsupportedOperationException();  //TODO
		
	}

	@Override
	public void parseDataFields(String... data) throws ParseException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Ref getRef() {
		// TODO Auto-generated method stub
		return null;
	}

}
