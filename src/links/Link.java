package links;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import auxiliar.PairOfNeurons;

public class Link implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isFinal, isChangeConstrained;
	protected double weight;
	private double weightDerivative;
	private double minValue, maxValue, minChange, maxChange;
	protected ArrayList<PairOfNeurons> neuronPairs;

	public Link(boolean beginAtRandom) {
		if (beginAtRandom) {
			weight = -1.0 + 2.0 * Math.random();
			// weight = 1.0;
		} else {
			weight = 1.0;
		}
		weightDerivative = 0.0;

		isFinal = false;

		minValue = -Double.MAX_VALUE;
		maxValue = Double.MAX_VALUE;

		isChangeConstrained = false;
		minChange = 0.0;
		maxChange = Double.MAX_VALUE;

		neuronPairs = new ArrayList<PairOfNeurons>();
	}

	@Override
	public String toString() {
		return "[Link, weight = " + weight + ", min = " + minValue + ", max = "
				+ maxValue + ", Pairs = "
				+ Arrays.toString(neuronPairs.toArray()) + "]";
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setChangeConstrained(boolean isChangeConstrained) {
		this.isChangeConstrained = isChangeConstrained;
	}

	public void setMinChange(double minChange) {
		this.minChange = minChange;
	}

	public void setMaxChange(double maxChange) {
		this.maxChange = maxChange;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getMinimum() {
		return minValue;
	}

	public double getMaximum() {
		return maxValue;
	}

	public void addPairOfNeurons(PairOfNeurons pair) {
		neuronPairs.add(pair);
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		// if (!isFinal) {
		// System.out.println("Changing weight to "+weight);
		this.weight = Math.min(Math.max(weight, minValue), maxValue);
		// }
	}

	public double getWeightDerivative() {
		return weightDerivative;
	}

	public void setWeightDerivative(double weightDerivative) {
		this.weightDerivative = weightDerivative;
	}

	public void addWeightDerivative(double weightDerivative) {
		this.weightDerivative = this.weightDerivative + weightDerivative;
	}

	public void activateDerivative(double learningRate) {
		if (isChangeConstrained) {
			double change = -learningRate * weightDerivative;
			double sign = Math.signum(change);
			double absValue = Math.abs(change);
			absValue = Math.min(Math.max(absValue, minChange), maxChange);
			setWeight(weight + sign * absValue);
		} else {
			setWeight(weight - learningRate * weightDerivative);
		}
	}

	public ArrayList<PairOfNeurons> getPairsOfNeurons() {
		return neuronPairs;
	}

	public void transmit() {
		neuronPairs.stream().forEach((pair) -> {
			 pair.getOutputNeuron().addIncomeInformation(weight,
			 pair.getInputNeuron().getOutput(),
			 pair.getInputNeuron().getID());
//				pair.getOutputNeuron().addIncomeInformation(weight,
//						pair.getInputNeuron());
			});
	}

	public void transmitDelta() {

		neuronPairs.stream().forEach(
				(pair) -> {
					pair.getInputNeuron().addDelta(
							pair.getInputNeuron().getDerivative()
									* pair.getOutputNeuron().getGamma(
											pair.getInputNeuron().getOutput(),
											weight,
											pair.getInputNeuron().getID())
									* pair.getOutputNeuron().getDelta());
				});

		// for (PairOfNeurons pair : neuronPairs) {
		// pair.getInputNeuron().addDelta(
		// pair.getInputNeuron().getDerivative()
		// * pair.getOutputNeuron().getGamma(
		// pair.getInputNeuron().getOutput(), weight,
		// pair.getInputNeuron().getID())
		// * pair.getOutputNeuron().getDelta());
		// }
	}
}
