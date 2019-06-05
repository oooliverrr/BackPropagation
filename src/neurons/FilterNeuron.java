package neurons;

import java.util.ArrayList;

public class FilterNeuron extends GenericNeuron {

	private static final long serialVersionUID = 1L;

	// private final double alpha = 1.0 / 255.0;
	// private final double piAlpha2;
	// private double auxFrac, auxFrac2;

	// private ArrayList<Double> weightsInput;
	// private ArrayList<Double> valuesInput;
	private int inputSize;
	public boolean computedInputSize;
	private double auxFrac, auxFrac2;

	public FilterNeuron(int id, int rgb) {
		super(id, rgb);

		// piAlpha2 = Math.PI * alpha / 2;

		args = new ArrayList<Double>();
		args.add(1.0);
		args.add(0.0);

		inputSize = 0;
		computedInputSize = false;

		// weightsInput = new ArrayList<Double>();
		// valuesInput = new ArrayList<Double>();
	}

	@Override
	public String toString() {
		return "[FilterNeuron, ID " + ID + "]";
	}

	@Override
	public NeuronType getType() {
		return NeuronType.Filter;
	}

	@Override
	public void addIncomeInformation(double weight, double value, int ID) {
		if (computedInputSize) {
			addIncomeInformationA(weight, value);
		} else {
			addIncomeInformationB(weight, value);
		}
	}

	private void addIncomeInformationA(double weight, double value) {
		double frac = (value - weight) / 255.0;
		beta = beta + (1 - frac * frac);
	}

	private void addIncomeInformationB(double weight, double value) {
		inputSize = inputSize + 1;
		double frac = (value - weight) / 255.0;
		beta = beta + (1 - frac * frac);
	}

	// @Override
	// public void activateBeta() {
	// // beta = 0.0;
	// //
	// // informationInput.stream().forEach((triplet) -> {// beta = beta
	// // // + Math.cos(piAlpha2 * (triplet.getZ() -
	// // // triplet.getW()));
	// // double frac = (triplet.getZ() - triplet.getW()) / 255.0;
	// // beta = beta + (1 - frac * frac);
	// // });
	// //
	// // // for (TripletOfWeightValueID triplet : informationInput) {
	// // // // beta = beta
	// // // // + Math.cos(piAlpha2 * (triplet.getZ() - triplet.getW()));
	// // // double frac = (triplet.getZ() - triplet.getW()) / 255.0;
	// // // beta = beta + (1 - frac * frac);
	// // // }
	// // beta = beta * auxFrac;
	//
	// beta = auxFrac*informationInput.stream()
	// .mapToDouble((triplet)->{
	// double frac = (triplet.getZ() - triplet.getW()) / 255.0;
	// return (1 - frac * frac);
	// }).sum();
	//
	// activateOutput();
	// }

	@Override
	public void activateBeta() {
		// beta = 0.0;
		//
		// for (int i = 0; i < inputSize; i++) {
		// double frac = (valuesInput.get(i) - weightsInput.get(i)) / 255.0;
		// beta = beta + (1 - frac * frac);
		// }

		beta = beta * auxFrac;

		activateOutput();
	}

	// @Override
	// public void activateStartingBeta() {
	// beta = 0.0;
	// for (TripletOfWeightValueID triplet : informationInput) {
	// // beta = beta
	// // + Math.cos(piAlpha2 * (triplet.getZ() - triplet.getW()));
	// double frac = (triplet.getZ() - triplet.getW()) / 255.0;
	// beta = beta + (1 - frac * frac);
	// }
	// auxFrac = 255.0 / informationInput.size();
	// auxFrac2 = 2.0 / (255.0 * informationInput.size());
	// beta = beta * auxFrac;
	// activateStartingOutput();
	// }

	@Override
	public void activateStartingBeta() {

		// inputSize = valuesInput.size();
		auxFrac = 255.0 / inputSize;
		auxFrac2 = 2.0 / (255.0 * inputSize);

		// beta = 0.0;
		//
		// for (int i = 0; i < inputSize; i++) {
		// double frac = (valuesInput.get(i) - weightsInput.get(i)) / 255.0;
		// beta = beta + (1 - frac * frac);
		// }

		beta = beta * auxFrac;

		computedInputSize = true;

		activateStartingOutput();
	}

	@Override
	protected void activateOutput() {
		theta = beta;
		// phi = 1.0;
	}

	@Override
	protected void activateStartingOutput() {
		theta = beta;
		// phi = 1.0;
	}

	@Override
	public double getOmega(double x, double y, int id) {
		// return 255.0 * piAlpha2 * Math.sin(piAlpha2 * (x - y))
		// / informationSize;
		return auxFrac2 * (x - y);
	}

	@Override
	public double getGamma(double x, double y, int id) {
		// return -255.0 * piAlpha2 * Math.sin(piAlpha2 * (x - y))
		// / informationSize;
		return auxFrac2 * (y - x);
	}
}
