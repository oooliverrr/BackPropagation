package neuralNets;

import java.util.ArrayList;
import java.util.Arrays;

import links.Link;
import neurons.GenericNeuron;
import neurons.NeuronType;
import auxiliar.PairOfNeurons;

public class Example3NeuralNet extends GenericNeuralNet {

	private static final long serialVersionUID = 1L;

	public Example3NeuralNet(String name) {
		super(name);

		// Init links, biasLinks, linkWeightDerivatives
		ArrayList<ArrayList<Link>> biasLinks = new ArrayList<ArrayList<Link>>();
		ArrayList<ArrayList<Link>> links = new ArrayList<ArrayList<Link>>();

		// 2 x input, Linear, m=1, n=0
		ArrayList<GenericNeuron> inputNeurons = new ArrayList<GenericNeuron>();
		NeuronType inputNeuronType = NeuronType.Linear;
		ArrayList<Double> inputArgs = new ArrayList<Double>(Arrays.asList(1.0,
				0.0));
		inputNeurons.add(getNewNeuron(0, inputNeuronType, inputArgs, -1));
		inputNeurons.add(getNewNeuron(1, inputNeuronType, inputArgs, -1));
		inputNeurons.add(getNewNeuron(2, inputNeuronType, inputArgs, -1));
		inputNeurons.add(getNewNeuron(3, inputNeuronType, inputArgs, -1));
		inputNeurons.add(getNewNeuron(4, inputNeuronType, inputArgs, -1));
		setInputNeurons(inputNeurons);
		System.out.println("-----------------");

		// 2 x output, Sigmoid, min=0, max=1, lambda=2, threshold=0
		ArrayList<GenericNeuron> outputNeurons = new ArrayList<GenericNeuron>();
		NeuronType outputNeuronType = NeuronType.Sigmoid;
		ArrayList<Double> outputArgs = new ArrayList<Double>(Arrays.asList(2.0,
				0.0, 0.0, 1.0));
		outputNeurons.add(getNewNeuron(5, outputNeuronType, outputArgs, -1));
		outputNeurons.add(getNewNeuron(6, outputNeuronType, outputArgs, -1));
		setOutputNeurons(outputNeurons);
		System.out.println("-----------------");

		// Bias (same number as number of hidden layers plus one (output)),
		// Linear, m=1.0, n=0.0
		ArrayList<GenericNeuron> biasNeurons = new ArrayList<GenericNeuron>();
		NeuronType biasNeuronType = NeuronType.Linear;
		ArrayList<Double> biasArgs = new ArrayList<Double>(Arrays.asList(1.0,
				0.0));
		setBiasNeurons(biasNeurons);
		System.out.println("-----------------");

		// 1 x hidden layer with 2 neurons, Sigmoid, min=1, max=2, lambda=3,
		// threshold=4
		ArrayList<ArrayList<GenericNeuron>> hiddenNeurons = new ArrayList<ArrayList<GenericNeuron>>();
		ArrayList<Double> hiddenArgs = new ArrayList<Double>(Arrays.asList(1.0,
				0.0));
		ArrayList<GenericNeuron> hiddenNeuronsLayer = new ArrayList<GenericNeuron>();
		hiddenNeuronsLayer.add(getNewNeuron(7, NeuronType.Filter, hiddenArgs,
				-1));
		hiddenNeuronsLayer.add(getNewNeuron(8, NeuronType.Filter, hiddenArgs,
				-1));
		hiddenNeuronsLayer.add(getNewNeuron(9, NeuronType.Filter, hiddenArgs,
				-1));
		hiddenNeuronsLayer.add(getNewNeuron(10, NeuronType.Filter, hiddenArgs,
				-1));
		hiddenNeurons.add(hiddenNeuronsLayer);
		hiddenNeuronsLayer = new ArrayList<GenericNeuron>();
		hiddenNeuronsLayer
				.add(getNewNeuron(11, NeuronType.Pool, hiddenArgs, -1));
		hiddenNeuronsLayer
				.add(getNewNeuron(12, NeuronType.Pool, hiddenArgs, -1));
		hiddenNeurons.add(hiddenNeuronsLayer);
		setHiddenNeurons(hiddenNeurons);
		System.out.println("-----------------");
		System.out.println("");

		// Links input-1st layer
		ArrayList<Link> X1Links = new ArrayList<Link>();
		Link l = new Link(true);
		l.setWeight(255.0);
		l.setMinValue(0.0);
		l.setMaxValue(255.0);
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(0), hiddenNeurons
				.get(0).get(0)));
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(1), hiddenNeurons
				.get(0).get(1)));
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(2), hiddenNeurons
				.get(0).get(2)));
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(3), hiddenNeurons
				.get(0).get(3)));
		X1Links.add(l);
		l = new Link(true);
		l.setWeight(0.0);
		l.setMinValue(0.0);
		l.setMaxValue(255.0);
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(1), hiddenNeurons
				.get(0).get(0)));
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(2), hiddenNeurons
				.get(0).get(1)));
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(3), hiddenNeurons
				.get(0).get(2)));
		l.addPairOfNeurons(new PairOfNeurons(inputNeurons.get(4), hiddenNeurons
				.get(0).get(3)));
		X1Links.add(l);
		links.add(X1Links);
		System.out.println("-----------------");

		// Links filter-pool
		X1Links = new ArrayList<Link>();
		l = new Link(true);
		l.setWeight(1.0);
		l.setFinal(true);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(0),
				hiddenNeurons.get(1).get(0)));
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(1),
				hiddenNeurons.get(1).get(0)));
		X1Links.add(l);
		l = new Link(true);
		l.setWeight(1.0);
		l.setFinal(true);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(2),
				hiddenNeurons.get(1).get(1)));
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(0).get(3),
				hiddenNeurons.get(1).get(1)));
		X1Links.add(l);
		links.add(X1Links);
		System.out.println("-----------------");

		// Links pool-output
		X1Links = new ArrayList<Link>();
		l = new Link(true);
		l.setWeight(0.1);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(1).get(0),
				outputNeurons.get(0)));
		X1Links.add(l);
		l = new Link(true);
		l.setWeight(0.2);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(1).get(0),
				outputNeurons.get(1)));
		X1Links.add(l);
		l = new Link(true);
		l.setWeight(0.3);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(1).get(1),
				outputNeurons.get(0)));
		X1Links.add(l);
		l = new Link(true);
		l.setWeight(0.4);
		l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(1).get(1),
				outputNeurons.get(1)));
		X1Links.add(l);
		links.add(X1Links);
		System.out.println("-----------------");

		setLinks(links);

		// No Bias
		for (int j = 0; j < 4; j++) {
			ArrayList<Link> layerOfLinks = new ArrayList<Link>();
			biasLinks.add(layerOfLinks);
		}
		setBiasLinks(biasLinks);
	}
}
