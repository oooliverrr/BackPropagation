package graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TestImagePanel extends JPanel {

	private ImageManager imageManager;
	private String fileName;
	private int width, height;
	private BufferedImage imageToDraw;

	public TestImagePanel(Dimension dim, int width, int height) {

		imageManager = new ImageManager();
		fileName = "";
		this.width = width;
		this.height = height;

		this.setSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		this.setPreferredSize(dim);
	}

	public void drawPicture(String filePath) {
		fileName = filePath;
		this.repaint();
	}

	public void drawPicture(BufferedImage image) {
		fileName = "";
		imageToDraw = image;
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Graphics2D g2d = (Graphics2D) g.create();

		boolean error = false;
		try {
			// 1) Draw image
			BufferedImage image = null;
			if (fileName.length() < 1) {
				image = imageToDraw;
			} else {
				image = imageManager.getImage(fileName);
			}
			if (image != null) {
				BufferedImage resizedImage = resize(image, this.getWidth(),
						this.getHeight());

				g.drawImage(resizedImage, 0, 0, this);
			} else {
				error = true;
			}
		} catch (Exception e) {
			error = true;
			System.out.println("Cannot draw image [" + fileName + "]: "
					+ e.getMessage());
		}

		if (error) {
			ArrayList<Integer> imageVector = new ArrayList<Integer>();
			for (int k = 0; k < width * height * 3; k++) {
				//imageVector.add((int) (127+Math.round(-10.0+20.0 * Math.random())));
				imageVector.add((int)(Math.round(127.0-10.0+20.0*Math.random())));
				//imageVector.add((int) (Math.round(255.0*Math.random())));
			}
			BufferedImage image = imageManager.getBufferedImageFromArray(
					imageVector, width, height);
			imageManager.saveToImage(image, fileName);
			BufferedImage resizedImage = resize(image, this.getWidth(),
					this.getHeight());
			g.drawImage(resizedImage, 0, 0, this);
		}
	}

	private static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}
}
