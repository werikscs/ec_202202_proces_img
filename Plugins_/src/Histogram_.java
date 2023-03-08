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

		if (chosenTechnique.equals(Techniques.EXPANSAO.name())) {
			ImagePlus img = IJ.getImage();
			int imgWidth = img.getWidth();
			int imgHeight = img.getHeight();
			ImageProcessor imgProcessor = img.getProcessor();

			if (BACKUP_IMG == null) {
				BACKUP_IMG = img.duplicate();
				BACKUP_IMG.setTitle(img.getTitle());
			}

			int minPossiblePixelValue = 0;
			int maxPossiblePixelValue = 255;
			int lowestPixelValue = getLowestPixel(img);
			int highestPixelValue = getHighestPixel(img);

			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValueArray[] = BACKUP_IMG.getPixel(column, row);

					int newPixelValue = minPossiblePixelValue + (pixelValueArray[0] - lowestPixelValue)* ((maxPossiblePixelValue - minPossiblePixelValue) / (highestPixelValue - lowestPixelValue));

					imgProcessor.putPixel(column, row, new int[] { newPixelValue, newPixelValue, newPixelValue });
				}
			}

			img.updateAndDraw();
		}

		if (chosenTechnique.equals(Techniques.EQUALIZACAO.name())) {
			ImagePlus img = IJ.getImage();
			int imgWidth = img.getWidth();
			int imgHeight = img.getHeight();
			ImageProcessor imgProcessor = img.getProcessor();

			if (BACKUP_IMG == null) {
				BACKUP_IMG = img.duplicate();
				BACKUP_IMG.setTitle(img.getTitle());
			}

			int numOfPixelsByIntensity[] = new int[256];
			double pixelProbability[] = new double[256];

			int totalNumberOfPixels = imgHeight * imgWidth;

			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValueArray[] = BACKUP_IMG.getPixel(column, row);
					int RChannel = (int) (pixelValueArray[0] * 0.333);
					int GChannel = (int) (pixelValueArray[1] * 0.333);
					int BChannel = (int) (pixelValueArray[2] * 0.333);
					int pixelValue = RChannel + GChannel + BChannel;

					// registra numeros de pixel de determinada intensidade
					numOfPixelsByIntensity[pixelValue] += 1;
					// calcula a probabilidade do pixel
					pixelProbability[pixelValue] = (numOfPixelsByIntensity[pixelValue] / (double) totalNumberOfPixels);

					imgProcessor.putPixel(column, row, new int[] { pixelValue, pixelValue, pixelValue });
				}
			}

			// calcula a probabilidade acumulada
			double cumulativeProbability[] = new double[256];
			cumulativeProbability[0] = pixelProbability[0];

			for (int i = 1; i < pixelProbability.length; i++) {
				cumulativeProbability[i] = cumulativeProbability[i - 1] + pixelProbability[i];
			}

			// novo range de intensidade
			int NEW_RANGE = 255;

			int pixelsToNewRange[] = new int[256];

			for (int i = 0; i < cumulativeProbability.length; i++) {
				pixelsToNewRange[i] = (int) Math.round(cumulativeProbability[i] * NEW_RANGE);
			}

			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValueArray[] = BACKUP_IMG.getPixel(column, row);
					int newPixelValue = pixelsToNewRange[pixelValueArray[0]];

					imgProcessor.putPixel(column, row, new int[] { newPixelValue, newPixelValue, newPixelValue });
				}
			}

			img.updateAndDraw();
		}

		return true;
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
