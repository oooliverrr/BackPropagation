package neuralNets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import links.Link;
import neurons.GenericNeuron;
import neurons.NeuronType;
import auxiliar.PairOfNeurons;
import auxiliar.TripletLayerNumberAndTypeAndArgs;

public class BasicNeuralNet extends GenericNeuralNet {

	private static final long serialVersionUID = 1L;

	public BasicNeuralNet(String name,
			TripletLayerNumberAndTypeAndArgs inputLayer,
			TripletLayerNumberAndTypeAndArgs outputLayer,
			ArrayList<TripletLayerNumberAndTypeAndArgs> layers) {
		super(name);

		// Sizes
		int inputNeuronsSize = inputLayer.getNumberOfNeurons();
		int outputNeuronsSize = outputLayer.getNumberOfNeurons();
		int numberOfLayers = layers.size();

		System.out.println("Creating basic neural network. ");
		System.out.println("inputNeuronsSize = " + inputNeuronsSize + " ["
				+ inputLayer.getNeuronType() + "]");
		System.out.println("outputNeuronsSize = " + outputNeuronsSize + " ["
				+ outputLayer.getNeuronType() + "]");
		System.out.println("numberOfLayers = "
				+ numberOfLayers
				+ " "
				+ Arrays.toString(layers.stream().map(t -> t.getNeuronType())
						.collect(Collectors.toList()).toArray()));
		for (int i = 0; i < numberOfLayers; i++) {
			System.out.println("... layer " + i + " size = "
					+ layers.get(i).getNumberOfNeurons() + " ["
					+ layers.get(i).getNeuronType() + "]");
		}
		System.out.println("-----------------");
		System.out.println("");

		// Init links, biasLinks, linkWeightDerivatives
		ArrayList<ArrayList<Link>> biasLinks = new ArrayList<ArrayList<Link>>(
				numberOfLayers + 1);
		ArrayList<ArrayList<Link>> links = new ArrayList<ArrayList<Link>>(
				numberOfLayers + 1);

		int idCount = 0;

		// Input
		ArrayList<GenericNeuron> inputNeurons = new ArrayList<GenericNeuron>(
				inputNeuronsSize);
		NeuronType inputNeuronType = inputLayer.getNeuronType();
		ArrayList<Double> inputArgs = inputLayer.getArgs();
		for (int i = 0; i < inputNeuronsSize; i++) {
			inputNeurons.add(getNewNeuron(idCount, inputNeuronType, inputArgs,
					-1));
			System.out.println("Creating input neuron [" + idCount + "] = "
					+ inputNeurons.get(i).toString());
			idCount = idCount + 1;
		}
		setInputNeurons(inputNeurons);
		System.out.println("-----------------");

		// Output
		ArrayList<GenericNeuron> outputNeurons = new ArrayList<GenericNeuron>(
				outputNeuronsSize);
		NeuronType outputNeuronType = outputLayer.getNeuronType();
		ArrayList<Double> outputArgs = outputLayer.getArgs();
		for (int i = 0; i < outputNeuronsSize; i++) {
			outputNeurons.add(getNewNeuron(idCount, outputNeuronType,
					outputArgs, -1));
			System.out.println("Creating output neuron [" + idCount + "] = "
					+ outputNeurons.get(i).toString());
			idCount = idCount + 1;
		}
		setOutputNeurons(outputNeurons);
		System.out.println("-----------------");

		// Bias (same number as number of hidden layers plus one (output))
		ArrayList<GenericNeuron> biasNeurons = new ArrayList<GenericNeuron>(
				numberOfLayers + 1);
		NeuronType biasNeuronType = NeuronType.Linear;
		ArrayList<Double> biasArgs = new ArrayList<Double>(Arrays.asList(1.0,
				0.0));
		for (int i = 0; i < numberOfLayers + 1; i++) {
			biasNeurons
					.add(getNewNeuron(idCount, biasNeuronType, biasArgs, -1));
			System.out.println("Creating bias neuron [" + idCount + "] = "
					+ biasNeurons.get(i).toString());
			idCount = idCount + 1;
		}
		for (GenericNeuron biasNeuron : biasNeurons) {
			// biasNeuron
			biasNeuron.addIncomeInformation(1.0, 1.0, -1);
			biasNeuron.activateBeta();
		}
		setBiasNeurons(biasNeurons);
		System.out.println("-----------------");

		// Hidden Layers
		ArrayList<ArrayList<GenericNeuron>> hiddenNeurons = new ArrayList<ArrayList<GenericNeuron>>(
				numberOfLayers);
		for (int i = 0; i < numberOfLayers; i++) {
			TripletLayerNumberAndTypeAndArgs hiddenTriplet = layers.get(i);

			int numberOfHiddenNeurons = hiddenTriplet.getNumberOfNeurons();
			NeuronType hiddenNeuronType = hiddenTriplet.getNeuronType();
			ArrayList<Double> hiddenArgs = hiddenTriplet.getArgs();

			ArrayList<GenericNeuron> hiddenNeuronsLayer = new ArrayList<GenericNeuron>(
					numberOfHiddenNeurons);
			for (int j = 0; j < numberOfHiddenNeurons; j++) {
				hiddenNeuronsLayer.add(getNewNeuron(idCount, hiddenNeuronType,
						hiddenArgs, -1));
				System.out.println("Creating hidden neuron [" + idCount
						+ "] (layer " + i + ") = "
						+ hiddenNeuronsLayer.get(j).toString());
				idCount = idCount + 1;
			}
			hiddenNeurons.add(hiddenNeuronsLayer);
		}
		setHiddenNeurons(hiddenNeurons);
		System.out.println("-----------------");
		System.out.println("");

		// Bias Links-hidden
		for (int i = 0; i < numberOfLayers; i++) {
			TripletLayerNumberAndTypeAndArgs hiddenTriplet = layers.get(i);

			int numberOfHiddenNeurons = hiddenTriplet.getNumberOfNeurons();

			ArrayList<Link> layerOfLinks = new ArrayList<Link>(
					numberOfHiddenNeurons);
			for (int j = 0; j < numberOfHiddenNeurons; j++) {
				Link l = new Link(true);
				l.addPairOfNeurons(new PairOfNeurons(biasNeurons.get(i),
						hiddenNeurons.get(i).get(j)));
				System.out.println("Creating bias link between ["
						+ biasNeurons.get(i).getID() + "]["
						+ hiddenNeurons.get(i).get(j).getID() + "] = "
						+ l.toString());
				layerOfLinks.add(l);
			}
			biasLinks.add(layerOfLinks);
		}
		// Bias Links-Y
		ArrayList<Link> layerOfLinks = new ArrayList<Link>(outputNeuronsSize);
		for (int j = 0; j < outputNeuronsSize; j++) {
			Link l = new Link(true);
			l.addPairOfNeurons(new PairOfNeurons(biasNeurons
					.get(numberOfLayers), outputNeurons.get(j)));
			System.out.println("Creating bias link between ["
					+ biasNeurons.get(numberOfLayers).getID() + "]["
					+ outputNeurons.get(j).getID() + "] = " + l.toString());
			layerOfLinks.add(l);
		}
		biasLinks.add(layerOfLinks);
		setBiasLinks(biasLinks);
		System.out.println("-----------------");

		if (numberOfLayers > 0) {
			// Links input-1st layer
			int numberOfNeuronsIn1stHiddenLayer = layers.get(0)
					.getNumberOfNeurons();
			ArrayList<Link> X1Links = new ArrayList<Link>(inputNeuronsSize);
			for (int i = 0; i < inputNeuronsSize; i++) {
				for (int j = 0; j < numberOfNeuronsIn1stHiddenLayer; j++) {
					Link l = new Link(true);
					l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(i),
							hiddenNeurons.get(0).get(j)));
					System.out.println("Creating normal link between X["
							+ inputNeurons.get(i).getID() + "]H["
							+ hiddenNeurons.get(0).get(j).getID() + "] = "
							+ l.toString());
					X1Links.add(l);
				}
			}
			links.add(X1Links);
			System.out.println("-----------------");

