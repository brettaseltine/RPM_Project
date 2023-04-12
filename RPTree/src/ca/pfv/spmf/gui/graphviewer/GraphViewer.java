package ca.pfv.spmf.gui.graphviewer;

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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import ca.pfv.spmf.gui.MainWindow;
import ca.pfv.spmf.gui.graphviewer.graphlayout.GraphLayoutCircle;
import ca.pfv.spmf.gui.graphviewer.graphlayout.GraphLayoutFruchtermanReingold;
import ca.pfv.spmf.gui.graphviewer.graphlayout.GraphLayoutFruchtermanReingoldGrid;
import ca.pfv.spmf.gui.graphviewer.graphlayout.GraphLayoutGrid;
import ca.pfv.spmf.gui.graphviewer.graphlayout.GraphLayoutRandom;
import ca.pfv.spmf.gui.graphviewer.graphmodel.GEdge;
import ca.pfv.spmf.gui.graphviewer.graphmodel.GGraph;
import ca.pfv.spmf.gui.graphviewer.graphmodel.GNode;

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
 * Tool to visualize subgraphs, implemented as a JFrame using Swing.
 * 
 * @author Philippe Fournier-Viger
 *
 */
public class GraphViewer extends JFrame {
	/** Serial UID */
	private static final long serialVersionUID = -9054590513003092459L;

	/** The panel to display subgraphs */
	GraphViewerPanel viewerPanel;

	/** A label to display the edge count */
	private JLabel labelEdgeCount;

	/** A label to display the node count */
	private JLabel labelNodeCount;

	/** A field for the node count */
	private JTextField fieldNodeCount;

	/** A field for the edge count */
	private JTextField fieldEdgeCount;

	/** A field to display name */
	private JTextField fieldName;

	/** label to show the number of graph */
	private JLabel labelNumberOf;


	/** The current graph that is displayed if a graph database is loaded */
	int currentGraphIndex = 0;

	/** Button to change to the previous graph */
	private JButton buttonPrevious;
	/** Button to change to the next graph */
	private JButton buttonNext;

	/** Label to display the support of the current subgraph */
	private JLabel labelSupport;
	/** Field for the support */
	private JTextField fieldSupport;

	/** Boolean indicating if the current graphs have support values or not */
	private boolean hasSupportValues;

	/** A JLabel to display the current graph name */
	private JLabel labelGraphName;

	/** Combox for graph layout */
	private JComboBox comboBoxGraphLayout;
	
	/** Keep string representation of input file in memory (true or false */
	private boolean SHOW_STRING_REPRESENTATION_OF_FILE = true;
	
	/** The current graph database */
	private List<GGraph> graphDatabase;

	/** The current graph database */
	private List<String> graphStringRepresentations;
	private JScrollPane scrollPaneStrings;
	private JTextPane textPaneStrings;

