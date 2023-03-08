import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Histogram_ implements PlugIn, DialogListener {
	ImagePlus BACKUP_IMG = null;

	@Override
	public void run(String arg) {
		GenericDialog gui = createGUI();
		checkGUIOutput(gui);
	}

	private GenericDialog createGUI() {
		GenericDialog gui = new GenericDialog("Técnicas de Histograma");
		gui.addDialogListener(this);

		gui.addMessage("Escolha a técnica:");
		String techniques[] = { Techniques.EXPANSAO.name(), Techniques.EQUALIZACAO.name() };
		gui.addRadioButtonGroup(null, techniques, 2, 1, Techniques.EQUALIZACAO.name());

		gui.showDialog();
		return gui;
	}

	private void checkGUIOutput(GenericDialog gui) {
		ImagePlus img = IJ.getImage();

		if (gui.wasCanceled()) {
			img.setImage(BACKUP_IMG);
			img.updateAndDraw();
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gui, AWTEvent event) {
		String chosenTechnique = gui.getNextRadioButton();

		ImagePlus img = IJ.getImage();
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		ImageProcessor imgProcessor = img.getProcessor();

		if (BACKUP_IMG == null) {
			BACKUP_IMG = img.duplicate();
			BACKUP_IMG.setTitle(img.getTitle());
		}

		if (chosenTechnique.equals(Techniques.EXPANSAO.name())) {

			int minPossiblePixelValue = 0;
			int maxPossiblePixelValue = 255;
			int lowestPixelValue = getLowestPixel(BACKUP_IMG);
			int highestPixelValue = getHighestPixel(BACKUP_IMG);

			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValue = BACKUP_IMG.getPixel(column, row)[0];

					int pl = pixelValue - lowestPixelValue;
					int maxMin = maxPossiblePixelValue - minPossiblePixelValue;
					int hl = highestPixelValue - lowestPixelValue;

					int newPixelValue = (int) (minPossiblePixelValue + (pl) * ((double)(maxMin) / (hl)));

					imgProcessor.putPixel(column, row, new int[] { newPixelValue, newPixelValue, newPixelValue });
				}
			}

			img.updateAndDraw();
		}

		if (chosenTechnique.equals(Techniques.EQUALIZACAO.name())) {

			int numOfPixelsByIntensity[] = getNumOfPixelsByIntensity(BACKUP_IMG);
			double pixelProbability[] = getPixelsProbability(BACKUP_IMG, numOfPixelsByIntensity);
			double cumulativeProbability[] = getCumulativeProbability(pixelProbability);
			int newIntensityRange = 255;
			int pixelsIntensityConvertedToNewRange[] = getPixelsConvertedToNewRange(cumulativeProbability, newIntensityRange);

			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValueArray = BACKUP_IMG.getPixel(column, row)[0];
					int newPixelValue = pixelsIntensityConvertedToNewRange[pixelValueArray];

					imgProcessor.putPixel(column, row, new int[] { newPixelValue, newPixelValue, newPixelValue });
				}
			}

			img.updateAndDraw();
		}

		return true;
	}

	private int[] getPixelsConvertedToNewRange(double[] cumulativeProbability, int newIntensityRange) {
		int pixelsToNewRange[] = new int[256];

		for (int i = 0; i < cumulativeProbability.length; i++) {
			pixelsToNewRange[i] = (int) Math.floor(cumulativeProbability[i] * newIntensityRange);
		}

		return pixelsToNewRange;
	}

	private double[] getCumulativeProbability(double[] pixelProbability) {
		double cumulativeProbability[] = new double[256];
		cumulativeProbability[0] = pixelProbability[0];

		for (int i = 1; i < pixelProbability.length; i++) {
			cumulativeProbability[i] = cumulativeProbability[i - 1] + pixelProbability[i];
		}

		return cumulativeProbability;
	}

	private double[] getPixelsProbability(ImagePlus img, int numOfPixelsByIntensity[]) {
		double pixelProbability[] = new double[256];
		int imgHeight = img.getHeight();
		int imgWidth = img.getWidth();
		int totalNumberOfPixels = imgHeight * imgWidth;

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {
				int pixelValue = img.getPixel(column, row)[0];
				pixelProbability[pixelValue] = (numOfPixelsByIntensity[pixelValue] / (double) totalNumberOfPixels);
			}
		}

		return pixelProbability;
	}

	private int[] getNumOfPixelsByIntensity(ImagePlus img) {
		int numOfPixelsByIntensity[] = new int[256];

		for (int row = 0; row < img.getHeight(); row++) {
			for (int column = 0; column < img.getWidth(); column++) {
				int pixelValue = img.getPixel(column, row)[0];
				numOfPixelsByIntensity[pixelValue] += 1;
			}
		}

		return numOfPixelsByIntensity;
	}

	private int getLowestPixel(ImagePlus img) {
		int lowestPixel = img.getPixel(0, 0)[0];

		for (int row = 0; row < img.getHeight(); row++) {
			for (int column = 0; column < img.getWidth(); column++) {
				if (img.getPixel(row, column)[0] < lowestPixel) {
					lowestPixel = img.getPixel(row, column)[0];
				}
			}
		}

		return lowestPixel;
	}

	private int getHighestPixel(ImagePlus img) {
		int highestPixel = img.getPixel(0, 0)[0];

		for (int row = 0; row < img.getHeight(); row++) {
			for (int column = 0; column < img.getWidth(); column++) {
				if (img.getPixel(row, column)[0] > highestPixel) {
					highestPixel = img.getPixel(row, column)[0];
				}
			}
		}

		return highestPixel;
	}

	enum Techniques {
		EXPANSAO("Expansão"), EQUALIZACAO("Equalização");

		Techniques(String technique) {
		}
	}

}
