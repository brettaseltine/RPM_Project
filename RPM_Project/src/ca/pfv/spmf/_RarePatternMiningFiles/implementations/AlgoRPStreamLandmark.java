package ca.pfv.spmf._RarePatternMiningFiles.implementations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.LinkedList;


import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FPTree;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.tools.MemoryLogger;

/** 
 * Implementation of the Rare Pattern Stream Landmark algorithm.
 * This is an optimized version that saves the result into memory.
 * 
 * @author Brett Aseltine
 * Acknowledgement: Builds off RP-Growth algorithm implemented by Ryan Benton and Blake Johns.
 * 
 * @see RPTree
 * @see FPTree
 * @see Itemset
 * @see Itemsets
 */

public class AlgoRPStreamLandmark {

		//for statistics
		 private long startTimestamp; // start time of the latest execution
		 private long endTime; // end time of the latest execution
		 private int transactionCount = 0; 		// transaction count in the database following landmark (used transactions)
		 private int seenTransactionCount = 0;	// number of transactions seen in stream (before and after landmark)
		 private int itemsetCount; // number of freq. itemsets found
		 private int numBatches = 0;

		 // parameter
		 public int preMinRareSupportRelative;//the relative minimum rare support
		 public int minSupportRelative; // the relative minimum support

		 // The  patterns that are found
		 // (if the user want to keep them into memory)
		 protected Itemsets patterns = null;

		 // This variable is used to determine the size of buffers to store itemsets.
		 // A value of 50 is enough because it allows up to 2^50 patterns!
		 final int BUFFERS_SIZE = 2000;

		 // buffer for storing the current itemset that is mined when performing mining
		 // the idea is to always reuse the same buffer to reduce memory usage.
		 private int[] itemsetBuffer = null;
		 // another buffer for storing rpnodes in a single path of the tree
		 private RPNode[] rpNodeTempBuffer = null;

		 /** maximum pattern length */
		 private int maxPatternLength = 1000;
		 
		/** minimum pattern length */
		private int minPatternLength = 0;
		
		// OUR ADDED VARIABLES
		private Map<Itemset, Integer> potentialResult = new HashMap<>();	// key = itemset, value = support
		
		private BufferedReader reader1;	// performs 1st and only scan
		private Queue<String> curBatch = new LinkedList<>();	// each element holds transaction line as String
		
		private int batchCount = 0;
		
		private boolean isStream = true;
		
		

		 /**
		  * Constructor
		  */
		 public AlgoRPStreamLandmark() {

		 }

