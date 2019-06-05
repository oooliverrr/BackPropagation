package neurons;

import java.util.ArrayList;

public class PoolNeuron extends GenericNeuron {

	private static final long serialVersionUID = 1L;

	private double currentZ;
	private int currentID;

	public PoolNeuron(int id, int rgb) {
		super(id, rgb);

		currentZ = -Double.MAX_VALUE;
		currentID = -1;

		args = new ArrayList<Double>();
		args.add(1.0);
		args.add(0.0);
	}

	@Override
	public String toString() {
		return "[LinearPoolNeuron, ID " + ID + "]";
	}

	@Override
	public NeuronType getType() {
		return NeuronType.Pool;
	}

	@Override
	public void activateBeta() {
		beta = currentZ;
		activateOutput();
	}

	@Override
	public void activateStartingBeta() {
		beta = currentZ;
		activateStartingOutput();
	}

	@Override
	public void addIncomeInformation(double weight, double value, int ID) {
		if (value > currentZ) {
			currentZ = value;
			currentID = ID;
		}
	}

	@Override
	public void resetInput() {
		currentZ = -Double.MAX_VALUE;
		currentID = -1;
		// beta = 0;
		// theta = 0;
		// phi = 0;
	}

	@Override
	public double getOmega(double x, double y, int id) {
		if (currentID == id) {
			return x;
		} else {
			// return x;
			return 0;
		}
	}

	@Override
	public double getGamma(double x, double y, int id) {
		if (currentID == id) {
			return y;
		} else {
			// return y;
			return 0;
		}
	}
}
