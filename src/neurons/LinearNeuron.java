package neurons;

import java.util.ArrayList;

public class LinearNeuron extends GenericNeuron {

	private static final long serialVersionUID = 1L;

	private double m, n;

	public LinearNeuron(int id, double m, double n, int rgb) {
		super(id, rgb);
		this.m = m;
		this.n = n;

		args = new ArrayList<Double>();
		args.add(m);
		args.add(n);

		phi = m;
	}

	public LinearNeuron(int id, ArrayList<Double> args, int rgb) {
		super(id, rgb);
		this.m = args.get(0);
		this.n = args.get(1);

		args = new ArrayList<Double>();
		args.add(m);
		args.add(n);

		phi = m;
	}

	@Override
	public String toString() {
		return "[LinearNeuron, ID " + ID + ", m = " + m + ", n = " + n + "]";
	}

	@Override
	public NeuronType getType() {
		return NeuronType.Linear;
	}

	@Override
	public void activateOutput() {
		theta = m * beta + n;
	}

	@Override
	protected void activateStartingOutput() {
		theta = m * beta + n;
	}
}
