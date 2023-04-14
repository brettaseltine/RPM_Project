package ca.pfv.spmf._group7.rpgrowthstreaming;

/*This file is copyright (c) 2018 Ryan Benton and Blake Johns
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
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.Set;
import java.util.HashSet;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FPTree;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.tools.MemoryLogger;

/** 
 * This is an implementation of the Rare Pattern Tree Mining algorithm using the FP-Growth algorithm.
 * More information on rare pattern tree mining can be found in the paper here:
 * "RP-Tree: Rare Pattern Tree Mining",
 * A. Cuzzocrea and U. Dayal (Eds.): Data Warehousing and Knowledge Discovery 2011, LNCS 6862, pp. 277�288, 2011
 * 
 * The FP-Growth algorithm was originally created by Philippe Fournier-Viger and modified by 
 * Blake Johns and Ryan Benton.
 * 
 * This is an optimized version that saves the result to a file
 * or keep it into memory if no output path is provided
 * by the user to the runAlgorithm method().
 *
 * @see FPTree
 * @see Itemset
 * @see Itemsets
 * @author Philippe Fournier-Viger, Ryan Benton, Blake Johns
 */

public class AlgoSRPLandmark {

		//for statistics
		 private long startTimestamp; // start time of the latest execution
		 private long endTime; // end time of the latest execution
		 private int transactionCount = 0; 		// transaction count in the database following landmark (used transactions)
		 private int seenTransactionCount = 0;	// number of transactions seen in stream (before and after landmark)
		 private int itemsetCount; // number of freq. itemsets found

		 // parameter
		 public int preMinRareSupportRelative;//the relative minimum rare support
		 public int minSupportRelative; // the relative minimum support
		 
		 BufferedWriter writer = null; // object to write the output file

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

		 // This buffer is used to store an itemset that will be written to file
		 // so that the algorithm can sort the itemset before it is output to file
		 // (when the user choose to output result to file).
		 private int[] itemsetOutputBuffer = null;

		 /** maximum pattern length */
		 private int maxPatternLength = 1000;
		 
		/** minimum pattern length */
		private int minPatternLength = 0;
		
		// OUR ADDED VARIABLES
		private Itemsets potentialResult = new Itemsets("RARE ITEMSETS");
		
		private BufferedReader reader1;	// performs 1st and only scan
		
		private int batchCount = 0;
		
		private boolean isStream = true;
		

		 /**
		  * Constructor
		  */
		 public AlgoSRPLandmark() {

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

//		   // if the user want to keep the result into memory
//		   if(output == null){
//		     writer = null;
//		     patterns =  new Itemsets("RARE ITEMSETS"); 
//		     }else{ // if the user want to save the result to a file
//		     patterns = null;
//		     writer = new BufferedWriter(new FileWriter(output));
//		     itemsetOutputBuffer = new int[BUFFERS_SIZE];
//		   }
		   
		   // set file reader
		   reader1 = new BufferedReader(new FileReader(input));
		   
		   int batchNum = 0;
		   while(isStream) {
			   System.out.println("PROCESSING BATCH: " + batchNum);
			   Itemsets mined = runInstance(minsupp, preminraresupp, landmark);
			   saveMinedItemsets(potentialResult, mined);
		       batchNum++;
		   }
		   		
		   return potentialResult;
		   // return getTrulyRare(potentialResult, minraresupp); // beneficial when using preminrare diff than minrare
		   
		 }
		 
		 public List<Integer> getNextTransaction(int landmark) {
			 	
	         List<Integer> transaction = new LinkedList<>();
			 
			 try {  
			     String line = reader1.readLine();
			     
			     // check if end of stream
				 if(line == null) isStream = false;
				 // return null if not transaction data (end of stream or end of batch)
				 if(line == null || line.equals("")) return null;
			     
				 seenTransactionCount++;
			     // add transactions >= landmark
			     if(seenTransactionCount >= landmark) {
		
				     // split the line into items
				     String[] lineSplited = line.split(" ");
				     // for each item
				     for(String itemString : lineSplited){
				    	 // add to transaction
				       Integer item = Integer.parseInt(itemString);
				       transaction.add(item);
				     }
				     // increase the transaction count
				     batchCount++;
				     transactionCount++;
			     }
			    
				 
				 
				 return transaction;
			 }
			 
			 catch (IOException e) {
				 System.out.println(e);
				 return null;
			 }
		 }
		 
		 public void updateItemFrequencyList(Map<Integer, Integer> ifl, List<Integer> t) {
			 for(int item : t) {
				 ifl.put(item, ifl.getOrDefault(item,0) + 1);
			 }
		 }
		 
