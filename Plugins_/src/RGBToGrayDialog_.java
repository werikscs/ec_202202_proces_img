import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class RGBToGrayDialog_ implements PlugIn, DialogListener {
	ImagePlus BACKUP_IMG = null;

	@Override
	public void run(String arg) {
		GenericDialog gui = createGUI();
		checkGUIOutput(gui);
	}

	private GenericDialog createGUI() {
		GenericDialog gui = new GenericDialog("RBG to Gray");
		gui.addDialogListener(this);

		gui.addMessage("Escolha a estratégia de conversão:");
		String strategies[] = {
				Strategies.MEDIA_ARITMETICA.name(),
				Strategies.LUMINANCE_TV_ANALOGICAS.name(),
				Strategies.LUMINANCE_SISTEMAS_DIGITAIS.name()
		};
		gui.addRadioButtonGroup(null, strategies, 3, 1, Strategies.MEDIA_ARITMETICA.name());
		gui.addCheckbox("Deseja aplicar as modificações em uma cópia?", false);

		gui.showDialog();
		return gui;
	}

	private void checkGUIOutput(GenericDialog gui) {
		ImagePlus img = IJ.getImage();

		if (gui.wasCanceled()) {
			img.setImage(BACKUP_IMG);
			img.updateAndDraw();
			return;
		}

		if (gui.wasOKed()) {
			boolean isToApplyModificationsToCopy = gui.getNextBoolean();
			String imgNewTitle = gui.getNextRadioButton() + " - " + img.getTitle();

			if (isToApplyModificationsToCopy) {
				ImagePlus imgCopy = img.duplicate();
				imgCopy.setTitle(imgNewTitle);
				img.setImage(BACKUP_IMG);
				img.updateAndDraw();
				imgCopy.show();
				return;
			} else {
				img.setTitle(imgNewTitle);
			}

		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gui, AWTEvent event) {

		String chosenStrategy = gui.getNextRadioButton();
		double channelsWeight[] = getPixelWeightsOfChosenStrategy(chosenStrategy);
		applyChosenStrategyToImage(channelsWeight);

		return true;
	}

	private double[] getPixelWeightsOfChosenStrategy(String chosenStrategy) {
		double channelsWeight[] = null;

		if (chosenStrategy.equals(Strategies.MEDIA_ARITMETICA.name())) {
			channelsWeight = new double[] { 0.333, 0.333, 0.333 };
		}

		if (chosenStrategy.equals(Strategies.LUMINANCE_TV_ANALOGICAS.name())) {
			channelsWeight = new double[] { 0.299, 0.587, 0.114 };
		}

		if (chosenStrategy.equals(Strategies.LUMINANCE_SISTEMAS_DIGITAIS.name())) {
			channelsWeight = new double[] { 0.2125, 0.7154, 0.072 };
		}

		return channelsWeight;
	}

	private void applyChosenStrategyToImage(double channelsWeight[]) {
		ImagePlus img = IJ.getImage();
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		ImageProcessor imgProcessor = img.getProcessor();

		if (BACKUP_IMG == null) {
			BACKUP_IMG = img.duplicate();
			BACKUP_IMG.setTitle(img.getTitle());
		}

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {
				int pixelValueArray[] = BACKUP_IMG.getPixel(column, row);
				int RChannel = (int) (pixelValueArray[0] * channelsWeight[0]);
				int GChannel = (int) (pixelValueArray[1] * channelsWeight[1]);
				int BChannel = (int) (pixelValueArray[2] * channelsWeight[2]);
				int newPixelvalue = RChannel + GChannel + BChannel;
				imgProcessor.putPixel(column, row, new int[] { newPixelvalue, newPixelvalue, newPixelvalue });
			}
		}

		img.updateAndDraw();
	}

	enum Strategies {
		MEDIA_ARITMETICA("Média Aritmética"),
		LUMINANCE_TV_ANALOGICAS("Luminance: TVs analógicas"),
		LUMINANCE_SISTEMAS_DIGITAIS("Luminance: sistemas digitais"),;

		Strategies(String strategy) {}
	}
}
