package PROJECTS;

import java.util.ArrayList;

import training.Trainer;
import training.TrainingDataSet;
import auxiliar.PairOfFilterNumberAndProperties;
import neuralNets.ConvolutionalNeuralNetwork;
import neuralNets.Example1NeuralNet;
import neuralNets.GenericNeuralNet;
import costFunctions.CostFunctionUsingTrainingDataSet;
import fileManager.SaveLoadManager;
import filters.FilterProperties;

public class Example2 {

	public Example2() {

		// Example1NeuralNet
		ArrayList<String> outputNames = new ArrayList<String>();
		outputNames.add("Dog");
		outputNames.add("Cat");

		ArrayList<PairOfFilterNumberAndProperties> filtersData = new ArrayList<PairOfFilterNumberAndProperties>();
		int nOfFilters = 2;
		int filterW = 2;
		int filterH = 3;
		int borderW = 1;
		int borderH = 1;
		int poolW = 4;
		int poolH = 1;
		filtersData.add(new PairOfFilterNumberAndProperties(nOfFilters,
				new FilterProperties(filterW, filterH, borderW, borderH, poolW,
						poolH)));
		nOfFilters = 2;
		filterW = 2;
		filterH = 2;
		borderW = 1;
		borderH = 1;
		poolW = 1;
		poolH = 3;
		filtersData.add(new PairOfFilterNumberAndProperties(nOfFilters,
				new FilterProperties(filterW, filterH, borderW, borderH, poolW,
						poolH)));

		ConvolutionalNeuralNetwork example2NN = new ConvolutionalNeuralNetwork(
				"example2", 3, 2, filtersData, outputNames);
		// example2NN.saveToImage();

		// Training data set
		TrainingDataSet trainingDataSet = new TrainingDataSet();
		ArrayList<Double> inputSet = new ArrayList<Double>();
		ArrayList<Double> outputSet = new ArrayList<Double>();
		// R
		inputSet.add(1.0);
		inputSet.add(2.0);
		inputSet.add(3.0);
		inputSet.add(4.0);
		inputSet.add(5.0);
		inputSet.add(6.0);
		// G
		inputSet.add(255.0);
		inputSet.add(255.0);
		inputSet.add(255.0);
		inputSet.add(255.0);
		inputSet.add(255.0);
		inputSet.add(255.0);
		// B
		inputSet.add(10.0);
		inputSet.add(20.0);
		inputSet.add(30.0);
		inputSet.add(40.0);
		inputSet.add(50.0);
		inputSet.add(60.0);
		// output
		outputSet.add(0.1);
		outputSet.add(0.2);
		trainingDataSet.addPair(inputSet, outputSet);

		// SaveLoadManager
		SaveLoadManager manager = new SaveLoadManager("SavedNNs/");

		// CostFunctionUsingTrainingDataSet
		CostFunctionUsingTrainingDataSet costFunction = new CostFunctionUsingTrainingDataSet(
				trainingDataSet);

		// Trainer
		Trainer trainer = new Trainer(example2NN, costFunction, manager);

		// Train
		// 1) Backpropagate, save current weights and compute current error
		double learningRate = 1.0;
		boolean verbose = true;
		if (verbose) {
			System.out.println("Before backPropagate:");
			example2NN.printNN();
		}
		trainer.backPropagate();
		example2NN.saveCurrentWeights();
		double currentTotalError = costFunction.getError(example2NN);
		System.out.println("before backpropagation, error = "
				+ currentTotalError);

		// 2) update links and compute new error
		example2NN.updateLinkWeights(learningRate);
		double newTotalError = costFunction.getError(example2NN);
		System.out.println("after backpropagation,  error = " + newTotalError);

		// manager.saveNeuralNet(example2NN);
		// example2NN.saveToImage();
	}
}
