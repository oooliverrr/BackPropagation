package training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import links.Link;
import neuralNets.ConvolutionalNeuralNetwork;
import neuralNets.GenericNeuralNet;
import neurons.GenericNeuron;
import neurons.NeuronType;
import auxiliar.PairOfInputOutput;
import auxiliar.PairOfNeurons;
import costFunctions.CostFunctionUsingTrainingDataSet;
import fileManager.SaveLoadManager;
import graphics.ImageManager;

public class Trainer {

	private GenericNeuralNet neuralNetworkToTrain;
	private CostFunctionUsingTrainingDataSet costFunction;
	private SaveLoadManager manager;
	private ArrayList<ArrayList<Double>> biasLinksWeights,
			bestBiasLinksWeights;
	private ArrayList<ArrayList<Double>> linksWeights, bestLinksWeights;
	public AtomicBoolean continueTraining, continueThinking;

	public Trainer(GenericNeuralNet neuralNetworkToTrain,
			CostFunctionUsingTrainingDataSet costFunction,
			SaveLoadManager manager) {
		this.neuralNetworkToTrain = neuralNetworkToTrain;
		this.costFunction = costFunction;
		this.manager = manager;

		continueTraining = new AtomicBoolean();
		continueTraining.set(false);
		continueThinking = new AtomicBoolean();
		continueThinking.set(false);
	}

	public void minimizeErrorIterating(int iterations, double maxWeightChange) {
		double newError = 0.0;
		double currentError = 0.0;

		// Init weight arrays
		biasLinksWeights = new ArrayList<ArrayList<Double>>();
		bestBiasLinksWeights = new ArrayList<ArrayList<Double>>();
		for (ArrayList<Link> list : neuralNetworkToTrain.getBiasLinks()) {
			ArrayList<Double> aux = new ArrayList<Double>();
			for (Link l : list) {
				aux.add(l.getWeight());
			}
			biasLinksWeights.add(aux);
			bestBiasLinksWeights.add(aux);
		}

		linksWeights = new ArrayList<ArrayList<Double>>();
		bestLinksWeights = new ArrayList<ArrayList<Double>>();
		for (ArrayList<Link> list : neuralNetworkToTrain.getLinks()) {
			ArrayList<Double> aux = new ArrayList<Double>();
			for (Link l : list) {
				aux.add(l.getWeight());
			}
			linksWeights.add(aux);
			bestLinksWeights.add(aux);
		}

		// Begin iterations
		for (int it = 1; it <= iterations; it++) {

			currentError = costFunction.getError(neuralNetworkToTrain);
			// System.out.println("currentError = " + currentError);

			// Modify weights
			for (int i = 0; i < biasLinksWeights.size(); i++) {
				for (int j = 0; j < biasLinksWeights.get(i).size(); j++) {
					double heff = -maxWeightChange + 2 * maxWeightChange
							* Math.random();
					biasLinksWeights.get(i).set(j,
							biasLinksWeights.get(i).get(j) + heff);
				}
			}
			for (int i = 0; i < linksWeights.size(); i++) {
				for (int j = 0; j < linksWeights.get(i).size(); j++) {
					double heff = -maxWeightChange + 2 * maxWeightChange
							* Math.random();
					linksWeights.get(i).set(j,
							linksWeights.get(i).get(j) + heff);
				}
			}
			neuralNetworkToTrain.setBiasLinksWeights(biasLinksWeights);
			neuralNetworkToTrain.setLinksWeights(linksWeights);

			newError = costFunction.getError(neuralNetworkToTrain);
			// System.out.println("newErrorA = " + newErrorA);

			// Change values if error decreases
			if (newError > currentError) {
				neuralNetworkToTrain.setBiasLinksWeights(bestBiasLinksWeights);
				neuralNetworkToTrain.setLinksWeights(bestLinksWeights);
			} else {
				currentError = newError;
				for (int i = 0; i < biasLinksWeights.size(); i++) {
					for (int j = 0; j < biasLinksWeights.get(i).size(); j++) {
						bestBiasLinksWeights.get(i).set(j,
								biasLinksWeights.get(i).get(j));
					}
				}
				for (int i = 0; i < linksWeights.size(); i++) {
					for (int j = 0; j < linksWeights.get(i).size(); j++) {
						bestLinksWeights.get(i).set(j,
								linksWeights.get(i).get(j));
					}
				}
			}

			System.out.println("[" + (100.0 * it / iterations) + "%] error = "
					+ currentError);
		}
		manager.saveNeuralNet(neuralNetworkToTrain);
	}

