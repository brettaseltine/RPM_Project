package ca.pfv.spmf.gui.algorithmexplorer;
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
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComponentInputMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ca.pfv.spmf.algorithmmanager.AlgorithmManager;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.gui.MainWindow;

/**
 * JTree that displays the algorithms offered in SPMF
 * 
 * @author Philippe Fournier-Viger
 */
public class AlgorithmExplorer extends JFrame {
	/** Serial UID */
	private static final long serialVersionUID = 6208435839510050052L;

	private MainTestAlgorithmExplorer data = new MainTestAlgorithmExplorer();
	private JTextField fieldName;
	private JTextField fieldAuthors;
	private JTextField fieldCategory;
	private JTextField fieldDoc;

	DefaultListModel<String> listInputModel = new DefaultListModel();
	DefaultListModel<String> listOutputModel = new DefaultListModel();
	DefaultTableModel listParametersModel;

	private JButton buttonWeb;
	private JTable tableParameters;

	private AlgorithmJTree treePanel;

	JButton buttonRemoveHighlight;

	JButton buttonAddHighlightWithoutTheParams;
	JButton buttonAddHighlightWithParams;

	public AlgorithmExplorer(boolean runAsStandalone) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/ca/pfv/spmf/gui/spmf.png")));

		treePanel = new AlgorithmJTree(true, true, true);
//		treePanel.setSize(new java.awt.Dimension(200,200));
//		getContentPane().add(treePanel);
		getContentPane().setLayout(null);

		JScrollPane scroll = new JScrollPane(treePanel);
		getContentPane().add(scroll);

		scroll.setBounds(20, 45, 276, 649);

