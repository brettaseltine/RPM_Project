package ca.pfv.spmf._group7.testing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
//import java.util.regex.PatternSyntaxException;

//import ca.pfv.spmf.algorithms.frequentpatterns.rpgrowth.AlgoRPGrowth;
import ca.pfv.spmf._group7.rpgrowthstreaming.AlgoSRPLandmark;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;

/**
 * Example of how to use the RPGrowth from the source code
 * @author Ryan Benton and Blake Johns
 */

public class RunAlgoSRPLandmark {
	public static void main(String[] arg) throws FileNotFoundException, IOException{
		//load the transaction database
		String fileName = "srpTestFile.txt";
		System.out.println("INPUT FILE: " + fileName + "\n");
		String input = fileToPath(fileName);
		//System.out.println("Using file: " + fileName + "\n\n");
		
		//threshold range [minimum rare (min) and minimum support (max)]
		double minsup =        4.0/12.0;
		double minraresup =     2.0/12.0;
		double preminraresup =  2.0/12.0;
		int landmark = 1;
		
		//Apply the RPGrowth algorithm
		AlgoSRPLandmark algo = new AlgoSRPLandmark();
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset, e.g. 3 )
//		algo.setMaximumPatternLength(3);
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset, e.g. 2 )
//		algo.setMinimumPatternLength(2);
		
		//Run the algo
		//NOTE that here we use "null" as the output file path because we are saving to memory
		Itemsets patterns = algo.runAlgorithm(input, null, minsup, minraresup, preminraresup, landmark);
		algo.printStats();
		
		patterns.printItemsets(algo.getDatabaseSize());
	}
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFPGrowth_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
