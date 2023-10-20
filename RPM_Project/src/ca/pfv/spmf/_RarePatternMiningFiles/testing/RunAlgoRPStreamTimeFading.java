package ca.pfv.spmf._RarePatternMiningFiles.testing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf._RarePatternMiningFiles.implementations.AlgoRPStreamTimeFading;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;


public class RunAlgoRPStreamTimeFading {
	public static void main(String[] arg) throws FileNotFoundException, IOException{
		//load the transaction database
		String fileName = "context2RP.txt";
		System.out.println("INPUT FILE: " + fileName + "\n");
		String input = fileToPath(fileName);
		
		//threshold range [minimum rare (min) and minimum support (max)]
		double minsup = 0.6;
		double minraresup = 0.2;
		double preminraresup = 0.2;
		double timeFactor = 0.8;
		
		//Apply the RPGrowth algorithm
		AlgoRPStreamTimeFading algo = new AlgoRPStreamTimeFading();
		
		//Run the algo
		//NOTE that here we use "null" as the output file path because we are saving to memory
		Itemsets patterns = algo.runAlgorithm(input, null, minsup, minraresup, preminraresup, timeFactor);
		algo.printStats();
		
		patterns.printItemsets(algo.getDatabaseSize());
	}
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFPGrowth_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
