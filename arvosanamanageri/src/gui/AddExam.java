package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kentat.FN;
import tietorakenne.Exam;
import arvosanamanageri.Controller;

/**
 * Kokeenlisäysikkuna
 * 
 * @author Sampo Osmonen
 */
public class AddExam extends JDialog {

	protected Controller controller;

	protected Exam student;
	
	private final JPanel contentPanel = new JPanel();
	JPanel buttonPane = new JPanel();
	
	protected JButton btn_cancel = new JButton("Peruuta");
	protected JButton btn_save = new JButton("Tallenna");
	protected final AutocompleteJComboBox autocompleteJComboBox;
	private final JLabel lbl_subject = new JLabel("Aine");
	private final JLabel lbl_date = new JLabel("Päivämäärä");
	protected final JTextField textField_date = new JTextField();

	/**
	 * Create the dialog.
	 * 
	 * @param controller Viite Controlleriin.
	 * @param exam Uusi koe.
	 * @param modality Modaalisuuden tyyppi.
	 */
	public AddExam(Controller controller, Exam exam, ModalityType modality) {
		this.controller = controller;
		this.student = exam;
		
		setModalityType(modality);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			panel_container.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panel_container, BorderLayout.CENTER);
		}
		panel_container.setLayout(new BorderLayout(0, 0));
		
		autocompleteJComboBox = new AutocompleteJComboBox();
		autocompleteJComboBox.set(controller.getSubjects(), FN.SUBJECT_NAME);
		autocompleteJComboBox.setPreferredSize(new Dimension(100, 20));
		autocompleteJComboBox.setMaximumSize(new Dimension(100, 32767));
		autocompleteJComboBox.setMinimumSize(new Dimension(100, 20));
		
		textField_date.setColumns(10);
		panel_container.add(contentPanel, BorderLayout.CENTER);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			GridBagConstraints gbc_lbl_subject = new GridBagConstraints();
			gbc_lbl_subject.anchor = GridBagConstraints.WEST;
			gbc_lbl_subject.insets = new Insets(0, 0, 5, 5);
			gbc_lbl_subject.gridx = 1;
			gbc_lbl_subject.gridy = 0;
			contentPanel.add(lbl_subject, gbc_lbl_subject);
		}
		{
			GridBagConstraints gbc_autocompleteJComboBox = new GridBagConstraints();
			gbc_autocompleteJComboBox.anchor = GridBagConstraints.WEST;
			gbc_autocompleteJComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_autocompleteJComboBox.gridx = 1;
			gbc_autocompleteJComboBox.gridy = 1;
			contentPanel.add(autocompleteJComboBox, gbc_autocompleteJComboBox);
		}
		{
			GridBagConstraints gbc_lbl_date = new GridBagConstraints();
			gbc_lbl_date.anchor = GridBagConstraints.WEST;
			gbc_lbl_date.insets = new Insets(0, 0, 5, 5);
			gbc_lbl_date.gridx = 1;
			gbc_lbl_date.gridy = 2;
			contentPanel.add(lbl_date, gbc_lbl_date);
		}
		{
			GridBagConstraints gbc_textField_date = new GridBagConstraints();
			gbc_textField_date.insets = new Insets(0, 0, 0, 5);
			gbc_textField_date.anchor = GridBagConstraints.WEST;
			gbc_textField_date.gridx = 1;
			gbc_textField_date.gridy = 3;
			contentPanel.add(textField_date, gbc_textField_date);
		}
		GridBagLayout gbl_buttonPane = new GridBagLayout();
		gbl_buttonPane.rowHeights = new int[]{23, 0};
		gbl_buttonPane.columnWeights = new double[]{1.0, 0.0, 0.0};
		gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_container.add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(gbl_buttonPane);
		{
			GridBagConstraints gbc_btn_cancel = new GridBagConstraints();
			gbc_btn_cancel.anchor = GridBagConstraints.EAST;
			gbc_btn_cancel.insets = new Insets(0, 0, 0, 5);
			gbc_btn_cancel.gridx = 1;
			gbc_btn_cancel.gridy = 0;
			buttonPane.add(btn_cancel, gbc_btn_cancel);
		}
		{
			GridBagConstraints gbc_btn_save = new GridBagConstraints();
			gbc_btn_save.anchor = GridBagConstraints.EAST;
			gbc_btn_save.gridx = 2;
			gbc_btn_save.gridy = 0;
			buttonPane.add(btn_save, gbc_btn_save);
			getRootPane().setDefaultButton(btn_save);
		}
		

		init();
		pack();
	}
	
	
	
	
	protected void init() {
		btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controller.confirmIfUnsavedChanges()) {
					return;
				}
				dispose();
			}
		});

		textField_date.getDocument().addDocumentListener(typeListener);
		autocompleteJComboBox.getEditor().addActionListener(editListener);

		btn_save.addActionListener(examSaveListener);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!controller.confirmIfUnsavedChanges()) {
					dispose();
				}
			}
		});
	}


	private ActionListener examSaveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String date = textField_date.getText();
			String subject = autocompleteJComboBox.getText();
			boolean saved = controller.saveExam(student, date, subject);
			if (saved) {
				dispose();
			}
		}
	};

	private DocumentListener typeListener = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
			// tätä ei tarvita
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			controller.registerChanges();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			controller.registerChanges();
		}
	};
	
	private ActionListener editListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.registerChanges();
		}
	};
	private final JPanel panel_container = new JPanel();

}
