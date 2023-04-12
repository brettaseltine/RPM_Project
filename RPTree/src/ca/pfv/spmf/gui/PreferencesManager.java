package ca.pfv.spmf.gui;

import java.nio.charset.Charset;
/*
 * Copyright (c) 2008-2013 Philippe Fournier-Viger
 *
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.prefs.Preferences;
/*
 * Copyright (c) 2008-2022 Philippe Fournier-Viger
 *
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This class is used to manage registry keys for storing user preferences for
 * the SPMF GUI.
 * 
 * @see MainWindow
 * @author Philippe Fournier-Viger
 */
public class PreferencesManager {

	// We use two registry key to store
	// the paths of the last folders used by the user
	// for input and output files.
	public static final String REGKEY_SPMF_INPUT_FILE = "ca.pfv.spmf.gui.input";
	public static final String REGKEY_SPMF_OUTPUT_FILE = "ca.pfv.spmf.gui.output";
//    public static final String REGKEY_SPMF_PLUGIN_FOLDER_PATH = "ca.pfv.spmf.plugin.folderpath";
	public static final String REGKEY_SPMF_PLUGIN_REPOSITORY_URL = "ca.pfv.spmf.plugin.repositoryurl";
	public static final String REGKEY_SPMF_PREFERED_CHARSET = "ca.pfv.spmf.gui.charset";
	public static final String REGKEY_SPMF_RUN_EXTERNAL = "ca.pfv.spmf.gui.runexternal";
//    public static final String REGKEY_SPMF_MAX_SECONDS = "ca.pfv.spmf.gui.maxseconds";;
	public static final String REGKEY_SPMF_JAR_FILE_PATH = "ca.pfv.spmf.jar_file_path";
	public static final String REGKEY_SPMF_EXPERIMENT_DIRECTORY_PATH = "ca.pfv.spmf.experiment_directory_path";
	public static final String REGKEY_LAST_MEMORY_USAGE = "ca.pfv.spmf.experiments.lastmemory";

	public static final String REGKEY_NIGHT_MODE = "ca.pfv.spmf.experiments.nightmode";
	public static final String REGKEY_TEXTEDITOR_FONTSIZE = "ca.pfv.spmf.gui.texteditor.fontsize";
	public static final String REGKEY_TEXT_EDITOR_LINE_WRAP = "ca.pfv.spmf.gui.texteditor.linewrap";
	public static final String REGKEY_TEXT_EDITOR_WORD_WRAP = "ca.pfv.spmf.gui.texteditor.wordwrap";

	public static final String REGKEY_TEXT_EDITOR_FONT_FAMILLY = "ca.pfv.spmf.gui.texteditor.fontfamilly";

	public static final String REGKEY_TEXTEDITOR_WIDTH = "ca.pfv.spmf.gui.texteditor.width";
	public static final String REGKEY_TEXTEDITOR_HEIGHT = "ca.pfv.spmf.gui.texteditor.height";
	public static final String REGKEY_TEXTEDITORAREA_WIDTH = "ca.pfv.spmf.gui.texteditor.areawidth";
	public static final String REGKEY_TEXTEDITORAREA_HEIGHT = "ca.pfv.spmf.gui.texteditor.areaheight";

	public static final String REGKEY_TEXTEDITOR_X = "ca.pfv.spmf.gui.texteditor.x";
	public static final String REGKEY_TEXTEDITOR_Y = "ca.pfv.spmf.gui.texteditor.y";
	

	public static final String REGKEY_SPMF_USE_SYSTEM_TEXT_EDITOR = "ca.pfv.spmf.system_text_editor";

	// Implemented as a singleton
	private static PreferencesManager instance;

	/**
	 * Default constructor
	 */
	private PreferencesManager() {

	}

	/**
	 * Get the only instance of this class (a singleton)
	 * 
	 * @return the instance
	 */
	public static PreferencesManager getInstance() {
		if (instance == null) {
			instance = new PreferencesManager();
		}
		return instance;
	}

