package PROJECTS;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import training.Trainer;
import training.TrainingDataSet;
import neuralNets.ConvolutionalNeuralNetwork;
import auxiliar.PairOfFilterNumberAndProperties;
import costFunctions.CostFunctionUsingTrainingDataSet;
import fileManager.SaveLoadManager;
import filters.FilterProperties;
import graphics.ImageManager;
import graphics.TestImagePanel;

public class ImageRecognition {

	private ConvolutionalNeuralNetwork CNN;
	private SaveLoadManager saveLoadManager;
	private ImageManager imageManager;
	private TrainingDataSet trainingDataSet;
	private CostFunctionUsingTrainingDataSet costFunction;
	private Trainer trainer;
	private TestImagePanel tip;

	private Dimension mainFrameDimension, createCNNDimension, testCNNDimension,
			platonicCNNDimension, progressBarPanelDimension,
			radioButtonsPanelDimension, radioButtonsPanelDimension2,
			progressBarPanelDimension2, buttonDimension, buttonDimension2,
			buttonDimension3, filterFrameDimension;
	private int standardHeight, imagePanelHeight, selectedRB;
	private DefaultListModel<String> listModel, hiddenlistModel, listModel2;
	private JSpinner imageWidthSpinner, imageHeightSpinner;
	private JFrame mainFrame;
	private double h;
	private int pix, pix2;
	public AtomicBoolean trainORthinkBoolean;
	private String whatToDo;
	private ArrayList<JRadioButton> groupRB;
	private JLabel infoLabel;
	private JButton startStopButton, beginTrainbutton;

	public ImageRecognition() {

		initVars();

		createSwing();

		run();
	}

	private void initVars() {

		saveLoadManager = new SaveLoadManager("SavedNNs/");
		imageManager = new ImageManager();

		mainFrameDimension = new Dimension(300, 370);
		buttonDimension = new Dimension(200, 40);

		createCNNDimension = new Dimension(400, 500);
		standardHeight = 40;

		imagePanelHeight = 200;

		filterFrameDimension = new Dimension(300, 300);

		listModel = new DefaultListModel<String>();
		hiddenlistModel = new DefaultListModel<String>();
		listModel2 = new DefaultListModel<String>();

		h = 1.0;
		pix = 5;
		pix2 = 5;
		trainORthinkBoolean = new AtomicBoolean();
		trainORthinkBoolean.set(false);
		whatToDo = "nothing";
	}

