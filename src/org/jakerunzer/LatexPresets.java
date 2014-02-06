package org.jakerunzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

public class LatexPresets {

	/* Colour Presets */
	/* Name to be displayed, latex equation for colour */
	public static TreeMap<String, String> colourPreset = new TreeMap<String, String>();
	private static String colourFile = "src/res/colours.txt";
	private static BufferedReader colourReader;

	/* Equation Presets */
	/* Name to be displayed, latex equation */
	public static TreeMap<String, String> equationPreset = new TreeMap<String, String>();
	private static String equationFile = "src/res/equations.txt";
	private static BufferedReader equationReader;

	public static void initPresets() {
		// Read Colour Presets
		try {
			colourReader = new BufferedReader(new FileReader(colourFile));
			
			String colourString = colourReader.readLine();
			while (colourString != null) {
				String latexColour = "\\textcolor{" + colourString
						+ "}{TEXT}";
				colourPreset.put(colourString, latexColour);

				colourString = colourReader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Read Equation Presets
		try {
			equationReader = new BufferedReader(new FileReader(equationFile));
			
			String equationString = equationReader.readLine();
			while (equationString != null) {
				String[] equArray = equationString.split(":");
				String equ = equArray[0];
				String lat = equArray[1];
				equationPreset.put(equ, lat);

				equationString = equationReader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
