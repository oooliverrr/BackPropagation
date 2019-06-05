package PROJECTS;

import java.util.ArrayList;
import java.util.Arrays;

import training.Trainer;
import training.TrainingDataSet;
import neuralNets.BasicNeuralNet;
import neuralNets.GenericNeuralNet;
import neurons.NeuronType;
import auxiliar.TripletLayerNumberAndTypeAndArgs;
import costFunctions.CostFunctionUsingTrainingDataSet;
import fileManager.SaveLoadManager;

public class AverageOfTwoNumbers {

	public AverageOfTwoNumbers() {
		// Neural net
		// input

		ArrayList<Double> inputArgs = new ArrayList<Double>(Arrays.asList(0.01,
				0.0));
		TripletLayerNumberAndTypeAndArgs inputTriplet = new TripletLayerNumberAndTypeAndArgs(
				2, NeuronType.Linear, inputArgs);
		// ArrayList<Double> inputArgs = new
		// ArrayList<Double>(Arrays.asList(1.0,
		// 0.0));
		// TripletLayerNumberAndTypeAndArgs inputTriplet = new
		// TripletLayerNumberAndTypeAndArgs(
		// 2, LayerType.Linear, inputArgs);

		// output

		// ArrayList<Double> outputArgs = new
		// ArrayList<Double>(Arrays.asList(2.0,
		// 0.0, -100.0, 100.0));
		// TripletLayerNumberAndTypeAndArgs outputTriplet = new
		// TripletLayerNumberAndTypeAndArgs(
		// 1, LayerType.Sigmoid, outputArgs);
		ArrayList<Double> outputArgs = new ArrayList<Double>(Arrays.asList(
				100.0 / 3, 0.0));
		TripletLayerNumberAndTypeAndArgs outputTriplet = new TripletLayerNumberAndTypeAndArgs(
				1, NeuronType.Linear, outputArgs);

		// hidden layers
		ArrayList<Double> hiddenLayersArgs = new ArrayList<Double>(
				Arrays.asList(2.0, 0.0, -1.0, 1.0));
		ArrayList<TripletLayerNumberAndTypeAndArgs> hiddenLayers = new ArrayList<TripletLayerNumberAndTypeAndArgs>();
		// hiddenLayers.add(new TripletLayerNumberAndTypeAndArgs(3,
		// LayerType.Sigmoid, hiddenLayersArgs));
		hiddenLayers.add(new TripletLayerNumberAndTypeAndArgs(2,
				NeuronType.Sigmoid, hiddenLayersArgs));
		// BasicNeuralNet
		GenericNeuralNet averageOfTwoNumbersNN = new BasicNeuralNet("AVG2",
				inputTriplet, outputTriplet, hiddenLayers);

		// Training data set
		TrainingDataSet trainingDataSet = new TrainingDataSet();
		int numberOfRandomPairs = 50;
		for (int k = 0; k < numberOfRandomPairs; k++) {
			double randomNumberA = -100 + 200 * Math.random();
			double randomNumberB = -100 + 200 * Math.random();
			// double randomNumberA = -100 + 100;
			// double randomNumberB = -100 + 160;

			double sum = 0.5 * (randomNumberA + randomNumberB);

			ArrayList<Double> inputSet = new ArrayList<Double>();
			ArrayList<Double> outputSet = new ArrayList<Double>();

			inputSet.add(randomNumberA);
			inputSet.add(randomNumberB);
			outputSet.add(sum);

			trainingDataSet.addPair(inputSet, outputSet);
		}
		int numberOf100Sampling = 11;
		for (int k = 0; k < numberOf100Sampling; k++) {
			double randomNumberA = -100 + 200 * k / (numberOf100Sampling - 1);
			for (int q = 0; q < numberOf100Sampling; q++) {
				double randomNumberB = -100 + 200 * q
						/ (numberOf100Sampling - 1);

				double sum = 0.5 * (randomNumberA + randomNumberB);

				ArrayList<Double> inputSet = new ArrayList<Double>();
				ArrayList<Double> outputSet = new ArrayList<Double>();

				inputSet.add(randomNumberA);
				inputSet.add(randomNumberB);
				outputSet.add(sum);

				trainingDataSet.addPair(inputSet, outputSet);
			}
		}

		// ArrayList<Double> inputSet = new ArrayList<Double>();
		// ArrayList<Double> outputSet = new ArrayList<Double>();
		// inputSet.add(20.0);
		// inputSet.add(40.0);
		// outputSet.add(30.0);
		// trainingDataSet.addPair(inputSet, outputSet);
		// inputSet = new ArrayList<Double>();
		// outputSet = new ArrayList<Double>();
		// inputSet.add(-80.0);
		// inputSet.add(50.0);
		// outputSet.add(-15.0);
		// trainingDataSet.addPair(inputSet, outputSet);

		// SaveLoadManager
		SaveLoadManager manager = new SaveLoadManager("SavedNNs/");

		// CostFunctionUsingTrainingDataSet
		CostFunctionUsingTrainingDataSet costFunction = new CostFunctionUsingTrainingDataSet(
				trainingDataSet);

		// Trainer
		Trainer trainer = new Trainer(averageOfTwoNumbersNN, costFunction,
				manager);

		// Train
		// 1) Backpropagate, save current weights and compute current error
		double learningRate = 1.0;
		boolean condition = true;
		boolean verbose = false;
		while (condition) {
			if (verbose) {
				System.out.println("Before backPropagate:");
				averageOfTwoNumbersNN.printNN();
			}
			trainer.backPropagate();
			// averageOfTwoNumbersNN.printNN();
			averageOfTwoNumbersNN.saveCurrentWeights();
			if (verbose) {
				System.out
						.println("After backPropagate, Before currentTotalError:");
				averageOfTwoNumbersNN.printNN();
			}
			double currentTotalError = costFunction
					.getError(averageOfTwoNumbersNN);
			// System.out.println("currentTotalError = " + currentTotalError);

			// 2) update links and compute new error
			averageOfTwoNumbersNN.updateLinkWeights(learningRate);
			double newTotalError = costFunction.getError(averageOfTwoNumbersNN);
			// System.out.println("newTotalError = " + newTotalError);
			// System.out.println();

			// If error is larger, rollback
			if (newTotalError >= currentTotalError) {
				averageOfTwoNumbersNN.rollbackLinkWeights();
				learningRate = learningRate * 0.9;
				System.out.println("learningRate => " + learningRate
						+ " (currentTotalError = " + currentTotalError + ")");
				if (learningRate < 1.2E-10) {
					condition = false;
				}
			}
		}
		manager.saveNeuralNet(averageOfTwoNumbersNN);

		// TEST
		double averageError = 0.0;
		int Ntests = 11;
		int cc = 0;
		System.out.println("");
		System.out.println("TEST");
		System.out.println("------");
		for (int i = 0; i < Ntests; i++) {
			ArrayList<Double> inputSett = new ArrayList<Double>();
			double r1 = -100 + 200 * Math.random();
			double r2 = -100 + 200 * Math.random();
			double sum = 0.5 * (r1 + r2);
			inputSett.add(r1);
			inputSett.add(r2);
			ArrayList<Double> outputSett = averageOfTwoNumbersNN
					.getExpectedOutput(inputSett);
			double errorr = Math.abs((0.5 * (r1 + r2) - outputSett.get(0)));
			averageError = averageError + errorr;
			cc = cc + 1;
			System.out.println("For " + Arrays.toString(inputSett.toArray())
					+ " we get (expected)"
					+ Arrays.toString(outputSett.toArray()) + " (" + sum + ")"
					+ " [error = " + errorr + "]");
		}
		for (int i = 0; i < Ntests; i++) {
			double r1 = -100 + i * 200.0 / (Ntests - 1);
			for (int j = 0; j < Ntests; j++) {
				ArrayList<Double> inputSett = new ArrayList<Double>();
				double r2 = -100 + j * 200.0 / (Ntests - 1);
				double sum = 0.5 * (r1 + r2);
				inputSett.add(r1);
				inputSett.add(r2);
				ArrayList<Double> outputSett = averageOfTwoNumbersNN
						.getExpectedOutput(inputSett);
				double errorr = Math.abs((0.5 * (r1 + r2) - outputSett.get(0)));
				averageError = averageError + errorr;
				cc = cc + 1;
				System.out.println("For "
						+ Arrays.toString(inputSett.toArray())
						+ " we get (expected)"
						+ Arrays.toString(outputSett.toArray()) + " (" + sum
						+ ")" + " [error = " + errorr + "]");
			}
		}
		averageError = averageError / cc;
		System.out.println("");
		System.out.println("Average error = " + averageError);
		System.out.println("");
		// averageOfTwoNumbersNN.printNN();
	}
}
