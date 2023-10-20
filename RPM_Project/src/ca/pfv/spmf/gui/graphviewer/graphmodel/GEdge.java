package ca.pfv.spmf.gui.graphviewer.graphmodel;

import ca.pfv.spmf.gui.graphviewer.GraphViewerPanel;

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
 * This class represents an edge as used by the GraphViewerPanel
 * 
 * @author Philippe Fournier-Viger
 * @see GraphViewerPanel
 */
public class GEdge extends GraphElement {
	/** A node */
	private final GNode fromNode;
	/** Another node */
	private final GNode toNode;
	/** If this edge is directed */
	private final boolean directed;

	/** Size of an arrow head */
	public final static int ARROW_HEAD_SIZE = 10;

	/**
	 * Constructor
	 * 
	 * @param fromNode node
	 * @param toNode   second node
	 * @param name     name of the edge if any
	 * @param directed true if directed, otherwise false
	 */
	public GEdge(final GNode fromNode, final GNode toNode, final String name, final boolean directed) {
		super(name);
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.directed = directed;
	}

	/**
	 * Get the "from" node
	 * 
	 * @return the from node
	 */
	public GNode getFromNode() {
		return fromNode;
	}

	/**
	 * Get the "to" node
	 * 
	 * @return the node
	 */
	public GNode getToNode() {
		return toNode;
	}

	/**
	 * Check if this edge is directed
	 * 
	 * @return true if directed, otherwise false
	 */
	public boolean isDirected() {
		return directed;
	}

}