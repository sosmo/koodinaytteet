package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Paneeli, jossa näytetään opiskelijan tietoja.
 * 
 * @author Sampo Osmonen
 */
public class StudentPanel extends JPanel {
	
	private final JPanel panel_fields = new JPanel();
	private final JPanel panel_defaultButtons = new JPanel();

	protected final JLabel lbl_firstName = new JLabel("Etunimi");
	protected final JLabel lbl_lastName = new JLabel("Sukunimi");
	protected final JLabel lbl_id = new JLabel("Henkilötunnus");
	protected final JTextField textField_firstName = new JTextField();
	protected final JTextField textField_lastName = new JTextField();
	protected final JTextField textField_id = new JTextField();
	protected final JButton btn_cancel = new JButton("Peruuta");
	protected final JButton btn_save = new JButton("Tallenna");
	protected final JPanel panel_buttons = new JPanel();
	
	/**
	 * Create the frame.
	 */
	public StudentPanel() {
		setLayout(new BorderLayout(0, 0));

		textField_id.setColumns(10);
		textField_lastName.setColumns(10);
		textField_firstName.setColumns(10);

		GridBagLayout gbl_panel_fields = new GridBagLayout();
		gbl_panel_fields.columnWidths = new int[]{0, 0, 0};
		gbl_panel_fields.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_fields.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_fields.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_fields.setLayout(gbl_panel_fields);
		add(panel_fields, BorderLayout.CENTER);

		GridBagConstraints gbc_lbl_firstName = new GridBagConstraints();
		gbc_lbl_firstName.anchor = GridBagConstraints.EAST;
		gbc_lbl_firstName.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_firstName.gridx = 0;
		gbc_lbl_firstName.gridy = 0;
		panel_fields.add(lbl_firstName, gbc_lbl_firstName);

		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel_fields.add(textField_firstName, gbc_textField);

		GridBagConstraints gbc_lbl_lastName = new GridBagConstraints();
		gbc_lbl_lastName.anchor = GridBagConstraints.EAST;
		gbc_lbl_lastName.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_lastName.gridx = 0;
		gbc_lbl_lastName.gridy = 1;
		panel_fields.add(lbl_lastName, gbc_lbl_lastName);

		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		panel_fields.add(textField_lastName, gbc_textField_1);

		GridBagConstraints gbc_lbl_id = new GridBagConstraints();
		gbc_lbl_id.anchor = GridBagConstraints.NORTHEAST;
		gbc_lbl_id.insets = new Insets(0, 0, 0, 5);
		gbc_lbl_id.gridx = 0;
		gbc_lbl_id.gridy = 2;
		panel_fields.add(lbl_id, gbc_lbl_id);

		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.anchor = GridBagConstraints.NORTH;
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 2;
		panel_fields.add(textField_id, gbc_textField_2);

		GridBagLayout gbl_buttonPane = new GridBagLayout();
		gbl_buttonPane.columnWeights = new double[]{1.0, 0.0, 0.0};
		gbl_buttonPane.rowWeights = new double[]{1.0};
		panel_defaultButtons.setBorder(new EmptyBorder(5, 0, 0, 0));
		add(panel_defaultButtons, BorderLayout.SOUTH);
		panel_defaultButtons.setLayout(gbl_buttonPane);

		GridBagConstraints gbc_panel_customButtons = new GridBagConstraints();
		gbc_panel_customButtons.insets = new Insets(0, 0, 0, 5);
		gbc_panel_customButtons.fill = GridBagConstraints.BOTH;
		gbc_panel_customButtons.gridx = 0;
		gbc_panel_customButtons.gridy = 0;
		panel_buttons.setBorder(new EmptyBorder(0, 0, 0, 10));
		panel_defaultButtons.add(panel_buttons, gbc_panel_customButtons);
		panel_buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

		GridBagConstraints gbc_btn_cancel = new GridBagConstraints();
		gbc_btn_cancel.anchor = GridBagConstraints.EAST;
		gbc_btn_cancel.insets = new Insets(0, 0, 0, 5);
		gbc_btn_cancel.gridx = 1;
		gbc_btn_cancel.gridy = 0;
		panel_defaultButtons.add(btn_cancel, gbc_btn_cancel);


		GridBagConstraints gbc_btn_save = new GridBagConstraints();
		gbc_btn_save.anchor = GridBagConstraints.EAST;
		gbc_btn_save.gridx = 2;
		gbc_btn_save.gridy = 0;
		panel_defaultButtons.add(btn_save, gbc_btn_save);
	}

}