		 /**
		  * Method to run the RPGRowth algorithm.
		  * @param input the path to an input file containing a transaction database.
		  * @param output the output file path for saving the result (if null, the result
		  *        will be returned by the method instead of being saved).
		  * @param minsupp the minimum support threshold.
		  * @PARAM misraresupp the minimum support threshold.
		  * @return the result if no output file path is provided.
		  * @throws IOException exception if error reading or writing files
		  */
		 public Itemsets runAlgorithm(String input, String output, double minsupp, double minraresupp, double preminraresupp, int landmark) throws FileNotFoundException, IOException {
		   // record start time
		   startTimestamp = System.currentTimeMillis();
		   // number of itemsets found
		   itemsetCount = 0;

		   //initialize tool to record memory usage
		   MemoryLogger.getInstance().reset();
		   MemoryLogger.getInstance().checkMemory();
		   
		   // set file reader
		   reader1 = new BufferedReader(new FileReader(input));
		   
		   while(isStream) {
			   System.out.println("PROCESSING BATCH: " + numBatches);
			   Itemsets mined = processBatch(minsupp, preminraresupp, landmark);
			   aggregateMinedItemsets(mined);
		       numBatches++;
		   }
		   		
		   Itemsets result = getTrulyRare(minraresupp);
		   
		   this.itemsetCount = result.getItemsetsCount();
		   
		   // record the execution end time
		   endTime= System.currentTimeMillis();

		   // check the memory usage
		   MemoryLogger.getInstance().checkMemory();
		   
		   return result;
		   
		 }
		 
		   
		 public Itemsets processBatch(double minsupp, double preminraresupp, int landmark) throws FileNotFoundException, IOException {
		   // initialize result holding variable 
		   patterns = new Itemsets("BATCH RARE ITEMSETS");
		   
		   // (1) PREPROCESSING: Initial database scan to determine the frequency of each item
		   // The frequency is stored in a map:
		   //    key: item   value: support
		   final Map<Integer, Integer> mapSupport = scanBatchToDetermineFrequencyOfSingleItems(landmark);

		   // convert the minimum support as percentage to a relative minimum support
		   // convert the minimum rare support as percentage to a minimum rare support
		   this.preMinRareSupportRelative = (int) Math.ceil(preminraresupp * batchCount);
		   this.minSupportRelative = (int) Math.ceil(minsupp * batchCount); 
		   
		   // (2) Scan batch from memory to build the initial RP-Tree
		   // Before inserting a transaction in the RPTree, we sort the items
		   // by descending order of support.  We ignore items that
		   // have over the minimum support.
		   RPTree tree = new RPTree();

		   // read the batch in memory
		   String line = curBatch.poll();
		   // for each line (transaction) until the end of the file OR BATCH
		   while( line != null && line != "\n"){
		     // if the line is  a comment, is  empty or is a
		     // kind of metadata
		     if (line.isEmpty() == true ||	line.charAt(0) == '#' || line.charAt(0) == '%'
		       || line.charAt(0) == '@') {
		       continue;
		     }

		     String[] lineSplited = line.split(" ");
		     List<Integer> transaction = new ArrayList<Integer>();

		     // for each item in the transaction
		     for(String itemString : lineSplited){
		       Integer item = Integer.parseInt(itemString);
		// only add items that have less than or equal to the minimum support 
	    // and more than or equal to the minimum rare support
		       if(mapSupport.get(item) >= preMinRareSupportRelative){
		    	   //so the items being added are >= preMinRareSupportRelative
		    	  transaction.add(item);}																
		     }
		
		     // sort item in the transaction by descending order of support
		     Collections.sort(transaction, new Comparator<Integer>(){ 
		       public int compare(Integer item1, Integer item2){																	
		         // compare the frequency
		         int compare = mapSupport.get(item2) - mapSupport.get(item1);
		         // if the same frequency, we check the lexical ordering!
		         if(compare == 0){ 
		           return (item1 - item2);}
		         // otherwise, just use the frequency
		         return compare;}	       
		     });
		     //Add the sorted items to the RP tree
		     //If (last item in sorted transaction is < minRelSup, we accept the transaction)   		     
		     //get the last item in transaction; because the last item in the transaction is the smallest count size
		     
		     	 if(!transaction.isEmpty()) {/* BUG FIX to account for a check against an empty transaction 01/29/2020 Blake Johns*/
		     		 int myCheck = transaction.get(transaction.size() - 1);
		     		 //take item and get its count
		     		 int count = mapSupport.get(myCheck);
		     		 //if the last item is below minSupportRelative then it is Rare by our definition, so it is of interest and added to the tree
		     		 if(count < this.minSupportRelative) {
		     			 tree.addTransaction(transaction);
		     		 }	    	 
		     	 }
		     line = curBatch.poll();
		   }

		   // We create the header table for the tree using the calculated support of single items
		   tree.createHeaderList(mapSupport);
		   
		   //System.out.println("INITIAL TREE");
		   //System.out.println(tree.toString());
		  
		   // (5) We start to mine the RP-Tree by calling the recursive method.
		   // Initially, the prefix alpha is empty.
		   // if at least one item is not frequent
		   if(tree.headerList.size() > 0) {
		     // initialize the buffer for storing the current itemset
		     itemsetBuffer = new int[BUFFERS_SIZE];
		     // and another buffer
		     rpNodeTempBuffer = new RPNode[BUFFERS_SIZE];
		     // recursively generate rare itemsets using the RP-tree
		     // Note: we assume that the initial RP-Tree has more than one path
		     // which should generally be the case.
		     rpgrowth(tree, itemsetBuffer, 0, batchCount, mapSupport);
		   }
		   
		   // done processing batch
		   curBatch.clear();
		   batchCount = 0;

		   // return the result (if saved to memory)
		   return patterns;
		 }

