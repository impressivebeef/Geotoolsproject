package org.geotools.tutorial.txtreader;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Fill;
import org.geotools.api.style.Graphic;
import org.geotools.api.style.LineSymbolizer;
import org.geotools.api.style.Mark;
import org.geotools.api.style.PointSymbolizer;
import org.geotools.api.style.PolygonSymbolizer;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Stroke;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.api.style.Symbolizer;
import org.geotools.factory.CommonFactoryFinder;

/**
 * Styles: Provides methods to create and choose styles for mapping.
 */
public class Styles {

	/**
	 * Opens a dialog to let the user choose stroke and fill colors for a style.
	 *
	 * @param mapFrame the parent {@link javax.swing.JFrame} for the dialog.
	 * @return a {@link org.geotools.api.style.Style} object based on the selected
	 *         colors.
	 */
	public static Style chooseStyle(JFrame mapFrame) {

		// Initialize dialog and panel
		JDialog dialog = DialogHelper.createDialog(mapFrame, "Choose Style");
		JPanel panel = DialogHelper.createPanel(Constants.STYLEMENU_ROW_COUNT, Constants.STYLEMENU_COL_COUNT);

		// Initialize Stroke and Fill buttons
		JButton strokeButton = DialogHelper.createColorChooserButton(dialog, Color.BLACK, "Stroke");
		JButton fillButton = DialogHelper.createColorChooserButton(dialog, Color.GRAY, "fill");

		// Initialize confirmation button
		JButton confButton = new JButton("Confirm");

		// Create an empty array for selected style
		// => allows for extracting the Style object out of the actionListener
		// Not the most clean solution
		final Style[] selectedStyle = new Style[1];

		// Add actionlistener to conf button => retrieves colors from buttons
		confButton.addActionListener(e -> {
			selectedStyle[0] = createStyle(strokeButton.getBackground(), fillButton.getBackground());
			dialog.dispose();
		});

		// Add everything to panel
		// TODO find a cleaner method for this
		panel.add(new JLabel("Stroke Color:"));
		panel.add(strokeButton);
		panel.add(new JLabel("Fill Color:"));
		panel.add(fillButton);
		panel.add(new JLabel());
		panel.add(confButton);

		// Finish dialog setup
		DialogHelper.setupDialog(dialog, panel, mapFrame);

		return selectedStyle[0];
	}

	/**
	 * Creates a {@link org.geotools.api.style.Style} object using the specified
	 * stroke and fill colors.
	 *
	 * @param strokeColor the stroke color.
	 * @param fillColor   the fill color.
	 * @return a {@link org.geotools.api.style.Style} object representing the
	 *         specified colors.
	 */
	public static Style createStyle(Color strokeColor, Color fillColor) {

		// Initiate style factory
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

		// create rules List
		List<Rule> rules = createRules(styleFactory, strokeColor, fillColor);

		// create feature type style and add rules
		FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
		featureTypeStyle.rules().addAll(rules);

		// Create style
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(featureTypeStyle);

		return style;
	}

	/**
	 * Creates a {@link java.util.List} of {@link org.geotools.api.style.Rule}
	 * objects.
	 * 
	 * @param styleFactory the {@link org.geotools.api.style.StyleFactory} used to
	 *                     create the {@link org.geotools.api.style.Rule} list.
	 * @param strokeColor  the stroke color.
	 * @param fillColor    the fill color.
	 * @return a {@link java.util.List} of {@link org.geotools.api.style.Rule}
	 *         objects.
	 */
	private static List<Rule> createRules(StyleFactory styleFactory, Color strokeColor, Color fillColor) {

		// Initiate filterfactory
		FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

		// Create list to store Rules
		List<Rule> rules = new ArrayList<>();

		// loop through each Geometry type
		for (GeometryType type : GeometryType.values()) {

			// Create stroke for type
			Stroke stroke = styleFactory.createStroke(filterFactory.literal(strokeColor),
					filterFactory.literal(type.getStrokeWidth()));

			// Create fill for type
			Fill fill = styleFactory.createFill(filterFactory.literal(fillColor),
					filterFactory.literal(type.getFillOpacity()));

			// Create symbolizer and Rule for type
			Symbolizer symbolizer = createSymbolizer(styleFactory, filterFactory, type, stroke, fill);
			Rule rule = styleFactory.createRule();
			rule.symbolizers().add(symbolizer);
			rules.add(rule);
		}

		return rules;
	}

	/**
	 * Creates a {@link org.geotools.api.style.Symbolizer} for the specified
	 * geometry type.
	 *
	 * @param styleFactory  the {@link org.geotools.api.style.StyleFactory} used to
	 *                      create the symbolizer.
	 * @param filterFactory the {@link org.geotools.api.filter.FilterFactory} used
	 *                      to define literals and expressions.
	 * @param type          the type of geometry for which an symbolizer is created.
	 *                      Accepts "point", "line" or "polygon".
	 * @param stroke        the {@link org.geotools.api.style.Stroke} to use for the
	 *                      symbolizer.
	 * @param fill          the {@link org.geotools.api.style.Fill} to use for the
	 *                      symbolizer.
	 * @return {@link org.geotools.api.style.Symbolizer} for the specified geometry
	 *         type.
	 * @throws IllegalArgumentException if the provided {@code type} is not one of
	 *                                  "point", "line", or "polygon".
	 */
	private static Symbolizer createSymbolizer(StyleFactory styleFactory, FilterFactory filterFactory,
			GeometryType type, Stroke stroke, Fill fill) {

		switch (type) {

		case POINT:
			// Create marker for point
			Mark mark = styleFactory.getDefaultMark();
			mark.setFill(fill);
			mark.setStroke(stroke);

			// Create graphic
			Graphic graphic = styleFactory.createGraphic(null, new Mark[] { mark }, null,
					filterFactory.literal(type.getFillOpacity()), filterFactory.literal(Constants.MARKER_SIZE),
					filterFactory.literal(Constants.MARKER_ROTATION));

			PointSymbolizer pointSymbolizer = styleFactory.createPointSymbolizer(graphic, null);

			return pointSymbolizer;

		case LINE:
			LineSymbolizer lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);

			return lineSymbolizer;

		case POLYGON:
			PolygonSymbolizer polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);

			return polygonSymbolizer;

		default:
			throw new IllegalArgumentException("invalid type: " + type);
		}
	}

}
