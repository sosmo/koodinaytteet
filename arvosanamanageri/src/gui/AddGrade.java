package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tietorakenne.Exam;
import tietorakenne.Grade;
import tietorakenne.Student;
import arvosanamanageri.Controller;

/**
 * Arvosananlisäysikkuna
 * 
 * @author Sampo Osmonen
 */
public class AddGrade extends JDialog {

	protected final Controller controller;
	protected final Grade grade = new Grade();
	protected final Exam exam;

	private final JPanel contentPanel = new JPanel();

	private final JLabel lbl_grade = new JLabel("Arvosana");
	protected final JPanel panel_defaultButtons = new JPanel();

	protected final JTextField textField_grade = new JTextField();

	protected final StudentsPanel studentsView = new StudentsPanel();

	protected final JButton btn_cancel = new JButton("Peruuta");
	protected final JButton btn_save = new JButton("Tallenna");

	private final JPanel panel_container = new JPanel();
	protected final JPanel panel_buttons = new JPanel();


	/**
	 * Create the frame.
	 * 
	 * @param controller Viite Controlleriin.
	 * @param exam Arvosanaan liittyvä koe.
	 * @param modality Modaalisuuden tyyppi.
	 */
	public AddGrade(Controller controller, Exam exam, ModalityType modality) {
		this.controller = controller;
		this.exam = exam;

		setModalityType(modality);

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			panel_container.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panel_container, BorderLayout.CENTER);
		}
		panel_container.setLayout(new BorderLayout(0, 0));
		panel_container.add(contentPanel, BorderLayout.CENTER);

		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{138, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lbl_student = new JLabel("Opiskelija");
			GridBagConstraints gbc_lbl_student = new GridBagConstraints();
			gbc_lbl_student.anchor = GridBagConstraints.WEST;
			gbc_lbl_student.insets = new Insets(0, 0, 5, 5);
			gbc_lbl_student.gridx = 0;
			gbc_lbl_student.gridy = 0;
			contentPanel.add(lbl_student, gbc_lbl_student);
		}
		{
			GridBagConstraints gbc_studentsView = new GridBagConstraints();
			gbc_studentsView.insets = new Insets(0, 0, 5, 0);
			gbc_studentsView.gridwidth = 2;
			gbc_studentsView.fill = GridBagConstraints.BOTH;
			gbc_studentsView.gridx = 0;
			gbc_studentsView.gridy = 1;
			contentPanel.add(studentsView, gbc_studentsView);
		}
		{
			GridBagConstraints gbc_lbl_grade = new GridBagConstraints();
			gbc_lbl_grade.anchor = GridBagConstraints.WEST;
			gbc_lbl_grade.insets = new Insets(5, 0, 5, 5);
			gbc_lbl_grade.gridx = 0;
			gbc_lbl_grade.gridy = 2;
			contentPanel.add(lbl_grade, gbc_lbl_grade);
		}
		{
			GridBagConstraints gbc_textField_grade = new GridBagConstraints();
			gbc_textField_grade.anchor = GridBagConstraints.WEST;
			gbc_textField_grade.insets = new Insets(0, 0, 0, 5);
			gbc_textField_grade.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_grade.gridx = 0;
			gbc_textField_grade.gridy = 3;
			contentPanel.add(textField_grade, gbc_textField_grade);
			textField_grade.setColumns(10);
		}

		{
			GridBagLayout gbl_panel_buttons = new GridBagLayout();
			gbl_panel_buttons.rowHeights = new int[]{23, 0};
			gbl_panel_buttons.columnWeights = new double[]{1.0, 0.0, 0.0};
			gbl_panel_buttons.rowWeights = new double[]{1.0, Double.MIN_VALUE};
			panel_defaultButtons.setBorder(new EmptyBorder(5, 0, 0, 0));
			panel_container.add(panel_defaultButtons, BorderLayout.SOUTH);
			panel_defaultButtons.setLayout(gbl_panel_buttons);
			{
				GridBagConstraints gbc_btn_remove = new GridBagConstraints();
				gbc_btn_remove.anchor = GridBagConstraints.NORTHWEST;
				gbc_btn_remove.insets = new Insets(0, 0, 0, 5);
				gbc_btn_remove.gridx = 0;
				gbc_btn_remove.gridy = 0;
			}
			{
				GridBagConstraints gbc_panel_buttons = new GridBagConstraints();
				gbc_panel_buttons.insets = new Insets(0, 0, 0, 5);
				gbc_panel_buttons.fill = GridBagConstraints.BOTH;
				gbc_panel_buttons.gridx = 0;
				gbc_panel_buttons.gridy = 0;
				panel_buttons.setBorder(new EmptyBorder(0, 0, 0, 10));
				panel_defaultButtons.add(panel_buttons, gbc_panel_buttons);
			}
			panel_buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
			{
				GridBagConstraints gbc_btn_cancel = new GridBagConstraints();
				gbc_btn_cancel.anchor = GridBagConstraints.EAST;
				gbc_btn_cancel.insets = new Insets(0, 0, 0, 5);
				gbc_btn_cancel.gridx = 1;
				gbc_btn_cancel.gridy = 0;
				panel_defaultButtons.add(btn_cancel, gbc_btn_cancel);
			}
			{
				GridBagConstraints gbc_btn_save = new GridBagConstraints();
				gbc_btn_save.anchor = GridBagConstraints.EAST;
				gbc_btn_save.gridx = 2;
				gbc_btn_save.gridy = 0;
				panel_defaultButtons.add(btn_save, gbc_btn_save);
				getRootPane().setDefaultButton(btn_save);
			}
		}

		init();
	}


	private void init() {
		studentsView.textField_name.getDocument().addDocumentListener(studentsTypeListener);

		studentsView.textField_id.getDocument().addDocumentListener(studentsTypeListener);

		studentsView.btn_clearName.addActionListener(studentsNameClearListener);

		studentsView.btn_clearId.addActionListener(studentsIdClearListener);


		btn_save.addActionListener(gradeSaveListener);

		btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});


		JTable table = studentsView.table_data;
		table.setModel(controller.getStudentTableModel("", ""));
		table.setAutoCreateRowSorter(true);
	}


	private ActionListener studentsNameClearListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			studentsView.textField_name.setText("");
		}
	};

	private ActionListener studentsIdClearListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			studentsView.textField_id.setText("");
		}
	};


	protected void studentsTypeEvent() {
		String name = studentsView.textField_name.getText();
		String id = studentsView.textField_id.getText();
		JTable table = studentsView.table_data;
		table.setModel(controller.getStudentTableModel(name, id));
		table.setRowSelectionInterval(0, 0);
	}

	private DocumentListener studentsTypeListener = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
			// tätä ei tarvita
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			studentsTypeEvent();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			studentsTypeEvent();
		}
	};


	private ActionListener gradeSaveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String gradeStr = textField_grade.getText();
			JTable table = studentsView.table_data;
			DataTableModel<Student> dtm = (DataTableModel<Student>)table.getModel();
			int tableRow = table.getSelectedRow();
			if (tableRow < 0) {
				JOptionPane.showMessageDialog(null, "Valitse ensin opiskelija!", "Virhe", JOptionPane.WARNING_MESSAGE);
			}
			int row = table.convertRowIndexToModel(tableRow);
			Student stu = dtm.getUnitAt(row);
			boolean saved = controller.saveGrade(grade, exam, gradeStr, stu);
			if (saved) {
				dispose();
			}
		}
	};

}
