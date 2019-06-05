package auxiliar;

import java.io.Serializable;

import neurons.GenericNeuron;

/**
 * A pair of two neurons.
 * 
 * @author Oliver
 * 
 */
public class PairOfNeurons implements Serializable {

	private static final long serialVersionUID = 1L;

	private final GenericNeuron neuronIn, neuronOut;

	/**
	 * @param neuronIn
	 *            The input neuron.
	 * @param neuronOut
	 *            The output neuron.
	 */
	public PairOfNeurons(GenericNeuron neuronIn, GenericNeuron neuronOut) {
		this.neuronIn = neuronIn;
		this.neuronOut = neuronOut;
	}

	@Override
	public String toString() {
		return "{[" + neuronIn.getID() + "],[" + neuronOut.getID() + "]}";
	}

	/**
	 * @return The input neuron.
	 */
	public GenericNeuron getInputNeuron() {
		return neuronIn;
	}

	/**
	 * @return The output neuron.
	 */
	public GenericNeuron getOutputNeuron() {
		return neuronOut;
	}
}
