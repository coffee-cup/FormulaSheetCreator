package org.jakerunzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.NoSuchElementException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class SheetCreator {

	private JFrame frmFormulaSheetCreator;
	private final JSplitPane splitPane = new JSplitPane();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnFile = new JMenu("File");
	private final JMenuItem mntmQuit = new JMenuItem("Quit");
	private final JPanel pnlFormulaPage = new JPanel();
	private final JPanel pnlFormulaText = new JPanel();
	private final JLabel lblLatex = new JLabel("");
	private final JTextArea formText = new JTextArea();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel pnlEditorOptions = new JPanel();
	private final JSpinner spinFormula = new JSpinner();
	private final JLabel lblNewLabel = new JLabel("Formula Size");
	private final JMenu mnSaveImage = new JMenu("Save Image");
	private final JMenuItem mntmSvg = new JMenuItem("SVG");
	private final JMenuItem mntmPng = new JMenuItem("PNG");
	private final JMenuItem mntmOpenProject = new JMenuItem("Open Project");
	private final JMenuItem mntmNewProject = new JMenuItem("New Project");
	private final JMenu mnHelp = new JMenu("Help");
	private final JMenuItem mntmAbout = new JMenuItem("About");
	private final JMenuItem mntmWhatIsLatex = new JMenuItem("What is LaTeX?");
	private final JMenuItem mntmLatexHelp = new JMenuItem("LaTeX Help");
	private final JMenu mnSettings = new JMenu("Settings");
	private final JMenuItem mntmPreferences = new JMenuItem("Preferences");
	private final JMenuItem mntmLatexPresets = new JMenuItem("LaTeX Presets");
	private final JPanel pnlFormulaFontOption = new JPanel();
	private final JPanel pnlEditorFontOption = new JPanel();
	private final JLabel lblEditorFontSize = new JLabel("Editor Font");
	private final JSpinner spinEditor = new JSpinner();
	private final JPanel pnlFontOptions = new JPanel();
	private final JPanel pnlPresetOptions = new JPanel();
	private final JComboBox presetEquations = new JComboBox();
	private final JComboBox presetColours = new JComboBox();
	private final JMenuItem mntmPdf = new JMenuItem("PDF");
	private final JMenuItem mntmSaveproject = new JMenuItem("Save Project");

	/*
	 * These variables manage the undoing
	 */
	private UndoManager undoManager = new UndoManager();
	private InputMap inputMap;
	private ActionMap actionMap;

	/*
	 * These variables manage a separte popout jframe to control the settings
	 */
	private JFrame settingsFrame;
	private JCheckBox chckNewline;
	private JButton btnApply;
	private JButton btnCancel;

	/*
	 * Sets if new line in the text area ('\n'), should be converted to a new
	 * line in latex formulas ('\\')
	 */
	private boolean newLine = true;
	private final JMenu mnEdit = new JMenu("Edit");
	private final JMenuItem mntmUndo = new JMenuItem("Undo");
	private final JMenuItem mntmRedo = new JMenuItem("Redo");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SheetCreator window = new SheetCreator();
					window.frmFormulaSheetCreator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SheetCreator() {
		initMainWindow();
		initSettingsWindow();
	}

	/**
	 * Create and setup the Settings Window
	 */
	private void initSettingsWindow() {
		settingsFrame = new JFrame();
		settingsFrame.setTitle("Settings");
		settingsFrame.setBounds(450, 150, 500, 350);
		settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		settingsFrame.setVisible(false);

		JPanel pnlButtons = new JPanel();
		settingsFrame.getContentPane().add(pnlButtons, BorderLayout.SOUTH);

		btnApply = new JButton("Apply");
		pnlButtons.add(btnApply);

		btnCancel = new JButton("Cancel");
		pnlButtons.add(btnCancel);

		JPanel pnlGeneral = new JPanel();
		settingsFrame.getContentPane().add(pnlGeneral, BorderLayout.CENTER);
		pnlGeneral.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		chckNewline = new JCheckBox("Convert \"\\n\" to \"\\\\\"");
		pnlGeneral.add(chckNewline);
		chckNewline.setSelected(true);

		settingsFrameEvents();
	}

	/**
	 * Because the settings window GUI is created in another class, the events
	 * and actions specific to the objects and components are created here.
	 */
	private void settingsFrameEvents() {
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingsFrame.setVisible(false);
			}
		});

		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newLine = chckNewline.isSelected();
				updateLatex();
			}
		});

		chckNewline
				.setToolTipText("If selected new lines in the editor will be new lines in the formula. If not selected, new lines can be created by typing '\\\\' in the editor.");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initMainWindow() {
		frmFormulaSheetCreator = new JFrame();
		frmFormulaSheetCreator.setTitle("Formula Sheet Creator");
		frmFormulaSheetCreator.setBounds(100, 100, 1200, 700);
		frmFormulaSheetCreator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		splitPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		splitPane.setDividerSize(10);
		splitPane.setContinuousLayout(true);

		frmFormulaSheetCreator.getContentPane().add(splitPane,
				BorderLayout.CENTER);
		pnlFormulaPage.setBorder(new EmptyBorder(10, 10, 10, 10));
		pnlFormulaPage.setBackground(Color.WHITE);

		splitPane.setLeftComponent(pnlFormulaPage);
		pnlFormulaPage
				.setLayout(new BoxLayout(pnlFormulaPage, BoxLayout.Y_AXIS));
		pnlFormulaPage.add(lblLatex);
		lblLatex.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlFormulaText.setBorder(new EmptyBorder(5, 5, 5, 5));

		splitPane.setRightComponent(pnlFormulaText);
		pnlFormulaText.setLayout(new BorderLayout(0, 0));
		scrollPane.setViewportBorder(new TitledBorder(new LineBorder(new Color(
				0, 0, 0)), "LaTeX Formula Editor", TitledBorder.CENTER,
				TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0)));

		pnlFormulaText.add(scrollPane);

		formText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateLatex();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateLatex();
			}
		});
		formText.setTabSize(2);
		formText.setText("\\JLaTeXMath \\\\");
		formText.getDocument().addUndoableEditListener(
				new UndoableEditListener() {
					public void undoableEditHappened(UndoableEditEvent e) {
						undoManager.addEdit(e.getEdit());
					}
				});

		scrollPane.setViewportView(formText);

		pnlFormulaText.add(pnlEditorOptions, BorderLayout.SOUTH);
		pnlEditorOptions.setLayout(new BorderLayout(0, 0));

		pnlEditorOptions.add(pnlFontOptions, BorderLayout.WEST);
		pnlFontOptions.setLayout(new BorderLayout(0, 0));
		spinEditor.setModel(new SpinnerNumberModel(13, 2, 40, 1));
		pnlFontOptions.add(pnlEditorFontOption, BorderLayout.NORTH);
		lblEditorFontSize.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlEditorFontOption.add(lblEditorFontSize);
		pnlEditorFontOption.add(spinEditor);
		pnlFontOptions.add(pnlFormulaFontOption, BorderLayout.SOUTH);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlFormulaFontOption.add(lblNewLabel);
		pnlFormulaFontOption.add(spinFormula);
		spinFormula.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updateLatex();
			}
		});

		spinEditor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Font currentFont = formText.getFont();
				String fontName = currentFont.getFontName();
				int size = (Integer) spinEditor.getModel().getValue();
				Font newFont = new Font(fontName, Font.PLAIN, size);
				formText.setFont(newFont);
			}
		});

		spinFormula.setModel(new SpinnerNumberModel(20, 2, 100, 1));

		pnlEditorOptions.add(pnlPresetOptions, BorderLayout.CENTER);
		pnlPresetOptions.setLayout(new BoxLayout(pnlPresetOptions,
				BoxLayout.Y_AXIS));
		((JLabel) presetEquations.getRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);
		presetEquations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String selected = presetEquations.getSelectedItem().toString();
				String latex = LatexPresets.equationPreset.get(selected);
				if (latex != null) {
					int pos = formText.getCaretPosition();
					formText.insert(latex, pos);
				}
				presetEquations.setSelectedIndex(0);
			}
		});

		// Set the text in the comboboxs in the center
		pnlPresetOptions.add(presetEquations);
		((JLabel) presetColours.getRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);

		presetColours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = presetColours.getSelectedItem().toString();
				String latex = LatexPresets.colourPreset.get(selected);
				if (latex != null) {
					int pos = formText.getCaretPosition();
					formText.insert(latex, pos);
				}
				presetColours.setSelectedIndex(0);
			}
		});

		pnlPresetOptions.add(presetColours);

		splitPane.setDividerLocation(700);

		frmFormulaSheetCreator.setJMenuBar(menuBar);

		// Quit the program if quit button clicked
		menuBar.add(mnFile);
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmFormulaSheetCreator.dispose();
				return;
			}
		});

		mnFile.add(mntmNewProject);

		mnFile.add(mntmOpenProject);

		mnFile.add(mntmSaveproject);

		// Saving a SVG File
		mnFile.add(mnSaveImage);
		mntmSvg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveImage("svg");
			}
		});

		// Saving a PNG File
		mnSaveImage.add(mntmSvg);
		mntmPng.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveImage("png");
			}
		});

		// Saving a PDF File
		mnSaveImage.add(mntmPng);
		mntmPdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveImage("pdf");
			}
		});

		mnSaveImage.add(mntmPdf);

		mnFile.add(mntmQuit);

		menuBar.add(mnEdit);
		mntmUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				undoText();
			}
		});

		mntmRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				redoText();
			}
		});

		mnEdit.add(mntmUndo);

		mnEdit.add(mntmRedo);

		menuBar.add(mnSettings);
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsFrame.setVisible(true);
			}
		});

		mnSettings.add(mntmPreferences);

		mnSettings.add(mntmLatexPresets);

		menuBar.add(mnHelp);

		mnHelp.add(mntmAbout);

		mnHelp.add(mntmLatexHelp);

		mnHelp.add(mntmWhatIsLatex);

		initMappings();
		initPresets();
		updateLatex();
	}

	private void saveImage(String type) {
		String latex = formText.getText();
		if (newLine) {
			latex = latex.replace("\n", "\\\\");
		}
		TeXFormula formula = new TeXFormula(latex);
		SaveLatexIcon.saveIcon(formula, type,
				frmFormulaSheetCreator.getContentPane());
	}

	private void initMappings() {
		inputMap = formText.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = formText.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

		actionMap.put("Undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canUndo()) {
						undoText();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});
		actionMap.put("Redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canRedo()) {
						redoText();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});
	}

	private void undoText() {
		undoManager.undo();
	}

	private void redoText() {
		undoManager.redo();
	}

	/**
	 * Loads the presets for equations and colours and puts them into their
	 * comboboxs
	 */
	private void initPresets() {
		// Call to another static class to open preset files
		LatexPresets.initPresets();

		presetEquations.addItem(new String("Equation Presets"));
		for (String equ : LatexPresets.equationPreset.keySet()) {
			presetEquations.addItem(equ);
		}

		presetColours.addItem(new String("Colour Presets"));
		for (String col : LatexPresets.colourPreset.keySet()) {
			presetColours.addItem(col);
		}
	}

	/**
	 * Creates an icon from a given LaTeX string
	 * 
	 * @param latex
	 *            LaTeX string to be converted into an icon
	 * @param size
	 *            size for the icon to be (20 is normal)
	 * @return TeXIcon icon created from latex string or null if there was an
	 *         error parseing latex
	 * 
	 */
	private TeXIcon getTexIcon(String latex, int size) {
		TeXFormula formula = new TeXFormula();
		try {
			formula.setLaTeX(latex);
		} catch (ParseException e) {
			return null;
		}
		TeXIcon icon = null;
		try {
			icon = formula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size).build();
		} catch (NoSuchElementException e) {
			return null;
		}

		return icon;
	}

	/**
	 * updates the formula icon in the formula viewer from a LaTeX string.
	 */
	private void updateLatex() {
		String latex = formText.getText();
		if (newLine) {
			latex = latex.replace("\n", "\\\\");
		}
		TeXIcon icon = getTexIcon(latex,
				(Integer) (spinFormula.getModel().getValue()));
		lblLatex.setIcon(icon);
	}
}
