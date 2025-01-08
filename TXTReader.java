package org.geotools.tutorial.txtreader;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.style.Style;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JFileDataStoreChooser;

/**
 * Main Class: Generates and handles UI inputs.
 */
public class TXTReader {

	private static MapManager mapManager = new MapManager(new MapContent());
	private static JMapFrame mapFrame = new JMapFrame(mapManager.getMap());

	/**
	 * Initiates UI.
	 * 
	 * @param args Default args (not used).
	 */
	public static void main(String[] args) {

		// Initiate tool- and statusbar
		mapFrame.enableToolBar(true);
		mapFrame.enableStatusBar(true);

		// Add toolbar to map
		JToolBar toolbar = mapFrame.getToolBar();
		toolbar.addSeparator();

		// Add "add layer" and "remove layer" actions
		toolbar.add(new JButton(new AddLayerAction()));
		toolbar.add(new JButton(new ShowRemoveLayerAction()));

		// Display the map frame. When it is closed the application will exit
		mapFrame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		mapFrame.setVisible(true);

	}

	/**
	 * AddLayerAction class: Nested class inside of {@link TXTReader}. Handles the
	 * "add layer" button action.
	 */
	private static class AddLayerAction extends SafeAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor: provides name and description to button.
		 */
		public AddLayerAction() {
			super("Add Layer");
			putValue(Action.SHORT_DESCRIPTION, "Add Layer");
		}

		/**
		 * Creates a {@link javax.swing.SwingWorker} to perform the
		 * {@link TXTReader#addLayer()} method. Displays an error message if the action
		 * fails.
		 *
		 * @param e the event triggering this action.
		 */
		@Override
		public void action(ActionEvent e) {

			// Use SwingWorker to make addLayer() work in a background thread
			SwingWorker<Object, Object> worker = new SwingWorker<>() {

				@Override
				protected Object doInBackground() throws Exception {

					// call the AddLayer method
					addLayer();

					return null;
				}

				@Override
				protected void done() {
					try {
						get(); // Check if layer was added successfully => if not throw error message
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Failed to add layer: " + e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};

			worker.execute(); // Start swingworker

		}
	}

	/**
	 * Adds a new layer to the map by parsing a selected TXT file. Uses
	 * {@link TXTFileParser#getTXTFile(File)} for parsing selected file and
	 * {@link Styles#chooseStyle(javax.swing.JFrame)} for obtaining a selected style
	 *
	 * @throws Exception if the file selection, validation or parsing fails.
	 */
	private static void addLayer() throws Exception {

		// Open file selector
		File sourceFile = JFileDataStoreChooser.showOpenFile("txt", null);

		if (sourceFile != null) { // Check if a file is selected

			// Get featuresource and style
			SimpleFeatureSource featureSource = TXTFileParser.getTXTFile(sourceFile);
			Style style = Styles.chooseStyle(mapFrame);

			// Add new layer
			mapManager.addLayer(featureSource, style);
		}
	}

	/**
	 * ShowRemoveLayerAction class: Nested class inside of {@link TXTReader}.
	 * Handles the "Remove Layer menu" button action.
	 */
	private static class ShowRemoveLayerAction extends SafeAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor: provides name and description to button
		 */
		public ShowRemoveLayerAction() {
			super("Remove Layer");
			putValue(Action.SHORT_DESCRIPTION, "Remove Layer");
		}

		/**
		 * Displays the remove layer menu by calling the
		 * {@link TXTReader#showRemoveLayerMenu()} method.
		 *
		 * @param e the event triggering this action.
		 * @throws Exception if an error occurs while showing the menu.
		 */
		@Override
		public void action(ActionEvent e) throws Exception {
			showRemoveLayerMenu();
		}
	}

	/**
	 * Displays a dialog allowing the user to remove layers from the map.
	 *
	 * @throws Exception if an error occurs while setting up the dialog or removing
	 *                   layers.
	 */
	private static void showRemoveLayerMenu() throws Exception {

		if (mapManager.getLayerCount() != 0) { // Check if there are layers present

			// Initiate dialog and panel
			JDialog dialog = DialogHelper.createDialog(mapFrame, "Remove Layer");
			JPanel panel = DialogHelper.createPanel(mapManager.getLayerCount(), Constants.REMOVEMENU_COL_COUNT);

			// Create local inner class for the "remove layer" action
			class RemoveLayerAction extends SafeAction {

				private static final long serialVersionUID = 1L;
				private int id;

				public RemoveLayerAction(int id) {
					super("Remove Layer " + id);
					this.id = id;
					putValue(Action.SHORT_DESCRIPTION, "Remove Layer " + id);
				}

				@Override
				public void action(ActionEvent e) throws Exception {
					mapManager.removeLayer(id);
					dialog.dispose();
					showRemoveLayerMenu(); // Recall function to generate updated list of layers
				}
			}
			
			// Generate remove layer buttons
			for (int i = 0; i < mapManager.getLayerCount(); i++) {
				JButton button = new JButton(new RemoveLayerAction(i));
				panel.add(button);
			}

			// Finish dialog setup
			DialogHelper.setupDialog(dialog, panel, mapFrame);

		} else {
			
			// Show message if there are no layers present
			JOptionPane.showMessageDialog(null, "There are no layers to remove", "Remove Layer",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