		 /**
		  * Mine an RP-Tree having more than one path.
		  * @param tree  the RP-tree
		  * @param prefix  the current prefix, named "alpha"
		  * @param mapSupport the frequency of items in the RP-Tree
		  * @throws IOException  exception if error writing the output file
		  */
		 private void rpgrowth(RPTree tree, int [] prefix, int prefixLength, int prefixSupport, Map<Integer, Integer> mapSupport) throws IOException {

		   if(prefixLength == maxPatternLength)
			   return;
		   
		  

		   ////		======= DEBUG ========
//				System.out.print("###### Prefix: ");
//				for(int k=0; k< prefixLength; k++) {
//					System.out.print(prefix[k] + "  ");
//				}
//				System.out.println("\n");
////						========== END DEBUG =======
//				System.out.println(tree); --constructs a visual representation of the tree to assist with debugging.

		   // We will check if the RPtree contains a single path
		   boolean singlePath = true;
		   // This variable is used to count the number of items in the single path
		   // if there is one
		   int position = 0;
		   // if the root has more than one child, than it is not a single path
		   if(tree.root.childs.size() > 1) {
		     singlePath = false;
		   }else {
		     // Otherwise,
		     // if the root has exactly one child, we need to recursively check children
		     // of the child to see if they also have one child
		     RPNode currentNode = tree.root.childs.get(0);
		     while(true){
		       // if the current child has more than one child, it is not a single path!
		       if(currentNode.childs.size() > 1) {
		         singlePath = false;
		         break;
		       }
		       // otherwise, we copy the current item in the buffer and move to the child
		       // the buffer will be used to store all items in the path
		       rpNodeTempBuffer[position] = currentNode;

		       position++;
		       // if this node has no child, that means that this is the end of this path
		       // and it is a single path, so we break
		       if(currentNode.childs.size() == 0) {
		         break;
		       }
		       currentNode = currentNode.childs.get(0);
		     }
		   }
		   // Case 1: the RPtree contains a single path
		   //If the prefix is NOT the root and there is a single path
		   if ((singlePath) && (prefixLength > 0))
		   {		    
				   saveAllCombinationsOfPrefixPath(rpNodeTempBuffer, position, prefix, prefixLength);
		   }
		   else {
		     // For each rare item in the header table list of the tree in reverse order.
		     for(int i = tree.headerList.size()-1; i>=0; i--){
		       // get the item
		       Integer item = tree.headerList.get(i);

		       // get the item support
		       int support = mapSupport.get(item);
		       if((prefixLength == 0) && (support >= minSupportRelative))
							return;
		       // Create Beta by concatenating prefix Alpha by adding the current item to alpha
		       prefix[prefixLength] = item;

		       // calculate the support of the new prefix beta
		       int betaSupport = (prefixSupport < support) ? prefixSupport: support;
		       
		       // save beta to the output file
		       
		       //If not the root OR support < minimum relative support; save item set
		       if ((prefixLength > 0) || (support < this.minSupportRelative))
		       		saveItemset(prefix, prefixLength+1, betaSupport);
		     

		       if(prefixLength+1 < maxPatternLength){
		         // === (A) Construct beta's conditional pattern base ===
		         // It is a sub-database which consists of the set of prefix paths
		         // in the RP-tree co-occurring with the prefix pattern.
		         List<List<RPNode>> prefixPaths = new ArrayList<List<RPNode>>();
		         RPNode path = tree.mapItemNodes.get(item);

		         // Map to count the support of items in the conditional prefix tree
		         // Key: item   Value: support
		         Map<Integer, Integer> mapSupportBeta = new HashMap<Integer, Integer>();
		         
		         while(path != null){
		           // if the path is not just the root node
		           if(path.parent.itemID != -1){
		             // create the prefixpath
		             List<RPNode> prefixPath = new ArrayList<RPNode>();
		             // add this node.
		             prefixPath.add(path);   // NOTE: we add it just to keep its support,
		             // actually it should not be part of the prefixPath
		             int pathCount = path.counter;

		             //Recursively add all the parents of this node.
		             RPNode parent = path.parent;
		             while(parent.itemID != -1){
		               prefixPath.add(parent);

		               // FOR EACH PATTERN WE ALSO UPDATE THE ITEM SUPPORT AT THE SAME TIME
		               // if the first time we see that node id
		               if(mapSupportBeta.get(parent.itemID) == null){
		                 // just add the path count
		                 mapSupportBeta.put(parent.itemID, pathCount);
		               }else{
		                 // otherwise, make the sum with the value already stored
		                 mapSupportBeta.put(parent.itemID, mapSupportBeta.get(parent.itemID) + pathCount);
		               }
		               parent = parent.parent;
		             }
		             // add the path to the list of prefix paths
		             prefixPaths.add(prefixPath);
		           }
		           // We will look for the next prefix path
		           path = path.nodeLink;
		         }

		         // (B) Construct beta's conditional RP-Tree
		         // Create the tree.
		         RPTree treeBeta = new RPTree();
		         // Add each prefix path in the RP-tree.
		         for(List<RPNode> prefixPath : prefixPaths){
		           treeBeta.addPrefixPath(prefixPath, mapSupportBeta, minSupportRelative, preMinRareSupportRelative); 
		         }

		         // Mine recursively the Beta tree if the root has child(s)
		         if(treeBeta.root.childs.size() > 0){
		           // Create the header list.
		           treeBeta.createHeaderList(mapSupportBeta);
		        
		           // recursive call
		           rpgrowth(treeBeta, prefix, prefixLength+1, betaSupport, mapSupportBeta);	         
		         }		        
		       }
		     }
		   }
		 }


