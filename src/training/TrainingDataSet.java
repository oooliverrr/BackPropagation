package training;

import java.util.ArrayList;

import auxiliar.PairOfInputOutput;

public class TrainingDataSet {

	private ArrayList<PairOfInputOutput> listOfPairs;

	public TrainingDataSet() {
		listOfPairs = new ArrayList<PairOfInputOutput>();
	}

	public void clear() {
		listOfPairs.clear();
	}

	public void addPair(ArrayList<Double> inputValues, ArrayList<Double> outputValues) {
		listOfPairs.add(new PairOfInputOutput(inputValues, outputValues));
	}

	public ArrayList<PairOfInputOutput> listOfPairs() {
		return listOfPairs;
	}
}
