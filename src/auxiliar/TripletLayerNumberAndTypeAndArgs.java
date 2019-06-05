package auxiliar;

import java.util.ArrayList;

import neurons.NeuronType;

/**
 * Triplet used to create a layer of hidden neurons. It contains [ number of
 * neurons ][ the NeuronType ][ the args ].
 * 
 * @author Oliver
 *
 */
public class TripletLayerNumberAndTypeAndArgs {

	private final int numberOfNeurons;
	private final NeuronType neuronType;
	private final ArrayList<Double> args;

	/**
	 * @param numberOfNeurons
	 *            The number of neurons in the hidden layer.
	 * @param neuronType
	 *            The NeuronType of the neurons.
	 * @param args
	 *            The args describing the Neuron properties.
	 */
	public TripletLayerNumberAndTypeAndArgs(int numberOfNeurons,
			NeuronType neuronType, ArrayList<Double> args) {
		this.numberOfNeurons = numberOfNeurons;
		this.neuronType = neuronType;
		this.args = args;
	}

	/**
	 * @return The number of neurons in the hidden layer.
	 */
	public int getNumberOfNeurons() {
		return numberOfNeurons;
	}

	/**
	 * @return The NeuronType.
	 */
	public NeuronType getNeuronType() {
		return neuronType;
	}

	/**
	 * @return The args describing the Neuron properties.
	 */
	public ArrayList<Double> getArgs() {
		return args;
	}
}