		 /**
		  * This method saves all combinations of a prefix path if it has enough support
		  * @param prefix the current prefix
		  * @param prefixLength the current prefix length
		  * @param prefixPath the prefix path
		  * @throws IOException if exception while writing to output file
		  */
		 private void saveAllCombinationsOfPrefixPath(RPNode[] rpNodeTempBuffer, int position,
		     int[] prefix, int prefixLength) throws IOException {
		   int support = 0;		   
		   		   if (prefixLength == 0) {
			   return;}
		   
		   // Generate all subsets of the prefixPath except the empty set
		   // and output them
		   // We use bits to generate all subsets.
		loop1:	for (long i = 1, max = 1 << position; i < max; i++) {
		     // we create a new subset
		     int newPrefixLength = prefixLength;
		     // for each bit
		     for (int j = 0; j < position; j++) {
		       // check if the j bit is set to 1
		       int isSet = (int) i & (1 << j);
		       // if yes, add the bit position as an item to the new subset
		       if (isSet > 0) {
		         if(newPrefixLength == maxPatternLength){
		           continue loop1;
		         }

		         prefix[newPrefixLength++] = rpNodeTempBuffer[j].itemID;
		           support = rpNodeTempBuffer[j].counter;
		       }
		     }
		     // save the item set
		     saveItemset(prefix, newPrefixLength, support);
		   }
		 }


		 /**
		  * This method scans the input database to calculate the support of single items
		  * @param input the path of the input file
		  * @throws IOException  exception if error while writing the file
		  * @return a map for storing the support of each item (key: item, value: support)
		  */
		 private  Map<Integer, Integer> scanBatchToDetermineFrequencyOfSingleItems(int landmark) throws FileNotFoundException, IOException {
		   // a map for storing the support of each item (key: item, value: support)
		    Map<Integer, Integer> mapSupport = new HashMap<Integer, Integer>();
		   //Create object for reading the input file
		   
		   String line = reader1.readLine();
		   
		   // for each line (transaction) until the end of file or batch (\n indicates batch end)
		   while( line != null && !line.equals("") ){
			   
		     // if the line is a comment, is  empty or is a kind of metadata
		     if (line.isEmpty() == true ||  line.charAt(0) == '#' || line.charAt(0) == '%' 	|| line.charAt(0) == '@') {
		       continue;
		     }
		     
		     seenTransactionCount++;
		     
		     // add transactions >= landmark
		     if(seenTransactionCount >= landmark) {
			     // add line to batch
			     curBatch.add(line);
	
			     // split the line into items
			     String[] lineSplited = line.split(" ");
			     // for each item
			     for(String itemString : lineSplited){
			       // increase the support count of the item
			       Integer item = Integer.parseInt(itemString);
			       // increase the support count of the item
			       Integer count = mapSupport.get(item);
			       if(count == null){
			         mapSupport.put(item, 1);
			       }else{
			         mapSupport.put(item, ++count);
			       }
			     }
			     // increase the transaction count
			     batchCount++;
			     transactionCount++;
		     }
		     
		     // get next line/transaction
		     line = reader1.readLine();
		   }
		   
		   // check if end of stream
		   if(line == null) isStream = false;

		   return mapSupport;
		 }


