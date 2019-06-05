package neurons;

import java.util.ArrayList;

public class SigmoidNeuron extends GenericNeuron {

	private static final long serialVersionUID = 1L;

	private double lambda, threshold, min, max;
	private double auxPhi, gap;

	public SigmoidNeuron(int id, double lambda, double threshold, double min,
			double max, int rgb) {
		super(id, rgb);
		this.lambda = lambda;
		this.threshold = threshold;
		this.min = min;
		this.max = max;

		args = new ArrayList<Double>();
		args.add(lambda);
		args.add(threshold);
		args.add(min);
		args.add(max);

		auxPhi = (max - min) * lambda;
		gap = max - min;
	}

	public SigmoidNeuron(int id, ArrayList<Double> args, int rgb) {
		super(id, rgb);
		this.lambda = args.get(0);
		this.threshold = args.get(1);
		this.min = args.get(2);
		this.max = args.get(3);

		this.args = new ArrayList<Double>();
		this.args.add(args.get(0));
		this.args.add(args.get(1));
		this.args.add(args.get(2));
		this.args.add(args.get(3));

		auxPhi = (max - min) * lambda;
		gap = max - min;
	}

	@Override
	public String toString() {
		return "[SigmoidNeuron, ID " + ID + ", lambda = " + lambda
				+ ", threshold = " + threshold + ", min = " + min + ", max = "
				+ max + "]";
	}

	@Override
	public NeuronType getType() {
		return NeuronType.Sigmoid;
	}

	@Override
	public void activateOutput() {
		double exponential = Math.exp(-lambda * (beta - threshold));
		double oneOver = 1.0 / (1.0 + exponential);
		double oneOver2 = oneOver * oneOver;

		theta = min + gap * oneOver;
		phi = auxPhi * exponential * oneOver2;
	}

	@Override
	public void activateStartingOutput() {
		double exponential = Math.exp(-lambda * (beta - threshold));
		double oneOver = 1.0 / (1.0 + exponential);
		double oneOver2 = oneOver * oneOver;

		theta = min + gap * oneOver;
		phi = auxPhi * exponential * oneOver2;
	}
}
