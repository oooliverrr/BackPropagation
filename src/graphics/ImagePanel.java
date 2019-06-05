package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import links.Link;
import neuralNets.GenericNeuralNet;
import neurons.DeadNeuron;
import neurons.GenericNeuron;
import neurons.NeuronType;
import auxiliar.PairOfNeurons;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private GenericNeuralNet NN;
	private int Xseparation, Xo, Yseparation, Yo, neuronRadius;

	public ImagePanel(GenericNeuralNet NN) {
		this.NN = NN;

		Xo=10;
		Yo=10;
		Xseparation = 500;
		Yseparation = 20;
		neuronRadius = 10;

		int panelWidth = Xo;
		int panelHeight = Yo + 2 * neuronRadius + Yseparation;

		int numberOfInputNeurons = NN.getInputNeurons().size();
		panelWidth = panelWidth + 2 * neuronRadius + Xseparation;

		int numberOfOutputNeurons = NN.getOutputNeurons().size();
		panelWidth = panelWidth + 2 * neuronRadius + Xseparation;

		int maxNumberOfNeurons = Math.max(numberOfInputNeurons,
				numberOfOutputNeurons);

		for (int i = 0; i < NN.getHiddenNeurons().size(); i++) {
			maxNumberOfNeurons = Math.max(maxNumberOfNeurons, NN
					.getHiddenNeurons().get(i).size());
			panelWidth = panelWidth + 2 * neuronRadius + Xseparation;
		}
		panelWidth=panelWidth+Xo-Xseparation;

		panelHeight = panelHeight + maxNumberOfNeurons
				* (2 * neuronRadius + Yseparation) + Yo-Yseparation;

		Dimension dim = new Dimension(panelWidth, panelHeight);
		this.setSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		this.setPreferredSize(dim);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Graphics2D g2d = (Graphics2D) g.create();

		ArrayList<Integer> ids = new ArrayList<Integer>();
		ArrayList<Integer> posX = new ArrayList<Integer>();
		ArrayList<Integer> posY = new ArrayList<Integer>();

		// Draw input neurons
		g.setColor(Color.BLUE);
		ArrayList<GenericNeuron> inputNeurons = NN.getInputNeurons();
		for (int i = 0; i < inputNeurons.size(); i++) {
			int id = inputNeurons.get(i).getID();
			int x = Xo;
			int y = Yo + (i + 1) * (Yseparation + 2 * neuronRadius);

			ids.add(id);
			posX.add(x);
			posY.add(y);

			if (inputNeurons.get(i).getClass()
					.equals(DeadNeuron.class)) {
				g.fillOval(x, y, 2 * neuronRadius, 2 * neuronRadius);
			} else {
				g.drawOval(x, y, 2 * neuronRadius, 2 * neuronRadius);
			}
			g.drawString("" + id, x, y);
		}

		// Draw hidden neurons
		g.setColor(Color.BLACK);
		ArrayList<ArrayList<GenericNeuron>> hiddenNeurons = NN
				.getHiddenNeurons();
		int numberOfLayers = hiddenNeurons.size();
		for (int i = 0; i < hiddenNeurons.size(); i++) {
			ArrayList<GenericNeuron> hiddenNeuronsLayer = hiddenNeurons.get(i);
			for (int j = 0; j < hiddenNeuronsLayer.size(); j++) {
				int id = hiddenNeuronsLayer.get(j).getID();
				int x = Xo + (2 * neuronRadius + Xseparation) + i
						* (2 * neuronRadius + Xseparation);
				int y = Yo + (j + 1)
						* (Yseparation + 2 * neuronRadius);

				ids.add(id);
				posX.add(x);
				posY.add(y);

				if (hiddenNeuronsLayer.get(j).getClass()
						.equals(DeadNeuron.class)) {
					g.fillOval(x, y, 2 * neuronRadius, 2 * neuronRadius);
				} else {
					g.drawOval(x, y, 2 * neuronRadius, 2 * neuronRadius);
				}
				g.drawString("" + id, x, y);
			}
		}

		// Draw output neurons
		g.setColor(Color.RED);
		ArrayList<GenericNeuron> outputNeurons = NN.getOutputNeurons();
		for (int i = 0; i < outputNeurons.size(); i++) {
			int id = outputNeurons.get(i).getID();
			int x = Xo + (numberOfLayers + 1)
					* (2 * neuronRadius + Xseparation);
			int y = Yo + (i + 1) * (Yseparation + 2 * neuronRadius);

			ids.add(id);
			posX.add(x);
			posY.add(y);

			g.drawOval(x, y, 2 * neuronRadius, 2 * neuronRadius);
			g.drawString("" + id, x, y);
		}

		// Draw bias neurons
		g.setColor(Color.GREEN);
		ArrayList<GenericNeuron> biasNeurons = NN.getBiasNeurons();
		for (int i = 0; i < biasNeurons.size(); i++) {
			int id = biasNeurons.get(i).getID();
			int x = Xo + i * (2 * neuronRadius + Xseparation);
			int y = Yo;

			ids.add(id);
			posX.add(x);
			posY.add(y);

			g.drawOval(x, y, 2 * neuronRadius, 2 * neuronRadius);
			g.drawString("" + id, x, y);
		}

		// Draw bias Links
		g.setColor(Color.GREEN);
		ArrayList<ArrayList<Link>> biasLinks = NN.getBiasLinks();
		for (ArrayList<Link> list : biasLinks) {
			for (Link link : list) {
				for (PairOfNeurons pair : link.getPairsOfNeurons()) {
					int idA = pair.getInputNeuron().getID();
					int idB = pair.getOutputNeuron().getID();

					int xA = posX.get(ids.indexOf(idA)) + 2 * neuronRadius;
					int yA = posY.get(ids.indexOf(idA)) + neuronRadius;

					int xB = posX.get(ids.indexOf(idB));
					int yB = posY.get(ids.indexOf(idB)) + neuronRadius;

					g.drawLine(xA, yA, xB, yB);
				}
			}
		}

		// Draw Links
		// g.setColor(Color.CYAN);
		ArrayList<ArrayList<Link>> Links = NN.getLinks();
		// for (ArrayList<Link> list : Links) {
		for (int i = 0; i < Links.size(); i++) {
			ArrayList<Link> list = Links.get(i);
			if (list != null) {
				for (Link link : list) {
//					g.setColor(new Color((int) (255 * Math.random()),
//							(int) (255 * Math.random()), (int) (255 * Math
//									.random())));
					//g.setColor(Color.BLACK);
					g.setColor(Color.BLACK);
					if(link.getPairsOfNeurons().get(0).getOutputNeuron().getType().equals(NeuronType.Filter)){
						int linkWeight=(int)(Math.round(link.getWeight()));
						if(link.getPairsOfNeurons().get(0).getOutputNeuron().getRGB()==0){
							g.setColor(new Color(255,255-linkWeight,255-linkWeight));
						}if(link.getPairsOfNeurons().get(0).getOutputNeuron().getRGB()==1){
							g.setColor(new Color(255-linkWeight,255,255-linkWeight));
						}if(link.getPairsOfNeurons().get(0).getOutputNeuron().getRGB()==2){
							g.setColor(new Color(255-linkWeight,255-linkWeight,255));
						}
//						rgb=rgb+1;
//						if(rgb>2){
//							rgb=0;
//						}
					}
					for (PairOfNeurons pair : link.getPairsOfNeurons()) {
						int idA = pair.getInputNeuron().getID();
						int idB = pair.getOutputNeuron().getID();

						int xA = posX.get(ids.indexOf(idA)) + 2 * neuronRadius;
						int yA = posY.get(ids.indexOf(idA)) + neuronRadius;

						int xB = posX.get(ids.indexOf(idB));
						int yB = posY.get(ids.indexOf(idB)) + neuronRadius;

						g.drawLine(xA, yA, xB, yB);
					}
				}
			}
		}
	}
}