	/**
	 * Constructor
	 * 
	 * @param runAsStandalone true if run as standalone program, otherwise false.
	 */
	public GraphViewer(boolean runAsStandalone, boolean displayGraphStringRepresentation) {
		
		this.SHOW_STRING_REPRESENTATION_OF_FILE = displayGraphStringRepresentation;
		
		// Set the icon
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/ca/pfv/spmf/gui/spmf.png")));
		setSize(800, 625);

		// Set the window in the center of the screen
		this.setLocationRelativeTo(null);

		// Set the title
		this.setTitle("SPMF Graph Viewer");

		// Set the size
		this.setMinimumSize(new Dimension(800, 300));

		// If running as standalone
		if (runAsStandalone) {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		// Set the layout
		getContentPane().setLayout(new BorderLayout(0, 0));

		// Create the tools panel
		JPanel toolsPanel = new JPanel();
		toolsPanel.setLayout(null);
		toolsPanel.setMinimumSize(new Dimension(800, 100));
		toolsPanel.setPreferredSize(new Dimension(800, 100));
		toolsPanel.setMaximumSize(new Dimension(800, 100));
		getContentPane().add(toolsPanel, BorderLayout.SOUTH);

		buttonPrevious = new JButton("<");
		buttonPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayPreviousGraph();
			}
		});
		buttonPrevious.setEnabled(false);
		buttonPrevious.setToolTipText("Redraw");
		buttonPrevious.setBounds(10, 28, 44, 29);
		toolsPanel.add(buttonPrevious);

		buttonNext = new JButton(">");
		buttonNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayNextGraph();
			}
		});
		buttonNext.setEnabled(false);
		buttonNext.setToolTipText("Redraw");
		buttonNext.setBounds(58, 28, 44, 29);
		toolsPanel.add(buttonNext);

		// Label for edge count
		labelEdgeCount = new JLabel("Edge count:");
		labelEdgeCount.setBounds(215, 39, 104, 14);
		toolsPanel.add(labelEdgeCount);

		// Label for node count
		labelNodeCount = new JLabel("Node count:");
		labelNodeCount.setBounds(215, 11, 104, 14);
		toolsPanel.add(labelNodeCount);

		labelGraphName = new JLabel("Graph ID:");
		labelGraphName.setBounds(133, 11, 104, 14);
		toolsPanel.add(labelGraphName);

		labelNumberOf = new JLabel("Graph 1 of 1");
		labelNumberOf.setBounds(10, 11, 113, 14);
		toolsPanel.add(labelNumberOf);

		fieldNodeCount = new JTextField();
		fieldNodeCount.setEditable(false);
		fieldNodeCount.setBounds(292, 8, 104, 20);
		toolsPanel.add(fieldNodeCount);
		fieldNodeCount.setColumns(10);

		fieldEdgeCount = new JTextField();
		fieldEdgeCount.setEditable(false);
		fieldEdgeCount.setColumns(10);
		fieldEdgeCount.setBounds(292, 36, 104, 20);
		toolsPanel.add(fieldEdgeCount);

		fieldName = new JTextField();
		fieldName.setEditable(false);
		fieldName.setColumns(10);
		fieldName.setBounds(133, 32, 66, 20);
		toolsPanel.add(fieldName);

		labelSupport = new JLabel("Support:");
		labelSupport.setBounds(235, 67, 104, 14);
		toolsPanel.add(labelSupport);

		fieldSupport = new JTextField();
		fieldSupport.setEditable(false);
		fieldSupport.setColumns(10);
		fieldSupport.setBounds(292, 64, 104, 20);
		toolsPanel.add(fieldSupport);

		JLabel layoutLabel = new JLabel("Layout:");
		layoutLabel.setBounds(436, 14, 46, 14);
		toolsPanel.add(layoutLabel);

		// Load the list of graph generator layout models
		String[] layoutAlgorithms = new String[] { "FruchtermanReingold91", "FruchtermanReingold91(grid)", "Grid", "Circle", "Random" };
		comboBoxGraphLayout = new JComboBox(layoutAlgorithms);
		comboBoxGraphLayout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLayout();

			}

		});
		comboBoxGraphLayout.setBounds(484, 10, 200, 22);
		toolsPanel.add(comboBoxGraphLayout);

		JButton btnNewButton = new JButton("Resize canvas");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseCanvasSize();
			}
		});
		btnNewButton.setBounds(483, 36, 142, 23);
		toolsPanel.add(btnNewButton);
		
		JButton buttonSaveAsPng = new JButton("Save as PNG");
		buttonSaveAsPng.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewerPanel.export();
			}
		});
		buttonSaveAsPng.setIcon(null);
		buttonSaveAsPng.setBounds(635, 36, 142, 23);
		toolsPanel.add(buttonSaveAsPng);

		// Panel to view the subgraph with scrollbars (JScrollpane)
		viewerPanel = new GraphViewerPanel(new GraphLayoutFruchtermanReingold(), 680, 478);
		viewerPanel.setPreferredSize(new Dimension(800, 900));
		JScrollPane scrollPane = new JScrollPane(viewerPanel);

		scrollPane.setPreferredSize(new Dimension(300, 250));
