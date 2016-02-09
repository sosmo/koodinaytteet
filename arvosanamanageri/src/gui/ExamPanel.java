package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * Paneeli, jossa näytetään kokeen tietoja.
 * 
 * @author Sampo Osmonen
 */
public class ExamPanel extends JPanel {

	private final JPanel panel_fields = new JPanel();
	protected final JTextField textField_date = new JTextField();
	private final JPanel panel_buttons = new JPanel();
	protected final JButton btn_remove = new JButton("Poista koe");
	private final JLabel lbl_date = new JLabel("Päivämäärä:");
	private final JLabel lbl_hSubject = new JLabel("Aine:");
	protected final JLabel lbl_subject = new JLabel("Aine");
	private final JPanel panel_data = new JPanel();
	private final JScrollPane scrollPane_data = new JScrollPane();
	protected final JTable table_data = new JTable();
	private final JLabel lbl_fieldsHeader = new JLabel("Kokeen ominaisuudet");
	private final JLabel lbl_dataHeader = new JLabel("Kokeen tulokset");
	protected final JButton btn_addGrade = new JButton("Lisää koetulos");

	/**
	 * Create the panel.
	 */
	public ExamPanel() {
		setLayout(new BorderLayout(0, 0));
		panel_fields.setBorder(new EmptyBorder(0, 0, 5, 0));


		add(panel_fields, BorderLayout.NORTH);
		GridBagLayout gbl_panel_fields = new GridBagLayout();
		gbl_panel_fields.columnWidths = new int[]{0, 0, 0};
		gbl_panel_fields.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel_fields.columnWeights = new double[]{0.0, 0.0, 1.0};
		gbl_panel_fields.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_fields.setLayout(gbl_panel_fields);

		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		gbc_lblNewLabel_1.gridwidth = 3;
		panel_fields.add(lbl_fieldsHeader, gbc_lblNewLabel_1);

		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		panel_fields.add(lbl_hSubject, gbc_lblNewLabel_2);

		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 2;
		gbc_lblNewLabel_3.gridwidth = 1;
		panel_fields.add(lbl_date, gbc_lblNewLabel_3);

		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(5, 0, 0, 0);
		gbc_lblNewLabel_4.gridwidth = 3;
		gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 3;
		panel_fields.add(lbl_dataHeader, gbc_lblNewLabel_4);

		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 1;
		panel_fields.add(lbl_subject, gbc_lblNewLabel_5);

		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_6.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 1;
		gbc_lblNewLabel_6.gridy = 2;
		textField_date.setPreferredSize(new Dimension(100, 20));
		panel_fields.add(textField_date, gbc_lblNewLabel_6);

		add(panel_data, BorderLayout.CENTER);
		panel_data.setLayout(new BorderLayout(0, 0));

		panel_data.add(scrollPane_data, BorderLayout.CENTER);

		table_data.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table_data.setIntercellSpacing(new Dimension(0, 0));
		table_data.setShowGrid(false);
		table_data.setFillsViewportHeight(true);

		scrollPane_data.setViewportView(table_data);
		panel_buttons.setBorder(new EmptyBorder(5, 0, 0, 0));


		add(panel_buttons, BorderLayout.SOUTH);

		GridBagLayout gbl_panel_buttons = new GridBagLayout();
		gbl_panel_buttons.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel_buttons.rowHeights = new int[]{0, 0};
		gbl_panel_buttons.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_buttons.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_buttons.setLayout(gbl_panel_buttons);

		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 0;
		panel_buttons.add(btn_addGrade, gbc_btnNewButton_1);

		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 0;
		panel_buttons.add(btn_remove, gbc_btnNewButton);

	}

}