		 /**
		  * Write an infrequent item set that is found to the output file or
		  * keep into memory if the user prefer that the result be saved into memory.
		  */
		 private void saveItemset(int [] itemset, int itemsetLength, int support) throws IOException {
			  if (itemsetLength < minPatternLength) {
			  	return;
			  }
		   
		     // create an object Itemset and add it to the set of patterns
		     // found.
		     int[] itemsetArray = new int[itemsetLength];
		     System.arraycopy(itemset, 0, itemsetArray, 0, itemsetLength);
	
		     // sort the itemset so that it is sorted according to lexical ordering before we show it to the user
		     Arrays.sort(itemsetArray);
	
		     Itemset itemsetObj = new Itemset(itemsetArray);
		     itemsetObj.setAbsoluteSupport(support);
		     patterns.addItemset(itemsetObj, itemsetLength);
		 }
		 
		 public void aggregateMinedItemsets(Itemsets input) {
			 List<List<Itemset>> levels = input.getLevels();			 
			 
			 for(int i = 0; i < levels.size(); i++) {
				 List<Itemset> curLevel = levels.get(i);
				 
				 for(int j = 0; j < curLevel.size(); j++) {
					 Itemset curItem = curLevel.get(j);
					 				 
					 this.potentialResult.put(curItem, potentialResult.getOrDefault(curItem,  0) + curItem.getAbsoluteSupport());
				 }
				 
			 }
			 
		 }
		 
		 
		 public Itemsets getTrulyRare(double minraresupp) {
			 Itemsets truly = new Itemsets("TRULY RARE ITEMSETS");
			 int minRareSupRelative = (int) (minraresupp * this.transactionCount);
			 
			 for(Entry<Itemset, Integer> entry: potentialResult.entrySet()) {
				 Itemset item = entry.getKey();
				 int support = entry.getValue();
				 
				 if(support >= minRareSupRelative) {
					 
					 item.setAbsoluteSupport(support);
					 truly.addItemset(item, item.size());
				 }
			 }
			 
			 return truly;
		 }
		 
		 
		 /**
		  * Print statistics about the algorithm execution to System.out.
		  */
		 public void printStats() {
		   System.out.println("=============  RP-GROWTH 2.38 - STATS =============");
		   long temps = endTime - startTimestamp;
		   System.out.println(" Transactions count from database : " + transactionCount);
		   System.out.println(" Number of batches : " + numBatches);
		   System.out.print(" Max memory usage: " + MemoryLogger.getInstance().getMaxMemory() + " mb \n");
		   System.out.println(" Rare itemsets count : " + itemsetCount);
		   System.out.println(" Total time ~ " + temps + " ms");
		   System.out.println("===================================================");
		 }

		 /**
		  * Get the number of transactions in the last transaction database read.
		  * @return the number of transactions.
		  */
		 public int getDatabaseSize() {
		   return transactionCount;
		 }

		 /**
		  * Set the maximum pattern length
		  * @param length the maximum length
		  */
		 public void setMaximumPatternLength(int length) {
		   maxPatternLength = length;
		 }
		 
	/**
	 * Set the minimum pattern length
	 * 
	 * @param length the minimum length
	 */
	public void setMinimumPatternLength(int minPatternLength) {
		this.minPatternLength = minPatternLength;
	}
}