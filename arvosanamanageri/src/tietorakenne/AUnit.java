package tietorakenne;

import java.text.ParseException;
import java.util.Arrays;

import kentat.AField;
import kentat.FN;
import kentat.FieldCollection;
import kentat.FieldException;
import kentat.IField;
import kentat.Ref;
import kentat.RefField;
import sekalaiset.Taulukot;

/**
 * Abstrakti datayksikkö, joka sisältää tietokenttiä.
 * 
 * @author Sampo Osmonen
 */
public abstract class AUnit implements IUnit {

	private Ref ref = null;
	private final FieldCollection refFields;
	private final FieldCollection dataFields;
	private final FieldCollection fields;


	/**
	 * Muodostaja.
	 * 
	 * @param refFields Viitekentät, jotka yksikköön tallennetaan.
	 * @param dataFields Datakentät, jotka yksikköön tallennetaan.
	 */
	public AUnit(RefField[] refFields, AField<?>[] dataFields) {
		this.refFields = new FieldCollection(refFields);
		this.dataFields = new FieldCollection(dataFields);
		AField<?>[] combined = Taulukot.combine(AField.class, refFields, dataFields);
		fields = new FieldCollection(combined);
		for (AField<?> field : refFields) {
			if (!(field instanceof RefField)) {
				throw new FieldException("Viitekentät voivat olla vain RefField-tyyppiä" + Constants.NL + "\"" + field.getDescription() + "\" ei kelpaa.");
			}
		}
	}


	@Override
	public Ref getRef() {
		return ref;
	}

	protected void setRef(Ref ref) {
		this.ref = ref;
	}

	protected AField<Comparable<?>> getFieldObject(FN name) {
		AField<Comparable<?>> field = (AField<Comparable<?>>)fields.get(name);
		if (field == null) {
			throw new FieldException("Virheellinen kentännimi (" + name + ")");
		}
		return field;
	}


	@Override
	public int fieldCount() {
		return fields.size();
	}

	@Override
	public int refFieldCount() {
		return refFields.size();
	}

	@Override
	public int dataFieldCount() {
		return dataFields.size();
	}

	@Override
	public <T> T getField(FN name, Class<T> targetClass) {
		AField<Comparable<?>> field = getFieldObject(name);
		try {
			return (T)field.getValue();
		}
		catch (ClassCastException e) {
			throw new ClassCastException("Anna parametrina validi luokka, jonka olioon haluat tallettaa kentän arvon (" + targetClass + " ei kelpaa): " + e.getMessage());
		}
	}

	@Override
	public Ref getRefField(FN name) {
		AField<Comparable<?>> field = (AField<Comparable<?>>)refFields.get(name);
		if (field == null) {
			throw new FieldException("Virheellinen kentännimi (" + name + ")");
		}
		return (Ref)field.getValue();
	}

	@Override
	public Comparable<?> getField(FN name) {
		AField<Comparable<?>> field = getFieldObject(name);
		return field.getValue();
	}

	@Override
	public String getFieldAsString(FN name) {
		return getFieldObject(name).toString();
	}

	@Override
	public void setField(FN name, Comparable<?> value) {
		getFieldObject(name).setValue(value);
	}

	/**
	 * Muodosta taulukko yksikön datasta (id-numero ja kenttien sisällöt) määrittelyjärjestyksessä. Käytetään tallennettaessa tekstitiedostoon.
	 * 
	 * @return Taulukko, jossa id, sitten kenttien sisällöt määrittelyjärjestyksessä.
	 */
	public String[] getFieldsAsStrings() {
		String id = getRef().toString();
		String[] fields = new String[this.fields.size() + 1];
		fields[0] = id;
		int i = 1;
		for (IField<?> field : this.fields) {
			fields[i++] = field.toString();
		}
		return fields;
	}

	private static Comparable<?>[] getFields(FieldCollection fields) {
		Comparable<?>[] all = new Comparable<?>[fields.size()];
		int i = 0;
		for (AField<?> field : fields) {
			all[i++] = field.getValue();
		}
		return all;
	}

	@Override
	public Comparable<?>[] getDataFields() {
		return getFields(dataFields);
	}

	@Override
	public Ref[] getRefFields() {
		Ref[] all = new Ref[refFields.size()];
		int i = 0;
		for (AField<?> field : refFields) {
			all[i++] = (Ref)field.getValue();
		}
		return all;
	}

	/**
	 * @param field Kentän nimi.
	 * @return Kentän kuvaus.
	 */
	public String getFieldDescription(FN field) {
		return getFieldObject(field).getDescription();
	}

	protected static FN[] getFieldNames(FieldCollection fields) {
		FN[] names = new FN[fields.size()];
		int i = 0;
		for (AField<?> field : fields) {
			names[i++] = field.getName();
		}
		return names;
	}

	@Override
	public FN[] getDataFieldNames() {
		return getFieldNames(dataFields);
	}

	@Override
	public FN[] getRefFieldNames() {
		return getFieldNames(refFields);
	}

	@Override
	public void parseDataFields(String... data) throws ParseException {
		parseFields(dataFields, data);
	}

	protected static void parseFields(FieldCollection fields, String... data) throws ParseException {
		if (data.length != fields.size()) {
			throw new ParseException("Tietojen määrä ei täsmää kenttien kokonaismäärään", 0);
		}
		int i = 0;
		for (AField<?> field : fields) {
			field.parse(data[i++]);
		}
	}

	@Override
	public void parseField(FN nimi, String tiedot) throws ParseException {
		AField<Comparable<?>> kentta = getFieldObject(nimi);
		kentta.parse(tiedot);
	}

	@Override
	public void parse(String line) throws ParseException {
		Class<?> type = getClass();
		if (getRef() != null) {
			throw new IllegalStateException("Parsinta on tehtävä ennen lisäämistä");
		}
		String[] columns = line.split(Constants.DB_SEP, -1);
		if (columns.length < dataFields.size() + refFields.size() + 1) {
			throw new ParseException("Tietojen määrä ei täsmää kenttien määrään", line.length());
		}
		int pos = 0;
		try {
			int id = Integer.parseInt(columns[pos++]);
			Ref ref = new Ref(type, id);
			setRef(ref);
		}
		catch (NumberFormatException e) {
			throw new ParseException("Olion ref on virheellisessä muodossa", 0);
		}
		int endOfRefs = pos + refFields.size();
		String[] refs = Arrays.copyOfRange(columns, pos, endOfRefs);
		parseFields(refFields, refs);
		pos = endOfRefs;
		String[] data = Arrays.copyOfRange(columns, pos, columns.length);
		parseDataFields(data);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AUnit)) {
			return false;
		}
		AUnit unit = (AUnit)o;
		if (unit.getRef().equals(getRef())) {
			return true;
		}
		return false;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			//throw new RuntimeException(e.getMessage(), e.getCause());
			System.err.println("Olion (" + getClass() + ") luonti ei onnistunut");
			e.printStackTrace();
			return null;
		}
	}

}
