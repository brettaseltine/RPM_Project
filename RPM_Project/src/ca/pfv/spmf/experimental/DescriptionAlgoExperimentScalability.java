package ca.pfv.spmf.experimental;

import java.io.IOException;

import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.experiments.oneparametervaried.ExperimenterForParameterChange;
import ca.pfv.spmf.gui.experiments.ExperimenterScalabilityWindow;

/**
 * This class describes the algorithm to compare algorithms when
 * one parameter is varied
 * 
 * @see ExperimenterForParameterChange
 * @author Philippe Fournier-Viger
 */
public class DescriptionAlgoExperimentScalability extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoExperimentScalability(){
	}

	@Override
	public String getName() {
		return "Performance_experiment_scalability";
	}

	@Override
	public String getAlgorithmCategory() {
		return "EXPERIMENTS";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.philippe-fournier-viger.com/spmf/ExperimenterPerformance_Scalability.php";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws IOException {

		ExperimenterScalabilityWindow experimenter = new ExperimenterScalabilityWindow();
		experimenter.setVisible(true);
		experimenter.setTitle("Run an experiment to test the performance of algorithm(s) when dataset size is varied.");
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
        
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[0];
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Philippe Fournier-Viger";
	}

	@Override
	public String[] getInputFileTypes() {
		return null;
	}

	@Override
	public String[] getOutputFileTypes() {
		return null;
	}

	
}
