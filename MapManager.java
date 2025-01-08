package org.geotools.tutorial.txtreader;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.style.Style;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;

/**
 * MapManager: Utility class for managing map content and layers.
 */
public class MapManager {

	private MapContent map;

	/**
	 * Constructor: Constructs a new {@link MapManager} with the specified map
	 * content.
	 *
	 * @param map the {@link org.geotools.map.MapContent} object to be managed.
	 */
	MapManager(MapContent map) {
		this.map = map;
	}

	/**
	 * Retrieves the {@link org.geotools.map.MapContent} managed by this
	 * {@link MapManager}.
	 *
	 * @return the {@link org.geotools.map.MapContent} object that is being managed.
	 */
	public MapContent getMap() {
		return this.map;
	}

	/**
	 * Retrieves the number of layers in the managed
	 * {@link org.geotools.map.MapContent}.
	 *
	 * @return the total number of {@link org.geotools.map.FeatureLayer}'s in the map.
	 */
	public int getLayerCount() {
		return map.layers().size();
	}

	/**
	 * Adds a new {@link org.geotools.map.FeatureLayer} to the managed
	 * {@link org.geotools.map.MapContent} using the specified
	 * {@link org.geotools.api.data.SimpleFeatureSource} and {@link org.geotools.api.style.Style}.
	 *
	 * @param featureSource the {@link org.geotools.api.data.SimpleFeatureSource}
	 *                      providing the data for the new layer.
	 * @param style         the {@link org.geotools.api.style.Style} to be applied to
	 *                      the new layer.
	 * @throws Exception if featureSource or style are invalid
	 */
	public void addLayer(SimpleFeatureSource featureSource, Style style) throws Exception {
		
		try {
			this.map.layers().add(new FeatureLayer(featureSource, style));
		} catch (Exception e) {
			throw new Exception("Could not add layer; featureSource/style invalid");
		}
		
	}

	/**
	 * Removes a {@link org.geotools.map.FeatureLayer} from the managed
	 * {@link org.geotools.map.MapContent} by its id.
	 *
	 * @param id the id of the {@link org.geotools.map.FeatureLayer} to be removed.
	 * @throws IllegalArgumentException if id does not exist in map.
	 */
	public void removeLayer(int id) throws IllegalArgumentException {
		
		try {
			this.map.layers().remove(id);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid layer ID: " + id);
		}
	}
}
