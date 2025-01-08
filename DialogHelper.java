package org.geotools.tutorial.txtreader;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.geotools.swing.action.SafeAction;

/**
 * DialogHelper: Utility class for creating and setting up dialogs and panels.
 */
public class DialogHelper {

	/**
	 * Creates a modal JDialog with a specified title.
	 *
	 * @param mapFrame the parent {@link javax.swing.JFrame} for the dialog.
	 * @param title    the title of the dialog.
	 * @return a new {@link javax.swing.JDialog} instance.
	 */
	public static JDialog createDialog(JFrame mapFrame, String title) {

		JDialog dialog = new JDialog(mapFrame, title, true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}

	/**
	 * Creates a {@link javax.swing.JPanel} with a {@link java.awt.GridLayout} the
	 * specified dimensions and padded borders.
	 *
	 * @param rows the number of rows in the grid layout.
	 * @param cols the number of columns in the grid layout.
	 * @return a new {@link javax.swing.JPanel} with the specified amount of rows
	 *         and columns.
	 */
	public static JPanel createPanel(int rows, int cols) {

		JPanel panel = new JPanel(new GridLayout(rows, cols));
		panel.setBorder(BorderFactory.createEmptyBorder(Constants.DIALOG_PADDING, Constants.DIALOG_PADDING,
				Constants.DIALOG_PADDING, Constants.DIALOG_PADDING));

		return panel;
	}

	/**
	 * Adds {@link javax.swing.JPanel} to {@link javax.swing.JDialog}, sets the
	 * positioning and displays the {@link javax.swing.JDialog}.
	 *
	 * @param dialog   the {@link javax.swing.JDialog} to be configured.
	 * @param panel    the {@link javax.swing.JPanel} to be added to the dialog.
	 * @param mapFrame the parent {@link javax.swing.JFrame}.
	 */
	public static void setupDialog(JDialog dialog, JPanel panel, JFrame mapFrame) {
		
		// Add panel to dialog
		dialog.add(panel);
		
		// Position dialog frame
		dialog.pack();
		dialog.setLocationRelativeTo(mapFrame);
		
		// Set dialog visible
		dialog.setVisible(true);
	}
	
	/**
	 * Creates a JButton that opens a color chooser dialog when clicked.
	 *
	 * @param dialog the parent dialog for the color chooser.
	 * @param defaultColor the initial color for the button.
	 * @param label the label for the button.
	 * @return a JButton configured to choose a color.
	 */
	public static JButton createColorChooserButton(JDialog dialog, Color defaultColor, String label) {

		// Create local inner class to handle the "choose color" action
		class ChooseColorAction extends SafeAction {

			private static final long serialVersionUID = 1L;
			private Color selectedColor;
			private String type;
			
			public ChooseColorAction(Color defaultColor, String type) {
				super("Choose " + type + " color");
				putValue(Action.SHORT_DESCRIPTION, "Choose " + type + " color");

				this.selectedColor = defaultColor;
				this.type = type;
			}

			@Override
			public void action(ActionEvent e) throws Exception {
				
				// Show color chooser dialog
				Color color = JColorChooser.showDialog(dialog, "Choose " + this.type + " Color", selectedColor);
				if (color != null) {
					
					// Set selected color
					selectedColor = color;
					
					// Change button color to selected color
					JButton sourceButton = (JButton) e.getSource();
					sourceButton.setBackground(selectedColor);
				}
			}
		}

		JButton button = new JButton(new ChooseColorAction(defaultColor, label));
		button.setBackground(defaultColor);

		return button;
	}
}
