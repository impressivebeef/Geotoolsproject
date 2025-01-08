package org.geotools.tutorial.txtreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.collection.CollectionFeatureSource;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * TXTFileParser: Parses TXT files containing Well-Known Text (WKT) geometries
 * and converts them into a SimpleFeatureSource for mapping.
 */
public class TXTFileParser {

	/**
	 * Parses a TXT file containing WKT geometries and converts it into a
	 * {@link org.geotools.api.data.SimpleFeatureSource}
	 *
	 * @param sourceFile the file containing WKT geometries.
	 * @return a {@link org.geotools.api.data.SimpleFeatureSource} containing the
	 *         geometries from the file.
	 * @throws Exception if an error occurs during file reading or geometry parsing.
	 */
	public static SimpleFeatureSource getTXTFile(File sourceFile) throws Exception {

		// Validate whether file is TXT => else throw exception
		validateFile(sourceFile);

		// Create Schema
		SimpleFeatureType featureType = createFeatureType();

		// Read and Parse file
		List<SimpleFeature> features = readAndParseFile(sourceFile, featureType);

		// Create a featureCollection to extract featureSource
		SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
		SimpleFeatureSource featureSource = new CollectionFeatureSource(collection);

		return featureSource;
	}

	/**
	 * Validates input file.
	 * 
	 * @param sourceFile the file containing WKT geometries.
	 * @throws Exception if file is not an TXT file.
	 */
	public static void validateFile(File sourceFile) throws Exception {

		if (!FilenameUtils.getExtension(sourceFile.toString()).equals("txt")) {
			throw new Exception("File selected is not an txt file");
		}

	}

	/**
	 * Creates the feature type schema for the geometries.
	 *
	 * @return a {@link org.geotools.api.feature.simple.SimpleFeatureType} that
	 *         defines the schema for the features.
	 */
	private static SimpleFeatureType createFeatureType() {
		
		// Initiate featuretypebuilder
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		
		// Add name, CRS and Geometry object to builder
		builder.setName("WKT_to_geom");
		builder.setCRS(DefaultGeographicCRS.WGS84);
		builder.add("the_geom", Geometry.class);
		
		// Build featuretype
		SimpleFeatureType featureType = builder.buildFeatureType();

		return featureType;
	}

	/**
	 * Reads and parses TXT files containing WKT geometries.
	 * 
	 * @param sourceFile  the file containing WKT geometries.
	 * @param featureType a
	 *                    {@link org.geotools.api.feature.simple.SimpleFeatureType}
	 *                    that defines the schema for the features.
	 * @return A {@link java.util.List} containing the resulting
	 *         {@link org.geotools.api.feature.simple.SimpleFeature} from geometry
	 *         parsing
	 * @throws Exception if an error occurs during file reading or geometry parsing.
	 */
	private static List<SimpleFeature> readAndParseFile(File sourceFile, SimpleFeatureType featureType)
			throws Exception {
		
		// List to store geometry features
		List<SimpleFeature> features = new ArrayList<>(); 
		
		// Initiate WTKReader and SimpleFeatureBuilder
		WKTReader reader = new WKTReader(); 
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

		// Read txt file and add geometry features to list
		try (Scanner s = new Scanner(sourceFile)) {
			
			 // Array for storing invalid (non-WKT) lines
			List<String> invalidLines = new ArrayList<>();
			
			while (s.hasNext()) {
				String line = s.nextLine();

				// Try catch reading wkt string
				//  => If the string cant be converted to Geometry object store the string
				// in invalidLines array
				try {
					features.add(parseLine(reader, featureBuilder, line));
				} catch (Exception e) {
					invalidLines.add(line);
				}

			}

			s.close();

			if (!invalidLines.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Number of invalid lines: " + invalidLines.size(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (IOException e) {
			throw new IOException("Error reading file: " + e.getMessage(), e);
		}

		return features;
	}

	/**
	 * Parses a single WKT input line to
	 * {@link org.geotools.api.feature.simple.SimpleFeature}.
	 * 
	 * @param reader         a {@link org.locationtech.jts.io.WKTReader} object.
	 * @param featureBuilder a
	 *                       {@link org.geotools.feature.simple.SimpleFeatureBuilder}
	 *                       object.
	 * @param line           input WKT String.
	 * @return a {@link org.geotools.api.feature.simple.SimpleFeature} resulting
	 *         from the input WKT String
	 * @throws ParseException if input WKT String is not valid.
	 */
	private static SimpleFeature parseLine(WKTReader reader, SimpleFeatureBuilder featureBuilder, String line)
			throws ParseException {
		
		// Parse wkt String to Geometry object
		Geometry geometry = reader.read(line);
		
		// Add Geometry object to featurebuilder
		featureBuilder.add(geometry); 
		
		// Build feature
		SimpleFeature feature = featureBuilder.buildFeature(null); 

		return feature;
	}
}
