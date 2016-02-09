package gui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import kentat.FN;
import tietorakenne.IUnit;

/**
 * http://www.algosome.com/articles/java-jcombobox-autocomplete.html
 * 
 * JComboBox with an autocomplete drop down menu. This class is hard-coded for String objects, but can be
 * altered into a generic form to allow for any searchable item.
 * @author G. Cope
 *
 */
public class AutocompleteJComboBox extends JComboBox {

	private List<? extends IUnit> items = null;
	protected FN field = null;
	protected JTextComponent currentText;

	/**
	 * Muodostaja.
	 */
	public AutocompleteJComboBox() {
		super();

		setEditable(true);

		Component c = getEditor().getEditorComponent();
		final JTextComponent tc = (JTextComponent)c;

		tc.getDocument().addDocumentListener(comboListener);

		currentText = tc;

		// When the text component changes, focus is gained and the menu disappears. To account for this, whenever the focus is gained by the JTextComponent and it has searchable values, we show the popup.
		tc.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				if (tc.getText().length() > 0) {
					//setPopupVisible(true);
					showPopup();
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				//setPopupVisible(false);
				hidePopup();
			}

		});
	}


	/**
	 * Aseta ComboBoxin sisältö.
	 * 
	 * @param items Lista yksiköistä.
	 * @param field Kenttä, jota yksiköistä käytetään.
	 */
	public void set(List<? extends IUnit> items, FN field) {
		this.items = items;
		this.field = field;
	}

	protected void update() {
		if (items == null) {
			return;
		}
		// perform separately, as listener conflicts between the editing component and JComboBox will result in an IllegalStateException due to editing the component when it is locked.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String txt = currentText.getText();
				List<IUnit> found = findUnitsStartingWith(txt);
				List<String> names = new ArrayList<>();
				for (IUnit s : found) {
					names.add(s.getFieldAsString(field));
				}
				Collections.sort(names);

				//currentText.setEditable(false);
				currentText.getDocument().removeDocumentListener(comboListener);
				//currentText.removeAll();
				hidePopup();
				removeAllItems();

				if (!names.contains(txt.toLowerCase())) {
					addItem(txt);
				}
				for (String s : names) {
					addItem(s);
				}

				//currentText.setText(txt);
				currentText.getDocument().addDocumentListener(comboListener);
				//currentText.setEditable(true);
				//update();
				//setPopupVisible(true);
				showPopup();
				fireActionEvent();
				currentText.setCaretPosition(currentText.getText().length());
			}
		});
	}

	protected String getText() {
		Component c = getEditor().getEditorComponent();
		final JTextComponent tc = (JTextComponent)c;
		return tc.getText();
	}

	private DocumentListener comboListener = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update();
		}
	};


	protected List<IUnit> findUnitsStartingWith(String str) {
		List<IUnit> matches = new ArrayList<>();
		for (IUnit s : items) {
			String data = s.getFieldAsString(field);
			if (data.indexOf(str) == 0) {
				matches.add(s);
			}
		}
		return matches;
	}

}
