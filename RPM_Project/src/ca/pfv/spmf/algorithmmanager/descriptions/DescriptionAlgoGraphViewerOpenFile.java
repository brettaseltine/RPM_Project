package ca.pfv.spmf.algorithmmanager.descriptions;

import java.io.File;
import java.io.IOException;

import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.gui.graphviewer.GraphViewer;
import ca.pfv.spmf.gui.texteditor.SPMFTextEditor;

/**
 * This class describes the algorithm to run the graph viewer to open a graph database file
 * 
 * @see GraphViewer
 * @author Philippe Fournier-Viger
 */
public class DescriptionAlgoGraphViewerOpenFile extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoGraphViewerOpenFile() {
	}

	@Override
	public String getName() {
		return "Open_graph_database_file_with_graph_viewer";
	}

	@Override
	public String getAlgorithmCategory() {
		return "DATASET TOOLS";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.philippe-fournier-viger.com/spmf/GraphViewerToolDB.php";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws IOException {
		boolean displayStringRepresentation = false;
		if (parameters.length >=1 && "".equals(parameters[0]) == false) {
			displayStringRepresentation  = getParamAsBoolean(parameters[0]);
		}
		
		boolean runAsStandalone = false;
		GraphViewer tool = new GraphViewer(runAsStandalone,displayStringRepresentation);
		tool.loadFileGSPANFormat(inputFile);
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {

		DescriptionOfParameter[] parameters = new DescriptionOfParameter[1];
		parameters[0] = new DescriptionOfParameter("Show text (true/false)", "(e.g. true)", Boolean.class, true);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Philippe Fournier-Viger";
	}

	@Override
	public String[] getInputFileTypes() {
		return new String[] { "Graph database file" };
	}

	@Override
	public String[] getOutputFileTypes() {
		return null;
	}
//
//	@Override
//	String[] getSpecialInputFileTypes() {
//		return null; //new String[]{"ARFF"};
//	}

}
