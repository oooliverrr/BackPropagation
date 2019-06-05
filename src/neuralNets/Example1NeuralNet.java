package neuralNets;

import java.util.ArrayList;
import java.util.Arrays;

import links.Link;
import neurons.GenericNeuron;
import neurons.NeuronType;
import auxiliar.PairOfNeurons;

public class Example1NeuralNet extends GenericNeuralNet {

	private static final long serialVersionUID = 1L;

	public Example1NeuralNet(String name) {
		super(name);

		// Sizes
		int inputNeuronsSize = 2;
		int outputNeuronsSize = 2;
		int numberOfLayers = 1;

		System.out.println("Creating basic neural network. ");
		System.out.println("inputNeuronsSize = " + inputNeuronsSize + " ["
				+ NeuronType.Linear + "]");
		System.out.println("outputNeuronsSize = " + outputNeuronsSize + " ["
				+ NeuronType.Sigmoid + "]");
		System.out.println("numberOfLayers = " + numberOfLayers + " " + "["
				+ NeuronType.Sigmoid + "]");
		System.out.println("... layer " + 0 + " size = " + 2 + " ["
				+ NeuronType.Sigmoid + "]");
		System.out.println("-----------------");
		System.out.println("");

		// Init links, biasLinks, linkWeightDerivatives
		ArrayList<ArrayList<Link>> biasLinks = new ArrayList<ArrayList<Link>>(
				numberOfLayers + 1);
		ArrayList<ArrayList<Link>> links = new ArrayList<ArrayList<Link>>(
				numberOfLayers + 1);

		int idCount = 0;

		// 2 x input, Linear, m=2, n=1
		ArrayList<GenericNeuron> inputNeurons = new ArrayList<GenericNeuron>(
				inputNeuronsSize);
		NeuronType inputNeuronType = NeuronType.Linear;
		ArrayList<Double> inputArgs = new ArrayList<Double>(Arrays.asList(2.0,
				1.0));
		for (int i = 0; i < inputNeuronsSize; i++) {
			inputNeurons.add(getNewNeuron(idCount, inputNeuronType, inputArgs,
					-1));
			System.out.println("Creating input neuron [" + idCount + "] = "
					+ inputNeurons.get(i).toString());
			idCount = idCount + 1;
		}
		setInputNeurons(inputNeurons);
		System.out.println("-----------------");

		// 2 x output, Sigmoid, min=2, max=5, lambda=-1, threshold=6
		ArrayList<GenericNeuron> outputNeurons = new ArrayList<GenericNeuron>(
				outputNeuronsSize);
		NeuronType outputNeuronType = NeuronType.Sigmoid;
		ArrayList<Double> outputArgs = new ArrayList<Double>(Arrays.asList(
				-1.0, 6.0, 2.0, 5.0));
		for (int i = 0; i < outputNeuronsSize; i++) {
			outputNeurons.add(getNewNeuron(idCount, outputNeuronType,
					outputArgs, -1));
			System.out.println("Creating output neuron [" + idCount + "] = "
					+ outputNeurons.get(i).toString());
			idCount = idCount + 1;
		}
		setOutputNeurons(outputNeurons);
		System.out.println("-----------------");

		// Bias (same number as number of hidden layers plus one (output)),
		// Linear, m=1.0, n=0.0
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
			// .addIncomeInformation(new TripletOfWeightValueID(1.0, 1.0));
			biasNeuron.addIncomeInformation(1.0, 1.0, -1);
			biasNeuron.activateBeta();
		}
		setBiasNeurons(biasNeurons);
		System.out.println("-----------------");

		// 1 x hidden layer with 2 neurons, Sigmoid, min=1, max=2, lambda=3,
		// threshold=4
		ArrayList<ArrayList<GenericNeuron>> hiddenNeurons = new ArrayList<ArrayList<GenericNeuron>>(
				numberOfLayers);
		ArrayList<Double> hiddenArgs = new ArrayList<Double>(Arrays.asList(3.0,
				4.0, 1.0, 2.0));
		int numberOfHiddenNeurons = 2;
		NeuronType hiddenNeuronType = NeuronType.Sigmoid;
		for (int i = 0; i < numberOfLayers; i++) {
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
			ArrayList<Link> layerOfLinks = new ArrayList<Link>(
					numberOfHiddenNeurons);
			for (int j = 0; j < numberOfHiddenNeurons; j++) {
				Link l = new Link(false);
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
			Link l = new Link(false);
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

		// Links input-1st layer
		ArrayList<Link> X1Links = new ArrayList<Link>(inputNeuronsSize);
		Link l = new Link(true);
		l.setWeight(0.1);
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(0), hiddenNeurons
				.get(0).get(0)));
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(0), hiddenNeurons
				.get(0).get(1)));
		System.out.println("Creating normal link between X["
				+ inputNeurons.get(0).getID() + "]H["
				+ hiddenNeurons.get(0).get(0).getID() + "] and X["
				+ inputNeurons.get(0).getID() + "]H["
				+ hiddenNeurons.get(0).get(1).getID() + "] = " + l.toString());
		X1Links.add(l);

		l = new Link(true);
		l.setWeight(0.4);
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(1), hiddenNeurons
				.get(0).get(0)));
		System.out.println("Creating normal link between X["
				+ inputNeurons.get(1).getID() + "]H["
				+ hiddenNeurons.get(0).get(0).getID() + "] = " + l.toString());
		X1Links.add(l);
		links.add(X1Links);
		System.out.println("-----------------");

		// Links 1st layer-output
		ArrayList<Link> Y1Links = new ArrayList<Link>(inputNeuronsSize);
		l = new Link(true);
		l.setWeight(0.3);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(0),
				outputNeurons.get(0)));
		System.out.println("Creating normal link between H["
				+ hiddenNeurons.get(0).get(0).getID() + "]Y["
				+ outputNeurons.get(0).getID() + "] = " + l.toString());
		Y1Links.add(l);

		l = new Link(true);
		l.setWeight(-0.2);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(0),
				outputNeurons.get(0)));
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(1),
				outputNeurons.get(0)));
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(1),
				outputNeurons.get(1)));
		System.out.println("Creating normal link between H["
				+ hiddenNeurons.get(0).get(0).getID() + "]Y["
				+ outputNeurons.get(0).getID() + "] and H["
				+ hiddenNeurons.get(0).get(1).getID() + "]Y["
				+ outputNeurons.get(0).getID() + "] and H["
				+ hiddenNeurons.get(0).get(1).getID() + "]Y["
				+ outputNeurons.get(1).getID() + "] = " + l.toString());
		Y1Links.add(l);
		links.add(Y1Links);
		System.out.println("-----------------");

		setLinks(links);
	}
}
