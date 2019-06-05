package auxiliar;

import java.util.ArrayList;

/**
 * This pair contains two ArrayList of Double. The first array (inputValues) is
 * supposed to be fed as an input of the neural network. The second array
 * (outputValues) is the expected output.
 * 
 * @author Oliver
 * 
 */
public class PairOfInputOutput {

	private final ArrayList<Double> inputValues, outputValues;

	/**
	 * @param inputValues
	 *            To be fed as an input of the neural network.
	 * @param outputValues
	 *            Expected output of the neural network.
	 */
	public PairOfInputOutput(ArrayList<Double> inputValues,
			ArrayList<Double> outputValues) {
		this.inputValues = inputValues;
		this.outputValues = outputValues;
	}

	/**
	 * @return The inputValues ArrayList.
	 */
	public ArrayList<Double> getInputValues() {
		return inputValues;
	}

	/**
	 * @return The outputValues ArrayList.
	 */
	public ArrayList<Double> getOutputValues() {
		return outputValues;
	}
}
