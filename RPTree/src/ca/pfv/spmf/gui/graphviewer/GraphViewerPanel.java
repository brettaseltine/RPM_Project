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
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputListener;

import ca.pfv.spmf.gui.PreferencesManager;
import ca.pfv.spmf.gui.clusterviewer.ClusterViewerPanel;
import ca.pfv.spmf.gui.graphviewer.graphlayout.AbstractGraphLayout;
import ca.pfv.spmf.gui.graphviewer.graphlayout.GraphLayoutFruchtermanReingold;
import ca.pfv.spmf.gui.graphviewer.graphmodel.GEdge;
import ca.pfv.spmf.gui.graphviewer.graphmodel.GNode;
import ca.pfv.spmf.test.MainTestApriori_saveToFile;

/**
 * A special type of JPanel to visualize subgraphs.
 * 
 * @author Philippe Fournier-Viger
 *
 */
public class GraphViewerPanel extends JPanel implements MouseInputListener// , Scrollable
{
	/** Serial UID */
	private static final long serialVersionUID = -9054590513003092459L;

	/** Graph layout class */
	private AbstractGraphLayout graphLayoutGenerator;

	/** The list of nodes */
	private List<GNode> nodes;

	/** The list of edges */
	private List<GEdge> edges;

	/** The node that is currently dragged by the mouse */
	private GNode currentlyDraggedNode = null;

	/** Maximum allowed value of X for nodes */
	int maxX;

	/** Maximum allowed value of Y for nodes */
	int maxY;

	/** This indicates the level of zoom */
	double scaleLevel = 1.0;

	/** width */
	int width;
	
	/** height */
	int height;
	
	/** Highlight color for nodes */
	Color NODE_HIGHLIGHT_COLOR = Color.YELLOW;
	
	/** Color for nodes */
	Color NODE_COLOR = new Color(235, 235, 235);
	
	/** Color for edges */
	Color EDGE_COLOR = Color.BLUE;
	
	/** Color for node labels */
	Color NODE_LABEL_COLOR = Color.BLACK;
	
	/** Color for node borders */
	Color NODE_BORDER_COLOR = Color.BLACK;
	
	/** Background color */
	Color BACKGROUND_COLOR = Color.WHITE;