	public boolean continueBackPropagation(double learningRate) {
		continueTraining.set(true);
		// boolean condition = true;

		// System.out.println("BEFORE ANYTHING:");
		// neuralNetworkToTrain.printNN();

		double currentTotalError = costFunction.getError(neuralNetworkToTrain);
		// System.out.println("AFTER getError:");
		// neuralNetworkToTrain.printNN();

		while (continueTraining.get()) {
//			 float ta = System.nanoTime();
			// backPropagate();
			double errorAfterBackPropagate = backPropagate();
			// System.out.println("AFTER backPropagate:");
			// neuralNetworkToTrain.printNN();
//			 float tb = System.nanoTime();
//			 float t1 = tb - ta;
			System.out.println("errorAfterBackPropagate = "
					+ errorAfterBackPropagate);

//			 ta = System.nanoTime();
			neuralNetworkToTrain.updateLinkWeights(learningRate);
			// System.out.println("AFTER updateLinkWeights:");
			// neuralNetworkToTrain.printNN();
			// System.exit(0);
//			 tb = System.nanoTime();
//			 float t2 = tb - ta;
//			
//			 float TT = t1 + t2;
//			 System.out.println("t1 = " + t1 + " [" + 100.0 * t1 / TT +
//			 " %]");
//			 System.out.println("t2 = " + t2 + " [" + 100.0 * t2 / TT +
//			 " %]");
//			 System.out.println("TT = " + TT + " [" + 100.0 + " %]");
//			
//			 System.exit(0);

			// if (!continueTraining.get()) {
			// condition = false;
			// }
			// condition=false;
		}
		currentTotalError = costFunction.getError(neuralNetworkToTrain);
		System.out.println("--- DONE (error = " + currentTotalError + ")");
		return true;
	}

	public ArrayList<Integer> continueThinking(double learningRate,
			CostFunctionUsingTrainingDataSet costFunction,
			ArrayList<Double> initialWeights) {
		continueThinking.set(true);
		boolean condition = true;
		
		System.err.println("CONTINUE THINKING");

		GenericNeuralNet neuralNetworkToTrainExpanded = neuralNetworkToTrain
				.createExpandedNeuralNet(initialWeights);
		System.err.println("CREATED THINKING");

		// double currentTotalError = costFunction
		// .getError(neuralNetworkToTrainExpanded);

		while (condition) {
			double errorAfterBackPropagate = backPropagate(
					neuralNetworkToTrainExpanded, costFunction);
			System.out.println("errorAfterBackPropagate = "
					+ errorAfterBackPropagate);

			// System.out.println("BEFORE UPDATE: ");
			// neuralNetworkToTrainExpanded.printNN();
			neuralNetworkToTrainExpanded.updateLinkWeights(learningRate);
			// System.out.println("AFTER UPDATE: ");
			// neuralNetworkToTrainExpanded.printNN();
			// System.exit(0);

			if (!continueThinking.get()) {
				condition = false;
			}
		}
		double currentTotalError = costFunction
				.getError(neuralNetworkToTrainExpanded);
		System.out.println("--- DONE (error = " + currentTotalError + ")");

		ArrayList<Integer> arrayToReturn = new ArrayList<Integer>();
		for (Link l : neuralNetworkToTrainExpanded.getLinks().get(0)) {
			arrayToReturn.add((int) (Math.round(l.getWeight())));
		}

		return arrayToReturn;
	}