	/**
	 * Get the input file path stored in the registry
	 * 
	 * @return a path as a string
	 */
	public String getInputFilePath() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		return p.get(REGKEY_SPMF_INPUT_FILE, null);
	}

	/**
	 * Store an input file path in the registry
	 * 
	 * @param filepath a path as a string
	 */
	public void setInputFilePath(String filepath) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_INPUT_FILE, filepath);
	}

	/**
	 * Get the output file path stored in the registry
	 * 
	 * @return a path as a string
	 */
	public String getOutputFilePath() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		return p.get(REGKEY_SPMF_OUTPUT_FILE, null);
	}

	/**
	 * Store an output file path in the registry
	 * 
	 * @param filepath a path as a string
	 */
	public void setOutputFilePath(String filepath) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_OUTPUT_FILE, filepath);
	}

	/**
	 * Get the experiment directory path stored in the registry
	 * 
	 * @return a path as a string
	 */
	public String getExperimentDirectoryPath() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		return p.get(REGKEY_SPMF_EXPERIMENT_DIRECTORY_PATH, null);
	}

	/**
	 * Store an output file path in the registry
	 * 
	 * @param filepath a path as a string
	 */
	public void setExperimentDirectoryPath(String filepath) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_EXPERIMENT_DIRECTORY_PATH, filepath);
	}
	// REGKEY_SPMF_EXPERIMENT_DIRECTORY_PATH

	/**
	 * Store the path to spmf.jar in the registry
	 * 
	 * @param path the path
	 */
	public void setSPMFJarFilePath(String path) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_JAR_FILE_PATH, path);
	}

	/**
	 * Get the path to the spmf.jar file stored in a registry key
	 * 
	 * @return the path as a string
	 */
	public String getSPMFJarFilePath() {
		Preferences p = Preferences.userRoot();
		return p.get(REGKEY_SPMF_JAR_FILE_PATH, null);
	}

//    /**
//     * Get the output file path stored in the registry
//     * @return a path as a string
//     */
//    public String getPluginFolderFilePath() {
//        //      read back from registry HKCurrentUser
//        Preferences p = Preferences.userRoot();
//        return p.get(REGKEY_SPMF_PLUGIN_FOLDER_PATH, null);
//    }
//
//    /**
//     * Store an output file path in the registry
//     * @param filepath a path as a string
//     */
//    public void setPluginFolderFilePath(String filepath) {
//        // write into HKCurrentUser
//        Preferences p = Preferences.userRoot();
//        p.put(REGKEY_SPMF_PLUGIN_FOLDER_PATH, filepath);
//    }
//    
//    /**
//     * Delete the plugin file path from the registry
//     * @param filepath a path as a string
//     */
//    public void deletePluginFolderFilePath() {
//        // write into HKCurrentUser
//        Preferences p = Preferences.userRoot();
//        p.remove(REGKEY_SPMF_PLUGIN_FOLDER_PATH);
//    }
//    
//    
	// ---
	/**
	 * Store a repository URL in the registry
	 * 
	 * @param filepath a repository URL as a string
	 */
	public void setRepositoryURL(String filepath) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_PLUGIN_REPOSITORY_URL, filepath);
	}

	/**
	 * Get the repository URL path stored in the registry
	 * 
	 * @return a path as a string
	 */
	public String getRepositoryURL() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String url = p.get(REGKEY_SPMF_PLUGIN_REPOSITORY_URL, null);
		return (url == null) ? "http://www.philippe-fournier-viger.com/spmf/plugins/" : url;
	}

	// ---

	/**
	 * Get the prefered charset stored in the registry
	 * 
	 * @return Charset the prefered charset
	 */
	public Charset getPreferedCharset() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String charsetName = p.get(REGKEY_SPMF_PREFERED_CHARSET, null);

		return (charsetName == null) ? Charset.defaultCharset() : Charset.forName(charsetName);
	}

	/**
	 * Store the prefered charset in the registry
	 * 
	 * @param charsetName the prefered charset
	 */
	public void setPreferedCharset(String charsetName) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_PREFERED_CHARSET, charsetName);
	}

	/**
	 * Get the preference if algorithms should be run as an external program by
	 * SPMF's GUI
	 * 
	 * @return true or false
	 */
	public boolean getRunAsExternalProgram() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_SPMF_RUN_EXTERNAL, null);

		return (value == null) ? false : Boolean.parseBoolean(value);
	}

	/**
	 * Store the preference if algorithms should be run as an external program by
	 * SPMF's GUI
	 * 
	 * @param value true of false
	 */
	public void setRunAsExternalProgram(boolean value) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_RUN_EXTERNAL, Boolean.toString(value));
	}

	/**
	 * Get the memory usage of the last algorithm that was run (this is stored in a
	 * registry key)
	 * 
	 * @return the memory usage as a double
	 */
	public double getLastMemoryUsage() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_LAST_MEMORY_USAGE, null);

		if (value == null) {
			return 0;
		}

		return Double.parseDouble(value);
	}

	/**
	 * Store the memory usage of the last execution in the registry
	 * 
	 * @param lastMemoryUsage a number representing the memory usage in megabytes
	 */
	public void setLastMemoryUsage(double lastMemoryUsage) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_LAST_MEMORY_USAGE, Double.toString(lastMemoryUsage));
	}

	/**
	 * Get the preference about whether the night mode is on or off (this is stored
	 * in a registry key)
	 * 
	 * @return the memory usage as a double
	 */
	public boolean getNightMode() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_NIGHT_MODE, null);

		return (value == null) ? false : Boolean.parseBoolean(value);
	}

	/**
	 * Store the preference if night mode is activated or not
	 * 
	 * @param value true of false
	 */
	public void setNightMode(boolean value) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_NIGHT_MODE, Boolean.toString(value));
	}

	/**
	 * Get the font size for the text editor (this is stored in a registry key)
	 * 
	 * @return thefont size
	 */
	public int getTextEditorFontSize() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXTEDITOR_FONTSIZE, null);
		if (value == null) {
			return 12;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Store the font size for the text edito
	 * 
	 * @param fontsize a number
	 */
	public void setTextEditorFontSize(int fontsize) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXTEDITOR_FONTSIZE, Integer.toString(fontsize));
	}