		JLabel lblNewLabel;
		int algorithmCount = 0;
		try {
			algorithmCount = AlgorithmManager.getInstance().getListOfAlgorithmsAsString(true, true, true).size();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		lblNewLabel = new JLabel("Choose an algorithm (" + algorithmCount + "):");

		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(10, 20, 278, 14);
		getContentPane().add(lblNewLabel);

		JLabel labelName = new JLabel("Name");
		labelName.setBounds(307, 70, 46, 14);
		getContentPane().add(labelName);

		fieldName = new JTextField();
		fieldName.setHorizontalAlignment(SwingConstants.LEFT);
		fieldName.setEditable(false);
		fieldName.setBounds(395, 67, 380, 20);
		getContentPane().add(fieldName);
		fieldName.setColumns(10);

		JLabel labelAuthor = new JLabel("Coded by:");
		labelAuthor.setBounds(307, 126, 75, 14);
		getContentPane().add(labelAuthor);

		fieldAuthors = new JTextField();
		fieldAuthors.setHorizontalAlignment(SwingConstants.LEFT);
		fieldAuthors.setEditable(false);
		fieldAuthors.setColumns(10);
		fieldAuthors.setBounds(395, 123, 380, 20);
		getContentPane().add(fieldAuthors);

		JLabel labelCategory = new JLabel("Category:");
		labelCategory.setBounds(307, 101, 58, 14);
		getContentPane().add(labelCategory);

		fieldCategory = new JTextField();
		fieldCategory.setHorizontalAlignment(SwingConstants.LEFT);
		fieldCategory.setEditable(false);
		fieldCategory.setColumns(10);
		fieldCategory.setBounds(395, 95, 380, 20);
		getContentPane().add(fieldCategory);

		JLabel lblNewLabel_1 = new JLabel("Algorithm information");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(306, 45, 469, 14);
		getContentPane().add(lblNewLabel_1);

		JLabel labelDoc = new JLabel("Example:");
		labelDoc.setBounds(307, 154, 100, 14);
		getContentPane().add(labelDoc);

		fieldDoc = new JTextField();
		fieldDoc.setHorizontalAlignment(SwingConstants.LEFT);
		fieldDoc.setEditable(false);
		fieldDoc.setColumns(10);
		fieldDoc.setBounds(395, 151, 300, 20);
		getContentPane().add(fieldDoc);

		buttonWeb = new JButton("Open");
		buttonWeb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openWebPage(fieldDoc.getText());
			}
		});
		buttonWeb.setBounds(702, 150, 73, 23);
		getContentPane().add(buttonWeb);

		JLabel labelInput = new JLabel("Input type:");
		labelInput.setBounds(307, 191, 100, 14);
		getContentPane().add(labelInput);

		JLabel labelOutput = new JLabel("Output type:");
		labelOutput.setBounds(307, 305, 100, 14);
		getContentPane().add(labelOutput);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(395, 189, 380, 95);
		getContentPane().add(scrollPane);

		JList listInput = new JList(listInputModel);
		listInput.setBackground(getContentPane().getBackground());
		scrollPane.setViewportView(listInput);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(395, 303, 380, 95);
		getContentPane().add(scrollPane_1);

		JList listOutput = new JList(listOutputModel);
		listOutput.setBackground(getContentPane().getBackground());
		scrollPane_1.setViewportView(listOutput);

		JLabel labelParameters = new JLabel("Parameters:");
		labelParameters.setBounds(307, 410, 100, 14);
		getContentPane().add(labelParameters);

		JScrollPane scrollPane_1_1 = new JScrollPane();
		scrollPane_1_1.setBounds(395, 409, 380, 194);
		getContentPane().add(scrollPane_1_1);

		tableParameters = new JTable();
		listParametersModel = new DefaultTableModel(new Object[][] {}, new String[] { "Name", "Type", "Optional?" });
		tableParameters.setModel(listParametersModel);

		scrollPane_1_1.setViewportView(tableParameters);

		buttonAddHighlightWithoutTheParams = new JButton("Highlight algorithms with same in/out");
		buttonAddHighlightWithoutTheParams.setEnabled(false);
		buttonAddHighlightWithoutTheParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addHighlightWithoutParameters();
			}
		});
		buttonAddHighlightWithoutTheParams.setBounds(364, 614, 411, 23);
		getContentPane().add(buttonAddHighlightWithoutTheParams);

		buttonRemoveHighlight = new JButton("Remove highlight");
		buttonRemoveHighlight.setEnabled(false);
		buttonRemoveHighlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeHighlight();
			}
		});
		buttonRemoveHighlight.setBounds(365, 671, 410, 23);
		getContentPane().add(buttonRemoveHighlight);

		buttonAddHighlightWithParams = new JButton("Highlight algorithms with same in/out/mandatory parameters");
		buttonAddHighlightWithParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addHighlightWithParameters();
			}
		});
		buttonAddHighlightWithParams.setEnabled(false);
		buttonAddHighlightWithParams.setBounds(364, 643, 411, 23);
		getContentPane().add(buttonAddHighlightWithParams);
		this.setSize(800, 743);
		if (runAsStandalone) {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		this.setTitle("Algorithm Explorer");
		// Set the window in the center of the screen
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		// When the user select something in the tree, we need to load the data about
		// the algorithm
		treePanel.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treePanel.getLastSelectedPathComponent();

				boolean selectedSomething = false;

				if (selectedNode != null) {
					String algoName = selectedNode.getUserObject().toString();
//	                selectedLabel.setText(selectedNode.getUserObject().toString());

					// Get the algorithm manager
					AlgorithmManager manager = null;
					try {
						manager = AlgorithmManager.getInstance();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					DescriptionOfAlgorithm description = manager.getDescriptionOfAlgorithm(algoName);

					if (description != null) {
						// Name
						fieldName.setText(algoName);
						fieldAuthors.setText(description.getImplementationAuthorNames());
						fieldCategory.setText(description.getAlgorithmCategory());
						fieldDoc.setText(description.getURLOfDocumentation());
						buttonWeb.setEnabled(true);
						selectedSomething = true;

						listInputModel.clear();
						String[] inputTypes = description.getInputFileTypes();
						if (inputTypes != null) {
							if (inputTypes != null) {
								for (String type : inputTypes) {
									listInputModel.addElement(type);
								}
							}
							listInput.setBackground(Color.WHITE);
							listInput.setForeground(Color.BLACK);
						} else {
							listInput.setBackground(getContentPane().getBackground());
						}

						listOutputModel.clear();
						String[] outputTypes = description.getOutputFileTypes();
						if (inputTypes != null) {
							if (outputTypes != null) {
								for (String type : outputTypes) {
									listOutputModel.addElement(type);
								}
							}
							listOutput.setBackground(Color.WHITE);
							listInput.setForeground(Color.BLACK);
						} else {
							listOutput.setBackground(getContentPane().getBackground());
						}

//						description.getNumberOfMandatoryParameters();
//						description.getParametersDescription();
//						listParametersModel.);
						listParametersModel.setRowCount(0);
						DescriptionOfParameter[] parameters = description.getParametersDescription();
						if (parameters != null) {
							for (DescriptionOfParameter parameter : parameters) {
								listParametersModel.addRow(new String[] { parameter.getName(),
										parameter.getParameterType().getSimpleName(),
										Boolean.toString(parameter.isOptional()) });
							}
						}

					}

					if (treePanel.isActivatedHighlight() == false) {
						buttonAddHighlightWithoutTheParams.setEnabled(true);
						buttonAddHighlightWithParams.setEnabled(true);
						buttonRemoveHighlight.setEnabled(false);
					}
				}

				if (selectedSomething == false) {
					// Name
					fieldName.setText("");
					fieldAuthors.setText("");
					fieldCategory.setText("");
					fieldDoc.setText("");
					buttonWeb.setEnabled(false);

					listInputModel.clear();
					listParametersModel.setRowCount(0);
					listOutputModel.clear();
					buttonAddHighlightWithoutTheParams.setEnabled(false);
					buttonAddHighlightWithParams.setEnabled(false);
					buttonRemoveHighlight.setEnabled(true);

//					treePanel.setActivatedHighlight(false);
				}
			}

		});
	}

	protected void removeHighlight() {
		// TODO Auto-generated method stub
		treePanel.setActivatedHighlight(false);
		buttonAddHighlightWithoutTheParams.setEnabled(true);
		buttonAddHighlightWithParams.setEnabled(true);
		buttonRemoveHighlight.setEnabled(false);

	}

	protected void addHighlightWithParameters() {
		treePanel.highlightSimilarAlgorithmsToSelection(true);
		buttonAddHighlightWithoutTheParams.setEnabled(false);
		buttonAddHighlightWithParams.setEnabled(false);
		buttonRemoveHighlight.setEnabled(true);
	}

	protected void addHighlightWithoutParameters() {
		treePanel.highlightSimilarAlgorithmsToSelection(false);
		buttonAddHighlightWithoutTheParams.setEnabled(false);
		buttonAddHighlightWithParams.setEnabled(false);
		buttonRemoveHighlight.setEnabled(true);
	}

	/**
	 * This method open a URL in the default web browser.
	 *
	 * @param url : URL of the webpage
	 */
	private void openWebPage(String url) {
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		} catch (java.io.IOException e) {
			System.out.println(e.getMessage());
		}
	} // openWebPage("http://www.philippe-fournier-viger.com/spmf/");
}