		 public void updateConnectionTable(Map<Integer, List<Itemset>> connectionTable, List<Integer> t) {
			 for(int i = 0; i < t.size(); i++) {
				 
				 int cur_item = t.get(i);
				 connectionTable.putIfAbsent( cur_item, new LinkedList<>() );
				 List<Itemset> item_connections = connectionTable.get(cur_item);
				 
				 // connect with future items
				 for(int j = i+1; j < t.size(); j++) {
					 int connect_item = t.get(j);
					 
					 boolean hasItem = false;
					 for(int k = 0; k < item_connections.size(); k++) {
						 if(item_connections.get(k).itemset[0] == connect_item) {
							 hasItem = true;
							 item_connections.get(k).support += 1;
							 break;
						 }
					 }
					 if(!hasItem) {
						 Itemset new_item = new Itemset(connect_item);
						 new_item.setAbsoluteSupport(1);
						 item_connections.add(new_item);
					 }
				 }
			 }
		 }
		 
		 public Set<Integer> buildR(Map<Integer, Integer> itemFrequencyList) {
			 Set<Integer> r = new HashSet<>();
			 
			 for(Entry<Integer, Integer> entry: itemFrequencyList.entrySet()) {
				 if(entry.getValue() >= preMinRareSupportRelative && entry.getValue() < minSupportRelative) {
					 r.add(entry.getKey());
				 }
			 }
			 
			 return r;
		 }
		 
		 public Set<Integer> buildC(Map <Integer, List<Itemset>> connectionTable, Map<Integer, Integer> itemFrequencyList, Set<Integer> rSet) {
			 Set<Integer> c = new HashSet<>();
			 
			 for(Entry<Integer, List<Itemset>> entry: connectionTable.entrySet()) {
				 int keyItem = entry.getKey();
				
				// firstly keyItem must be >= preMinRareSupportRelative
				 if(itemFrequencyList.get(keyItem) >= preMinRareSupportRelative) {
					 List<Itemset> coItems = entry.getValue();
					 for(int i = 0; i < coItems.size(); i++) {
						 
						 // only items with occuring frequency >= preMinRareSupport are added
						 if(coItems.get(i).getAbsoluteSupport() >= preMinRareSupportRelative)
							 c.add(coItems.get(i).itemset[0]);
					 }
				 }
				
			 }
			 c.removeAll(rSet);
			 return c;
		 }
		 
		   
		 public Itemsets runInstance(double minsupp, double preminraresupp, int landmark) throws FileNotFoundException, IOException {
		   // initialize result holding variable 
		   patterns = new Itemsets("BATCH RARE ITEMSETS");
		   
		   Map<Integer, Integer> itemFrequencyList = new HashMap<>();
		   Map<Integer, List<Itemset> > connectionTable = new HashMap<>();
		   SRPTree tree = new SRPTree();
		   
		   
		 // BUILD TREE FOR CURRENT BATCH
		   List<Integer> cur_t = getNextTransaction(landmark);
		   while (cur_t != null) {
			   
			   // order cur_t in canonical ordering (lexicographic)
			   Collections.sort(cur_t);
			   
			   updateItemFrequencyList(itemFrequencyList, cur_t);
			   
			   updateConnectionTable(connectionTable, cur_t);
			   
			   // updateTree
			   tree.addTransaction(cur_t);
			   
			   cur_t = getNextTransaction(landmark);
			   
		   }
		   
		   // convert the minimum support as percentage to a relative minimum support
		   // convert the minimum rare support as percentage to a minimum rare support
		   this.preMinRareSupportRelative = (int) Math.ceil(preminraresupp * batchCount);
		   this.minSupportRelative = (int) Math.ceil(minsupp * batchCount); 
		   //System.out.println("THis is RARESUP: " + this.preMinRareSupportRelative);
		   //System.out.println("THIS IS FREQSUP: " + this.minSupportRelative);
		   
		   //DEBUG
//		   System.out.println("THIS IS THE FREQUENCY LIST");
//		   for(Entry<Integer, Integer> entry: itemFrequencyList.entrySet()) {
//			      System.out.println(entry + "\n");
//		   }
//		   
//		   System.out.println("THIS IS THE CONNECTION TABLE");
//		   for(Entry<Integer, List<Itemset>> entry: connectionTable.entrySet()) {
//			      System.out.print(entry.getKey() + ": [");
//			      List<Itemset> val = entry.getValue();
//			      for(int i = 0; i < val.size(); i++) {
//			    	  System.out.print(val.get(i).itemset[0] + "=" + val.get(i).getAbsoluteSupport());
//			    	  System.out.print(", ");
//			      }
//			      System.out.println("]");
//		   }
//		   
		   //return new Itemsets("EMPTY TEST");
		   //}
		   // ENDDEBUG
		   
		     
		 // MINE TREE
		   // We create the header table for the tree based on lexicographical ordering
		   tree.createHeaderList(itemFrequencyList);
//		   System.out.println("INITIAL TREE WITH HEADER");
//		   System.out.println(tree.toString());
		   // 
		   
		   final Set<Integer> rSet = buildR(itemFrequencyList);
		   final Set<Integer> cSet = buildC(connectionTable, itemFrequencyList, rSet);
		  
		   // (5) We start to mine the RP-Tree by calling the recursive method.
		   // Initially, the prefix alpha is empty.
		   // if at least one item is not frequent
		   //if(tree.headerList.size() > 0) {
		     // initialize the buffer for storing the current itemset
		     itemsetBuffer = new int[BUFFERS_SIZE];
		     // and another buffer
		     rpNodeTempBuffer = new RPNode[BUFFERS_SIZE];
		     // recursively generate rare itemsets using the RP-tree
		     // Note: we assume that the initial RP-Tree has more than one path
		     // which should generally be the case.
		     rpgrowth(tree, itemsetBuffer, 0, batchCount, itemFrequencyList, 0, rSet, cSet);
		   //}		   

		   // close the output file if the result was saved to a file
		   if(writer != null){
		     writer.close();
		   }
		   // record the execution end time
		   endTime= System.currentTimeMillis();

		   // check the memory usage
		   MemoryLogger.getInstance().checkMemory();
		   
		   // done processing batch
		   batchCount = 0;
		   
		   Itemsets ans = patterns;

		   // return the result (if saved to memory)
		   return ans;
		 }