	public double backPropagate(GenericNeuralNet neuralNetworkToTrain,
			CostFunctionUsingTrainingDataSet costFunction) {

		double totalError = 0.0;

		// if (verbose) {
		// System.out.println("");
		// System.out.println("===================");
		// System.out.println("backPropagate");
		// System.out.println("--------");
		// }

		TrainingDataSet trainingDataSet = costFunction.getTrainingDataSet();

		// Reset
		neuralNetworkToTrain.resetLinkDeltas();

		// iterate training data set
		int idx = 1;
		int totalNumberOfPairOfInputOutput = trainingDataSet.listOfPairs()
				.size();
		for (PairOfInputOutput pairOfInputOutputDATA : trainingDataSet
				.listOfPairs()) {

			// Reset
			neuralNetworkToTrain.resetNeuronValues();

			ArrayList<Double> inputValues = pairOfInputOutputDATA
					.getInputValues();
			ArrayList<Double> outputValues = pairOfInputOutputDATA
					.getOutputValues();

			// if (verbose) {
			// System.out.println("{");
			// System.out.println("	data set pair [" + idx + "/"
			// + totalNumberOfPairOfInputOutput + "]:");
			// System.out.println("	inputValues = "
			// + Arrays.toString(inputValues.toArray()));
			// System.out.println("	outputValues = "
			// + Arrays.toString(outputValues.toArray()));
			// System.out.println("	--------");
			// }

			// Fill in input neurons
			// if (verbose) {
			// System.out.println("");
			// }
			neuralNetworkToTrain.setInput(inputValues);
			// for (GenericNeuron inputNeuron : neuralNetworkToTrain
			// .getInputNeurons()) {
			// if (verbose) {
			// System.out.println("	filling inputNeuron ["
			// + inputNeuron.getID() + "] input = "
			// + inputNeuron.getBeta() + ", output = "
			// + inputNeuron.getOutput() + ", derivative = "
			// + inputNeuron.getDerivative() + ", delta = "
			// + inputNeuron.getDelta());
			// }
			// }
			// if (verbose) {
			// System.out.println("	--------");
			// }

			// Transmit
			// if (verbose) {
			// System.out.println("");
			// System.out.println("	==> going to transmit");
			// neuralNetworkToTrain.printNN();
			// }
			// System.out.println("");
			// System.out.println("");
			// System.out.println("????????????????????????????????????????????????????????????");
			// System.out.println("");
			// System.out.println("");
			neuralNetworkToTrain.forwardFeed();
			// System.exit(0);
			// if (verbose) {
			// System.out.println("	==> transmit complete!");
			// neuralNetworkToTrain.printNN();
			// System.out.println("	--------");
			// }

			// Error
			// if (verbose) {
			// System.out.println("");
			// }
			// ArrayList<Double> neuralNetworkOutput = n.getOutput();
			int i = 0;
			for (GenericNeuron outputNeuron : neuralNetworkToTrain
					.getOutputNeurons()) {
				double error = outputNeuron.getOutput() - outputValues.get(i);
				totalError = totalError + 0.5 * error * error;
				// n.setOutputDelta(i, error);
				outputNeuron.setDelta(error * outputNeuron.getDerivative());
				// if (verbose) {
				// System.out.println("	Output neuron ["
				// + outputNeuron.getID() + "] input = "
				// + outputNeuron.getBeta() + ", output = "
				// + outputNeuron.getOutput() + " (expected = "
				// + outputValues.get(i) + "), derivative = "
				// + outputNeuron.getDerivative() + ", delta = "
				// + outputNeuron.getDelta() + " (error = " + error
				// + ")");
				// }
				i = i + 1;
			}
			// if (verbose) {
			// System.out.println("	--------");
			// }

			// backPropagate
			// if (verbose) {
			// System.out.println("");
			// System.out.println("	==> going to transmit backwards");
			// neuralNetworkToTrain.printNN();
			// }
			neuralNetworkToTrain.backwardFeed();
			// if (verbose) {
			// System.out.println("	==> backwards complete!");
			// neuralNetworkToTrain.printNN();
			// }

			// Compute derivatives
			for (ArrayList<Link> list : neuralNetworkToTrain.getBiasLinks()) {
				for (Link biasLink : list) {
					if (!biasLink.isFinal()) {
						double derivative = 0.0;
						for (PairOfNeurons pair : biasLink.getPairsOfNeurons()) {
							// if (verbose) {
							// ArrayList<Integer> inIds = new
							// ArrayList<Integer>();
							// ArrayList<Integer> outIds = new
							// ArrayList<Integer>();
							// inIds.add(pair.getInputNeuron().getID());
							// outIds.add(pair.getOutputNeuron().getID());
							// System.out.println("");
							// System.out.println("	...["
							// + pair.getOutputNeuron().getID()
							// + "].getDeltaValue() = "
							// + pair.getOutputNeuron().getDelta());
							// System.out.println("	...["
							// + pair.getOutputNeuron().getID()
							// + "].getOmega(["
							// + pair.getInputNeuron().getID()
							// + "].getOutputValue()="
							// + pair.getInputNeuron().getOutput()
							// + ", w="
							// + biasLink.getWeight()
							// + ", "
							// + pair.getInputNeuron().getID()
							// + ") = "
							// + pair.getOutputNeuron().getOmega(
							// pair.getInputNeuron().getOutput(),
							// biasLink.getWeight(),
							// pair.getInputNeuron().getID()));
							// }
							derivative = derivative
									+ pair.getOutputNeuron().getDelta()
									* pair.getOutputNeuron().getOmega(
											pair.getInputNeuron().getOutput(),
											biasLink.getWeight(),
											pair.getInputNeuron().getID());
							// System.out.println("derivative = " + derivative);
						}
						biasLink.addWeightDerivative(derivative);
						// if (verbose) {
						// System.out.println("	bias link connecting "
						// + Arrays.toString(inIds.toArray()) + "->"
						// + Arrays.toString(outIds.toArray())
						// + " has derivative weight: "
						// + biasLink.getWeightDerivative());
						// System.out.println("	---");
						// }
					}
				}
			}

			for (ArrayList<Link> list : neuralNetworkToTrain.getLinks()) {
				for (Link link : list) {
					if (!link.isFinal()) {
						double derivative = 0.0;
						// ArrayList<Integer> inIds = new ArrayList<Integer>();
						// ArrayList<Integer> outIds = new ArrayList<Integer>();
						// System.out.println("derivative = " + derivative);
						for (PairOfNeurons pair : link.getPairsOfNeurons()) {
							// System.out.println("derivative = " + derivative);
							// inIds.add(pair.getInputNeuron().getID());
							// outIds.add(pair.getOutputNeuron().getID());
							// if (verbose) {
							// System.out.println("");
							// System.out.println("	...["
							// + pair.getOutputNeuron().getID()
							// + "].getDeltaValue() = "
							// + pair.getOutputNeuron().getDelta());
							// System.out.println("	...["
							// + pair.getOutputNeuron().getID()
							// + "].getOmega(["
							// + pair.getInputNeuron().getID()
							// + "].getOutputValue()="
							// + pair.getInputNeuron().getOutput()
							// + ", w="
							// + link.getWeight()
							// + ", "
							// + pair.getInputNeuron().getID()
							// + ") = "
							// + pair.getOutputNeuron().getOmega(
							// pair.getInputNeuron().getOutput(),
							// link.getWeight(),
							// pair.getInputNeuron().getID()));
							// }
							derivative = derivative
									+ pair.getOutputNeuron().getDelta()
									* pair.getOutputNeuron().getOmega(
											pair.getInputNeuron().getOutput(),
											link.getWeight(),
											pair.getInputNeuron().getID());
							// if (verbose) {
							// System.out.println("derivative = "
							// + derivative
							// + " [added "
							// + pair.getOutputNeuron().getDelta()
							// * pair.getOutputNeuron().getOmega(
							// pair.getInputNeuron().getOutput(),
							// link.getWeight(),
							// pair.getInputNeuron().getID())
							// + "] => [ delta = "
							// + pair.getOutputNeuron().getDelta()
							// + " ][ omega = "
							// + pair.getOutputNeuron().getOmega(
							// pair.getInputNeuron().getOutput(),
							// link.getWeight(),
							// pair.getInputNeuron().getID())
							// + " ][ inputNeuronOutput = "
							// + pair.getInputNeuron().getOutput()
							// + " ][ weight = " + link.getWeight()
							// + " ][ iputNeuronID = "
							// + pair.getInputNeuron().getID()
							// + " ] => [ outputNeuron = "
							// + pair.getOutputNeuron().getClass() + " ]");
							// }
						}
						link.addWeightDerivative(derivative);
						// if (verbose || !link.isFinal()) {
						// if (verbose) {
						// System.out.println("	link connecting "
						// + Arrays.toString(inIds.toArray()) + "->"
						// + Arrays.toString(outIds.toArray())
						// + " has derivative weight: "
						// + link.getWeightDerivative());
						// System.out.println("	---");
						// }
					}
				}
			}

			// if (verbose) {
			// System.out.println("	==> After computing derivatives:");
			// neuralNetworkToTrain.printNN();
			// }
			//
			// if (verbose) {
			// System.out.println("}");
			// idx = idx + 1;
			// }
		}
		// n.resetLinkDeltas();
		// Normalize
		// totalError = totalError / totalNumberOfPairOfInputOutput;

		return totalError;
	}

