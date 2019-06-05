package neuralNets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import links.Link;
import neurons.GenericNeuron;
import neurons.NeuronType;
import auxiliar.PairOfFilterNumberAndProperties;
import auxiliar.PairOfNeurons;
import filters.FilterProperties;

public class ConvolutionalNeuralNetwork extends GenericNeuralNet {

	private static final long serialVersionUID = 1L;

	private ArrayList<String> outputNames;
	private ArrayList<PairOfFilterNumberAndProperties> filtersData;
	private int firstBorderW, firstBorderH, imageWidth, imageHeight;

	public ConvolutionalNeuralNetwork(String convNetName, int inputWidth,
			int inputHeight,
			ArrayList<PairOfFilterNumberAndProperties> filtersData,
			ArrayList<String> outputNames) {

		super(convNetName);

		this.outputNames = outputNames;
		this.imageWidth = inputWidth;
		this.imageHeight = inputHeight;
		this.filtersData = filtersData;

		// Sizes
		firstBorderW = filtersData.get(0).getFilterProperties()
				.getBorderWidth();
		firstBorderH = filtersData.get(0).getFilterProperties()
				.getBorderHeight();
		int widthOf1stImage = inputWidth + 2
				* filtersData.get(0).getFilterProperties().getBorderWidth();
		int heightOf1stImage = inputHeight + 2
				* filtersData.get(0).getFilterProperties().getBorderHeight();
		System.out.println("Creating convolutional neural network. ");
		System.out.println("Input image = [" + inputWidth + " x " + inputHeight
				+ "]");
		System.out.println("Input image bordered = [" + widthOf1stImage + " x "
				+ heightOf1stImage + "]");
		int inputNeuronsSize = 3 * widthOf1stImage * heightOf1stImage;
		System.out.println("Number of input Neurons = " + inputNeuronsSize);
		int outputNeuronsSize = outputNames.size();
		System.out.println("Number of output Neurons = " + outputNeuronsSize
				+ " = " + Arrays.toString(outputNames.toArray()));
		int numberOfFilterPoolLayers = filtersData.size();
		System.out.println("Number of Filter-Pool layers = "
				+ numberOfFilterPoolLayers);
		for (int i = 0; i < numberOfFilterPoolLayers; i++) {
			System.out.println("... layer [" + i + "] => "
					+ filtersData.get(i).getNumberOfFilters() + " filters = "
					+ filtersData.get(i).getFilterProperties().toString());
		}
		System.out.println("-----------------");
		System.out.println("");

		// Some stuff
		ArrayList<Integer> numberOfImagesAfterLayer = new ArrayList<Integer>();
		ArrayList<Integer> widthsAfterFilter = new ArrayList<Integer>();
		ArrayList<Integer> heightsAfterFilter = new ArrayList<Integer>();
		ArrayList<Integer> widthsAfterPooler = new ArrayList<Integer>();
		ArrayList<Integer> heightsAfterPooler = new ArrayList<Integer>();
		ArrayList<Integer> widthsAfterPoolerBordered = new ArrayList<Integer>();
		ArrayList<Integer> heightsAfterPoolerBordered = new ArrayList<Integer>();
		int num = 1;
		int w = inputWidth;
		int h = inputHeight;
		for (int i = 0; i < filtersData.size(); i++) {
			FilterProperties fp = filtersData.get(i).getFilterProperties();
			int filterWidth = fp.getFilterWidth();
			int filterHeight = fp.getFilterHeight();
			int borderWidth = fp.getBorderWidth();
			int borderHeight = fp.getBorderHeight();
			int poolWidth = fp.getPoolWidth();
			int poolHeight = fp.getPoolHeight();

			int nextBorderWidth = 0;
			int nextBorderHeight = 0;
			if (i < filtersData.size() - 1) {
				FilterProperties fp2 = filtersData.get(i + 1)
						.getFilterProperties();
				nextBorderWidth = fp2.getBorderWidth();
				nextBorderHeight = fp2.getBorderHeight();
			}

			int wAfterFilter = w + 2 * borderWidth - filterWidth + 1;
			int hAfterFilter = h + 2 * borderHeight - filterHeight + 1;
			widthsAfterFilter.add(wAfterFilter);
			heightsAfterFilter.add(hAfterFilter);

			int wAfterPool = (int) (Math.ceil((wAfterFilter * 1.0)
					/ (poolWidth * 1.0)));
			int hAfterPool = (int) (Math.ceil((hAfterFilter * 1.0)
					/ (poolHeight * 1.0)));

			int wAfterPoolBordered = ((int) (Math.ceil((wAfterFilter * 1.0)
					/ (poolWidth * 1.0))))
					+ 2 * nextBorderWidth;
			int hAfterPoolBordered = ((int) (Math.ceil((hAfterFilter * 1.0)
					/ (poolHeight * 1.0))))
					+ 2 * nextBorderHeight;

			widthsAfterPooler.add(wAfterPool);
			heightsAfterPooler.add(hAfterPool);
			widthsAfterPoolerBordered.add(wAfterPoolBordered);
			heightsAfterPoolerBordered.add(hAfterPoolBordered);

			w = wAfterPool;
			h = hAfterPool;

			num = num * filtersData.get(i).getNumberOfFilters();
			numberOfImagesAfterLayer.add(num);
		}
		for (int i = 0; i < numberOfImagesAfterLayer.size(); i++) {
			System.out
					.println("After layer ["
							+ i
							+ "], we have "
							+ numberOfImagesAfterLayer.get(i)
							+ " images of size ["
							+ widthsAfterFilter.get(i)
							+ " x "
							+ heightsAfterFilter.get(i)
							+ "](="
							+ (3 * numberOfImagesAfterLayer.get(i)
									* widthsAfterFilter.get(i) * heightsAfterFilter
										.get(i))
							+ " neurons) pooled to ["
							+ widthsAfterPooler.get(i)
							+ " x "
							+ heightsAfterPooler.get(i)
							+ "]->["
							+ widthsAfterPoolerBordered.get(i)
							+ " x "
							+ heightsAfterPoolerBordered.get(i)
							+ "](="
							+ (3 * numberOfImagesAfterLayer.get(i)
									* widthsAfterPoolerBordered.get(i) * heightsAfterPoolerBordered
										.get(i)) + " neurons)");
		}
		System.out.println("-----------------");
		System.out.println("");
		int numberOfPooledNeuronsAtTheEnd = (3
				* numberOfImagesAfterLayer
						.get(numberOfImagesAfterLayer.size() - 1)
				* widthsAfterPoolerBordered
						.get(numberOfImagesAfterLayer.size() - 1) * heightsAfterPoolerBordered
				.get(numberOfImagesAfterLayer.size() - 1));

		// Types & Args
		NeuronType deadNeuronType = NeuronType.Dead;
		ArrayList<Double> deadArgs = new ArrayList<Double>();

		NeuronType inputNeuronType = NeuronType.Linear;
		ArrayList<Double> inputArgs = new ArrayList<Double>();
		inputArgs.add(1.0);
		inputArgs.add(0.0);

		NeuronType outputNeuronType = NeuronType.Sigmoid;
		ArrayList<Double> outputArgs = new ArrayList<Double>();
		outputArgs.add(5.0 / (255.0 * (numberOfPooledNeuronsAtTheEnd + 1))); // Lambda
		outputArgs.add(0.0); // T = 0
		outputArgs.add(0.0); // min = 0
		outputArgs.add(1.0); // max = 1

		NeuronType biasNeuronType = NeuronType.Linear;
		ArrayList<Double> biasArgs = new ArrayList<Double>();
		biasArgs.add(1.0);
		biasArgs.add(0.0);

		NeuronType filterNeuronType = NeuronType.Filter;
		ArrayList<Double> hiddenArgs = new ArrayList<Double>();
		hiddenArgs.add(1.0);
		hiddenArgs.add(0.0);

		NeuronType poolNeuronType = NeuronType.Pool;

		// Start creating stuff
		int idCount = 0;

		// Input
		ArrayList<GenericNeuron> inputNeurons = new ArrayList<GenericNeuron>(
				inputNeuronsSize);
		int idx = 0;
		for (int rgb = 0; rgb < 3; rgb++) {
			// top border
			for (int i = 0; i < filtersData.get(0).getFilterProperties()
					.getBorderHeight(); i++) {
				for (int j = 0; j < widthOf1stImage; j++) {
					inputNeurons.add(getNewNeuron(idCount, deadNeuronType,
							deadArgs, rgb));
					System.out.println("Creating input dead neuron [" + idCount
							+ "] = " + inputNeurons.get(idx).toString());
					idx = idx + 1;
					idCount = idCount + 1;
				}
			}
			// middle
			for (int i = 0; i < inputHeight; i++) {
				for (int j = 0; j < filtersData.get(0).getFilterProperties()
						.getBorderWidth(); j++) {
					inputNeurons.add(getNewNeuron(idCount, deadNeuronType,
							deadArgs, rgb));
					System.out.println("Creating input dead neuron [" + idCount
							+ "] = " + inputNeurons.get(idx).toString());
					idx = idx + 1;
					idCount = idCount + 1;
				}
				for (int j = 0; j < inputWidth; j++) {
					inputNeurons.add(getNewNeuron(idCount, inputNeuronType,
							inputArgs, rgb));
					System.out.println("Creating input neuron [" + idCount
							+ "] = " + inputNeurons.get(idx).toString());
					idx = idx + 1;
					idCount = idCount + 1;
				}
				for (int j = 0; j < filtersData.get(0).getFilterProperties()
						.getBorderWidth(); j++) {
					inputNeurons.add(getNewNeuron(idCount, deadNeuronType,
							deadArgs, rgb));
					System.out.println("Creating input dead neuron [" + idCount
							+ "] = " + inputNeurons.get(idx).toString());
					idx = idx + 1;
					idCount = idCount + 1;
				}
			}

			// bottom border
			for (int i = 0; i < filtersData.get(0).getFilterProperties()
					.getBorderHeight(); i++) {
				for (int j = 0; j < widthOf1stImage; j++) {
					inputNeurons.add(getNewNeuron(idCount, deadNeuronType,
							deadArgs, rgb));
					System.out.println("Creating input dead neuron [" + idCount
							+ "] = " + inputNeurons.get(idx).toString());
					idx = idx + 1;
					idCount = idCount + 1;
				}
			}
		}
		setInputNeurons(inputNeurons);
		System.out.println("-----------------");

		// Output
		ArrayList<GenericNeuron> outputNeurons = new ArrayList<GenericNeuron>(
				outputNeuronsSize);
		for (int i = 0; i < outputNeuronsSize; i++) {
			outputNeurons.add(getNewNeuron(idCount, outputNeuronType,
					outputArgs, -1));
			System.out.println("Creating output neuron [" + idCount + "] = "
					+ outputNeurons.get(i).toString());
			idCount = idCount + 1;
		}
		setOutputNeurons(outputNeurons);
		System.out.println("-----------------");

		// Bias : only one neuron! (output layer)
		ArrayList<GenericNeuron> biasNeurons = new ArrayList<GenericNeuron>(1);
		biasNeurons.add(getNewNeuron(idCount, biasNeuronType, biasArgs, -1));
		System.out.println("Creating bias neuron [" + idCount + "] = "
				+ biasNeurons.get(0).toString());
		idCount = idCount + 1;
		for (GenericNeuron biasNeuron : biasNeurons) {
			// biasNeuron.addIncomeInformation(new TripletOfWeightValueID(1.0,
			// 255.0));
			biasNeuron.addIncomeInformation(1.0, 255.0, -1);
			biasNeuron.activateBeta();
		}
		setBiasNeurons(biasNeurons);
		System.out.println("-----------------");

		// Hidden Layers
		ArrayList<ArrayList<GenericNeuron>> hiddenNeurons = new ArrayList<ArrayList<GenericNeuron>>(
				2 * numberOfFilterPoolLayers);
		for (int i = 0; i < numberOfFilterPoolLayers; i++) {

			int widthAfterFilter = widthsAfterFilter.get(i);
			int heightAfterFilter = heightsAfterFilter.get(i);
			int widthAfterPool = widthsAfterPooler.get(i);
			int heightAfterPool = heightsAfterPooler.get(i);
			int widthAfterPoolBordered = widthsAfterPoolerBordered.get(i);
			int heightAfterPoolBordered = heightsAfterPoolerBordered.get(i);

			int borderW = (widthAfterPoolBordered - widthAfterPool) / 2;
			int borderH = (heightAfterPoolBordered - heightAfterPool) / 2;

			int numberOfImagesAfterLayerr = numberOfImagesAfterLayer.get(i);

			int numberOfFilterNeurons = numberOfImagesAfterLayerr * 3
					* widthAfterFilter * heightAfterFilter;
			int numberOfPoolNeurons = numberOfImagesAfterLayerr * 3
					* widthAfterPoolBordered * heightAfterPoolBordered;

			System.out.println("There are " + numberOfImagesAfterLayerr
					+ " filtered images to be created of size ["
					+ widthAfterFilter + " x " + heightAfterFilter + "] => :");

			ArrayList<GenericNeuron> hiddenNeuronsFilteredLayer = new ArrayList<GenericNeuron>(
					numberOfFilterNeurons);

			for (int j = 0; j < numberOfImagesAfterLayerr; j++) {
				for (int rgb = 0; rgb < 3; rgb++) {
					for (int k = 0; k < widthAfterFilter * heightAfterFilter; k++) {
						hiddenNeuronsFilteredLayer.add(getNewNeuron(idCount,
								filterNeuronType, hiddenArgs, rgb));
						System.out.println("Creating hidden (filter) neuron ["
								+ idCount + "] (layer " + i + ") = "
								+ hiddenNeuronsFilteredLayer.get(j).toString()); // This
																					// syso
																					// is
																					// not
																					// good,
																					// check
																					// commented
																					// stuff
																					// below
						idCount = idCount + 1;
					}
				}
			}
			// for (int j = 0; j < numberOfFilterNeurons; j++) {
			// hiddenNeuronsFilteredLayer.add(getNewNeuron(idCount,
			// filterNeuronType, hiddenArgs, rgb));
			// System.out.println("Creating hidden (filter) neuron ["
			// + idCount + "] (layer " + i + ") = "
			// + hiddenNeuronsFilteredLayer.get(j).toString());
			// idCount = idCount + 1;
			// }

			System.out.println("... created "
					+ hiddenNeuronsFilteredLayer.size()
					+ " hidden filter neurons (filter layer " + i
					+ ")(network layer " + (2 * i) + ")");
			System.out.println("-----------------");
			hiddenNeurons.add(hiddenNeuronsFilteredLayer);

			System.out.println("There are " + numberOfImagesAfterLayerr
					+ " pooled images to be created of size ["
					+ widthAfterPoolBordered + " x " + heightAfterPoolBordered
					+ "] => :");

			ArrayList<GenericNeuron> hiddenNeuronsPooledLayer = new ArrayList<GenericNeuron>(
					numberOfPoolNeurons);
			idx = 0;
			for (int img = 0; img < numberOfImagesAfterLayerr; img++) {
				for (int rgb = 0; rgb < 3; rgb++) {
					// top border
					for (int m = 0; m < borderH; m++) {
						for (int n = 0; n < widthAfterPoolBordered; n++) {
							hiddenNeuronsPooledLayer.add(getNewNeuron(idCount,
									deadNeuronType, deadArgs, rgb));
							System.out
									.println("Creating hidden (pool) dead neuron ["
											+ idCount
											+ "] (layer "
											+ i
											+ ") = "
											+ hiddenNeuronsPooledLayer.get(idx)
													.toString());
							idx = idx + 1;
							idCount = idCount + 1;
						}
					}

					// middle
					for (int m = 0; m < heightAfterPool; m++) {
						for (int n = 0; n < borderW; n++) {
							hiddenNeuronsPooledLayer.add(getNewNeuron(idCount,
									deadNeuronType, deadArgs, rgb));
							System.out
									.println("Creating hidden (pool) dead neuron ["
											+ idCount
											+ "] (layer "
											+ i
											+ ") = "
											+ hiddenNeuronsPooledLayer.get(idx)
													.toString());
							idx = idx + 1;
							idCount = idCount + 1;
						}
						for (int n = 0; n < widthAfterPool; n++) {
							hiddenNeuronsPooledLayer.add(getNewNeuron(idCount,
									poolNeuronType, hiddenArgs, rgb));
							System.out
									.println("Creating hidden (pool) neuron ["
											+ idCount
											+ "] (layer "
											+ i
											+ ") = "
											+ hiddenNeuronsPooledLayer.get(idx)
													.toString());
							idx = idx + 1;
							idCount = idCount + 1;
						}
						for (int n = 0; n < borderW; n++) {
							hiddenNeuronsPooledLayer.add(getNewNeuron(idCount,
									deadNeuronType, deadArgs, rgb));
							System.out
									.println("Creating hidden (pool) dead neuron ["
											+ idCount
											+ "] (layer "
											+ i
											+ ") = "
											+ hiddenNeuronsPooledLayer.get(idx)
													.toString());
							idx = idx + 1;
							idCount = idCount + 1;
						}
					}

					// bottom border
					for (int m = 0; m < borderH; m++) {
						for (int n = 0; n < widthAfterPoolBordered; n++) {
							hiddenNeuronsPooledLayer.add(getNewNeuron(idCount,
									deadNeuronType, deadArgs, rgb));
							System.out
									.println("Creating hidden (pool) dead neuron ["
											+ idCount
											+ "] (layer "
											+ i
											+ ") = "
											+ hiddenNeuronsPooledLayer.get(idx)
													.toString());
							idx = idx + 1;
							idCount = idCount + 1;
						}
					}
				}
			}
			System.out.println("... created " + hiddenNeuronsPooledLayer.size()
					+ " hidden pool neurons (filter layer " + i
					+ ")(network layer " + (2 * i + 1) + ")");
			System.out.println("-----------------");
			hiddenNeurons.add(hiddenNeuronsPooledLayer);
		}
		setHiddenNeurons(hiddenNeurons);
		System.out.println("-----------------");
		System.out.println("");

		// No Bias until Y
		ArrayList<ArrayList<Link>> biasLinks = new ArrayList<ArrayList<Link>>(
				2 * numberOfFilterPoolLayers + 1);
		for (int j = 0; j < 2 * numberOfFilterPoolLayers; j++) {
			ArrayList<Link> layerOfLinks = new ArrayList<Link>();
			biasLinks.add(layerOfLinks);
		}

		// Bias Links-Y
		ArrayList<Link> layerOfLinks = new ArrayList<Link>(outputNeuronsSize);
		for (int j = 0; j < outputNeuronsSize; j++) {
			Link l = new Link(true);
			l.addPairOfNeurons(new PairOfNeurons(biasNeurons.get(0),
					outputNeurons.get(j)));
			System.out.println("Creating bias link between ["
					+ biasNeurons.get(0).getID() + "]["
					+ outputNeurons.get(j).getID() + "] = " + l.toString());
			layerOfLinks.add(l);
		}
		biasLinks.add(layerOfLinks);
		setBiasLinks(biasLinks);
		System.out.println("-----------------");
		System.out.println("");

		// Links Filter-pool
		ArrayList<ArrayList<Link>> links = new ArrayList<ArrayList<Link>>(
				2 * numberOfFilterPoolLayers + 1);
		System.out.println("links size: " + (2 * numberOfFilterPoolLayers + 1));
		for (int k = 0; k < 2 * numberOfFilterPoolLayers + 1; k++) {
			links.add(null);
		}
		for (int layer = 0; layer < filtersData.size(); layer++) {

			FilterProperties fp = filtersData.get(layer).getFilterProperties();
			int poolWidth = fp.getPoolWidth();
			int poolHeight = fp.getPoolHeight();

			int numberOfImages = numberOfImagesAfterLayer.get(layer);
			int filterW = widthsAfterFilter.get(layer);
			int filterH = heightsAfterFilter.get(layer);
			int poolW = widthsAfterPooler.get(layer);
			int poolH = heightsAfterPooler.get(layer);
			int borderedPoolW = widthsAfterPoolerBordered.get(layer);
			int borderedPoolH = heightsAfterPoolerBordered.get(layer);

			int borderW = (borderedPoolW - poolW) / 2;
			int borderH = (borderedPoolH - poolH) / 2;

			int numberOfFilterNeuronsPerRGB = filterW * filterH;
			int numberOfPoolNeuronsPerRGB = borderedPoolW * borderedPoolH;

			layerOfLinks = new ArrayList<Link>();
			for (int img = 0; img < numberOfImages; img++) {
				for (int rgb = 0; rgb < 3; rgb++) {
					for (int poolNeuronY = 0; poolNeuronY < poolH; poolNeuronY++) {
						for (int poolNeuronX = 0; poolNeuronX < poolW; poolNeuronX++) {
							int poolIndex = img * 3 * numberOfPoolNeuronsPerRGB
									+ rgb * numberOfPoolNeuronsPerRGB
									+ borderedPoolW * borderH + borderW
									+ poolNeuronX + borderedPoolW * poolNeuronY;

							Link l = new Link(true);
							l.setWeight(1.0);
							l.setFinal(true);
							System.out.println("Creating Filter-Pool link :");
							for (int ph = 0; ph < poolHeight; ph++) {
								for (int pw = 0; pw < poolWidth; pw++) {
									// int aux = poolNeuronY * filterW + pw + ph
									// * filterW + poolNeuronX * poolWidth;
									int aux = poolNeuronY * filterW
											* poolHeight + poolNeuronX
											* poolWidth + ph * filterW + pw;
									boolean isInside = (pw + poolNeuronX
											* poolWidth < filterW)
											&& (aux < numberOfFilterNeuronsPerRGB);
									if (isInside) {
										int filterIndex = img * 3
												* numberOfFilterNeuronsPerRGB
												+ rgb
												* numberOfFilterNeuronsPerRGB
												+ aux;
										// System.out.println("[img="
										// + img
										// + "][rgb="
										// + rgb
										// + "][aux="
										// + aux
										// + "][filterIndex="
										// + filterIndex
										// + "][hiddenNeurons.get("
										// + (2 * layer)
										// + ").size()="
										// + hiddenNeurons.get(2 * layer)
										// .size() + "]");
										l.addPairOfNeurons(new PairOfNeurons(
												hiddenNeurons.get(2 * layer)
														.get(filterIndex),
												hiddenNeurons
														.get(2 * layer + 1)
														.get(poolIndex)));
										System.out.println("...between ["
												+ hiddenNeurons.get(2 * layer)
														.get(filterIndex)
														.getID()
												+ "]["
												+ hiddenNeurons
														.get(2 * layer + 1)
														.get(poolIndex).getID()
												+ "]");
									}
								}
							}
							if (l.getPairsOfNeurons().size() > 0) {
								System.out.println("==> "
										+ l.toString()
										+ ", "
										+ l.getPairsOfNeurons().stream()
												.map(p -> p.toString())
												.collect(Collectors.toList()));
								layerOfLinks.add(l);
							}
						}
					}
				}
			}
			links.set(2 * layer + 1, layerOfLinks);
			System.out.println("Setting filter-pool links[" + (2 * layer + 1)
					+ "]");
			System.out.println("");
			System.out.println("-----------------");
		}
		System.out.println("-----------------");
		System.out.println("");

		// Links input-1st layer filter
		int numberOfImagesA = 1;

		int imageWidthA = widthOf1stImage;
		int imageHeightA = heightOf1stImage;
		int imageWidthB = widthsAfterFilter.get(0);
		int imageHeightB = heightsAfterFilter.get(0);

		int numberOfFiltersAB = filtersData.get(0).getNumberOfFilters();

		FilterProperties fp = filtersData.get(0).getFilterProperties();
		int filterWidth = fp.getFilterWidth();
		int filterHeight = fp.getFilterHeight();

		int numberOfANeuronsPerRGB = imageWidthA * imageHeightA;
		int numberOfBNeuronsPerRGB = imageWidthB * imageHeightB;

		layerOfLinks = new ArrayList<Link>();
		for (int imgA = 0; imgA < numberOfImagesA; imgA++) {
			for (int filter = 0; filter < numberOfFiltersAB; filter++) {
				int imgB = filter + imgA * numberOfFiltersAB;
				for (int fh = 0; fh < filterHeight; fh++) {
					for (int fw = 0; fw < filterWidth; fw++) {
						for (int rgb = 0; rgb < 3; rgb++) {
							Link l = new Link(true);
							l.setWeight(255.0 * Math.random());
							l.setMinValue(0.0);
							l.setMaxValue(255.0);
							l.setChangeConstrained(true);
							l.setMinChange(0.4); // 0.4
							l.setMaxChange(3.0);
							System.out.println("Creating Input-Filter link :");
							int groundIndexA = imgA * 3
									* numberOfANeuronsPerRGB + rgb
									* numberOfANeuronsPerRGB;
							int groundIndexB = imgB * 3
									* numberOfBNeuronsPerRGB + rgb
									* numberOfBNeuronsPerRGB;

							for (int y = 0; y < imageHeightB; y++) {
								for (int x = 0; x < imageWidthB; x++) {
									int auxB = imageWidthB * y + x;
									int indexB = groundIndexB + auxB;

									int auxA = imageWidthA * y + x + fw + fh
											* imageWidthA;
									int indexA = groundIndexA + auxA;

									// System.out.println("fh = " + fh);
									// System.out.println("fw = " + fw);
									// System.out.println("y = " + y);
									// System.out.println("x = " + x);
									// System.out.println("imageWidthA = "
									// + imageWidthA);
									// System.out.println("imageWidthB = "
									// + imageWidthB);
									// System.out.println("auxA = " + auxA);

									l.addPairOfNeurons(new PairOfNeurons(
											inputNeurons.get(indexA),
											hiddenNeurons.get(0).get(indexB)));
									System.out.println("...between ["
											+ inputNeurons.get(indexA).getID()
											+ "]["
											+ hiddenNeurons.get(0).get(indexB)
													.getID() + "]");
								}
							}
							layerOfLinks.add(l);
						}
					}
				}
			}
		}
		links.set(0, layerOfLinks);
		System.out.println("Setting input links[" + 0 + "]");
		System.out.println("-----------------");
		System.out.println("");

		// Links hiddenLayers (pool->filter)
		if (filtersData.size() > 1) {

			for (int layer = 0; layer < filtersData.size() - 1; layer++) {
				numberOfImagesA = numberOfImagesAfterLayer.get(layer);

				imageWidthA = widthsAfterPoolerBordered.get(layer);
				imageHeightA = heightsAfterPoolerBordered.get(layer);
				imageWidthB = widthsAfterFilter.get(layer + 1);
				imageHeightB = heightsAfterFilter.get(layer + 1);

				numberOfFiltersAB = filtersData.get(layer + 1)
						.getNumberOfFilters();

				fp = filtersData.get(layer).getFilterProperties();
				filterWidth = fp.getFilterWidth();
				filterHeight = fp.getFilterHeight();

				FilterProperties fpNext = filtersData.get(layer + 1)
						.getFilterProperties();
				int filterWidthNext = fpNext.getFilterWidth();
				int filterHeightNext = fpNext.getFilterHeight();

				numberOfANeuronsPerRGB = imageWidthA * imageHeightA;
				numberOfBNeuronsPerRGB = imageWidthB * imageHeightB;

				layerOfLinks = new ArrayList<Link>();
				for (int imgA = 0; imgA < numberOfImagesA; imgA++) {
					for (int filter = 0; filter < numberOfFiltersAB; filter++) {
						int imgB = filter + imgA * numberOfFiltersAB;
						// for (int fh = 0; fh < filterHeight; fh++) {
						// for (int fw = 0; fw < filterWidth; fw++) {
						for (int fh = 0; fh < filterHeightNext; fh++) {
							for (int fw = 0; fw < filterWidthNext; fw++) {
								for (int rgb = 0; rgb < 3; rgb++) {
									Link l = new Link(true);
									l.setWeight(255.0 * Math.random());
									l.setMinValue(0.0);
									l.setMaxValue(255.0);
									l.setChangeConstrained(true);
									l.setMinChange(0.4); // 0.4
									l.setMaxChange(3.0);
									System.out
											.println("Creating Pool-Filter link: [imgA="
													+ imgA
													+ "][imgB="
													+ imgB
													+ "][rgb=" + rgb + "]");
									int groundIndexA = imgA * 3
											* numberOfANeuronsPerRGB + rgb
											* numberOfANeuronsPerRGB;
									int groundIndexB = imgB * 3
											* numberOfBNeuronsPerRGB + rgb
											* numberOfBNeuronsPerRGB;

									for (int y = 0; y < imageHeightB; y++) {
										for (int x = 0; x < imageWidthB; x++) {
											int auxB = imageWidthB * y + x;
											int indexB = groundIndexB + auxB;

											int auxA = imageWidthA * y + x + fw
													+ fh * imageWidthA;
											int indexA = groundIndexA + auxA;

											l.addPairOfNeurons(new PairOfNeurons(
													hiddenNeurons.get(
															2 * layer + 1).get(
															indexA),
													hiddenNeurons.get(
															2 * layer + 2).get(
															indexB)));
											System.out.println("...between ["
													+ hiddenNeurons
															.get(2 * layer + 1)
															.get(indexA)
															.getID()
													+ "]["
													+ hiddenNeurons
															.get(2 * layer + 2)
															.get(indexB)
															.getID() + "]");
										}
									}
									layerOfLinks.add(l);
								}
							}
						}
					}
				}
				links.set(2 * (layer + 1), layerOfLinks);
				System.out.println("Setting pool-filter links["
						+ (2 * (layer + 1)) + "]");
				System.out.println("");
				System.out.println("-----------------");
			}
		}
		System.out.println("-----------------");
		System.out.println("");

		// Links layer N(pool)->y
		int lastHiddenNeuronsSize = hiddenNeurons.get(
				2 * numberOfFilterPoolLayers - 1).size();
		layerOfLinks = new ArrayList<Link>();
		for (int i = 0; i < lastHiddenNeuronsSize; i++) {
			for (int j = 0; j < outputNeuronsSize; j++) {
				System.out.println("Creating Pool-Output link :");
				Link l = new Link(true);
				l.addPairOfNeurons(new PairOfNeurons(hiddenNeurons.get(
						2 * numberOfFilterPoolLayers - 1).get(i), outputNeurons
						.get(j)));
				System.out.println("...between ["
						+ hiddenNeurons.get(2 * numberOfFilterPoolLayers - 1)
								.get(i).getID() + "]["
						+ outputNeurons.get(j).getID() + "]");
				layerOfLinks.add(l);
			}
		}
		links.set(2 * numberOfFilterPoolLayers, layerOfLinks);
		System.out.println("Setting output links["
				+ (2 * numberOfFilterPoolLayers) + "]");
		System.out.println("-----------------");
		System.out.println("");
		setLinks(links);
		System.out.println("-- CONSTRUCTION DONE");
		System.out.println("-- startingForwardFeed...");

		startingForwardFeed();
		resetNeuronValues();
		resetLinkDeltas();

		System.out.println("-- DONE");
		System.out.println("");
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getBorderW() {
		return firstBorderW;
	}

	public int getBorderH() {
		return firstBorderH;
	}

	public ArrayList<String> getOutputNames() {
		return outputNames;
	}

	public ArrayList<PairOfFilterNumberAndProperties> getFiltersData() {
		return filtersData;
	}

	@Override
	protected void startPrintNN() {
		System.out.println("--------");
		System.out.println("ConvolutionalNeuralNetwork");
		System.out.println("--------");
		System.out.println("Image size: [ " + imageWidth + " x " + imageHeight
				+ " ]");
		System.out.println("Filters:");
		for (PairOfFilterNumberAndProperties pair : filtersData) {
			System.out.println(pair.toString());
		}
		System.out.println("--------");
	}
}
