package neurons;

import java.io.Serializable;
import java.util.ArrayList;

public class GenericNeuron implements Serializable {

	private static final long serialVersionUID = 1L;

	protected double beta;
	protected double theta;
	protected double phi;
	protected double delta;
	protected int ID;
	protected ArrayList<Double> args;
	private final int rgb;

	public GenericNeuron(int id, int rgb) {
		ID = id;
		this.rgb = rgb;

		beta = 0.0;
		theta = 0.0;
		phi = 1.0;
		delta = 0.0;

		args = new ArrayList<Double>();
		args.add(1.0);
		args.add(0.0);
	}

	@Override
	public String toString() {
		return "[Neuron, ID " + ID + "]";
	}

	public int getRGB() {
		return rgb;
	}

	public NeuronType getType() {
		return NeuronType.Linear;
	}

	public ArrayList<Double> getArgs() {
		return args;
	}
	
	public void addIncomeInformation(double weight, double value, int ID) {		
		beta = beta+weight * value;
	}

	public void activateBeta() {
		activateOutput();
	}

	public void activateStartingBeta() {
		activateStartingOutput();
	}

	protected void activateOutput() {
		theta = beta;
		// phi = 1.0;
	}

	protected void activateStartingOutput() {
		theta = beta;
		// phi = 1.0;
	}

	public void resetInput() {
		 beta = 0;
		// theta = 0;
	}

	public void resetDelta() {
		delta = 0.0;
	}

	public void setDelta(double value) {
		delta = value;
	}

	public void addDelta(double value) {
		delta = delta + value;
	}

	public int getID() {
		return ID;
	}

	public double getBeta() {
		return beta;
	}

	public double getOmega(double x, double y, int id) {
		return x;
	}

	public double getGamma(double x, double y, int id) {
		return y;
	}

	public double getOutput() {
		return theta;
	}

	public double getDerivative() {
		return phi;
	}

	public double getDelta() {
		return delta;
	}
}
