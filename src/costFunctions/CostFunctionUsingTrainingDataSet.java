package costFunctions;

import java.util.ArrayList;
import java.util.Arrays;

import training.TrainingDataSet;
import neuralNets.GenericNeuralNet;
import auxiliar.PairOfInputOutput;

public class CostFunctionUsingTrainingDataSet {

	TrainingDataSet trainingDataSet;

	public CostFunctionUsingTrainingDataSet(TrainingDataSet trainingDataSet) {
		this.trainingDataSet = trainingDataSet;
	}

	public TrainingDataSet getTrainingDataSet() {
		return trainingDataSet;
	}

	public double getError(GenericNeuralNet n) {
		double answer = 0.0;
		//System.out.println("--------");
		for (PairOfInputOutput p : trainingDataSet.listOfPairs()) {
			ArrayList<Double> neuralNetworkOutput = n.getExpectedOutput(p
					.getInputValues());
			ArrayList<Double> expectedOutput = p.getOutputValues();
//			System.out.println("Want "
//					+ Arrays.toString(expectedOutput.toArray()) + ", got "
//					+ Arrays.toString(neuralNetworkOutput.toArray()));
			for (int i = 0; i < neuralNetworkOutput.size(); i++) {
				double difference = neuralNetworkOutput.get(i)
						- expectedOutput.get(i);
				answer = answer + 0.5 * difference * difference;
			}
			//System.out.println();
		}
		return answer;
	}
}
