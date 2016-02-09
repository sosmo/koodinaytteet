package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * Paneeli, jossa näytetään kokeiden tietoja.
 * 
 * @author Sampo Osmonen
 */
public class ExamsPanel extends JPanel {

	private final JPanel panel_filters = new JPanel();
	private final JPanel panel_data = new JPanel();
	private final JScrollPane scrollPane_data = new JScrollPane();
	private final JLabel lbl_date = new JLabel("Päivämäärä");

	protected final JButton btn_clearDate = new JButton("x");
	protected final JButton btn_add = new JButton("Uusi koe");
	protected final JComboBox comboBox_subject = new JComboBox();
	protected final JTextField textField_date = new JTextField();
	protected final JTable table_data = new JTable();
	protected final JPanel panel_buttons = new JPanel();

	/**
	 * Create the panel.
	 */
	public ExamsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		panel_filters.setBorder(new EmptyBorder(0, 0, 0, 5));
		panel_filters.setMinimumSize(new Dimension(150, 10));
		panel_filters.setPreferredSize(new Dimension(150, 10));

		add(panel_filters);
		GridBagLayout gbl_panel_filters = new GridBagLayout();
		gbl_panel_filters.columnWidths = new int[]{0, 0, 0};
		gbl_panel_filters.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel_filters.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_filters.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel_filters.setLayout(gbl_panel_filters);

		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0, 0, 5, 0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			panel_filters.add(comboBox_subject, gbc);
		}
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0, 0, 5, 5);
			gbc.gridx = 0;
			gbc.gridy = 1;
			panel_filters.add(lbl_date, gbc);
		}
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0, 0, 5, 5);
			gbc.gridx = 0;
			gbc.gridy = 2;
			panel_filters.add(textField_date, gbc);
		}
		textField_date.setColumns(10);
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, 5, 0);
			gbc.gridx = 1;
			gbc.gridy = 2;
			panel_filters.add(btn_clearDate, gbc);
		}
		btn_clearDate.setMargin(new Insets(2, 5, 0, 5));

		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 2;
			panel_filters.add(panel_buttons, gbc);
		}
		panel_buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));


		panel_buttons.add(btn_add);

		add(panel_data);
		panel_data.setLayout(new BorderLayout(0, 0));

		panel_data.add(scrollPane_data, BorderLayout.CENTER);

		table_data.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table_data.setIntercellSpacing(new Dimension(0, 0));
		table_data.setShowGrid(false);
		table_data.setFillsViewportHeight(true);

		scrollPane_data.setViewportView(table_data);
	}

}
