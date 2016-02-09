package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tietorakenne.Student;
import arvosanamanageri.Controller;

/**
 * @author Sampo Osmonen
 */
public class AddStudent extends JDialog {

	protected Controller controller;

	private JPanel contentPanel = new JPanel();
	protected Student student;

	protected StudentPanel studentView;


	/**
	 * Create the frame.
	 * 
	 * @param controller Viite Controlleriin.
	 * @param student Uusi oppilas.
	 * @param modality Modaalisuuden tyyppi.
	 */
	public AddStudent(Controller controller, Student student, ModalityType modality) {
		this.controller = controller;
		this.student = student;
		studentView = new StudentPanel();
		
		setModalityType(modality);

		setBounds(100, 100, 450, 300);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPanel);

		getContentPane().add(studentView, BorderLayout.CENTER);

		init();
		pack();
	}

	protected void init() {
		studentView.btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controller.confirmIfUnsavedChanges()) {
					return;
				}
				dispose();
			}
		});

		studentView.textField_firstName.getDocument().addDocumentListener(typeListener);
		studentView.textField_lastName.getDocument().addDocumentListener(typeListener);
		studentView.textField_id.getDocument().addDocumentListener(typeListener);

		studentView.btn_save.addActionListener(studentSaveListener);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!controller.confirmIfUnsavedChanges()) {
					dispose();
				}
			}
		});
	}


	private ActionListener studentSaveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String first = studentView.textField_firstName.getText();
			String last = studentView.textField_lastName.getText();
			String id = studentView.textField_id.getText();
			
			boolean saved = controller.saveStudent(student, first, last, id);
			if (saved) {
				dispose();
			}
		}
	};

	private DocumentListener typeListener = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
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

}