package PROJECTS;

import java.util.ArrayList;

import training.Trainer;
import training.TrainingDataSet;
import neuralNets.Example1NeuralNet;
import neuralNets.GenericNeuralNet;
import costFunctions.CostFunctionUsingTrainingDataSet;
import fileManager.SaveLoadManager;

public class Example1 {

	public Example1() {

		// Example1NeuralNet
		GenericNeuralNet example1NN = new Example1NeuralNet("example1");

		// Training data set
		TrainingDataSet trainingDataSet = new TrainingDataSet();
		ArrayList<Double> inputSet = new ArrayList<Double>();
		ArrayList<Double> outputSet = new ArrayList<Double>();
		inputSet.add(1.0);
		inputSet.add(2.0);
		outputSet.add(3.0);
		outputSet.add(-1.0);
		trainingDataSet.addPair(inputSet, outputSet);

		// SaveLoadManager
		SaveLoadManager manager = new SaveLoadManager("SavedNNs/");

		// CostFunctionUsingTrainingDataSet
		CostFunctionUsingTrainingDataSet costFunction = new CostFunctionUsingTrainingDataSet(
				trainingDataSet);

		// Trainer
		Trainer trainer = new Trainer(example1NN, costFunction, manager);

		// Train
		// 1) Backpropagate, save current weights and compute current error
		double learningRate = 1.0;
		boolean verbose = true;
		if (verbose) {
			System.out.println("Before backPropagate:");
			example1NN.printNN();
		}
		trainer.backPropagate();
		example1NN.saveCurrentWeights();
		double currentTotalError = costFunction.getError(example1NN);
		System.out.println("before backpropagation, error = "
				+ currentTotalError);

		// 2) update links and compute new error
		example1NN.updateLinkWeights(learningRate);
		double newTotalError = costFunction.getError(example1NN);
		System.out.println("after backpropagation,  error = " + newTotalError);

		manager.saveNeuralNet(example1NN);
		// example1NN.saveToImage();
	}
}
