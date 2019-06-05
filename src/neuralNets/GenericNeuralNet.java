package neuralNets;

import graphics.ImageManager;
import graphics.ImagePanel;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import links.Link;
import neurons.DeadNeuron;
import neurons.FilterNeuron;
import neurons.GenericNeuron;
import neurons.LinearNeuron;
import neurons.NeuronType;
import neurons.PoolNeuron;
import neurons.SigmoidNeuron;
import auxiliar.PairOfNeurons;

public class GenericNeuralNet implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<GenericNeuron> inputNeurons, outputNeurons, biasNeurons;
	private ArrayList<ArrayList<GenericNeuron>> hiddenNeurons;
	private ArrayList<ArrayList<Link>> biasLinks;
	private ArrayList<ArrayList<Link>> links;
	private int inputNeuronsSize, outputNeuronsSize, numberOfLayers;
	private String name;

	private ArrayList<ArrayList<Double>> currentLinksWeights;
	private ArrayList<ArrayList<Double>> currentBiasLinksWeights;

	public GenericNeuralNet(String name) {
		this.name = name;
	}

	public GenericNeuralNet createExpandedNeuralNet(
			ArrayList<Double> initialWeights) {
		GenericNeuralNet genericNeuralNetToReturn = new GenericNeuralNet(
				this.getName());

		int maxIdFound = 0;

		// Output Neurons
		ArrayList<GenericNeuron> outputNeurons = new ArrayList<GenericNeuron>();
		for (GenericNeuron originalNeuron : this.outputNeurons) {
			maxIdFound = Math.max(maxIdFound, originalNeuron.getID());
			outputNeurons.add(getNewNeuron(originalNeuron.getID(),
					originalNeuron.getType(), originalNeuron.getArgs(), -1));
		}
		genericNeuralNetToReturn.setOutputNeurons(outputNeurons);

		// Bias Neurons
		ArrayList<GenericNeuron> biasNeurons = new ArrayList<GenericNeuron>();
		for (GenericNeuron originalNeuron : this.biasNeurons) {
			maxIdFound = Math.max(maxIdFound, originalNeuron.getID());
			biasNeurons.add(getNewNeuron(originalNeuron.getID(),
					originalNeuron.getType(), originalNeuron.getArgs(), -1));
		}
		for (GenericNeuron biasNeuron : biasNeurons) {
			// biasNeuron.addIncomeInformation(new TripletOfWeightValueID(1.0,
			// 255.0));
			biasNeuron.addIncomeInformation(1.0, 255.0, -1);
			biasNeuron.activateBeta();
		}
		genericNeuralNetToReturn.setBiasNeurons(biasNeurons);

		// Hidden Neurons
		ArrayList<ArrayList<GenericNeuron>> hiddenNeurons = new ArrayList<ArrayList<GenericNeuron>>();
		ArrayList<GenericNeuron> layer1 = new ArrayList<GenericNeuron>();
		ArrayList<Double> inputArgs = new ArrayList<Double>();
		inputArgs.add(1.0);
		inputArgs.add(0.0);
		ArrayList<Double> deadArgs = new ArrayList<Double>();
		inputArgs.add(0.0);
		inputArgs.add(0.0);
		for (GenericNeuron originalNeuron : this.inputNeurons) {
			maxIdFound = Math.max(maxIdFound, originalNeuron.getID());
			if (originalNeuron.getClass().equals(neurons.DeadNeuron.class)) {
				layer1.add(getNewNeuron(originalNeuron.getID(),
						NeuronType.Dead, deadArgs, -1));
			} else {
				layer1.add(getNewNeuron(originalNeuron.getID(),
						NeuronType.Linear, inputArgs, -1));
			}
		}
		hiddenNeurons.add(layer1);
		for (ArrayList<GenericNeuron> array : this.hiddenNeurons) {
			ArrayList<GenericNeuron> newArray = new ArrayList<GenericNeuron>();
			for (GenericNeuron originalNeuron : array) {
				maxIdFound = Math.max(maxIdFound, originalNeuron.getID());
				newArray.add(getNewNeuron(originalNeuron.getID(),
						originalNeuron.getType(), originalNeuron.getArgs(), -1));
			}
			hiddenNeurons.add(newArray);
		}
		genericNeuralNetToReturn.setHiddenNeurons(hiddenNeurons);

		// InputNeurons
		int nextID = maxIdFound + 1;
		ArrayList<GenericNeuron> inputNeurons = new ArrayList<GenericNeuron>();
		for (GenericNeuron originalNeuron : this.inputNeurons) {
			if (!originalNeuron.getClass().equals(neurons.DeadNeuron.class)) {
				inputNeurons.add(getNewNeuron(nextID, NeuronType.Linear,
						inputArgs, -1));
				nextID = nextID + 1;
			}
		}
		genericNeuralNetToReturn.setInputNeurons(inputNeurons);

		// Bias Links
		ArrayList<ArrayList<Link>> biasLinks = new ArrayList<ArrayList<Link>>();
		biasLinks.add(new ArrayList<Link>());
		for (ArrayList<Link> array : this.biasLinks) {
			ArrayList<Link> newArray = new ArrayList<Link>();
			for (Link oldLink : array) {
				Link linkToAdd = new Link(false);
				for (PairOfNeurons pair : oldLink.getPairsOfNeurons()) {
					int inID = pair.getInputNeuron().getID();
					int outID = pair.getOutputNeuron().getID();
					for (GenericNeuron nin : biasNeurons) {
						if (nin.getID() == inID) {
							for (GenericNeuron nout : outputNeurons) {
								if (nout.getID() == outID) {
									linkToAdd
											.addPairOfNeurons(new PairOfNeurons(
													nin, nout));
								}
							}
							for (ArrayList<GenericNeuron> alist : hiddenNeurons) {
								for (GenericNeuron nout : alist) {
									if (nout.getID() == outID) {
										linkToAdd
												.addPairOfNeurons(new PairOfNeurons(
														nin, nout));
									}
								}
							}
						}
					}

				}
				linkToAdd.setWeight(oldLink.getWeight());
				linkToAdd.setFinal(true);
				newArray.add(linkToAdd);
			}
			biasLinks.add(newArray);
		}
		genericNeuralNetToReturn.setBiasLinks(biasLinks);

		// Links
		ArrayList<ArrayList<Link>> links = new ArrayList<ArrayList<Link>>();
		ArrayList<Link> array = new ArrayList<Link>();
		int hiddenIndex = 0;
		for (int i = 0; i < inputNeurons.size(); i++) {
			while (hiddenNeurons.get(0).get(hiddenIndex).getClass()
					.equals(neurons.DeadNeuron.class)) {
				hiddenIndex = hiddenIndex + 1;
			}
			Link linkToAdd = new Link(false);
			linkToAdd.setFinal(false);
			linkToAdd.setWeight(initialWeights.get(i));
			linkToAdd.setMinValue(0.0);
			linkToAdd.setMaxValue(255.0);
			linkToAdd.setChangeConstrained(true);
			linkToAdd.setMinChange(0.1);
			linkToAdd.setMaxChange(3.0);
			linkToAdd.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(i),
					hiddenNeurons.get(0).get(hiddenIndex)));
			array.add(linkToAdd);
			hiddenIndex = hiddenIndex + 1;
		}
		links.add(array);
		for (ArrayList<Link> linksArray : this.links) {
			ArrayList<Link> newLinksArray = new ArrayList<Link>();
			for (Link oldLink : linksArray) {
				Link linkToAdd = new Link(false);
				for (PairOfNeurons pair : oldLink.getPairsOfNeurons()) {
					int inID = pair.getInputNeuron().getID();
					int outID = pair.getOutputNeuron().getID();

					for (ArrayList<GenericNeuron> alist : hiddenNeurons) {
						for (GenericNeuron nin : alist) {
							if (nin.getID() == inID) {
								for (ArrayList<GenericNeuron> alist2 : hiddenNeurons) {
									for (GenericNeuron nout : alist2) {
										if (nout.getID() == outID) {
											linkToAdd
													.addPairOfNeurons(new PairOfNeurons(
															nin, nout));
										}
									}
								}
								for (GenericNeuron nout : outputNeurons) {
									if (nout.getID() == outID) {
										linkToAdd
												.addPairOfNeurons(new PairOfNeurons(
														nin, nout));
									}
								}
							}
						}
					}
				}
				linkToAdd.setWeight(oldLink.getWeight());
				linkToAdd.setFinal(true);
				newLinksArray.add(linkToAdd);
			}
			links.add(newLinksArray);
		}
		genericNeuralNetToReturn.setLinks(links);

		genericNeuralNetToReturn.setName(this.getName() + "EXPANDED");
		genericNeuralNetToReturn.setInputNeuronsSize(inputNeurons.size());
		genericNeuralNetToReturn.setOutputNeuronsSize(this.outputNeuronsSize);
		genericNeuralNetToReturn.setNumberOfLayers(this.numberOfLayers + 1);

		genericNeuralNetToReturn.startingForwardFeed();
		genericNeuralNetToReturn.resetNeuronValues();
		genericNeuralNetToReturn.resetLinkDeltas();

		return genericNeuralNetToReturn;
	}

	public GenericNeuron getNewNeuron(int id, NeuronType type,
			ArrayList<Double> args, int rgb) {
		switch (type) {
		case Linear:
			return new LinearNeuron(id, args, rgb);
		case Pool:
			return new PoolNeuron(id, rgb);
		case Sigmoid:
			return new SigmoidNeuron(id, args, rgb);
		case Dead:
			return new DeadNeuron(id, rgb);
		case Filter:
			return new FilterNeuron(id, rgb);
		}
		return null;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setInputNeuronsSize(int inputNeuronsSize) {
		this.inputNeuronsSize = inputNeuronsSize;
	}

	private void setOutputNeuronsSize(int outputNeuronsSize) {
		this.outputNeuronsSize = outputNeuronsSize;
	}

	private void setNumberOfLayers(int numberOfLayers) {
		this.numberOfLayers = numberOfLayers;
	}

	public int getInputNeuronsSize() {
		return inputNeuronsSize;
	}

	public int getOutputNeuronsSize() {
		return outputNeuronsSize;
	}

	public void setInputNeurons(ArrayList<GenericNeuron> inputNeurons) {
		this.inputNeurons = inputNeurons;
		inputNeuronsSize = inputNeurons.size();
	}

	public void setOutputNeurons(ArrayList<GenericNeuron> outputNeurons) {
		this.outputNeurons = outputNeurons;
		outputNeuronsSize = outputNeurons.size();
	}

	public void setBiasNeurons(ArrayList<GenericNeuron> biasNeurons) {
		this.biasNeurons = biasNeurons;
	}

	public void setHiddenNeurons(
			ArrayList<ArrayList<GenericNeuron>> hiddenNeurons) {
		this.hiddenNeurons = hiddenNeurons;
		numberOfLayers = hiddenNeurons.size();
	}

	public void setBiasLinks(ArrayList<ArrayList<Link>> biasLinks) {
		this.biasLinks = biasLinks;
	}

	public void setLinks(ArrayList<ArrayList<Link>> links) {
		this.links = links;
	}

	public ArrayList<GenericNeuron> getInputNeurons() {
		return inputNeurons;
	}

	public ArrayList<GenericNeuron> getOutputNeurons() {
		return outputNeurons;
	}

	public ArrayList<GenericNeuron> getBiasNeurons() {
		return biasNeurons;
	}

	public ArrayList<ArrayList<GenericNeuron>> getHiddenNeurons() {
		return hiddenNeurons;
	}

	public int getNumberOfLayers() {
		return numberOfLayers;
	}

	public ArrayList<ArrayList<Link>> getBiasLinks() {
		return biasLinks;
	}

	public ArrayList<ArrayList<Link>> getLinks() {
		return links;
	}

	public void setBiasLinksWeights(ArrayList<ArrayList<Double>> weights) {
		for (int i = 0; i < biasLinks.size(); i++) {
			for (int j = 0; j < biasLinks.get(i).size(); j++) {
				biasLinks.get(i).get(j).setWeight(weights.get(i).get(j));
			}
		}
	}

	public void setLinksWeights(ArrayList<ArrayList<Double>> weights) {
		for (int i = 0; i < links.size(); i++) {
			for (int j = 0; j < links.get(i).size(); j++) {
				links.get(i).get(j).setWeight(weights.get(i).get(j));
			}
		}
	}

	public void resetNeuronValues() {

		inputNeurons.stream().forEach((n) -> {
			n.resetInput();
			n.resetDelta();
		});
		outputNeurons.stream().forEach((n) -> {
			n.resetInput();
			n.resetDelta();
		});
		hiddenNeurons.stream().forEach((nList) -> {
			nList.stream().forEach((n) -> {
				n.resetInput();
				n.resetDelta();
			});
		});
		biasNeurons.stream().forEach((n) -> {
			n.resetDelta();
		});
	}

	public void resetLinkDeltas() {

		links.stream().forEach((linkList) -> {
			linkList.stream().forEach((link) -> {
				link.setWeightDerivative(0.0);
			});
		});
		biasLinks.stream().forEach((biasLinkList) -> {
			biasLinkList.stream().forEach((biasLink) -> {
				biasLink.setWeightDerivative(0.0);
			});
		});

		// for (int i = 0; i < numberOfLayers + 1; i++) {
		// for (int j = 0; j < links.get(i).size(); j++) {
		// links.get(i).get(j).setWeightDerivative(0.0);
		// }
		// for (int j = 0; j < biasLinks.get(i).size(); j++) {
		// biasLinks.get(i).get(j).setWeightDerivative(0.0);
		// }
		// }
	}

	public void startingForwardFeed() {

		// hidden
		for (int i = 0; i < numberOfLayers; i++) {
			links.get(i).stream().forEach((link) -> {
				link.transmit();
			});
			biasLinks.get(i).stream().forEach((biasLink) -> {
				biasLink.transmit();
			});
			hiddenNeurons.get(i).stream().forEach((hiddenNeuron) -> {
				hiddenNeuron.activateStartingBeta();
			});

			// for (int j = 0; j < links.get(i).size(); j++) {
			// links.get(i).get(j).transmit();
			// }
			// for (int j = 0; j < biasLinks.get(i).size(); j++) {
			// biasLinks.get(i).get(j).transmit();
			// }
			// for (GenericNeuron hiddenNeuron : hiddenNeurons.get(i)) {
			// hiddenNeuron.activateStartingBeta();
			// }
		}

		// output

		links.get(numberOfLayers).stream().forEach((link) -> {
			link.transmit();
		});
		biasLinks.get(numberOfLayers).stream().forEach((biasLink) -> {
			biasLink.transmit();
		});
		outputNeurons.stream().forEach((outputNeuron) -> {
			outputNeuron.activateStartingBeta();
		});

		// for (int j = 0; j < links.get(numberOfLayers).size(); j++) {
		// links.get(numberOfLayers).get(j).transmit();
		// }
		// for (int j = 0; j < biasLinks.get(numberOfLayers).size(); j++) {
		// biasLinks.get(numberOfLayers).get(j).transmit();
		// }
		// for (GenericNeuron outputNeuron : outputNeurons) {
		// outputNeuron.activateStartingBeta();
		// }
	}

	public void forwardFeed() {

//		long t1 = 0;
//		long t2 = 0;
//		long t3 = 0;
//		long t4 = 0;
//		ArrayList<Long> t1s = new ArrayList<>();
//		ArrayList<String> t1types = new ArrayList<>();

		// hidden
		for (int i = 0; i < numberOfLayers; i++) {
//			long ta = System.nanoTime();
			links.get(i).stream().forEach((link) -> {
				link.transmit();
			});
//			long tb = System.nanoTime();
//			t1 = t1 + tb - ta;
//			t1s.add(tb - ta);
//			t1types.add(links.get(i).get(0).getPairsOfNeurons().get(0).getInputNeuron().getType()+" => "+links.get(i).get(0).getPairsOfNeurons().get(0).getOutputNeuron().getType());
//			ta = System.nanoTime();
			biasLinks.get(i).stream().forEach((biasLink) -> {
				biasLink.transmit();
			});
//			tb = System.nanoTime();
//			t2 = t2 + tb - ta;
//			ta = System.nanoTime();
			hiddenNeurons.get(i).stream().forEach((hiddenNeuron) -> {
				hiddenNeuron.activateBeta();
			});
//			tb = System.nanoTime();
//			t3 = t3 + tb - ta;
		}

		// output
//		long ta = System.nanoTime();
		links.get(numberOfLayers).stream().forEach((link) -> {
			link.transmit();
		});
//		long tb = System.nanoTime();
//		t1 = t1 + tb - ta;
//		t1s.add(tb - ta);
//
//		t1types.add(links.get(numberOfLayers).get(0).getPairsOfNeurons().get(0).getInputNeuron().getType()+" => "+links.get(numberOfLayers).get(0).getPairsOfNeurons().get(0).getOutputNeuron().getType());
//		ta = System.nanoTime();
		biasLinks.get(numberOfLayers).stream().forEach((biasLink) -> {
			biasLink.transmit();
		});
//		tb = System.nanoTime();
//		t2 = t2 + tb - ta;
//		ta = System.nanoTime();
		outputNeurons.stream().forEach((outputNeuron) -> {
			outputNeuron.activateBeta();
		});
//		tb = System.nanoTime();
//		t4 = t4 + tb - ta;
//
//		float TT = t1 + t2 + t3 + t4;
//		System.out.println("t1s = " + Arrays.toString(t1s.toArray()));
//		System.out.println("t1types = " + Arrays.toString(t1types.toArray()));
//		System.out.println("t1 = " + t1 + " [" + 100.0 * t1 / TT + " %]");
//		System.out.println("t2 = " + t2 + " [" + 100.0 * t2 / TT + " %]");
//		System.out.println("t3 = " + t3 + " [" + 100.0 * t3 / TT + " %]");
//		System.out.println("t4 = " + t4 + " [" + 100.0 * t4 / TT + " %]");
//		System.out.println("TT = " + TT + " [" + 100.0 + " %]");
//
//		System.exit(0);
	}

	public void backwardFeed() {

		// hidden deltas
		for (int i = 0; i < numberOfLayers; i++) {
			links.get(numberOfLayers - i).stream().forEach((link) -> {
				link.transmitDelta();
			});
			biasLinks.get(numberOfLayers - i).stream().forEach((biasLink) -> {
				biasLink.transmitDelta();
			});
		}
		// And this is enough, all deltas are computed
	}

	public void saveCurrentWeights() {
		currentLinksWeights = new ArrayList<ArrayList<Double>>();
		currentBiasLinksWeights = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < numberOfLayers + 1; i++) {
			ArrayList<Double> auxL = new ArrayList<Double>();
			for (int j = 0; j < links.get(i).size(); j++) {
				auxL.add(links.get(i).get(j).getWeight());
			}
			currentLinksWeights.add(auxL);

			ArrayList<Double> auxBL = new ArrayList<Double>();
			for (int j = 0; j < biasLinks.get(i).size(); j++) {
				auxBL.add(biasLinks.get(i).get(j).getWeight());
			}
			currentBiasLinksWeights.add(auxBL);
		}
	}

	public void rollbackLinkWeights() {
		for (int i = 0; i < numberOfLayers + 1; i++) {
			for (int j = 0; j < links.get(i).size(); j++) {
				if (!links.get(i).get(j).isFinal()) {
					links.get(i).get(j)
							.setWeight(currentLinksWeights.get(i).get(j));
				}
			}
			for (int j = 0; j < biasLinks.get(i).size(); j++) {
				if (!biasLinks.get(i).get(j).isFinal()) {
					biasLinks.get(i).get(j)
							.setWeight(currentBiasLinksWeights.get(i).get(j));
				}
			}
		}
	}

	public void updateLinkWeights(double learningRate) {

		links.stream().forEach((listOfLinks) -> {
			if (!listOfLinks.get(0).isFinal()) {
				listOfLinks.stream().forEach((link) -> {
					link.activateDerivative(learningRate);
				});
			}
		});
		biasLinks.stream().forEach((listOfBiasLinks) -> {
			if (listOfBiasLinks.size() > 0) {
				if (!listOfBiasLinks.get(0).isFinal()) {
					listOfBiasLinks.stream().forEach((biasLink) -> {
						biasLink.activateDerivative(learningRate);
					});
				}
			}
		});

		// for (int i = 0; i < numberOfLayers + 1; i++) {
		// for (int j = 0; j < links.get(i).size(); j++) {
		// if (!links.get(i).get(j).isFinal()) {
		// // ArrayList<Integer> inIds = new ArrayList<Integer>();
		// // ArrayList<Integer> outIds = new ArrayList<Integer>();
		// // for (PairOfNeurons pair : links.get(i).get(j)
		// // .getPairsOfNeurons()) {
		// // inIds.add(pair.getInputNeuron().getID());
		// // outIds.add(pair.getOutputNeuron().getID());
		// // }
		// // System.out
		// // .println("link connecting "
		// // + Arrays.toString(inIds.toArray())
		// // + "->"
		// // + Arrays.toString(outIds.toArray())
		// // + " is changing its weight from ["
		// // + links.get(i).get(j).getWeight()
		// // + "] => ["
		// // + (links.get(i).get(j).getWeight() - learningRate
		// // * links.get(i).get(j)
		// // .getWeightDerivative())
		// // + "] = ("
		// // + links.get(i).get(j).getWeight()
		// // + " - "
		// // + learningRate
		// // + " * "
		// // + links.get(i).get(j)
		// // .getWeightDerivative() + ")");
		//
		// links.get(i).get(j).activateDerivative(learningRate);
		// }
		// }
		// for (int j = 0; j < biasLinks.get(i).size(); j++) {
		// if (!biasLinks.get(i).get(j).isFinal()) {
		// // if (verbose) {
		// // ArrayList<Integer> inIds = new ArrayList<Integer>();
		// // ArrayList<Integer> outIds = new ArrayList<Integer>();
		// // for (PairOfNeurons pair : biasLinks.get(i).get(j)
		// // .getPairsOfNeurons()) {
		// // inIds.add(pair.getInputNeuron().getID());
		// // outIds.add(pair.getOutputNeuron().getID());
		// // }
		// // System.out
		// // .println("bias link connecting "
		// // + Arrays.toString(inIds.toArray())
		// // + "->"
		// // + Arrays.toString(outIds.toArray())
		// // + " is changing its weight from ["
		// // + biasLinks.get(i).get(j).getWeight()
		// // + "] => ["
		// // + (biasLinks.get(i).get(j).getWeight() - learningRate
		// // * biasLinks.get(i).get(j)
		// // .getWeightDerivative())
		// // + "] = ("
		// // + biasLinks.get(i).get(j).getWeight()
		// // + " - "
		// // + learningRate
		// // + " * "
		// // + biasLinks.get(i).get(j)
		// // .getWeightDerivative() + ")");
		// // }
		// // biasLinks
		// // .get(i)
		// // .get(j)
		// // .setWeight(
		// // biasLinks.get(i).get(j).getWeight()
		// // - learningRate
		// // * biasLinks.get(i).get(j)
		// // .getWeightDerivative());
		// biasLinks.get(i).get(j).activateDerivative(learningRate);
		// }
		// }
		// }
	}

	public void setInput(ArrayList<Double> inputData) {
		// Fill in input neurons
		int idx = 0;
		for (int i = 0; i < inputNeuronsSize; i++) {
			GenericNeuron inputNeuron = inputNeurons.get(i);
			if (!inputNeuron.getClass().equals(neurons.DeadNeuron.class)) {
				// inputNeuron.addIncomeInformation(new TripletOfWeightValueID(
				// 1.0, inputData.get(idx)));
				inputNeuron.addIncomeInformation(1.0, inputData.get(idx), -1);
				inputNeuron.activateBeta();
				idx = idx + 1;
			}
		}
	}

	public ArrayList<Double> getExpectedOutput(ArrayList<Double> inputData) {

		// Reset
		resetNeuronValues();
		// System.out.println("getExpectedOutput, AFTER resetNeuronValues:");
		// printNN();

		// Fill in input neurons
		setInput(inputData);
		// System.out.println("getExpectedOutput, AFTER setInput:");
		// printNN();

		// Transmit
		forwardFeed();
		// System.out.println("getExpectedOutput, AFTER forwardFeed:");
		// printNN();
		// System.exit(0);

		ArrayList<Double> outputValues = new ArrayList<Double>(
				outputNeuronsSize);
		for (int i = 0; i < outputNeuronsSize; i++) {
			outputValues.add(outputNeurons.get(i).getOutput());
		}
		// printNN();

		return outputValues;
	}

	public void printNN() {
		System.out.println("");
		System.out.println("===================");
		System.out.println("Print NN");
		System.out.println("--------");
		startPrintNN();
		completePrintNN();
		startPrintNN();
	}

	protected void startPrintNN() {
		System.out.println("--------");
		System.out.println("GenericNeuralNet");
		System.out.println("--------");
	}

	private void completePrintNN() {
		System.out.println();

		int totalNumberOfNeurons = 0;
		int totalNumberOfLinks = 0;

		for (int i = 0; i < biasNeurons.size(); i++) {
			System.out.println("Bias [" + biasNeurons.get(i).getID() + "]["
					+ biasNeurons.get(i).getClass().toString() + "]["
					+ Arrays.toString(biasNeurons.get(i).getArgs().toArray())
					+ "] in/out = [" + biasNeurons.get(i).getBeta() + "]/["
					+ biasNeurons.get(i).getOutput() + "], derivative = "
					+ biasNeurons.get(i).getDerivative() + ", delta = "
					+ biasNeurons.get(i).getDelta());
		}
		totalNumberOfNeurons = totalNumberOfNeurons + biasNeurons.size();
		System.out.println("--------");
		for (int i = 0; i < inputNeuronsSize; i++) {
			System.out.println("Input [" + inputNeurons.get(i).getID() + "]["
					+ inputNeurons.get(i).getClass().toString() + "]["
					+ Arrays.toString(inputNeurons.get(i).getArgs().toArray())
					+ "]  in/out = [" + inputNeurons.get(i).getBeta() + "]/["
					+ inputNeurons.get(i).getOutput() + "], derivative = "
					+ inputNeurons.get(i).getDerivative() + ", delta = "
					+ inputNeurons.get(i).getDelta());
		}
		totalNumberOfNeurons = totalNumberOfNeurons + inputNeuronsSize;
		System.out.println("--------");
		for (int j = 0; j < numberOfLayers; j++) {
			for (int i = 0; i < hiddenNeurons.get(j).size(); i++) {
				System.out
						.println("Hidden ["
								+ hiddenNeurons.get(j).get(i).getID()
								+ "]["
								+ hiddenNeurons.get(j).get(i).getClass()
										.toString()
								+ "]["
								+ Arrays.toString(hiddenNeurons.get(j).get(i)
										.getArgs().toArray()) + "]  in/out = ["
								+ hiddenNeurons.get(j).get(i).getBeta() + "]/["
								+ hiddenNeurons.get(j).get(i).getOutput()
								+ "], derivative = "
								+ hiddenNeurons.get(j).get(i).getDerivative()
								+ ", delta = "
								+ hiddenNeurons.get(j).get(i).getDelta());
			}
			totalNumberOfNeurons = totalNumberOfNeurons
					+ hiddenNeurons.get(j).size();
			System.out.println("--");
		}
		System.out.println("--------");
		for (int i = 0; i < outputNeuronsSize; i++) {
			System.out.println("Output [" + outputNeurons.get(i).getID() + "]["
					+ outputNeurons.get(i).getClass().toString() + "]["
					+ Arrays.toString(outputNeurons.get(i).getArgs().toArray())
					+ "]  in/out = [" + outputNeurons.get(i).getBeta() + "]/["
					+ outputNeurons.get(i).getOutput() + "], derivative = "
					+ outputNeurons.get(i).getDerivative() + ", delta = "
					+ outputNeurons.get(i).getDelta());
		}
		totalNumberOfNeurons = totalNumberOfNeurons + outputNeuronsSize;
		System.out.println("--------");
		System.out.println("");

		for (int i = 0; i < numberOfLayers + 1; i++) {
			for (int j = 0; j < biasLinks.get(i).size(); j++) {
				ArrayList<Integer> inIds = new ArrayList<Integer>();
				ArrayList<Integer> outIds = new ArrayList<Integer>();
				for (PairOfNeurons pair : biasLinks.get(i).get(j)
						.getPairsOfNeurons()) {
					inIds.add(pair.getInputNeuron().getID());
					outIds.add(pair.getOutputNeuron().getID());
				}
				totalNumberOfLinks = totalNumberOfLinks
						+ biasLinks.get(i).get(j).getPairsOfNeurons().size();
				System.out.println("BiasLink connecting "
						+ Arrays.toString(inIds.toArray()) + "->"
						+ Arrays.toString(outIds.toArray()) + " has weight "
						+ biasLinks.get(i).get(j).getWeight()
						+ ", weightDerivative = "
						+ biasLinks.get(i).get(j).getWeightDerivative());
			}
			System.out.println("--");
		}
		System.out.println("--------");
		for (int i = 0; i < numberOfLayers + 1; i++) {
			for (int j = 0; j < links.get(i).size(); j++) {
				ArrayList<Integer> inIds = new ArrayList<Integer>();
				ArrayList<Integer> outIds = new ArrayList<Integer>();
				for (PairOfNeurons pair : links.get(i).get(j)
						.getPairsOfNeurons()) {
					inIds.add(pair.getInputNeuron().getID());
					outIds.add(pair.getOutputNeuron().getID());
				}
				totalNumberOfLinks = totalNumberOfLinks
						+ links.get(i).get(j).getPairsOfNeurons().size();
				System.out.println("Link connecting ["
						+ Arrays.toString(inIds.toArray()) + "->"
						+ Arrays.toString(outIds.toArray()) + " has weight "
						+ links.get(i).get(j).getWeight()
						+ ", weightDerivative = "
						+ links.get(i).get(j).getWeightDerivative());
			}
			System.out.println("--");
		}
		System.out.println("--------");
		System.out.println("Total number of Neurons = " + totalNumberOfNeurons);
		System.out.println("Total number of Links = " + totalNumberOfLinks);
		System.out.println("===================");
		System.out.println("");
	}

	public void saveToImage() {
		ImagePanel panel = new ImagePanel(this);

		ImageManager imageManager = new ImageManager();
		try {
			BufferedImage bi = imageManager.getScreenShot(panel);

			System.out.println("...saving " + "SavedNNs/" + name + ".png");

			imageManager.saveToImage(bi, "SavedNNs/" + name + ".png");

			System.out.println("OK saving " + "SavedNNs/" + name + ".png");
		} catch (Exception e) {
			System.out.println("Error saving CNN [" + "SavedNNs/" + name
					+ "]: " + e.getMessage());
		}
	}
}
