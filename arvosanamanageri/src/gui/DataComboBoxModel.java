package gui;

import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import tietorakenne.IUnit;

/**
 * ComboBox, johon voi tallentaa IUnit-rajapinnan olioita.
 * 
 * @author Sampo Osmonen
 * @param <T> Yksiköt, joita tallennetaan.
 */
public class DataComboBoxModel<T extends IUnit> extends AbstractListModel<Object> implements ComboBoxModel<Object> {

	private List<?> items;
	private Object selected = null;


	/**
	 * Muodostaja.
	 * 
	 * @param items Tallennettavat oliot.
	 */
	public DataComboBoxModel(Object[] items) {
		this.items = Arrays.asList(items);
		if (items.length > 0) {
			selected = items[0];
		}
	}


	@Override
	public Object getElementAt(int i) {
		return items.get(i);
	}


	@Override
	public int getSize() {
		return items.size();
	}


	@Override
	public Object getSelectedItem() {
		return selected;
	}


	@Override
	public void setSelectedItem(Object o) {
		if (!items.contains(o)) {
			selected = null;
			return;
		}
		selected = o;
		fireContentsChanged(this, 0, items.size());
	}

}