	public double backPropagate() {
		// public void backPropagate() {

		double totalError = 0.0;

		TrainingDataSet trainingDataSet = costFunction.getTrainingDataSet();

		// Reset
		neuralNetworkToTrain.resetLinkDeltas();

//		 float to=0;
//		 float t1 = 0;
//		 float t2 = 0;
//		 float t3 = 0;
//		 float t4 = 0;
//		 float t5 = 0;

		// iterate training data set
		for (PairOfInputOutput pairOfInputOutputDATA : trainingDataSet
				.listOfPairs()) {

			// Reset
//			 float ta = System.nanoTime();
			neuralNetworkToTrain.resetNeuronValues();

			ArrayList<Double> inputValues = pairOfInputOutputDATA
					.getInputValues();
			ArrayList<Double> outputValues = pairOfInputOutputDATA
					.getOutputValues();

			neuralNetworkToTrain.setInput(inputValues);
//			 float tb = System.nanoTime();
//			 to = to + tb - ta;

			// Transmit
//			 ta = System.nanoTime();
			neuralNetworkToTrain.forwardFeed();
//			 tb = System.nanoTime();
//			 t1 = t1 + tb - ta;
			
//			 ta = System.nanoTime();
			int i = 0;
			for (GenericNeuron outputNeuron : neuralNetworkToTrain
					.getOutputNeurons()) {
				double error = outputNeuron.getOutput() - outputValues.get(i);
				totalError = totalError + 0.5 * error * error;
				outputNeuron.setDelta(error * outputNeuron.getDerivative());
				i = i + 1;
			}
//			 tb = System.nanoTime();
//			 t2 = t2 + tb - ta;

			// backPropagate
//			 ta = System.nanoTime();
			neuralNetworkToTrain.backwardFeed();
//			 tb = System.nanoTime();
//			 t3 = t3 + tb - ta;

			// Compute derivatives
//			 ta = System.nanoTime();
			neuralNetworkToTrain
					.getBiasLinks()
					.stream()
					.forEach(
							(biasLinkslist) -> {
								if (biasLinkslist.size() > 0) {
									if (!biasLinkslist.get(0).isFinal()) {
										biasLinkslist
												.stream()
												.forEach((biasLink) -> {
													// if (!biasLink.isFinal())
													// {
														double derivative = 0.0;
														for (PairOfNeurons pair : biasLink
																.getPairsOfNeurons()) {
															derivative = derivative
																	+ pair.getOutputNeuron()
																			.getDelta()
																	* pair.getOutputNeuron()
																			.getOmega(
																					pair.getInputNeuron()
																							.getOutput(),
																					biasLink.getWeight(),
																					pair.getInputNeuron()
																							.getID());
														}
														biasLink.addWeightDerivative(derivative);
														// }
													});
									}
								}
							});
//			 tb = System.nanoTime();
//			 t4 = t4 + tb - ta;
//			
//			 ta = System.nanoTime();
			neuralNetworkToTrain
					.getLinks()
					.stream()
					.forEach(
							(linksList) -> {
								if (linksList.size() > 0) {
									if (!linksList.get(0).isFinal()) {
										linksList
												.stream()
												.forEach((link) -> {
													// if (!link.isFinal()) {
														double derivative = 0.0;
														for (PairOfNeurons pair : link
																.getPairsOfNeurons()) {
															derivative = derivative
																	+ pair.getOutputNeuron()
																			.getDelta()
																	* pair.getOutputNeuron()
																			.getOmega(
																					pair.getInputNeuron()
																							.getOutput(),
																					link.getWeight(),
																					pair.getInputNeuron()
																							.getID());
														}
														link.addWeightDerivative(derivative);
														// }
													});
									}
								}
							});
//			 tb = System.nanoTime();
//			 t5 = t5 + tb - ta;
		}

//		 float TT = to + t1 + t2 + t3 + t4 + t5;
//		 System.out.println("to = " + to + " [" + 100.0 * to / TT + " %]");
//		 System.out.println("t1 = " + t1 + " [" + 100.0 * t1 / TT + " %]");
//		 System.out.println("t2 = " + t2 + " [" + 100.0 * t2 / TT + " %]");
//		 System.out.println("t3 = " + t3 + " [" + 100.0 * t3 / TT + " %]");
//		 System.out.println("t4 = " + t4 + " [" + 100.0 * t4 / TT + " %]");
//		 System.out.println("t5 = " + t5 + " [" + 100.0 * t5 / TT + " %]");
//		 System.out.println("TT = " + TT + " [" + 100.0 + " %]");
//		
//		 System.exit(0);

		return totalError;
	}
}
