package neurons;

import java.util.ArrayList;

public class DeadNeuron extends GenericNeuron {

	private static final long serialVersionUID = 1L;

	public DeadNeuron(int id, int rgb) {
		super(id, rgb);

		args = new ArrayList<Double>();
		args.add(0.0);
		args.add(0.0);

		phi = 0.0;
	}

	@Override
	public String toString() {
		return "[DeadNeuron, ID " + ID + "]";
	}

	@Override
	public NeuronType getType() {
		return NeuronType.Dead;
	}

	@Override
	public void activateBeta() {
		// beta = 0.0;
		// activateOutput();
	}

	@Override
	public void activateStartingBeta() {
		// beta = 0.0;
		// activateStartingOutput();
	}

	@Override
	public void activateOutput() {
		// theta = 0.0;
		// phi = 0.0;
	}

	@Override
	public void activateStartingOutput() {
		// theta = 0.0;
		// phi = 0.0;
	}

	// @Override
	// public void addIncomeInformation(TripletOfWeightValueID triplet) {
	// }

	@Override
	public void addIncomeInformation(double weight, double value, int ID) {
	}

	@Override
	public void setDelta(double value) {
		// delta = 0.0;
	}

	@Override
	public void addDelta(double value) {
		// delta = 0.0;
	}

	@Override
	public void resetDelta() {
		// delta = 0.0;
	}

	@Override
	public double getOmega(double x, double y, int id) {
		return 0.0;
	}

	@Override
	public double getGamma(double x, double y, int id) {
		return 0.0;
	}

	@Override
	public double getDerivative() {
		return 0.0;
	}

	@Override
	public double getDelta() {
		return 0.0;
	}
}
