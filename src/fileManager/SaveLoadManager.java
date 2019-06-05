package fileManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import neuralNets.GenericNeuralNet;

public class SaveLoadManager {

	private String folder;

	public SaveLoadManager(String folder) {
		this.folder = folder;
	}

	public void saveNeuralNet(GenericNeuralNet neuralNetworkToSave) {
		System.out.println("--- Saving NeuralNet...");
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(folder + neuralNetworkToSave.getName());
			out = new ObjectOutputStream(fos);
			out.writeObject(neuralNetworkToSave);

			out.close();
		} catch (Exception ex) {
			System.err.println("Could not serialize neural network [" + folder
					+ neuralNetworkToSave.getName() + "]: " + ex.getMessage());
		}
		System.out.println("--- Save ok");
	}

	public GenericNeuralNet loadNeuralNetwork(String filePath) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filePath);
			in = new ObjectInputStream(fis);
			GenericNeuralNet neuralNetworkToLoad = (GenericNeuralNet) in
					.readObject();
			in.close();
			return neuralNetworkToLoad;
		} catch (Exception ex) {
			System.err.println("Could not load neural network [" + filePath
					+ "]: " + ex.getMessage());
			return null;
		}
	}
}