//    /**
//     * Store the preference about how many seconds an algorithm should run at most in the GUI
//     * @param text the number of seconds
//     */
//	public void setMaxSeconds(int number) {
//		 // write into HKCurrentUser
//        Preferences p = Preferences.userRoot();
//        p.put(REGKEY_SPMF_MAX_SECONDS, Integer.toString(number));
//	}
//	
//    /**
//     * Get the preference about how many seconds an algorithm should run at most in the GUI
//     * @return a string containing a number (integer)
//     */
//    public int getMaxSeconds() {
//        //      read back from registry HKCurrentUser
//        Preferences p = Preferences.userRoot();
//        String value = p.get(REGKEY_SPMF_MAX_SECONDS, null);
//
//        return (value == null) ? Integer.MAX_VALUE : Integer.parseInt(value);
//    }

	/**
	 * Get the preference about line wrap is on or off for the text editor(this is
	 * stored in a registry key)
	 * 
	 * @return the memory usage as a double
	 */
	public boolean getTextEditorLineWrap() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXT_EDITOR_LINE_WRAP, null);

		return (value == null) ? true : Boolean.parseBoolean(value);
	}

	/**
	 * Store the preference if line wrap mode is activated or not
	 * 
	 * @param value true of false
	 */
	public void setTextEditorLineWrap(boolean value) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXT_EDITOR_LINE_WRAP, Boolean.toString(value));
	}

	/**
	 * Get the preference about word wrap is on or off for the text editor(this is
	 * stored in a registry key)
	 * 
	 * @return the memory usage as a double
	 */
	public boolean getTextEditorWordWrap() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXT_EDITOR_WORD_WRAP, null);

		return (value == null) ? false : Boolean.parseBoolean(value);
	}

	/**
	 * Store the preference if line wrap mode is activated or not
	 * 
	 * @param value true of word
	 */
	public void setTextEditorWordWrap(boolean value) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXT_EDITOR_WORD_WRAP, Boolean.toString(value));
	}

	// ---
	/**
	 * Store the text editor font familly in the registry
	 * 
	 * @param filepath a font familly
	 */
	public void setFontFamilly(String filepath) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXT_EDITOR_FONT_FAMILLY, filepath);
	}

	/**
	 * Get the text editor font familly stored in the registry
	 * 
	 * @return a string (font familly)
	 */
	public String getFontFamilly() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String result = p.get(REGKEY_TEXT_EDITOR_FONT_FAMILLY, null);
		return (result == null) ? "Dialog" : result;
	}

