package PROJECTS;

import java.util.ArrayList;

import training.Trainer;
import training.TrainingDataSet;
import neuralNets.Example3NeuralNet;
import neuralNets.GenericNeuralNet;
import costFunctions.CostFunctionUsingTrainingDataSet;
import fileManager.SaveLoadManager;

public class Example3 {

	public Example3() {

		// Example1NeuralNet
		GenericNeuralNet example3NN = new Example3NeuralNet("example3");

		// Training data set
		TrainingDataSet trainingDataSet = new TrainingDataSet();
		ArrayList<Double> inputSet = new ArrayList<Double>();
		ArrayList<Double> outputSet = new ArrayList<Double>();
		inputSet.add(255.0);
		inputSet.add(255.0);
		inputSet.add(0.0);
		inputSet.add(255.0);
		inputSet.add(0.0);
		outputSet.add(0.8);
		outputSet.add(0.6);
		trainingDataSet.addPair(inputSet, outputSet);

		// SaveLoadManager
		SaveLoadManager manager = new SaveLoadManager("SavedNNs/");

		// CostFunctionUsingTrainingDataSet
		CostFunctionUsingTrainingDataSet costFunction = new CostFunctionUsingTrainingDataSet(
				trainingDataSet);

		// Trainer
		Trainer trainer = new Trainer(example3NN, costFunction, manager);

		// Train
		// 1) Backpropagate, save current weights and compute current error
		double learningRate = 1.0;
		boolean verbose = true;
		if (verbose) {
			System.out.println("Before backPropagate:");
			example3NN.printNN();
		}
		trainer.backPropagate();
		example3NN.saveCurrentWeights();
		double currentTotalError = costFunction.getError(example3NN);
		System.out.println("before backpropagation, error = "
				+ currentTotalError);

		// 2) update links and compute new error
		example3NN.updateLinkWeights(learningRate);
		double newTotalError = costFunction.getError(example3NN);
		System.out.println("after backpropagation,  error = " + newTotalError);

		manager.saveNeuralNet(example3NN);
		// example3NN.saveToImage();
	}
}
