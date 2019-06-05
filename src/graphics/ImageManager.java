package graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import links.Link;
import neuralNets.ConvolutionalNeuralNetwork;
import auxiliar.PairOfFilterNumberAndProperties;
import filters.FilterProperties;

public class ImageManager {

	public ImageManager() {

	}

	public BufferedImage getImage(String file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(file));
		} catch (IOException e) {
			System.out.println("{ImageManager} - Error reading image [" + file + "]: "
					+ e.getMessage());
		}
		return img;
	}

	public ArrayList<Integer> getImageArray(BufferedImage image) {
		return getImageArray(image, 0, 0);
	}

	public ArrayList<Integer> getImageArray(BufferedImage image, int borderW,
			int borderH) {
		ArrayList<Integer> R = new ArrayList<Integer>();
		ArrayList<Integer> G = new ArrayList<Integer>();
		ArrayList<Integer> B = new ArrayList<Integer>();

		int totalWidth = image.getWidth() + 2 * borderW;
		int totalHeight = image.getHeight() + 2 * borderH;

		for (int j = 0; j < borderH; j++) {
			for (int i = 0; i < totalWidth; i++) {
				R.add(0);
				G.add(0);
				B.add(0);
			}
		}
		for (int j = 0; j < image.getHeight(); j++) {
			for (int i = 0; i < borderW; i++) {
				R.add(0);
				G.add(0);
				B.add(0);
			}
			for (int i = 0; i < image.getWidth(); i++) {
				Color RGB = new Color(image.getRGB(i, j));
				R.add(RGB.getRed());
				G.add(RGB.getGreen());
				B.add(RGB.getBlue());
			}
			for (int i = 0; i < borderW; i++) {
				R.add(0);
				G.add(0);
				B.add(0);
			}
		}
		for (int j = 0; j < borderH; j++) {
			for (int i = 0; i < totalWidth; i++) {
				R.add(0);
				G.add(0);
				B.add(0);
			}
		}

		// Build imageVector
		ArrayList<Integer> imageVector = new ArrayList<Integer>();
		for (int k = 0; k < R.size(); k++) {
			imageVector.add(R.get(k));
		}
		for (int k = 0; k < G.size(); k++) {
			imageVector.add(G.get(k));
		}
		for (int k = 0; k < B.size(); k++) {
			imageVector.add(B.get(k));
		}

		return imageVector;
	}

	public ArrayList<BufferedImage> getRGBImages(BufferedImage image) {

		int w = image.getWidth();
		int h = image.getHeight();

		BufferedImage imageRED = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage imageGREEN = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage imageBLUE = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage imageGRAY = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		for (int j = 0; j < image.getHeight(); j++) {
			for (int i = 0; i < image.getWidth(); i++) {
				Color RGB = new Color(image.getRGB(i, j));

				int R = RGB.getRed();
				int G = RGB.getGreen();
				int B = RGB.getBlue();
				int gray = (int) (Math.round((R + G + B) / 3.0));

				imageRED.setRGB(i, j, new Color(R, 0, 0).getRGB());
				imageGREEN.setRGB(i, j, new Color(0, G, 0).getRGB());
				imageBLUE.setRGB(i, j, new Color(0, 0, B).getRGB());
				imageGRAY.setRGB(i, j, new Color(gray, gray, gray).getRGB());
			}
		}

		ArrayList<BufferedImage> arrayToReturn = new ArrayList<BufferedImage>(4);
		arrayToReturn.add(imageRED);
		arrayToReturn.add(imageGREEN);
		arrayToReturn.add(imageBLUE);
		arrayToReturn.add(imageGRAY);

		return arrayToReturn;
	}

	// Saves image image
	public void saveToImage(BufferedImage image, String fileName) {
		if (fileName.contains(".png")) {

			if (fileName.contains("\\")) {
				String[] tokens = fileName.replace("\\", "rrrXXXrrr").split(
						"rrrXXXrrr");
				// System.out.println("tokens = " + Arrays.toString(tokens));
				String folder = tokens[tokens.length - 2];
				// System.out.println("folder = " + folder);
				File directory = new File("Platonic/" + folder);
				if (!directory.exists()) {
					directory.mkdir();
				}
			}

			File outputfile = new File(fileName);
			try {
				ImageIO.write(image, "png", outputfile);
			} catch (IOException ex) {
				System.out.println("Could not save image [" + fileName + "]: "
						+ ex.getMessage());
			}
			while (!outputfile.exists()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
				}
			}
		} else {
			// ArrayList<BufferedImage> RGBgrayImages = getRGBImages(image);
			//
			// File outputfile = new File("Platonic/" + "0_" + fileName +
			// ".png");
			// try {
			// ImageIO.write(image, "png", outputfile);
			// } catch (IOException ex) {
			// System.out.println("Could not save image [" + fileName + "]: "
			// + ex.getMessage());
			// }
			// File outputfile2 = new File("Platonic/" + "1RED_" + fileName
			// + ".png");
			// try {
			// ImageIO.write(RGBgrayImages.get(0), "png", outputfile2);
			// } catch (IOException ex) {
			// System.out.println("Could not save image(red) [" + fileName
			// + "]: " + ex.getMessage());
			// }
			// File outputfile3 = new File("Platonic/" + "2GREEN_" + fileName
			// + ".png");
			// try {
			// ImageIO.write(RGBgrayImages.get(1), "png", outputfile3);
			// } catch (IOException ex) {
			// System.out.println("Could not save image(blue) [" + fileName
			// + "]: " + ex.getMessage());
			// }
			// File outputfile4 = new File("Platonic/" + "3BLUE_" + fileName
			// + ".png");
			// try {
			// ImageIO.write(RGBgrayImages.get(2), "png", outputfile4);
			// } catch (IOException ex) {
			// System.out.println("Could not save image(green) [" + fileName
			// + "]: " + ex.getMessage());
			// }
			// File outputfile5 = new File("Platonic/" + "4GRAY_" + fileName
			// + ".png");
			// try {
			// ImageIO.write(RGBgrayImages.get(3), "png", outputfile5);
			// } catch (IOException ex) {
			// System.out.println("Could not save image(gray) [" + fileName
			// + "]: " + ex.getMessage());
			// }
		}
	}

	public void saveFilterToImage(BufferedImage image, String CNNname,
			int layer, int filter) {

		int count = 0;
		File outputfile = new File("Filters/" + CNNname + "/5TMP/" + "Layer_"
				+ String.format("%03d", layer + 1) + "_Filter_"
				+ String.format("%03d", filter + 1) + "_v_"
				+ String.format("%09d", count) + ".png");
		while (outputfile.exists()) {
			count = count + 1;
			outputfile = new File("Filters/" + CNNname + "/5TMP/" + "Layer_"
					+ String.format("%03d", layer + 1) + "_Filter_"
					+ String.format("%03d", filter + 1) + "_v_"
					+ String.format("%09d", count) + ".png");
		}
		try {
			// System.out.println("Saving outputfile = "+outputfile);
			ImageIO.write(image, "png", outputfile);
			ArrayList<BufferedImage> RGBimages = getRGBImages(image);
			ImageIO.write(RGBimages.get(0), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/1RED")));
			ImageIO.write(RGBimages.get(1), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/2GREEN")));
			ImageIO.write(RGBimages.get(2), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/3BLUE")));
			ImageIO.write(RGBimages.get(3), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/4GRAY")));
		} catch (IOException ex) {
			System.out.println("Could not save image ["
					+ outputfile.getAbsolutePath() + "]: " + ex.getMessage());
		}

		outputfile = new File("Filters/" + CNNname + "/" + "Layer_"
				+ String.format("%03d", layer + 1) + "_Filter_"
				+ String.format("%03d", filter + 1) + ".png");
		try {
			ImageIO.write(image, "png", outputfile);
			ArrayList<BufferedImage> RGBimages = getRGBImages(image);
			ImageIO.write(RGBimages.get(0), "png", new File(outputfile
					.getAbsolutePath().replace("Layer_", "1RED/Layer_")));
			ImageIO.write(RGBimages.get(1), "png", new File(outputfile
					.getAbsolutePath().replace("Layer_", "2GREEN/Layer_")));
			ImageIO.write(RGBimages.get(2), "png", new File(outputfile
					.getAbsolutePath().replace("Layer_", "3BLUE/Layer_")));
			ImageIO.write(RGBimages.get(3), "png", new File(outputfile
					.getAbsolutePath().replace("Layer_", "4GRAY/Layer_")));
		} catch (IOException ex) {
			System.out.println("Could not save image ["
					+ outputfile.getAbsolutePath() + "]: " + ex.getMessage());
		}
	}

	public BufferedImage getBufferedImageFromArray(
			ArrayList<Integer> imageVector, int width, int height) {
		try {
			BufferedImage bi = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			int size = width * height;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int R = imageVector.get(y * width + x + 0);
					int G = imageVector.get(y * width + x + size);
					int B = imageVector.get(y * width + x + 2 * size);

					bi.setRGB(x, y, new Color(R, G, B).getRGB());
				}
			}
			return bi;
		} catch (Exception e) {
			System.out.println("Could not get image from ArrayList<Integer>: "
					+ e.getMessage());
			return null;
		}
	}

	public BufferedImage getScreenShot(JPanel panel) {
		BufferedImage bi = new BufferedImage(panel.getWidth(),
				panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		try {
			panel.paint(bi.getGraphics());
		} catch (Exception e) {
			System.out.println("Error painting: " + e.getMessage());
		}
		return bi;
	}

	public void saveFilters(ConvolutionalNeuralNetwork CNN) {
		System.out.println("--- Saving Filters...");
		String CNNname = CNN.getName();
		ArrayList<ArrayList<Link>> links = CNN.getLinks();
		ArrayList<PairOfFilterNumberAndProperties> filtersData = CNN
				.getFiltersData();

		File directory = new File("Filters/" + CNNname);
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/1RED");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/2GREEN");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/3BLUE");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/4GRAY");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/5TMP");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/5TMP/1RED");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/5TMP/2GREEN");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/5TMP/3BLUE");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Filters/" + CNNname + "/5TMP/4GRAY");
		if (!directory.exists()) {
			directory.mkdir();
		}

		for (int i = 0; i < filtersData.size(); i++) {
			PairOfFilterNumberAndProperties filterProperties = filtersData
					.get(i);
			int linksLayer = 2 * i;
			int numberOfFilters = filterProperties.getNumberOfFilters();
			FilterProperties properties = filterProperties
					.getFilterProperties();
			int fw = properties.getFilterWidth();
			int fh = properties.getFilterHeight();
			int filterSize = fw * fh;

			for (int filter = 0; filter < numberOfFilters; filter++) {
				// System.out.println("===================");
				// System.out.println("filter = " + filter);
				// System.out.println("----");
				ArrayList<Integer> imageVector = new ArrayList<Integer>();
				for (int rgb = 0; rgb < 3; rgb++) {
					for (int idx = 0; idx < filterSize; idx++) {
						// int aux = filter * filterSize * 3 + rgb * filterSize
						// + idx;
						int aux = filter * filterSize * 3 + idx * 3 + rgb;
						int colorValue = (int) (Math.round(links
								.get(linksLayer).get(aux).getWeight()));
						imageVector.add(colorValue);
					}
				}
				BufferedImage filterImage = getBufferedImageFromArray(
						imageVector, fw, fh);
				saveFilterToImage(filterImage, CNNname, i, filter);
			}
		}
		System.out.println("--- Saving ok...");
	}

	public void savePlatonicImage(ArrayList<Integer> imageVector,
			String CNNname, String platonicImageName, int width, int height) {
		BufferedImage image = getBufferedImageFromArray(imageVector, width,
				height);

		File directory = new File("Platonic/" + CNNname);
		if (!directory.exists()) {
			directory.mkdir();
		}directory = new File("Platonic/" + CNNname + "/1RED");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/2GREEN");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/3BLUE");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/4GRAY");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/5TMP");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/5TMP/1RED");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/5TMP/2GREEN");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/5TMP/3BLUE");
		if (!directory.exists()) {
			directory.mkdir();
		}
		directory = new File("Platonic/" + CNNname + "/5TMP/4GRAY");
		if (!directory.exists()) {
			directory.mkdir();
		}

		int count = 0;
		File outputfile = new File("Platonic/" + CNNname + "/" + "5TMP/"
				+ platonicImageName + "_v_" + String.format("%09d", count)
				+ ".png");
		while (outputfile.exists()) {
			count = count + 1;
			outputfile = new File("Platonic/" + CNNname + "/" + "5TMP/"
					+ platonicImageName + "_v_" + String.format("%09d", count)
					+ ".png");
		}
		try {
			// System.out.println("Saving outputfile = "+outputfile);
			ImageIO.write(image, "png", outputfile);
			ArrayList<BufferedImage> RGBimages = getRGBImages(image);
			ImageIO.write(RGBimages.get(0), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/1RED")));
			ImageIO.write(RGBimages.get(1), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/2GREEN")));
			ImageIO.write(RGBimages.get(2), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/3BLUE")));
			ImageIO.write(RGBimages.get(3), "png", new File(outputfile
					.getAbsolutePath().replace("TMP", "TMP/4GRAY")));
		} catch (IOException ex) {
			System.out.println("Could not save image ["
					+ outputfile.getAbsolutePath() + "]: " + ex.getMessage());
		}

		outputfile = new File("Platonic/" + CNNname + "/" 
				+ platonicImageName + ".png");
		try {
			ImageIO.write(image, "png", outputfile);
			ArrayList<BufferedImage> RGBimages = getRGBImages(image);
			ImageIO.write(RGBimages.get(0), "png", new File(outputfile
					.getAbsolutePath().replace(platonicImageName, "1RED/"+platonicImageName)));
			ImageIO.write(RGBimages.get(1), "png", new File(outputfile
					.getAbsolutePath().replace(platonicImageName, "2GREEN/"+platonicImageName)));
			ImageIO.write(RGBimages.get(2), "png", new File(outputfile
					.getAbsolutePath().replace(platonicImageName, "3BLUE/"+platonicImageName)));
			ImageIO.write(RGBimages.get(3), "png", new File(outputfile
					.getAbsolutePath().replace(platonicImageName, "4GRAY/"+platonicImageName)));
		} catch (IOException ex) {
			System.out.println("Could not save image ["
					+ outputfile.getAbsolutePath() + "]: " + ex.getMessage());
		}
	}
}
