package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kentat.FN;
import tietorakenne.CombinedUnit;
import tietorakenne.Exam;
import tietorakenne.Grade;
import tietorakenne.IUnit;
import tietorakenne.Student;
import tietorakenne.Subject;
import arvosanamanageri.Controller;

/**
 * Ohjelman pääikkuna.
 * 
 * @author Sampo Osmonen
 */
public class Main extends JFrame {

	protected Controller controller = null;

	private JPanel contentPane = new JPanel();
	private final JPanel tabsPanel = new JPanel();
	private JPanel contentView = new JPanel();
	private final JButton btnExams = new JButton("Kokeet");
	private final JButton btnStudents = new JButton("Opiskelijat");

	protected ExamsPanel examsView = null;
	protected ExamPanel examView = null;
	protected StudentsPanel studentsView = null;

	private final JButton btn_open = new JButton("Avaa...");
	private final JButton btn_save = new JButton("Tallenna");
	private final JButton btn_new = new JButton("Uusi...");

	private JPanel activeContent = null;

	private final String tTitle = "Arvosanamanageri";
	
	protected Exam exam = null;


	/**
	 * Create the frame.
	 * 
	 * @param controller Viite controlleriin.
	 */
	public Main(Controller controller) {
		setTitle(tTitle);
		this.controller = controller;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		tabsPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		contentPane.add(tabsPanel, BorderLayout.NORTH);
		GridBagLayout gbl_tabsPanel = new GridBagLayout();
		gbl_tabsPanel.columnWidths = new int[]{65, 81, 0, 0, 0, 0, 0};
		gbl_tabsPanel.rowHeights = new int[]{23, 0};
		gbl_tabsPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_tabsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		tabsPanel.setLayout(gbl_tabsPanel);

		GridBagConstraints gbc_btnExams = new GridBagConstraints();
		gbc_btnExams.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnExams.insets = new Insets(0, 0, 0, 5);
		gbc_btnExams.gridx = 0;
		gbc_btnExams.gridy = 0;
		tabsPanel.add(btnExams, gbc_btnExams);

		GridBagConstraints gbc_btnStudents = new GridBagConstraints();
		gbc_btnStudents.insets = new Insets(0, 0, 0, 5);
		gbc_btnStudents.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnStudents.gridx = 1;
		gbc_btnStudents.gridy = 0;
		tabsPanel.add(btnStudents, gbc_btnStudents);

		GridBagConstraints gbc_btn_save = new GridBagConstraints();
		gbc_btn_save.insets = new Insets(0, 0, 0, 5);
		gbc_btn_save.gridx = 3;
		gbc_btn_save.gridy = 0;
		tabsPanel.add(btn_save, gbc_btn_save);

		GridBagConstraints gbc_btn_open = new GridBagConstraints();
		gbc_btn_open.insets = new Insets(0, 0, 0, 5);
		gbc_btn_open.gridx = 4;
		gbc_btn_open.gridy = 0;
		tabsPanel.add(btn_open, gbc_btn_open);

		GridBagConstraints gbc_btn_new = new GridBagConstraints();
		gbc_btn_new.gridx = 5;
		gbc_btn_new.gridy = 0;
		tabsPanel.add(btn_new, gbc_btn_new);

		contentView.setBorder(new EmptyBorder(10, 0, 0, 0));
		contentView.setLayout(new BorderLayout(0, 0));
		contentPane.add(contentView);

		init();
	}