			// Links layer 2-3, 3-4, ---, (N-1)-N
			if (numberOfLayers > 1) {
				for (int i = 0; i < numberOfLayers - 1; i++) {
					TripletLayerNumberAndTypeAndArgs hiddenTripletA = layers
							.get(i);
					TripletLayerNumberAndTypeAndArgs hiddenTripletB = layers
							.get(i + 1);

					int numberOfHiddenNeuronsInLayerA = hiddenTripletA
							.getNumberOfNeurons();
					int numberOfHiddenNeuronsInLayerB = hiddenTripletB
							.getNumberOfNeurons();

					ArrayList<Link> auxiliar1 = new ArrayList<Link>(
							numberOfHiddenNeuronsInLayerA);
					for (int j = 0; j < numberOfHiddenNeuronsInLayerA; j++) {
						for (int k = 0; k < numberOfHiddenNeuronsInLayerB; k++) {
							Link l = new Link(true);
							l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons
									.get(i).get(j), hiddenNeurons.get(i + 1)
									.get(k)));
							System.out
									.println("Creating normal link between H["
											+ hiddenNeurons.get(i).get(j)
													.getID()
											+ "]H["
											+ hiddenNeurons.get(i + 1).get(k)
													.getID() + "] = "
											+ l.toString());
							auxiliar1.add(l);
						}
					}
					links.add(auxiliar1);
				}
				System.out.println("-----------------");
			}

			// Links layer N-y
			int numberOfNeuronsInLastHiddenLayer = layers.get(
					numberOfLayers - 1).getNumberOfNeurons();
			ArrayList<Link> auxiliar1 = new ArrayList<Link>(
					numberOfNeuronsInLastHiddenLayer);
			for (int i = 0; i < numberOfNeuronsInLastHiddenLayer; i++) {
				for (int j = 0; j < outputNeuronsSize; j++) {
					Link l = new Link(true);
					l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(
							numberOfLayers - 1).get(i), outputNeurons.get(j)));
					System.out.println("Creating normal link between H["
							+ hiddenNeurons.get(numberOfLayers - 1).get(i)
									.getID() + "]Y["
							+ outputNeurons.get(j).getID() + "] = "
							+ l.toString());
					auxiliar1.add(l);
				}
			}
			links.add(auxiliar1);
			System.out.println("-----------------");
		} else {
			// Links input-output
			ArrayList<Link> X1Links = new ArrayList<Link>(inputNeuronsSize);
			for (int i = 0; i < inputNeuronsSize; i++) {
				for (int j = 0; j < outputNeuronsSize; j++) {
					Link l = new Link(true);
					l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(i),
							outputNeurons.get(j)));
					System.out.println("Creating normal link between X["
							+ inputNeurons.get(i).getID() + "]Y["
							+ outputNeurons.get(j).getID() + "] = "
							+ l.toString());
					X1Links.add(l);
				}
			}
			links.add(X1Links);
			System.out.println("-----------------");
		}
		setLinks(links);
	}
}
