package ca.pfv.spmf._RarePatternMiningFiles.implementations;

import java.util.LinkedList;

/**
 * Implementation of Window for Sliding Window models.
 * 
 * @author Brett Aseltine
 */


public class Window {
	
	private LinkedList<Integer> list;
	private int maxSize;
	
	
	public Window(int maxSize) {
		this.list = new LinkedList<>();
		this.maxSize = maxSize;
	}
	
	/*
	 * Add item to window
	 * If it is full then remove oldest item
	 */
	public void add(int item) {
		if(list.size() >= maxSize) {
			list.removeLast();
		}
		list.addFirst(item);
	}
	
	public int getSum() {
		int sum = 0;
		for(int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}
		
		return sum;
	}
	
	
}