	private void init() {
		showExams();

		btnExams.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!controller.confirmIfUnsavedChanges()) {
					Main.this.showExams();
				}
			}

		});

		btnStudents.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!controller.confirmIfUnsavedChanges()) {
					Main.this.showStudents();
				}
			}

		});

		btn_open.addActionListener(fileOpenListener);

		btn_save.addActionListener(saveListener);

		btn_new.addActionListener(newFileListener);

		center();
	}


	private void center() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
		int x = middle.x - (this.getWidth() / 2);
		int y = middle.y - (this.getHeight() / 2);
		setLocation(new Point(x, y));
	}



	private final ActionListener fileOpenListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			File dir = controller.openDir();

			if (dir != null) {
				resetViews();
				showExams();

				setTitle(tTitle + " - " + dir.getName());
			}
		}


	};

	private final ActionListener newFileListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			File dir = controller.saveNewDir();

			if (dir != null) {
				resetViews();
				showExams();

				setTitle(tTitle + " - " + dir.getName());
			}
		}

	};

	private ActionListener saveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			File dir = controller.saveCurrentDir();
			
			if (dir != null) {
				setTitle(tTitle + " - " + dir.getName());
			}
		}
	};


	private ActionListener examSaveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String date = examView.textField_date.getText();
			boolean saved = controller.saveExam(exam, date);
			if (saved) {
				showExams();
				controller.saveToStorage();
			}
		}
	};



	private DocumentListener typeListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			controller.registerChanges();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			controller.registerChanges();
		}

	};



	private MouseAdapter examsTableClickListener = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JTable table = (JTable)e.getSource();
				DataTableModel<CombinedUnit> tm = (DataTableModel<CombinedUnit>)table.getModel();
				int row = table.convertRowIndexToModel(table.getSelectedRow());
				CombinedUnit value = tm.getUnitAt(row);
				Exam exam = value.getComponent(Exam.class);
				showExam(exam);
			}
		}

	};

	private ActionListener examsSubjectSelectListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox source = (JComboBox)e.getSource();
			DataComboBoxModel<Subject> cm = (DataComboBoxModel<Subject>)source.getModel();
			Object selection = cm.getSelectedItem();
			Subject subject = null;
			if (selection instanceof IUnit) {
				subject = (Subject)selection;
			}
			String date = typedExamDate();
			JTable table = examsView.table_data;
			table.setModel(controller.getExamTableModel(subject, date));
		}

	};

	private ActionListener examsAddListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.newExam(Main.this);
		}

	};



	private ActionListener examRemoveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (controller.remove(exam)) {
				exam = null;
				showExams();
			}
		}
	};


	/**
	 * Tyhjennä painikkeen kuuntelijat ja aseta uusi.
	 * 
	 * @param b Painike.
	 * @param al Uusi kuuntelija.
	 */
	public static void prepareButtonAction(JButton b, ActionListener al) {
		removeActionListeners(b);
		if (al == null) {
			return;
		}
		b.addActionListener(al);
		b.setEnabled(true);
	}

	/**
	 * Tyhjennä painikkeen kuuntelijat.
	 * 
	 * @param b Painike.
	 */
	public static void removeActionListeners(JButton b) {
		ActionListener[] listeners = b.getActionListeners();
		for (ActionListener al : listeners) {
			b.removeActionListener(al);
		}
	}



	private MouseAdapter studentsTableClickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JTable table = (JTable)e.getSource();
				int row = table.getSelectedRow();
				DataTableModel<CombinedUnit> tm = (DataTableModel<CombinedUnit>)table.getModel();
				int iRow = table.convertRowIndexToModel(row);
				IUnit student = tm.getUnitAt(iRow);
				controller.openStudent((Student)student, Main.this);
			}
		}
	};




	private MouseAdapter examTableClickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JTable table = (JTable)e.getSource();
				int row = table.getSelectedRow();
				DataTableModel<CombinedUnit> tm = (DataTableModel<CombinedUnit>)table.getModel();
				int iRow = table.convertRowIndexToModel(row);
				CombinedUnit cu = tm.getUnitAt(iRow);
				Grade grade = cu.getComponent(Grade.class);
				controller.openGrade(grade, exam, Main.this);
			}
		}
	};


	private ActionListener studentsAddListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.newStudent(Main.this);
		}
	};

	private ActionListener examGradeAddListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.newGrade(exam, Main.this);
		}
	};

	private DocumentListener examsTypeListener = new DocumentListener() {

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			examsTypeEvent();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			examsTypeEvent();
		}
	};

	private void examsTypeEvent() {
		String date = examsView.textField_date.getText();
		Subject subject = selectedExamSubject();
		JTable table = examsView.table_data;
		table.setModel(controller.getExamTableModel(subject, date));
	}


	private DocumentListener studentsTypeListener = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
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

	protected void studentsTypeEvent() {
		String name = studentsView.textField_name.getText();
		String id = studentsView.textField_id.getText();
		JTable table = studentsView.table_data;
		table.setModel(controller.getStudentTableModel(name, id));
	}



	protected ActionListener examsDateClearListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			examsView.textField_date.setText("");
		}
	};

	protected ActionListener studentsNameClearListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			studentsView.textField_name.setText("");
		}
	};

	protected ActionListener studentsIdClearListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			studentsView.textField_id.setText("");
		}
	};



	protected Subject selectedExamSubject() {
		if (activeContent != examsView) {
			return null;
		}
		DataComboBoxModel<Subject> dm = (DataComboBoxModel<Subject>)examsView.comboBox_subject.getModel();
		Object selection = dm.getSelectedItem();
		Subject subject = null;
		if (selection instanceof IUnit) {
			subject = (Subject)selection;
		}
		return subject;
	}

	protected String typedExamDate() {
		if (activeContent != examsView) {
			return null;
		}
		return examsView.textField_date.getText();
	}


	protected String typedStudentId() {
		if (activeContent != studentsView) {
			return null;
		}
		return studentsView.textField_id.getText();
	}

	protected String typedStudentName() {
		if (activeContent != studentsView) {
			return null;
		}
		return studentsView.textField_name.getText();
	}



	private void clearContentView() {
		contentView.removeAll();
		activeContent = null;
	}

	private void prepareContentView(JPanel content) {
		activeContent = content;
		contentView.add(content);
		contentView.validate();
		contentView.repaint();
	}



	/**
	 * Päivitä nykyinen näkymä.
	 */
	public void update() {
		if (activeContent == studentsView) {
			showStudents();
		}
		else if (activeContent == examsView) {
			showExams();
		}
		else if (activeContent == examView) {
				showExam(exam);
		}
	}


	protected void resetViews() {
		examsView = null;
		examView = null;
		studentsView = null;
	}


	/**
	 * Näytä koepaneeli ikkunassa.
	 * 
	 * @param exam Näytettävä koe.
	 */
	public void showExam(Exam exam) {
		this.exam = exam;
		
		if (exam == null) {
			return;
		}

		clearContentView();

		examView = new ExamPanel();

		IUnit subject = controller.getRefTarget(exam, FN.EXAM_SUBJECT_REF);
		String subjectName = subject.getFieldAsString(FN.SUBJECT_NAME);
		examView.lbl_subject.setText(subjectName);

		String date = exam.getFieldAsString(FN.EXAM_DATE);
		examView.textField_date.setText(date);

		examView.btn_remove.addActionListener(examRemoveListener);

		examView.btn_addGrade.addActionListener(examGradeAddListener);

		examView.textField_date.getDocument().addDocumentListener(typeListener);

		JTable table = examView.table_data;
		table.setModel(controller.getGradeTableModel(exam));
		table.addMouseListener(examTableClickListener);
		table.setAutoCreateRowSorter(true);

		prepareContentView(examView);

		prepareButtonAction(btn_save, examSaveListener);
	}


	/**
	 * Näytä kokeet-paneeli ikkunassa.
	 */
	public void showExams() {
		if (controller.confirmIfUnsavedChanges()) {
			return;
		}

		clearContentView();

		if (examsView == null) {
			examsView = new ExamsPanel();

			examsView.btn_clearDate.addActionListener(examsDateClearListener);

			examsView.btn_add.addActionListener(examsAddListener);

			examsView.textField_date.getDocument().addDocumentListener(examsTypeListener);

			JComboBox subject = examsView.comboBox_subject;
			subject.setModel(controller.getSubjectModel());
			subject.setSelectedIndex(0);
			subject.addActionListener(examsSubjectSelectListener);

			examsView.btn_add.addActionListener(subject);

			JTable table = examsView.table_data;
			table.addMouseListener(examsTableClickListener);
		}

		prepareContentView(examsView);

		JComboBox subject = examsView.comboBox_subject;
		Object selectedSubject = subject.getSelectedItem();
		subject.setModel(controller.getSubjectModel());
		subject.setSelectedItem(selectedSubject);

		JTable table = examsView.table_data;
		table.setModel(controller.getExamTableModel(selectedExamSubject(), typedExamDate()));
		table.setAutoCreateRowSorter(true);

		prepareButtonAction(btn_save, saveListener);
	}

	/**
	 * Näytä opiskelijat-paneeli ikkunassa.
	 */
	public void showStudents() {
		clearContentView();

		if (studentsView == null) {
			studentsView = new StudentsPanel();

			studentsView.textField_name.getDocument().addDocumentListener(studentsTypeListener);

			studentsView.textField_id.getDocument().addDocumentListener(studentsTypeListener);

			studentsView.btn_clearName.addActionListener(studentsNameClearListener);

			JButton add = new JButton("Lisää opiskelija");
			add.addActionListener(studentsAddListener);
			studentsView.panel_buttons.add(add);

			studentsView.btn_clearId.addActionListener(studentsIdClearListener);

			studentsView.table_data.addMouseListener(studentsTableClickListener);
		}

		prepareContentView(studentsView);

		JTable table = studentsView.table_data;
		table.setModel(controller.getStudentTableModel(typedStudentName(), typedStudentId()));
		table.setAutoCreateRowSorter(true);

		prepareButtonAction(btn_save, saveListener);
	}





	/**
	 * Launch the application.
	 * 
	 * @param args Ei käytössä.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Controller controller = new Controller();
				try {
					Main frame = new Main(controller);
					frame.setVisible(true);

				}
				catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}

}