	/**
	 * Constructor
	 */
	public GraphViewerPanel(AbstractGraphLayout graphLayoutGenerator, int i, int j) {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resized();
			}
		});

		width = i;
		height = j;

		nodes = new ArrayList<GNode>();
		edges = new ArrayList<GEdge>();

		this.graphLayoutGenerator = graphLayoutGenerator;

		addMouseMotionListener(this);
		addMouseListener(this);

		setBackground(BACKGROUND_COLOR);
		resized();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Change the graph layout generator for this panel
	 * 
	 * @param graphLayoutGenerator a new graph layout generator
	 */
	public void setGraphLayoutGenerator(AbstractGraphLayout graphLayoutGenerator) {
		this.graphLayoutGenerator = graphLayoutGenerator;
	}

	/**
	 * Method that is called when the JPanel is resized to recalculate some values
	 * that depends on the size.
	 */
	protected void resized() {
		maxX = getWidth() - GNode.RADIUS;
		maxY = getHeight() - GNode.RADIUS;
		setPreferredSize(new Dimension(width, height));
		revalidate();
		repaint();
	}

	/**
	 * Add an edge
	 * 
	 * @param newEdge a new edge
	 */
	public void addEdge(GEdge newEdge) {
		edges.add(newEdge);
	}

	/**
	 * Add a node
	 * 
	 * @param newNode a new node
	 */
	public void addNode(GNode newNode) {
		nodes.add(newNode);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g; // cast g to Graphics2D

		// Draw the plot
		Image image = drawTheVisual();
		g2.drawImage(image, 0, 0, this);
	}

	/**
	 * Draw the image for this panel
	 * 
	 * @return an Image object
	 */
	private Image drawTheVisual() {
		int width = getWidth();
		int height = getHeight();
		Image image = createImage(width, height);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
//		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setColor(BACKGROUND_COLOR);
		g2.fillRect(0, 0, width, height);

		// Draw edges
		g2.setColor(EDGE_COLOR);
		for (GEdge edge : edges) {
			drawEdge(g2, edge.getFromNode().getCenterX(), edge.getFromNode().getCenterY(),
					edge.getToNode().getCenterX(), edge.getToNode().getCenterY(), edge.isDirected(), edge.getName());
		}

		// Draw nodes
		for (GNode node : nodes) {
			drawNode(g2, node);
		}

		return image;
	}

	/**
	 * Draw a node
	 * 
	 * @param g    the Graphics object
	 * @param node a node
	 */
	private void drawNode(Graphics g, GNode node) {

		if (node.isHighlighted()) {
			g.setColor(NODE_HIGHLIGHT_COLOR);
		} else {
			g.setColor(NODE_COLOR);
		}

		// Draw selection rectangle
//		g.drawRect(node.topLeftX, node.topLeftY, GNode.DIAMETER, GNode.DIAMETER);

		// Draw the vertex shape
		g.fillOval(node.getTopLeftX(), node.getTopLeftY(), GNode.DIAMETER, GNode.DIAMETER);
		g.setColor(NODE_BORDER_COLOR);
		g.drawOval(node.getTopLeftX(), node.getTopLeftY(), GNode.DIAMETER, GNode.DIAMETER);

		// Draw the vertex name
		/** Font metrics */
		int stringWidth = g.getFontMetrics().stringWidth(node.getName());
		int stringHeight = g.getFontMetrics().getHeight();

		int xlabel = node.getCenterX() - stringWidth / 2;
		int ylabel = node.getCenterY() + stringHeight /2;
//		if(node.isHighlighted()) {
//			g.setColor(Color.YELLOW);
//		}else {
//			g.setColor(new Color(235, 235, 235));
//		}
//		g.fillRect(xlabel, ylabel - stringHeight, stringWidth, stringHeight);
		g.setColor(NODE_LABEL_COLOR);
		g.drawString(node.getName(), xlabel, ylabel);
		
	}

	/**
	 * Draw an edge
	 * 
	 * @param g          Graphics2D object
	 * @param x1         position x where arrow starts
	 * @param y1         position y where arrow starts
	 * @param x2         position x where arrow ends
	 * @param y2         position y where arrow ends
	 * @param isDirected boolean indicating if the graph is directed
	 * @param edgeName   the name of the edge
	 */
	private void drawEdge(Graphics2D g, final int x1, final int y1, final int x2, final int y2, boolean isDirected,
			String edgeName) {

		// Get the current transform
		java.awt.geom.AffineTransform currentTransform = g.getTransform();

		// We prepare a transform to draw the arrow head
		double distanceX = x2 - x1;
		double distanceY = y2 - y1;

		double newAngle = Math.atan2(distanceY, distanceX);

		AffineTransform newTransform = AffineTransform.getTranslateInstance(x1, y1);
		newTransform.concatenate(AffineTransform.getRotateInstance(newAngle));

		// Do the transformation
		g.transform(newTransform);

		g.setColor(EDGE_COLOR);

		// Draw the line
		int arrowLength = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY) - (GNode.RADIUS);
		g.drawLine(0 + GNode.RADIUS, 0, (int) arrowLength, 0);

		// Draw the arrow from position (0, 0)
		if (isDirected) {
			g.fillPolygon(new int[] { arrowLength, arrowLength - GEdge.ARROW_HEAD_SIZE,
					arrowLength - GEdge.ARROW_HEAD_SIZE, arrowLength },
					new int[] { 0, -GEdge.ARROW_HEAD_SIZE, GEdge.ARROW_HEAD_SIZE, 0 }, 4);
		}

		// We finished drawing the arrow to we restore the previous transform
		g.setTransform(currentTransform);

		// Draw the edge label
		if (edgeName.isEmpty() == false) {
//			g.setColor(Color.RED);
			int stringWidth = g.getFontMetrics().stringWidth(edgeName);
			int xlabel = (x1 + x2) / 2 - stringWidth / 2;
			int ylabel = (y1 + y2) / 2 + g.getFontMetrics().getHeight() / 2;
			g.setColor(BACKGROUND_COLOR);
			g.fillRect(xlabel, ylabel - (g.getFontMetrics().getHeight()), stringWidth, g.getFontMetrics().getHeight());
			g.setColor(EDGE_COLOR);
			g.drawString(edgeName, xlabel, ylabel);
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point mousePosition = this.getMousePosition();

		if (mousePosition != null) {
			// find the node that is contained
			for (int i = nodes.size() - 1; i >= 0; i--) {
				GNode node = nodes.get(i);
				if (node.contains(mousePosition.x, mousePosition.y)) {
					currentlyDraggedNode = node;
					return;
				}
			}
		}
		currentlyDraggedNode = null;

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

//    public void mouseDragged(MouseEvent e) {
//
//    }
	@Override
	public void mouseDragged(MouseEvent e) {

		// The user is dragging us, so scroll!
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		scrollRectToVisible(r);

		Point mousePosition = this.getMousePosition();
		if (mousePosition != null && currentlyDraggedNode != null) {
			int newX = mousePosition.x;
			if (newX < GNode.RADIUS) {
				newX = GNode.RADIUS;
			} else if (newX > maxX) {
				newX = maxX;
			}

			int newY = mousePosition.y;
			if (newY < GNode.RADIUS) {
				newY = GNode.RADIUS;
			} else if (newY > maxY) {
				newY = maxY;
			}
			currentlyDraggedNode.updatePosition(newX, newY);
		}
		repaint();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point mousePosition = this.getMousePosition();
		if (mousePosition == null) {
			return;
		}

		// Find the node that the cursor is over it
		for (int i = nodes.size() - 1; i >= 0; i--) {
			GNode node = nodes.get(i);
			if (node.contains(mousePosition.x, mousePosition.y)) {
				node.setHighlight(true);
			} else {
				node.setHighlight(false);
			}
		}

		GraphViewerPanel.this.repaint();
	}

	/**
	 * Perform automatic layout of graph nodes.
	 */
	public void autoLayout() {
		if (nodes.size() == 0) {
			return;
		}
		graphLayoutGenerator.autoLayout(edges, nodes, getWidth(), getHeight());
	}

	/**
	 * Get edge count
	 * 
	 * @return the count
	 */
	public int getEdgeCount() {
		return edges.size();
	}

	/**
	 * Get node count
	 * 
	 * @return the count
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	@Override
	public void update(Graphics g) {
		super.update(g);

		paintComponent(g);
	}

	/**
	 * Remove all edges and nodes.
	 */
	public void clear() {
		edges.clear();
		nodes.clear();
	}

//	@Override
	public Dimension getPreferredSize() {
		if (width == 0) {
			return super.getPreferredSize();
		} else {
			return new Dimension(width, height);
		}
	}

	/**
	 * Update the size of this panel
	 * @param newWidth the new width
	 * @param newHeight the new height
	 */
	public void updateSize(int newWidth, int newHeight) {
		width = newWidth;
		height = newHeight;
		resized();
	}



	/**
	 * This method is called when the user click on the button to export the current plot to a file
	 * @throws IOException if an error occurs
	 */
	protected void export() {
		
		// ask the user to choose the filename and path
		String outputFilePath = null;
		try {
		    File path;
		    // Get the last path used by the user, if there is one
		    String previousPath = PreferencesManager.getInstance().getOutputFilePath();
		    // If there is no previous path (first time user), 
		    // show the files in the "examples" package of
		    // the spmf distribution.
		    if (previousPath == null) {
		        URL main = MainTestApriori_saveToFile.class.getResource("MainTestApriori_saveToFile.class");
		        if (!"file".equalsIgnoreCase(main.getProtocol())) {
		            path = null;
		        } else {
		            path = new File(main.getPath());
		        }
		    } else {
		        // Otherwise, use the last path used by the user.
		        path = new File(previousPath);
		    }

		    // ASK THE USER TO CHOOSE A FILE
		    final JFileChooser fc;
		    if (path != null) {
		        fc = new JFileChooser(path.getAbsolutePath());
		    } else {
		        fc = new JFileChooser();
		    }
		    int returnVal = fc.showSaveDialog(GraphViewerPanel.this);

		    // If the user chose a file
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		        File file = fc.getSelectedFile();
		        outputFilePath = file.getPath(); // save the file path
		        // save the path of this folder for next time.
		        if (fc.getSelectedFile() != null) {
		            PreferencesManager.getInstance().setOutputFilePath(fc.getSelectedFile().getParent());
		        }
		    }else{
		    	// the user did not choose so we return
		    	return;
		    }

		} catch (Exception e) {
		    JOptionPane.showMessageDialog(null,
		            "An error occured while opening the save plot dialog. ERROR MESSAGE = " + e.toString(), "Error",
		            JOptionPane.ERROR_MESSAGE);
		}

		try{
			
			// add the .png extension
			if(outputFilePath.endsWith("png") == false){
				outputFilePath = outputFilePath + ".png";
			}
			File outputFile = new File(outputFilePath);
			BufferedImage image = (BufferedImage)drawTheVisual();
			ImageIO.write(image, "png", outputFile);

		}catch (Exception e) {
		    JOptionPane.showMessageDialog(null,
		            "An error occured while attempting to save the plot. ERROR MESSAGE = " + e.toString(), "Error",
		            JOptionPane.ERROR_MESSAGE);
		}
	}
}