	private void createSwing() {
		mainFrame = new JFrame();
		mainFrame.setSize(mainFrameDimension);
		mainFrame.setMinimumSize(mainFrameDimension);
		mainFrame.setPreferredSize(mainFrameDimension);
		mainFrame.setMaximumSize(mainFrameDimension);
		mainFrame.setLocation(200, 200);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JFrame createCNNFrame = new JFrame();
		createCNNFrame.setSize(createCNNDimension);
		createCNNFrame.setMinimumSize(createCNNDimension);
		createCNNFrame.setPreferredSize(createCNNDimension);
		createCNNFrame.setMaximumSize(createCNNDimension);
		createCNNFrame.setLocation(200, 200);
		createCNNFrame.setVisible(false);
		createCNNFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mainFrame.setVisible(true);
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setSize(mainFrameDimension);
		mainPanel.setMinimumSize(mainFrameDimension);
		mainPanel.setPreferredSize(mainFrameDimension);
		mainPanel.setMaximumSize(mainFrameDimension);
		mainPanel.setBackground(Color.white);
		mainPanel.setBorder(BorderFactory
				.createTitledBorder("Convolutional Neural Network"));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		JPanel createCNNPanel = new JPanel();
		createCNNPanel.setSize(createCNNDimension);
		createCNNPanel.setMinimumSize(createCNNDimension);
		createCNNPanel.setPreferredSize(createCNNDimension);
		createCNNPanel.setMaximumSize(createCNNDimension);
		createCNNPanel.setBackground(Color.white);
		createCNNPanel.setBorder(BorderFactory
				.createTitledBorder("Create new CNN"));
		createCNNPanel.setLayout(new FlowLayout());

		JTextField CNNnameTF = new JTextField("noname");
		Dimension CNNnameTFDim = new Dimension(createCNNDimension.width - 50
				- 2 * (int) (2.0 * standardHeight), standardHeight);
		CNNnameTF.setSize(CNNnameTFDim);
		CNNnameTF.setMinimumSize(CNNnameTFDim);
		CNNnameTF.setPreferredSize(CNNnameTFDim);
		CNNnameTF.setMaximumSize(CNNnameTFDim);
		CNNnameTF.setBorder(BorderFactory.createTitledBorder("Name"));
		CNNnameTF.setBackground(Color.white);

		SpinnerModel modelW = new SpinnerNumberModel(500, 1, 4096, 1);
		SpinnerModel modelH = new SpinnerNumberModel(500, 1, 4096, 1);
		Dimension spinnerDim = new Dimension((int) (2.0 * standardHeight),
				standardHeight);
		imageWidthSpinner = new JSpinner(modelW);
		imageWidthSpinner.setSize(spinnerDim);
		imageWidthSpinner.setMinimumSize(spinnerDim);
		imageWidthSpinner.setPreferredSize(spinnerDim);
		imageWidthSpinner.setMaximumSize(spinnerDim);
		imageWidthSpinner.setBorder(BorderFactory.createTitledBorder("Width"));
		imageWidthSpinner.setBackground(Color.white);
		imageWidthSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updatelistModel();
			}
		});
		imageHeightSpinner = new JSpinner(modelH);
		imageHeightSpinner.setSize(spinnerDim);
		imageHeightSpinner.setMinimumSize(spinnerDim);
		imageHeightSpinner.setPreferredSize(spinnerDim);
		imageHeightSpinner.setMaximumSize(spinnerDim);
		imageHeightSpinner
				.setBorder(BorderFactory.createTitledBorder("Height"));
		imageHeightSpinner.setBackground(Color.white);
		imageHeightSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updatelistModel();
			}
		});

		Dimension listDimension = new Dimension(
				(int) ((createCNNDimension.width - 50)), 3 * standardHeight);
		JList<String> list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setBorder(BorderFactory.createTitledBorder("Filters"));
		listScroller.setBackground(Color.white);
		listScroller.setSize(listDimension);
		listScroller.setMinimumSize(listDimension);
		listScroller.setPreferredSize(listDimension);
		listScroller.setMaximumSize(listDimension);

		JList<String> list2 = new JList<String>(listModel2);
		list2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list2.setVisibleRowCount(-1);
		JScrollPane listScroller2 = new JScrollPane(list2);
		listScroller2.setBorder(BorderFactory.createTitledBorder("Output"));
		listScroller2.setBackground(Color.white);
		listScroller2.setSize(listDimension);
		listScroller2.setMinimumSize(listDimension);
		listScroller2.setPreferredSize(listDimension);
		listScroller2.setMaximumSize(listDimension);

		JButton addFilterbutton = new JButton("Add");
		JButton editFilterbutton = new JButton("Edit");
		JButton removeFilterbutton = new JButton("Remove");
		JButton addOutputbutton = new JButton("Add");
		JButton editOutputbutton = new JButton("Edit");
		JButton removeOutputbutton = new JButton("Remove");

		Dimension addButtonDimension = new Dimension(
				(int) ((createCNNDimension.width - 70) / 3.0), standardHeight);

		addFilterbutton.setSize(addButtonDimension);
		addFilterbutton.setMinimumSize(addButtonDimension);
		addFilterbutton.setPreferredSize(addButtonDimension);
		addFilterbutton.setMaximumSize(addButtonDimension);
		addFilterbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSubSwing(-1);
			}
		});
		editFilterbutton.setSize(addButtonDimension);
		editFilterbutton.setMinimumSize(addButtonDimension);
		editFilterbutton.setPreferredSize(addButtonDimension);
		editFilterbutton.setMaximumSize(addButtonDimension);
		editFilterbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSubSwing(list.getSelectedIndex());
			}
		});
		removeFilterbutton.setSize(addButtonDimension);
		removeFilterbutton.setMinimumSize(addButtonDimension);
		removeFilterbutton.setPreferredSize(addButtonDimension);
		removeFilterbutton.setMaximumSize(addButtonDimension);
		removeFilterbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int idx = list.getSelectedIndex();
					listModel.remove(idx);
					hiddenlistModel.remove(idx);
				} catch (Exception ex) {

				}
			}
		});
		addOutputbutton.setSize(addButtonDimension);
		addOutputbutton.setMinimumSize(addButtonDimension);
		addOutputbutton.setPreferredSize(addButtonDimension);
		addOutputbutton.setMaximumSize(addButtonDimension);
		addOutputbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(new JFrame(),
						"Add output", "");
				if (name != null) {
					if (name.length() > 0) {
						listModel2.addElement(name);
					}
				}
			}
		});
		editOutputbutton.setSize(addButtonDimension);
		editOutputbutton.setMinimumSize(addButtonDimension);
		editOutputbutton.setPreferredSize(addButtonDimension);
		editOutputbutton.setMaximumSize(addButtonDimension);
		editOutputbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(new JFrame(),
						"Edit output",
						listModel2.getElementAt(list2.getSelectedIndex()));
				if (name.length() > 0) {
					listModel2.set(list2.getSelectedIndex(), name);
				}
			}
		});
		removeOutputbutton.setSize(addButtonDimension);
		removeOutputbutton.setMinimumSize(addButtonDimension);
		removeOutputbutton.setPreferredSize(addButtonDimension);
		removeOutputbutton.setMaximumSize(addButtonDimension);
		removeOutputbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					listModel2.remove(list2.getSelectedIndex());
				} catch (Exception ex) {

				}
			}
		});

		JButton advancedbutton = new JButton("Advanced");
		JButton okCreatebutton = new JButton("Create");
		JButton cancelCreatebutton = new JButton("Cancel");

		Dimension advancedButtonDimension = new Dimension(
				(int) ((createCNNDimension.width - 70) / 3.0), standardHeight);

		advancedbutton.setSize(advancedButtonDimension);
		advancedbutton.setMinimumSize(advancedButtonDimension);
		advancedbutton.setPreferredSize(advancedButtonDimension);
		advancedbutton.setMaximumSize(advancedButtonDimension);
		advancedbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Change outputNeuronProperties
			}
		});
		okCreatebutton.setSize(advancedButtonDimension);
		okCreatebutton.setMinimumSize(advancedButtonDimension);
		okCreatebutton.setPreferredSize(advancedButtonDimension);
		okCreatebutton.setMaximumSize(advancedButtonDimension);
		okCreatebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int imageWidth = (int) (imageWidthSpinner.getValue());
				int imageHeight = (int) (imageHeightSpinner.getValue());

				String convNetName = CNNnameTF.getText();

				// properties: [widthFilter, heightFilter, widthBorder,
				// heightBorder,
				// widthPool, heightPool]
				ArrayList<PairOfFilterNumberAndProperties> filtersData = new ArrayList<PairOfFilterNumberAndProperties>();
				for (int i = 0; i < hiddenlistModel.size(); i++) {
					String[] hiddenString = hiddenlistModel.getElementAt(i)
							.toString().split(",");
					int nOfFilters = Integer.parseInt(hiddenString[0]);
					int filterW = Integer.parseInt(hiddenString[1]);
					int filterH = Integer.parseInt(hiddenString[2]);
					int borderW = Integer.parseInt(hiddenString[3]);
					int borderH = Integer.parseInt(hiddenString[4]);
					int poolW = Integer.parseInt(hiddenString[5]);
					int poolH = Integer.parseInt(hiddenString[6]);

					filtersData.add(new PairOfFilterNumberAndProperties(
							nOfFilters, new FilterProperties(filterW, filterH,
									borderW, borderH, poolW, poolH)));
				}

				ArrayList<String> outputNames = new ArrayList<String>();
				for (int i = 0; i < listModel2.size(); i++) {
					outputNames.add(listModel2.get(i).toString());
				}

				// Create Convolutional Neural Network
				createNewCNN(convNetName, imageWidth, imageHeight, filtersData,
						outputNames);

				// CNN = new ConvolutionalNeuralNetwork(convNetName, imageWidth,
				// imageHeight, numberOfFilters, filterProperties,
				// outputNames);
				// costFunction = new CostFunction(CNN.getImageWidth(), CNN
				// .getImageHeight());

				mainFrame.setVisible(true);
				createCNNFrame.setVisible(false);
			}
		});
		cancelCreatebutton.setSize(advancedButtonDimension);
		cancelCreatebutton.setMinimumSize(advancedButtonDimension);
		cancelCreatebutton.setPreferredSize(advancedButtonDimension);
		cancelCreatebutton.setMaximumSize(advancedButtonDimension);
		cancelCreatebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.setVisible(true);
				createCNNFrame.setVisible(false);
			}
		});

		JButton newCNNbutton = new JButton("New");
		JButton loadCNNbutton = new JButton("Load");
		JButton backbutton = new JButton("Back");
		JButton backbutton2 = new JButton("Back");
		JButton testbutton = new JButton("Test");
		JButton trainbutton = new JButton("Train");
		JButton platonicbutton = new JButton("Platonic");
		JButton savetoimagebutton = new JButton("Create image");
		JButton printbutton = new JButton("Print");
		beginTrainbutton = new JButton("Begin training");

		trainbutton.setSize(buttonDimension);
		trainbutton.setMinimumSize(buttonDimension);
		trainbutton.setPreferredSize(buttonDimension);
		trainbutton.setMaximumSize(buttonDimension);
		trainbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		trainbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainPanel.setBorder(BorderFactory.createTitledBorder("Train: "
						+ CNN.getName()));

				mainPanel.removeAll();

				mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
				mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
				mainPanel.add(beginTrainbutton);
				mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
				mainPanel.add(backbutton2);

				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		beginTrainbutton.setSize(buttonDimension);
		beginTrainbutton.setMinimumSize(buttonDimension);
		beginTrainbutton.setPreferredSize(buttonDimension);
		beginTrainbutton.setMaximumSize(buttonDimension);
		beginTrainbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		beginTrainbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean currentState = trainORthinkBoolean.get();
				if (currentState) {
					beginTrainbutton.setText("Begin training");
					trainer.continueTraining.set(false);
					trainORthinkBoolean.set(false);
				} else {
					whatToDo = "Train";
					synchronized (trainORthinkBoolean) {
						beginTrainbutton.setText("Stop & Save");
						trainORthinkBoolean.set(true);
						trainORthinkBoolean.notifyAll();
					}
				}
			}
		});

		testbutton.setSize(buttonDimension);
		testbutton.setMinimumSize(buttonDimension);
		testbutton.setPreferredSize(buttonDimension);
		testbutton.setMaximumSize(buttonDimension);
		testbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		testbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTestSwing();

				mainFrame.setVisible(false);
			}
		});

		savetoimagebutton.setSize(buttonDimension);
		savetoimagebutton.setMinimumSize(buttonDimension);
		savetoimagebutton.setPreferredSize(buttonDimension);
		savetoimagebutton.setMaximumSize(buttonDimension);
		savetoimagebutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		savetoimagebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CNN.saveToImage();
			}
		});

		printbutton.setSize(buttonDimension);
		printbutton.setMinimumSize(buttonDimension);
		printbutton.setPreferredSize(buttonDimension);
		printbutton.setMaximumSize(buttonDimension);
		printbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		printbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CNN.printNN();
			}
		});

		platonicbutton.setSize(buttonDimension);
		platonicbutton.setMinimumSize(buttonDimension);
		platonicbutton.setPreferredSize(buttonDimension);
		platonicbutton.setMaximumSize(buttonDimension);
		platonicbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		platonicbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createPlatonicSwing();

				mainFrame.setVisible(false);
			}
		});

		backbutton.setSize(buttonDimension);
		backbutton.setMinimumSize(buttonDimension);
		backbutton.setPreferredSize(buttonDimension);
		backbutton.setMaximumSize(buttonDimension);
		backbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		backbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainPanel.setBorder(BorderFactory
						.createTitledBorder("Convolutional Neural Network"));
				mainPanel.removeAll();

				mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
				mainPanel.add(newCNNbutton);
				mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
				mainPanel.add(loadCNNbutton);

				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		backbutton2.setSize(buttonDimension);
		backbutton2.setMinimumSize(buttonDimension);
		backbutton2.setPreferredSize(buttonDimension);
		backbutton2.setMaximumSize(buttonDimension);
		backbutton2.setAlignmentX(Component.CENTER_ALIGNMENT);
		backbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();

				int vertical = 10;
				mainPanel.add(Box.createRigidArea(new Dimension(0, vertical)));
				mainPanel.add(trainbutton);
				mainPanel.add(Box.createRigidArea(new Dimension(0, vertical)));
				mainPanel.add(testbutton);
				mainPanel.add(Box.createRigidArea(new Dimension(0, vertical)));
				mainPanel.add(platonicbutton);
				mainPanel.add(Box.createRigidArea(new Dimension(0, vertical)));
				mainPanel.add(savetoimagebutton);
				mainPanel.add(Box.createRigidArea(new Dimension(0, vertical)));
				mainPanel.add(printbutton);
				mainPanel.add(Box.createRigidArea(new Dimension(0, vertical)));
				mainPanel.add(backbutton);

				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		newCNNbutton.setSize(buttonDimension);
		newCNNbutton.setMinimumSize(buttonDimension);
		newCNNbutton.setPreferredSize(buttonDimension);
		newCNNbutton.setMaximumSize(buttonDimension);
		newCNNbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		newCNNbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CNNnameTF.setText("noname");
				imageWidthSpinner.setValue(500);
				imageHeightSpinner.setValue(500);
				listModel.removeAllElements();
				hiddenlistModel.removeAllElements();
				listModel2.removeAllElements();

				mainFrame.setVisible(false);
				createCNNFrame.setVisible(true);
			}
		});

		loadCNNbutton.setSize(buttonDimension);
		loadCNNbutton.setMinimumSize(buttonDimension);
		loadCNNbutton.setPreferredSize(buttonDimension);
		loadCNNbutton.setMaximumSize(buttonDimension);
		loadCNNbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadCNNbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System
						.getProperty("user.dir") + "/SavedNNs"));
				int result = fileChooser.showOpenDialog(new JFrame());
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File selectedFile = fileChooser.getSelectedFile();
						loadCNN(selectedFile.getAbsolutePath());

						trainingDataSet = createTrainingData();
						costFunction = new CostFunctionUsingTrainingDataSet(
								trainingDataSet);
						trainer = new Trainer(CNN, costFunction,
								saveLoadManager);

						mainPanel.setBorder(BorderFactory
								.createTitledBorder("Convolutional Neural Network: "
										+ CNN.getName()));

						mainPanel.removeAll();

						int vertical = 10;
						mainPanel.add(Box.createRigidArea(new Dimension(0,
								vertical)));
						mainPanel.add(trainbutton);
						mainPanel.add(Box.createRigidArea(new Dimension(0,
								vertical)));
						mainPanel.add(testbutton);
						mainPanel.add(Box.createRigidArea(new Dimension(0,
								vertical)));
						mainPanel.add(platonicbutton);
						mainPanel.add(Box.createRigidArea(new Dimension(0,
								vertical)));
						mainPanel.add(savetoimagebutton);
						mainPanel.add(Box.createRigidArea(new Dimension(0,
								vertical)));
						mainPanel.add(printbutton);
						mainPanel.add(Box.createRigidArea(new Dimension(0,
								vertical)));
						mainPanel.add(backbutton);

						mainPanel.revalidate();
						mainPanel.repaint();
					} catch (Exception ex) {
						System.out.println("Could not load CNN ["
								+ fileChooser.getSelectedFile()
										.getAbsolutePath() + "]: "
								+ ex.getMessage());
						mainPanel.setBorder(BorderFactory
								.createTitledBorder("Convolutional Neural Network"));
						mainPanel.revalidate();
						mainPanel.repaint();
					}
				}
			}
		});

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(newCNNbutton);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(loadCNNbutton);

		createCNNPanel.add(CNNnameTF);
		createCNNPanel.add(imageWidthSpinner);
		createCNNPanel.add(imageHeightSpinner);
		createCNNPanel.add(listScroller);
		createCNNPanel.add(addFilterbutton);
		createCNNPanel.add(editFilterbutton);
		createCNNPanel.add(removeFilterbutton);
		createCNNPanel.add(listScroller2);
		createCNNPanel.add(addOutputbutton);
		createCNNPanel.add(editOutputbutton);
		createCNNPanel.add(removeOutputbutton);
		createCNNPanel.add(advancedbutton);
		createCNNPanel.add(okCreatebutton);
		createCNNPanel.add(cancelCreatebutton);

		mainFrame.add(mainPanel);
		createCNNFrame.add(createCNNPanel);

		mainFrame.setVisible(true);
	}

	private void updatelistModel() {
		int currentW = (int) (imageWidthSpinner.getValue());
		int currentH = (int) (imageHeightSpinner.getValue());

		for (int i = 0; i < hiddenlistModel.size(); i++) {
			String[] hiddenString = hiddenlistModel.getElementAt(i).toString()
					.split(",");
			int numberOfFilters = Integer.parseInt(hiddenString[0]);
			int filterW = Integer.parseInt(hiddenString[1]);
			int filterH = Integer.parseInt(hiddenString[2]);
			int borderW = Integer.parseInt(hiddenString[3]);
			int borderH = Integer.parseInt(hiddenString[4]);
			int poolW = Integer.parseInt(hiddenString[5]);
			int poolH = Integer.parseInt(hiddenString[6]);

			int imageWAfterBorder = currentW + 2 * borderW;
			int imageHAfterBorder = currentH + 2 * borderH;

			int imageWAfterFilter = imageWAfterBorder + 1 - filterW;
			int imageHAfterFilter = imageHAfterBorder + 1 - filterH;

			int miageWAfterPool = (int) (Math.ceil((imageWAfterFilter)
					/ (1.0 * poolW)));
			int miageHAfterPool = (int) (Math.ceil((imageHAfterFilter)
					/ (1.0 * poolH)));

			String coolString = numberOfFilters + "x [" + currentW + ","
					+ currentH + "]>[" + imageWAfterBorder + ","
					+ imageHAfterBorder + "]>[" + imageWAfterFilter + ","
					+ imageHAfterFilter + "]>[" + miageWAfterPool + ","
					+ miageHAfterPool + "]";
			listModel.set(i, coolString);

			currentW = miageWAfterPool;
			currentH = miageHAfterPool;
		}
	}

	private void createSubSwing(int selected) {

		int numberOfFilters = 10;
		int filterW = 30;
		int filterH = 30;
		int borderW = 5;
		int borderH = 5;
		int poolW = 6;
		int poolH = 6;

		if (selected >= 0) {
			String[] hiddenString = hiddenlistModel.getElementAt(selected)
					.toString().split(",");
			numberOfFilters = Integer.parseInt(hiddenString[0]);
			filterW = Integer.parseInt(hiddenString[1]);
			filterH = Integer.parseInt(hiddenString[2]);
			borderW = Integer.parseInt(hiddenString[3]);
			borderH = Integer.parseInt(hiddenString[4]);
			poolW = Integer.parseInt(hiddenString[5]);
			poolH = Integer.parseInt(hiddenString[6]);
		}

		JFrame filterFrame = new JFrame();
		filterFrame.setSize(filterFrameDimension);
		filterFrame.setMinimumSize(filterFrameDimension);
		filterFrame.setPreferredSize(filterFrameDimension);
		filterFrame.setMaximumSize(filterFrameDimension);
		filterFrame.setLocation(200, 200);

		JPanel filterPanel = new JPanel();
		filterPanel.setSize(filterFrameDimension);
		filterPanel.setMinimumSize(filterFrameDimension);
		filterPanel.setPreferredSize(filterFrameDimension);
		filterPanel.setMaximumSize(filterFrameDimension);
		filterPanel.setBackground(Color.white);
		filterPanel.setBorder(BorderFactory
				.createTitledBorder("Create new Filter"));
		filterPanel.setLayout(new FlowLayout());

		SpinnerModel model1 = new SpinnerNumberModel(filterW, 1, 2048, 1);
		Dimension spinnerDim = new Dimension((int) (3.0 * standardHeight),
				standardHeight);
		JSpinner spinner1 = new JSpinner(model1);
		spinner1.setSize(spinnerDim);
		spinner1.setMinimumSize(spinnerDim);
		spinner1.setPreferredSize(spinnerDim);
		spinner1.setMaximumSize(spinnerDim);
		spinner1.setBorder(BorderFactory.createTitledBorder("Filter Width"));
		spinner1.setBackground(Color.white);

		SpinnerModel model2 = new SpinnerNumberModel(filterH, 1, 2048, 1);
		JSpinner spinner2 = new JSpinner(model2);
		spinner2.setSize(spinnerDim);
		spinner2.setMinimumSize(spinnerDim);
		spinner2.setPreferredSize(spinnerDim);
		spinner2.setMaximumSize(spinnerDim);
		spinner2.setBorder(BorderFactory.createTitledBorder("Filter Height"));
		spinner2.setBackground(Color.white);

		SpinnerModel model3 = new SpinnerNumberModel(borderW, 0, 200, 1);
		JSpinner spinner3 = new JSpinner(model3);
		spinner3.setSize(spinnerDim);
		spinner3.setMinimumSize(spinnerDim);
		spinner3.setPreferredSize(spinnerDim);
		spinner3.setMaximumSize(spinnerDim);
		spinner3.setBorder(BorderFactory.createTitledBorder("Border Width"));
		spinner3.setBackground(Color.white);

		SpinnerModel model4 = new SpinnerNumberModel(borderH, 0, 200, 1);
		JSpinner spinner4 = new JSpinner(model4);
		spinner4.setSize(spinnerDim);
		spinner4.setMinimumSize(spinnerDim);
		spinner4.setPreferredSize(spinnerDim);
		spinner4.setMaximumSize(spinnerDim);
		spinner4.setBorder(BorderFactory.createTitledBorder("Border Height"));
		spinner4.setBackground(Color.white);

		SpinnerModel model5 = new SpinnerNumberModel(poolW, 1, 2048, 1);
		JSpinner spinner5 = new JSpinner(model5);
		spinner5.setSize(spinnerDim);
		spinner5.setMinimumSize(spinnerDim);
		spinner5.setPreferredSize(spinnerDim);
		spinner5.setMaximumSize(spinnerDim);
		spinner5.setBorder(BorderFactory.createTitledBorder("Pool Width"));
		spinner5.setBackground(Color.white);

		SpinnerModel model6 = new SpinnerNumberModel(poolH, 1, 2048, 1);
		JSpinner spinner6 = new JSpinner(model6);
		spinner6.setSize(spinnerDim);
		spinner6.setMinimumSize(spinnerDim);
		spinner6.setPreferredSize(spinnerDim);
		spinner6.setMaximumSize(spinnerDim);
		spinner6.setBorder(BorderFactory.createTitledBorder("Pool Height"));
		spinner6.setBackground(Color.white);

		SpinnerModel model7 = new SpinnerNumberModel(numberOfFilters, 1, 5000,
				1);
		JSpinner spinner7 = new JSpinner(model7);
		spinner7.setSize(spinnerDim);
		spinner7.setMinimumSize(spinnerDim);
		spinner7.setPreferredSize(spinnerDim);
		spinner7.setMaximumSize(spinnerDim);
		spinner7.setBorder(BorderFactory.createTitledBorder("# Filters"));
		spinner7.setBackground(Color.white);

		JButton okButton = new JButton("Create");
		okButton.setSize(spinnerDim);
		okButton.setMinimumSize(spinnerDim);
		okButton.setPreferredSize(spinnerDim);
		okButton.setMaximumSize(spinnerDim);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numberOfFilters = (int) (spinner7.getValue());
				int filterW = (int) (spinner1.getValue());
				int filterH = (int) (spinner2.getValue());
				int borderW = (int) (spinner3.getValue());
				int borderH = (int) (spinner4.getValue());
				int poolW = (int) (spinner5.getValue());
				int poolH = (int) (spinner6.getValue());

				if (selected < 0) {
					listModel.addElement("huehue");

					hiddenlistModel.addElement(numberOfFilters + "," + filterW
							+ "," + filterH + "," + borderW + "," + borderH
							+ "," + poolW + "," + poolH);
					updatelistModel();
				} else {
					hiddenlistModel.set(selected, numberOfFilters + ","
							+ filterW + "," + filterH + "," + borderW + ","
							+ borderH + "," + poolW + "," + poolH);
					updatelistModel();
				}

				filterFrame.setVisible(false);
			}
		});

		filterPanel.add(spinner1);
		filterPanel.add(spinner2);
		filterPanel.add(spinner3);
		filterPanel.add(spinner4);
		filterPanel.add(spinner5);
		filterPanel.add(spinner6);
		filterPanel.add(spinner7);
		filterPanel.add(okButton);

		filterFrame.add(filterPanel);

		filterFrame.setVisible(true);
	}

	private void createTestSwing() {
		int imagePanelWidth = (int) (Math
				.round((CNN.getImageWidth() * imagePanelHeight)
						/ (1.0 * CNN.getImageHeight())));
		Dimension imagePanelDimension = new Dimension(imagePanelWidth,
				imagePanelHeight);

		testCNNDimension = new Dimension(400 + imagePanelWidth + 3 * 10, 4 * 10
				+ 2 * standardHeight + imagePanelHeight + 30);
		buttonDimension2 = new Dimension(
				(int) (testCNNDimension.getWidth() - 20), standardHeight);
		progressBarPanelDimension = new Dimension(400, imagePanelHeight);
		progressBarPanelDimension2 = new Dimension(400 - 20,
				imagePanelHeight - 20);

		JFrame testCNNFrame = new JFrame();
		testCNNFrame.setSize(testCNNDimension);
		testCNNFrame.setMinimumSize(testCNNDimension);
		testCNNFrame.setPreferredSize(testCNNDimension);
		testCNNFrame.setMaximumSize(testCNNDimension);
		testCNNFrame.setLocation(200, 200);
		testCNNFrame.setVisible(false);
		testCNNFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mainFrame.setVisible(true);
			}
		});

		JPanel testPanel = new JPanel();
		testPanel.setSize(testCNNDimension);
		testPanel.setMinimumSize(testCNNDimension);
		testPanel.setPreferredSize(testCNNDimension);
		testPanel.setMaximumSize(testCNNDimension);
		testPanel.setBackground(Color.white);
		testPanel.setLayout(new FlowLayout());

		ArrayList<String> outputNames = CNN.getOutputNames();
		ArrayList<JProgressBar> listOfProgressBars = new ArrayList<JProgressBar>();
		Dimension pbDimension = new Dimension(
				(int) (progressBarPanelDimension2.getWidth() - 20),
				standardHeight);
		for (int i = 0; i < outputNames.size(); i++) {
			JProgressBar pb = new JProgressBar();
			pb.setBackground(Color.white);
			pb.setMinimum(0);
			pb.setMaximum(100);
			pb.setSize(pbDimension);
			pb.setMinimumSize(pbDimension);
			pb.setPreferredSize(pbDimension);
			pb.setMaximumSize(pbDimension);
			pb.setStringPainted(true);
			pb.setValue(0);
			pb.setString(outputNames.get(i) + " [0.0%]");

			listOfProgressBars.add(pb);
		}

		TestImagePanel tip = new TestImagePanel(imagePanelDimension,
				CNN.getImageWidth(), CNN.getImageHeight());
		tip.setBackground(Color.WHITE);

		JLabel infoLabel = new JLabel("Best guess: ? [?%]");
		infoLabel.setSize(buttonDimension2);
		infoLabel.setMinimumSize(buttonDimension2);
		infoLabel.setPreferredSize(buttonDimension2);
		infoLabel.setMaximumSize(buttonDimension2);

		JButton addPicButton = new JButton("Select image");
		addPicButton.setSize(buttonDimension2);
		addPicButton.setMinimumSize(buttonDimension2);
		addPicButton.setPreferredSize(buttonDimension2);
		addPicButton.setMaximumSize(buttonDimension2);
		addPicButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				try {
					fileChooser.setCurrentDirectory(new File(System
							.getProperty("user.dir") + "/Test"));
				} catch (Exception ex) {
					fileChooser.setCurrentDirectory(new File(System
							.getProperty("user.dir")));
				}
				int result = fileChooser.showOpenDialog(new JFrame());
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File selectedFile = fileChooser.getSelectedFile();
						tip.drawPicture(selectedFile.getAbsolutePath());

						ImageManager imageManager = new ImageManager();
						BufferedImage image = imageManager
								.getImage(selectedFile.getAbsolutePath());

						ArrayList<Double> inputData = (ArrayList<Double>) imageManager
								// .getImageArray(image, CNN.getBorderW(),
								// CNN.getBorderH()).stream()
								.getImageArray(image).stream()
								.map(item -> 1.0 * item)
								.collect(Collectors.toList());
						ArrayList<Double> neuronOutputs = CNN
								.getExpectedOutput(inputData);
						// CNN.printNN();
						// System.exit(0);

						int bestIndex = 0;
						double bestScore = neuronOutputs.get(0);
						for (int i = 0; i < listOfProgressBars.size(); i++) {
							listOfProgressBars.get(i).setValue(
									(int) (Math.round(100.0 * neuronOutputs
											.get(i))));
							listOfProgressBars.get(i).setString(
									outputNames.get(i)
											+ " ["
											+ String.format("%.1f",
													100.0 * neuronOutputs
															.get(i)) + "%]");
							if (neuronOutputs.get(i) > bestScore) {
								bestScore = neuronOutputs.get(i);
								bestIndex = i;
							}
						}
						infoLabel.setText("Best guess: "
								+ outputNames.get(bestIndex) + " ["
								+ String.format("%.1f", 100.0 * bestScore)
								+ "%]");
					} catch (Exception ex) {
						System.out.println("Could not test image: "
								+ ex.getMessage());
					}
				}
			}
		});

		JPanel progressBarsPanel = new JPanel();
		progressBarsPanel.setSize(progressBarPanelDimension2);
		// progressBarsPanel.setMinimumSize(progressBarPanelDimension2);
		// progressBarsPanel.setPreferredSize(progressBarPanelDimension2);
		// progressBarsPanel.setMaximumSize(progressBarPanelDimension2);
		progressBarsPanel.setBackground(Color.white);
		// progressBarsPanel.setLayout(new FlowLayout());
		progressBarsPanel.setLayout(new BoxLayout(progressBarsPanel,
				BoxLayout.PAGE_AXIS));
		for (int i = 0; i < listOfProgressBars.size(); i++) {
			progressBarsPanel.add(listOfProgressBars.get(i));
		}

		JScrollPane listScroller = new JScrollPane(progressBarsPanel);
		listScroller.setBackground(Color.white);
		listScroller.setSize(progressBarPanelDimension);
		listScroller.setMinimumSize(progressBarPanelDimension);
		listScroller.setPreferredSize(progressBarPanelDimension);
		listScroller.setMaximumSize(progressBarPanelDimension);

		testPanel.add(addPicButton);
		testPanel.add(infoLabel);
		testPanel.add(listScroller);
		testPanel.add(tip);

		testCNNFrame.add(testPanel);
		testCNNFrame.setVisible(true);
	}

	private void createPlatonicSwing() {
		int imagePanelWidth = (int) (Math
				.round((CNN.getImageWidth() * imagePanelHeight)
						/ (1.0 * CNN.getImageHeight())));
		Dimension imagePanelDimension = new Dimension(imagePanelWidth,
				imagePanelHeight);

		platonicCNNDimension = new Dimension(400 + imagePanelWidth + 3 * 10, 4
				* 10 + 2 * standardHeight + imagePanelHeight + 30);
		buttonDimension3 = new Dimension(
				(int) (0.5 * (platonicCNNDimension.getWidth() - 20)),
				standardHeight);
		radioButtonsPanelDimension = new Dimension(400, imagePanelHeight);
		radioButtonsPanelDimension2 = new Dimension(400 - 20,
				imagePanelHeight - 20);

		JFrame platonicCNNFrame = new JFrame();
		platonicCNNFrame.setSize(platonicCNNDimension);
		platonicCNNFrame.setMinimumSize(platonicCNNDimension);
		platonicCNNFrame.setPreferredSize(platonicCNNDimension);
		platonicCNNFrame.setMaximumSize(platonicCNNDimension);
		platonicCNNFrame.setLocation(200, 200);
		platonicCNNFrame.setVisible(false);
		platonicCNNFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mainFrame.setVisible(true);
			}
		});

		JPanel platonicPanel = new JPanel();
		platonicPanel.setSize(platonicCNNDimension);
		platonicPanel.setMinimumSize(platonicCNNDimension);
		platonicPanel.setPreferredSize(platonicCNNDimension);
		platonicPanel.setMaximumSize(platonicCNNDimension);
		platonicPanel.setBackground(Color.white);
		platonicPanel.setLayout(new FlowLayout());

		ArrayList<String> outputNames = CNN.getOutputNames();

		tip = new TestImagePanel(imagePanelDimension, CNN.getImageWidth(),
				CNN.getImageHeight());
		tip.setBackground(Color.WHITE);
		tip.drawPicture(new File(System.getProperty("user.dir") + "/Platonic/"
				+ CNN.getName() + "/" + CNN.getOutputNames().get(0) + ".png")
				.getAbsolutePath());

		ArrayList<JRadioButton> listOfRadioButtons = new ArrayList<JRadioButton>();
		ButtonGroup group = new ButtonGroup();
		groupRB = new ArrayList<JRadioButton>();
		Dimension rbDimension = new Dimension(
				(int) (radioButtonsPanelDimension2.getWidth() - 20),
				standardHeight);
		for (int i = 0; i < outputNames.size(); i++) {
			JRadioButton rb = new JRadioButton(outputNames.get(i));
			rb.setBackground(Color.white);
			rb.setSize(rbDimension);
			rb.setMinimumSize(rbDimension);
			rb.setPreferredSize(rbDimension);
			rb.setMaximumSize(rbDimension);
			int idx = i;
			rb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedRB = idx;
					tip.drawPicture(new File(System.getProperty("user.dir")
							+ "/Platonic/" + CNN.getName() + "/"
							+ CNN.getOutputNames().get(selectedRB) + ".png")
							.getAbsolutePath());
				}
			});
			if (i == 0) {
				rb.setSelected(true);
			} else {
				rb.setSelected(false);
			}

			listOfRadioButtons.add(rb);
			group.add(rb);
			groupRB.add(rb);
		}

		infoLabel = new JLabel("Score(%) = ?");
		infoLabel.setSize(buttonDimension3);
		infoLabel.setMinimumSize(buttonDimension3);
		infoLabel.setPreferredSize(buttonDimension3);
		infoLabel.setMaximumSize(buttonDimension3);

		startStopButton = new JButton("Continue thinking");
		startStopButton.setSize(buttonDimension3);
		startStopButton.setMinimumSize(buttonDimension3);
		startStopButton.setPreferredSize(buttonDimension3);
		startStopButton.setMaximumSize(buttonDimension3);
		startStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean currentState = trainORthinkBoolean.get();
				if (currentState) {
					trainer.continueThinking.set(false);
					trainORthinkBoolean.set(false);
					startStopButton.setText("Continue thinking");
				} else {
					whatToDo = "Think";
					synchronized (trainORthinkBoolean) {
						trainORthinkBoolean.set(true);
						trainORthinkBoolean.notifyAll();
						startStopButton.setText("Stop & Save");
					}
				}
			}
		});

		JPanel radioButtonsPanel = new JPanel();
		radioButtonsPanel.setSize(radioButtonsPanelDimension2);
		// radioButtonsPanel.setMinimumSize(radioButtonsPanelDimension2);
		// radioButtonsPanel.setPreferredSize(radioButtonsPanelDimension2);
		// radioButtonsPanel.setMaximumSize(radioButtonsPanelDimension2);
		radioButtonsPanel.setBackground(Color.white);
		// radioButtonsPanel.setLayout(new FlowLayout());
		radioButtonsPanel.setLayout(new BoxLayout(radioButtonsPanel,
				BoxLayout.PAGE_AXIS));
		for (int i = 0; i < listOfRadioButtons.size(); i++) {
			radioButtonsPanel.add(listOfRadioButtons.get(i));
		}
		radioButtonsPanel.setBorder(BorderFactory
				.createTitledBorder("radioButtonsPanel"));

		JScrollPane listScroller = new JScrollPane(radioButtonsPanel);
		listScroller.setBackground(Color.white);
		listScroller.setSize(radioButtonsPanelDimension);
		listScroller.setMinimumSize(radioButtonsPanelDimension);
		listScroller.setPreferredSize(radioButtonsPanelDimension);
		listScroller.setMaximumSize(radioButtonsPanelDimension);
		listScroller
				.setBorder(BorderFactory.createTitledBorder("listScroller"));

		platonicPanel.add(startStopButton);
		platonicPanel.add(infoLabel);
		platonicPanel.add(listScroller);
		platonicPanel.add(tip);

		platonicCNNFrame.add(platonicPanel);
		platonicCNNFrame.setVisible(true);
	}

	private void createNewCNN(String convNetName, int inputWidth,
			int inputHeight,
			ArrayList<PairOfFilterNumberAndProperties> filtersData,
			ArrayList<String> outputNames) {
		CNN = new ConvolutionalNeuralNetwork(convNetName, inputWidth,
				inputHeight, filtersData, outputNames);
		try {
			// CNN.saveToImage();
		} catch (Exception e) {
			System.out.println("Error saving CNN: " + e.getMessage());
		}

		saveLoadManager.saveNeuralNet(CNN);
	}

	private void loadCNN(String fileName) {
		CNN = (ConvolutionalNeuralNetwork) saveLoadManager
				.loadNeuralNetwork(fileName);
	}

	private TrainingDataSet createTrainingData() {

		ImageManager imManager = new ImageManager();

		// Create training data
		TrainingDataSet trainingData = new TrainingDataSet();
		File dir = new File("./Train");
		ArrayList<String> outputNames = CNN.getOutputNames();
		for (File file : dir.listFiles()) {
			for (int j = 0; j < outputNames.size(); j++) {
				if (file.getName().startsWith(outputNames.get(j) + "_")
						&& file.getName().endsWith("png")) {

					BufferedImage im = imManager.getImage(file
							.getAbsolutePath());
					ArrayList<Double> inputValues = (ArrayList<Double>) imManager
							.getImageArray(im)
							.stream()
							// .getImageArray(im, CNN.getBorderW(),
							// CNN.getBorderH()).stream()
							.map(item -> 1.0 * item)
							.collect(Collectors.toList());

					ArrayList<Double> outputValues = new ArrayList<Double>();
					for (int i = 0; i < outputNames.size(); i++) {
						if (i == j) {
							outputValues.add(1.0);
						} else {
							outputValues.add(0.0);
						}
					}

					trainingData.addPair(inputValues, outputValues);
				}
			}
		}
		return trainingData;
	}

	private void run() {
		while (true) {
			synchronized (trainORthinkBoolean) {
				if (trainORthinkBoolean.get()) {
					if (whatToDo == "Train") {
						double learningRate = 10.0;
						boolean finished = trainer.continueBackPropagation(
								learningRate);
						saveLoadManager.saveNeuralNet(CNN);
						imageManager.saveFilters(CNN);
						beginTrainbutton.setText("Begin training");
					}
					if (whatToDo == "Think") {
						double learningRate = 1.0;
						TrainingDataSet customTrainingDataSet = new TrainingDataSet();
						ArrayList<Double> inputValues = new ArrayList<Double>();
						for (int i = 0; i < CNN.getInputNeuronsSize(); i++) {
							inputValues.add(1.0);
						}
						ArrayList<Double> outputValues = new ArrayList<Double>();
						for (int i = 0; i < CNN.getOutputNeuronsSize(); i++) {
							if (i == selectedRB) {
								outputValues.add(1.0);
							} else {
								outputValues.add(0.0);
							}
						}
						customTrainingDataSet
								.addPair(inputValues, outputValues);
						CostFunctionUsingTrainingDataSet costFunction = new CostFunctionUsingTrainingDataSet(
								customTrainingDataSet);
						BufferedImage initialImage = imageManager
								.getImage(System.getProperty("user.dir")
										+ "/Platonic/" + CNN.getName() + "/"
										+ CNN.getOutputNames().get(selectedRB)
										+ ".png");
						ArrayList<Double> initialWeights = new ArrayList<Double>();
						if (initialImage != null) {
							initialWeights = (ArrayList<Double>) (imageManager
									.getImageArray(initialImage).stream()
									.map(t -> 1.0 * t).collect(Collectors
									.toList()));
						} else {
							for (int i = 0; i < CNN.getInputNeuronsSize(); i++) {
								// initialWeights.add(255.0 * Math.random());
								initialWeights.add(127.0 - 10.0 + 20.0 * Math
										.random());
							}
						}
						ArrayList<Integer> imageArray = trainer
								.continueThinking(learningRate, costFunction,
										initialWeights);
						// System.out.println("imageArray = "+Arrays.toString(imageArray.toArray()));
						imageManager.savePlatonicImage(imageArray,
								CNN.getName(),
								CNN.getOutputNames().get(selectedRB),
								CNN.getImageWidth(), CNN.getImageHeight());
						// saveLoadManager.saveNeuralNet(CNN);
						// imageManager.saveFilters(CNN);
						// imageManager.saveToImage(image, fileName);
						startStopButton.setText("Continue thinking");
					}
					trainORthinkBoolean.set(false);
				} else {
					try {
						trainORthinkBoolean.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
}
