package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import kentat.FN;
import kentat.FieldException;
import tietorakenne.IUnit;

/**
 * JTable, johon voi tallentaa IUnit-rajapinnan olioita.
 * 
 * @author Sampo Osmonen
 * @param <T> Yksiköt, joita tallennetaan.
 */
public class DataTableModel<T extends IUnit> extends AbstractTableModel {

	List<T> items = new ArrayList<>();
	List<FN> fields = new ArrayList<>();
	List<String> fieldDescriptions = new ArrayList<>();


	/**
	 * Muodostaja
	 */
	public DataTableModel() {
	}

	/**
	 * Muodostaja
	 * 
	 * @param items Lisättävät IUnitit.
	 * @param fields Kentät, joita IUniteista käytetään.
	 * @param fieldDescriptions Kenttien kuvaukset (näytetään sarakeotsikoissa).
	 */
	public DataTableModel(List<? extends T> items, FN[] fields, String[] fieldDescriptions) {
		this.items = (List<T>)items;
		this.fields.addAll(Arrays.asList(fields));
		this.fieldDescriptions.addAll(Arrays.asList(fieldDescriptions));
	}


	/**
	 * Lisää sarake.
	 * 
	 * @param field Tallennetuista olioista löytyvä kenttä.
	 * @param fieldDesctiption Kentän kuvaus (näytetään sarakeotsikoissa).
	 */
	public void addColumn(FN field, String fieldDesctiption) {
		fields.add(field);
		fieldDescriptions.add(fieldDesctiption);
		fireTableStructureChanged();
	}

	/**
	 * Lisää yksikkö.
	 * 
	 * @param item Lisättävä IUnit.
	 */
	public void addRow(T item) {
		items.add(item);
		fireTableStructureChanged();
	}

	/**
	 * @param rowIndex Poistettavan rivin indeksi.
	 */
	public void removeRow(int rowIndex) {
		items.remove(rowIndex);
		fireTableStructureChanged();
	}

	@Override
	public int getColumnCount() {
		return fields.size();
	}

	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		T item = items.get(rowIndex);
		FN fieldName = getFieldName(columnIndex);
		return item.getField(fieldName);
	}

	protected FN getFieldName(int columnIndex) {
		if (columnIndex >= fields.size()) {
			throw new FieldException("Virheellinen taulukon sarakeindeksi (" + columnIndex + ")");
		}
		return this.fields.get(columnIndex);
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex >= fieldDescriptions.size()) {
			throw new FieldException("Virheellinen taulukon sarakeindeksi (" + columnIndex + ")");
		}
		return this.fieldDescriptions.get(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Comparable.class;
	}

	/**
	 * Hae riviä vastaava IUnit.
	 * 
	 * @param rowIndex Rivin indeksi.
	 * @return Rivin muodostava yksikkö.
	 */
	public T getUnitAt(int rowIndex) {
		return items.get(rowIndex);
	}

	/**
	 * Hae rivi, jolla yksikkö on.
	 * 
	 * @param unit Haettava yksikkö.
	 * @return Rivi-indeksi, jolla yksikkö on, -1 jos ei löydy.
	 */
	public int getRow(T unit) {
		int i = 0;
		for (T u : items) {
			if (u.equals(unit)) {
				return i;
			}
			i++;
		}
		return -1;
	}

}