		 /**
		  * Mine an RP-Tree having more than one path.
		  * @param tree  the RP-tree
		  * @param prefix  the current prefix, named "alpha"
		  * @param mapSupport the frequency of items in the RP-Tree
		  * @throws IOException  exception if error writing the output file
		  */
		 private void rpgrowth(SRPTree tree, int [] prefix, int prefixLength, int prefixSupport, Map<Integer, Integer> mapSupport, int depth, Set<Integer> rSet, Set<Integer> cSet) throws IOException {

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
			   saveAllCombinationsOfPrefixPathWithRare(rpNodeTempBuffer, position, prefix, prefixLength, rSet);
		   }
		   else {
		     // For each ITEM in the header table list of the tree in reverse order.
		     for(int i = tree.headerList.size()-1; i>=0; i--){
		       // get the item
		       Integer item = tree.headerList.get(i);
		       
		       // get the item support for the FP-Tree
		       int support = mapSupport.get(item);
		                     
		       // Create Beta by concatenating prefix Alpha by adding the current item to alpha
		       prefix[prefixLength] = item;

		       // calculate the support of the new prefix beta
		       int betaSupport = (prefixSupport < support) ? prefixSupport: support;
		       
		       // save beta to the output file
		       
		       //If not the root OR support < minimum relative support; save item set
		       if ((prefixLength > 0) || (support < this.minSupportRelative))
		       		saveItemset(prefix, prefixLength+1, betaSupport, rSet);
		       
		       
		       // CONSTRUCT (CONDITIONAL PATTERN-BASE and FP-TREE) IF ITEM IS RARE or ITEM IS IN CONNECTION TABLE
		       // i.e. skip if not true
		       if(!rSet.contains(item) && !cSet.contains(item)) {
		    	   continue;
		       }

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
		         SRPTree treeBeta = new SRPTree();
		         
		         // Add each prefix path in the RP-tree.
		         for(List<RPNode> prefixPath : prefixPaths){
		           treeBeta.addPrefixPath(prefixPath, mapSupportBeta, minSupportRelative, preMinRareSupportRelative); 
		         }

		         // Mine recursively the Beta tree if the root has child(s)
		         if(treeBeta.root.childs.size() > 0){
		           // Create the header list.
		           treeBeta.createHeaderList(mapSupportBeta);
		           
		             //DEBUG
			         //if(item == 110) {
			        //	 System.out.println(treeBeta.toString());
			         //}
			         //ENDDEBUG
		        
		           // recursive call
		           rpgrowth(treeBeta, prefix, prefixLength+1, betaSupport, mapSupportBeta, depth+1, rSet, cSet);	         
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
		 private void saveAllCombinationsOfPrefixPathWithRare(RPNode[] rpNodeTempBuffer, int position,
		     int[] prefix, int prefixLength, Set<Integer> rSet) throws IOException {
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
		    	saveItemset(prefix, newPrefixLength, support, rSet);
		   }
		 }
		 
		 /**
		  * Ensure itemset: 
		  *		- itemset >= preMinRare
		  * 	- itemset < preMinFreq
		  *	- itemset contains at least 1 rare item
		  * @param itemset
		  * @param support
		  * @param rSet
		  * @return
		  */
		 public boolean validRare(int[] itemset, int support, Set<Integer> rSet) {
			 if(support < this.preMinRareSupportRelative || support >= this.minSupportRelative)
				 return false;
			 
			 for(int i = 0; i < itemset.length; i++) {
				 if( rSet.contains(itemset[i]) ) {
					 return true;
				 }
			 }
			 return false;
		 }


		 /**
		  * Write an infrequent item set that is found to the output file or
		  * keep into memory if the user prefer that the result be saved into memory.
		  */
		 private void saveItemset(int [] itemset, int itemsetLength, int support, Set<Integer> rSet) throws IOException {
			if (itemsetLength < minPatternLength) {
				return;
			}
			
		   

		   // if the result should be saved to a file
		   if(writer != null){
		     // copy the item set in the output buffer and sort items
		     System.arraycopy(itemset, 0, itemsetOutputBuffer, 0, itemsetLength);
		     Arrays.sort(itemsetOutputBuffer, 0, itemsetLength);

		     // Create a string buffer
		     StringBuilder buffer = new StringBuilder();
		     // write the items of the item set
		     for(int i=0; i< itemsetLength; i++){
		       buffer.append(itemsetOutputBuffer[i]);
		       if(i != itemsetLength-1){
		         buffer.append(' ');
		       }
		     }
		     // Then, write the support
		     buffer.append(" #SUP: ");
		     buffer.append(support);
		     // write to file and create a new line
		     writer.write(buffer.toString());
		     writer.newLine();

		   }// otherwise the result is kept into memory
		   else{
		     // create an object Itemset and add it to the set of patterns
		     // found.
		     int[] itemsetArray = new int[itemsetLength];
		     System.arraycopy(itemset, 0, itemsetArray, 0, itemsetLength);

		     // sort the itemset so that it is sorted according to lexical ordering before we show it to the user
		     Arrays.sort(itemsetArray);
		     
		     // ensure it is valid
		     if(!validRare(itemsetArray, support, rSet)){return;}
		     
		     // increase the number of item sets found for statistics purpose
		 	 itemsetCount++;
		     
		     Itemset itemsetObj = new Itemset(itemsetArray);
		     itemsetObj.setAbsoluteSupport(support);
		     patterns.addItemset(itemsetObj, itemsetLength);
		   }
		 }
		 
		 public void saveMinedItemsets(Itemsets destination, Itemsets input) {
			 List<List<Itemset>> levels = input.getLevels();
			 for(int i = 0; i < levels.size(); i++) {
				 List<Itemset> level = levels.get(i);
				 for(int j = 0; j < level.size(); j++) {
					 destination.addItemset(level.get(j), i);
				 }
			 }
		 }
		 
		 public Itemsets getTrulyRare(Itemsets potentially, double minraresupp /*, Map<Integer, Integer> freqList*/) {
			 Itemsets truly = new Itemsets("TRULY RARE ITEMSETS");
			 List<List<Itemset>> levels = potentially.getLevels();
			 for(int i = 0; i < levels.size(); i++) {
				 List<Itemset> level = levels.get(i);
				 for(int j = 0; j < level.size(); j++) {
					 Itemset cur = level.get(j);
					 
					 //for(int k = 0; k < cur.itemset.length; k++) {
						 // if has rare item
						 //if(  freqList.get(cur.itemset[k]) >= minraresupp) {
							 // and overall support valid
							 if(cur.support >= minraresupp)
								 truly.addItemset(level.get(j), i);
						 //}
					 //}
					 
				 }
			 }
			 
			 return truly;
		 }

		 /**
		  * Print statistics about the algorithm execution to System.out.
		  */
		 public void printStats() {
		   System.out.println("=============  SRP-GROWTH LANDMARK - STATS =============");
		   long temps = endTime - startTimestamp;
		   System.out.println(" Transactions count from database : " + transactionCount);
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