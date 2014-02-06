package org.jakerunzer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.scilab.forge.jlatexmath.TeXFormula;

public class SaveLatexIcon {
	public static void saveIcon(TeXFormula tf, String filetype, Component parent) {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		int val = chooser.showSaveDialog(parent);
		if (val == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String filename = file.getAbsolutePath() + "." + filetype;
			file = new File(filename);
			try {
				if (filetype.equals("png")) {
					LaTeXGenerator.generate(tf, file);

				} else if (filetype.equals("svg")) {
					Convert.toSVG(tf, file.getAbsolutePath(), true);

				} else if (filetype.equals("pdf")) {
					String temp = "/tmp/SVGtmp";
					Convert.toSVG(tf, temp, true);
					Convert.SVGTo(temp, file.getAbsolutePath(), Convert.PDF);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