//		scrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.blue));
		scrollPane.setAutoscrolls(true);
		viewerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// Set the window visible
		setVisible(true);

		// Set the size of the panel (!! Important otherwise, no scrollbars!!)
		viewerPanel.setPreferredSize(new Dimension(viewerPanel.getWidth(), viewerPanel.getHeight()));
		

		if(SHOW_STRING_REPRESENTATION_OF_FILE) {
			textPaneStrings = new JTextPane();
			textPaneStrings.setEditable(false);
			textPaneStrings.setEnabled(true);
			scrollPaneStrings = new JScrollPane(textPaneStrings);
			scrollPaneStrings.setAutoscrolls(true);
			scrollPaneStrings.setPreferredSize(new Dimension(100,100));
			getContentPane().add(scrollPaneStrings, BorderLayout.EAST);
		}
	}

	/**
	 * Ask the user to choose a canvas size
	 */
	protected void chooseCanvasSize() {
		int currentWidth = viewerPanel.getWidth();
		int currentHeight = viewerPanel.getHeight();

		// Width
		String s = (String) JOptionPane.showInputDialog(this, "Choose width:", "Resize dialog",
				JOptionPane.QUESTION_MESSAGE, null, null, currentWidth);

		if (s == null || s.length() == 0) {
			return;
		}

		int newWidth = 0;
		try {
			newWidth = Integer.parseInt(s);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Width must be a positive integer number.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (newWidth < 1) {
			JOptionPane.showMessageDialog(this, "Width must be a positive integer number.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Height
		s = (String) JOptionPane.showInputDialog(this, "Choose height:", "Resize dialog",
				JOptionPane.QUESTION_MESSAGE, null, null, currentHeight);

		if (s == null || s.length() == 0) {
			return;
		}

		int newHeight = 0;
		try {
			newHeight = Integer.parseInt(s);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Height must be a positive integer number.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (newHeight < 1) {
			JOptionPane.showMessageDialog(this, "Height must be a positive integer number.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		viewerPanel.updateSize(newWidth, newHeight);
	}

	/**
	 * Check the graph layout after it is selected in the combo box
	 */
	private void changeLayout() {
		int selectedIndex = comboBoxGraphLayout.getSelectedIndex();
		if (selectedIndex == 0) {
			viewerPanel.setGraphLayoutGenerator(new GraphLayoutFruchtermanReingold());
		}else if (selectedIndex == 1) {
			viewerPanel.setGraphLayoutGenerator(new GraphLayoutFruchtermanReingoldGrid());
		}  else if (selectedIndex == 2) {
			viewerPanel.setGraphLayoutGenerator(new GraphLayoutGrid());
		} else if (selectedIndex == 3) {
			viewerPanel.setGraphLayoutGenerator(new GraphLayoutCircle());
		}else if (selectedIndex == 4) {
			viewerPanel.setGraphLayoutGenerator(new GraphLayoutRandom());
		}
		viewerPanel.autoLayout();
		viewerPanel.repaint();
		
		//String[] layoutAlgorithms = new String[] { "FruchtermanReingold91", "FruchtermanReingold91(faster)", "Grid", "Circle", "Random" };
	}

	/**
	 * Load a sambple graph for debugging
	 */
	public void loadSampleGraph() {

		GNode[] nodes = new GNode[] { new GNode("Paul"), new GNode("Jack"), new GNode("Katie"), new GNode("Paolo"),
				new GNode("Usman") };
		for (GNode node : nodes) {
			viewerPanel.addNode(node);
		}

		GEdge edge1 = new GEdge(nodes[0], nodes[1], "friend", true);
		GEdge edge2 = new GEdge(nodes[1], nodes[2], "roommate", false);
		GEdge edge3 = new GEdge(nodes[0], nodes[3], "friend", true);
		GEdge edge4 = new GEdge(nodes[2], nodes[3], "friend", true);
		GEdge edge5 = new GEdge(nodes[4], nodes[3], "friend", true);
		GEdge edge6 = new GEdge(nodes[4], nodes[0], "friend", true);

		viewerPanel.addEdge(edge1);
		viewerPanel.addEdge(edge2);
		viewerPanel.addEdge(edge3);
		viewerPanel.addEdge(edge4);
		viewerPanel.addEdge(edge5);
		viewerPanel.addEdge(edge6);

		for (int i = 0; i < 23; i++) {
			GNode nodeA = new GNode("Node " + i);
			viewerPanel.addNode(nodeA);
			viewerPanel.addEdge(new GEdge(nodeA, nodes[i % 5], "", false));
		}

		// **********************************
		// Do the layout
		viewerPanel.autoLayout();
		// Update edge and vertex count
		fieldNodeCount.setText("" + viewerPanel.getNodeCount());
		fieldEdgeCount.setText("" + viewerPanel.getEdgeCount());
		labelNumberOf.setVisible(false);
		buttonPrevious.setVisible(false);
		buttonNext.setVisible(false);
		labelSupport.setVisible(false);
		fieldSupport.setVisible(false);
		labelGraphName.setVisible(false);
		fieldName.setVisible(false);
		// ****************************************
	}

	/**
	 * Read graph from an input file in gSpan format
	 * 
	 * @param path the input file
	 * @return a list of input graph from the input graph database
	 * @throws IOException if error reading or writing to file
	 */
	public void loadFileGSPANFormat(String path) throws IOException {
		hasSupportValues = false;

		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		graphDatabase = new ArrayList<>();

		String line = br.readLine();
		Boolean hasNextGraph = (line != null) && line.startsWith("t");
		
		StringBuffer linesOfCurrentGraph = null;
		if(SHOW_STRING_REPRESENTATION_OF_FILE) {
			linesOfCurrentGraph = new StringBuffer();
			graphStringRepresentations = new ArrayList<>();
		}

		// For each graph of the graph database
		while (hasNextGraph) {

			String split[] = line.split(" ");
			int gId = Integer.parseInt(split[2]);
			GGraph currentGraph = new GGraph("" + gId);

			if (split.length == 5) {
				currentGraph.setSupport(Integer.parseInt(split[4]));
				hasSupportValues = true;
			}
			hasNextGraph = false;
			Map<Integer, GNode> vMap = new HashMap<>();
			while ((line = br.readLine()) != null && !line.startsWith("t")) {
				if(line.length() == 0) {
					continue;
				}
				if(SHOW_STRING_REPRESENTATION_OF_FILE) {
					linesOfCurrentGraph.append(line);
					linesOfCurrentGraph.append(System.lineSeparator());
				}

				String[] items = line.split(" ");

				if (line.startsWith("v")) {
					// If it is a vertex
					int vId = Integer.parseInt(items[1]);
					int vLabel = Integer.parseInt(items[2]);
					GNode node = new GNode("" + vId + ": " + vLabel);
					vMap.put(vId, node);
					currentGraph.getNodes().add(node);
				} else if (line.startsWith("e")) {
					// If it is an edge
					int v1 = Integer.parseInt(items[1]);
					int v2 = Integer.parseInt(items[2]);
					int eLabel = Integer.parseInt(items[3]);
					GNode node1 = vMap.get(v1);
					GNode node2 = vMap.get(v2);
					GEdge edge = new GEdge(node1, node2, "" + eLabel, false);
					currentGraph.getEdges().add(edge);
				}
			}
			graphDatabase.add(currentGraph);
			if(SHOW_STRING_REPRESENTATION_OF_FILE) {
				graphStringRepresentations.add(linesOfCurrentGraph.toString());
				linesOfCurrentGraph.setLength(0);
			}
			if (line != null) {
				hasNextGraph = true;
			}
		}

		br.close();

		setTitle("SPMF Subgraph Viewer    -    File: " + file.getName());

		// Load the first graph
		if (graphDatabase.get(0).getNodes().size() != 0) {
			currentGraphIndex = 0;
			displayCurrentGraphFromGraphDatabase();
		}
		
//		for(String string : graphStringRepresentations) {
//			System.out.println("============================");
//			System.out.println(string);
//		}
	}

	/**
	 * Display the currently selected graph from the graph database.
	 */
	private void displayCurrentGraphFromGraphDatabase() {
		viewerPanel.clear();

		GGraph graph = graphDatabase.get(currentGraphIndex);

		for (GNode node : graph.getNodes()) {
			viewerPanel.addNode(node);
		}

		for (GEdge edge : graph.getEdges()) {
			viewerPanel.addEdge(edge);
		}
		// Do the layout
		viewerPanel.autoLayout();
		// Update edge and vertex count
		fieldName.setText("" + graph.getName());
		labelNumberOf.setText("Graph " + (currentGraphIndex + 1) + " of " + graphDatabase.size());
		fieldNodeCount.setText("" + viewerPanel.getNodeCount());
		fieldEdgeCount.setText("" + viewerPanel.getEdgeCount());

		if (currentGraphIndex == 0) {
			buttonPrevious.setEnabled(false);
		} else {
			buttonPrevious.setEnabled(true);
		}

		if (currentGraphIndex == graphDatabase.size() - 1) {
			buttonNext.setEnabled(false);
		} else {
			buttonNext.setEnabled(true);
		}

		labelSupport.setVisible(hasSupportValues);
		fieldSupport.setVisible(hasSupportValues);
		if (hasSupportValues) {
			fieldSupport.setText("" + graph.getSupport());
		}
		
		if(SHOW_STRING_REPRESENTATION_OF_FILE && textPaneStrings !=null) {
			textPaneStrings.setText(graphStringRepresentations.get(currentGraphIndex));
		}

		viewerPanel.repaint();
	}

	/**
	 * Display the previous graph (if there are many) and assuming there is a
	 * previous one.
	 */
	protected void displayPreviousGraph() {
		currentGraphIndex--;
		displayCurrentGraphFromGraphDatabase();
	}

	/**
	 * Display the next graph (if there are many) and assuming there is a next one.
	 */
	protected void displayNextGraph() {
		currentGraphIndex++;
		displayCurrentGraphFromGraphDatabase();
	}
}