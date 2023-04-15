package ca.pfv.spmf._group7.testing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf._group7.rpgrowthstreaming.AlgoRPStreamLandmark;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;

/**
 * Example of how to use the RPGrowth from the source code
 * @author Ryan Benton and Blake Johns
 */

public class RunAlgoRPStreamLandmark {
	public static void main(String[] arg) throws FileNotFoundException, IOException{
		//load the transaction database
		String fileName = "context2RP.txt";
		System.out.println("INPUT FILE: " + fileName + "\n");
		String input = fileToPath(fileName);
		
		//threshold range [minimum rare (min) and minimum support (max)]
		double minsup = 0.6;
		double minraresup = 0.2;
		double preminraresup = 0.2;
		int landmark = 1;
		
		//Apply the RPGrowth algorithm
		AlgoRPStreamLandmark algo = new AlgoRPStreamLandmark();
		
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
