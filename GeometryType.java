package org.geotools.tutorial.txtreader;

/**
 * GeometryType: Enum representing different types of geometric shapes and their
 * respective fill opacity values.
 */
public enum GeometryType {
	POINT(1.0, 1.0), LINE(1.0, 1.0), POLYGON(0.5, 1.0);

	private final double fillOpacity;
	private final double strokeWidth;

	/**
	 * Constructor: Constructs a {@link GeometryType} object with the specified fill
	 * opacity and stroke width value.
	 *
	 * @param fillOpacity the opacity level associated with the geometry type.
	 * @param strokeWidth the width of the stroke associated with the geometry type.
	 */
	GeometryType(double fillOpacity, double strokeWidth) {
		this.fillOpacity = fillOpacity;
		this.strokeWidth = strokeWidth;
	}

	/**
	 * Retrieves the fill opacity of the geometry type.
	 *
	 * @return the fill opacity value.
	 */
	public double getFillOpacity() {
		return this.fillOpacity;
	}

	/**
	 * Retrieves the stroke width of the geometry type.
	 * 
	 * @return the stroke width value.
	 */
	public double getStrokeWidth() {
		return this.strokeWidth;
	}
}