//   public static final String REGKEY_TEXTEDITOR_WIDTH = "ca.pfv.spmf.gui.texteditor.width";
//   public static final String REGKEY_TEXTEDITOR_HEIGTH =

	/**
	 * Get the width of the text editor (this is stored in a registry key)
	 * 
	 * @return the width
	 */
	public int getTextEditorWidth() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXTEDITOR_WIDTH, null);
		if (value == null) {
			return 800;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Store window width for the text editor
	 * 
	 * @param width the width
	 */
	public void setTextEditorWidth(int width) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXTEDITOR_WIDTH, Integer.toString(width));
	}

	/**
	 * Get the height of the text editor (this is stored in a registry key)
	 * 
	 * @return the height
	 */
	public int getTextEditorHeight() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXTEDITOR_HEIGHT, null);
		if (value == null) {
			return 800;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Store window height for the text editor
	 * 
	 * @param heigth the height
	 */
	public void setTextEditorHeight(int height) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXTEDITOR_HEIGHT, Integer.toString(height));
	}

	// =================================

	/**
	 * Get the text area width of the text editor (this is stored in a registry key)
	 * 
	 * @return the text area width
	 */
	public int getTextEditorAreaWidth() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXTEDITORAREA_WIDTH, null);
		if (value == null) {
			return 800;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Store text area width for the text editor
	 * 
	 * @param width the width
	 */
	public void setTextEditorAreaWidth(int width) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXTEDITORAREA_WIDTH, Integer.toString(width));
	}

	/**
	 * Get the text area height of the text editor (this is stored in a registry
	 * key)
	 * 
	 * @return the height
	 */
	public int getTextEditorAreaHeight() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXTEDITORAREA_HEIGHT, null);
		if (value == null) {
			return 800;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Store text area height for the text editor
	 * 
	 * @param heigth the height
	 */
	public void setTextEditorAreaHeight(int height) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXTEDITORAREA_HEIGHT, Integer.toString(height));
	}

	// =============================
	/**
	 * Get the X position of the text editor (this is stored in a registry key)
	 * 
	 * @return the height
	 */
	public int getTextEditorX() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXTEDITOR_X, null);
		if (value == null) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Store X position of the text editor
	 * 
	 * @param position the position
	 */
	public void setTextEditorX(int position) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXTEDITOR_X, Integer.toString(position));
	}

	/**
	 * Get the Y position of the text editor (this is stored in a registry key)
	 * 
	 * @return the height
	 */
	public int getTextEditorY() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_TEXTEDITOR_Y, null);
		if (value == null) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Store Y position of the text editor
	 * 
	 * @param position the position
	 */
	public void setTextEditorY(int position) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_TEXTEDITOR_Y, Integer.toString(position));
	}
	
	/**
	 * Get the preference if SPMF should use the system text editor
	 * SPMF's GUI
	 * 
	 * @return true or false
	 */
	public boolean getShouldUseSystemTextEditor() {
		// read back from registry HKCurrentUser
		Preferences p = Preferences.userRoot();
		String value = p.get(REGKEY_SPMF_USE_SYSTEM_TEXT_EDITOR, null);

		return (value == null) ? false : Boolean.parseBoolean(value);
	}

	/**
	 * Store the preference if SPMF should use the system text editor
	 * 
	 * @param true = system text editor, false = not
	 */
	public void setShouldUseSystemTextEditor(boolean value) {
		// write into HKCurrentUser
		Preferences p = Preferences.userRoot();
		p.put(REGKEY_SPMF_USE_SYSTEM_TEXT_EDITOR, Boolean.toString(value));
	}

	/**
	 * Reset all the preferences for SPMF stored in the system properties (e.g. Windows registry or similar on other OSes)
	 */
	public void resetPreferences() {
		setInputFilePath("");
		setOutputFilePath("");
		setPreferedCharset(Charset.defaultCharset().name());
		setShouldUseSystemTextEditor(true);
		setRunAsExternalProgram(false);
		setNightMode(false);
	}

}
