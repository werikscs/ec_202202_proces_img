import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class PontoAPontoOperations_ implements PlugIn, DialogListener {
	ImagePlus BACKUP_IMG = null;

	@Override
	public void run(String arg) {
		GenericDialog gui = createGUI();
		checkGUIOutput(gui);
	}

	private GenericDialog createGUI() {
		GenericDialog gui = new GenericDialog("Operações Ponto a Ponto");
		gui.addDialogListener(this);

		gui.addSlider("Brilho", -255, 255, 0, 1);
		gui.addSlider("Contraste", -255, 255, 0, 1);
		gui.addSlider("Solarização", 0, 255, 0, 1);
		gui.addSlider("Dessaturação", 0, 1, 1, 0.01);

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
		double brightness = gui.getNextNumber();
		double contrast = gui.getNextNumber();
		double solarization = gui.getNextNumber();
		double desaturation = gui.getNextNumber();
		
		double operationsValue[] = {brightness, contrast, solarization, desaturation};
		
		updateImage(operationsValue);

		return true;
	}

	private void updateImage(double operationsValue[]) {
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
				int RGBValue[] = changeBrightness(pixelValueArray, operationsValue[0]);
				RGBValue = changeContrast(RGBValue, operationsValue[1]);
				RGBValue = changeSolarization(RGBValue, operationsValue[2]);
				RGBValue = changeDesaturation(RGBValue, operationsValue[3]);

				imgProcessor.putPixel(column, row, RGBValue);
			}
		}

		img.updateAndDraw();
	}

	private int[] changeBrightness(int pixelValueArray[], double brightness) {
		int RChannel = validatePixelValue((pixelValueArray[0] + brightness));
		int GChannel = validatePixelValue((pixelValueArray[1] + brightness));
		int BChannel = validatePixelValue((pixelValueArray[2] + brightness));
		return new int[] { RChannel, GChannel, BChannel };
	}

	private int[] changeContrast(int pixelValueArray[], double contrast) {
		double contrastFactor = (double) ((259 * (contrast + 255)) / (double) (255 * (259 - contrast)));
		int RChannel = validatePixelValue((contrastFactor * (pixelValueArray[0] - 128)) + 128);
		int GChannel = validatePixelValue((contrastFactor * (pixelValueArray[1] - 128)) + 128);
		int BChannel = validatePixelValue((contrastFactor * (pixelValueArray[2] - 128)) + 128);
		return new int[] { RChannel, GChannel, BChannel };
	}

	private int[] changeSolarization(int pixelValueArray[], double solarization) {
		int RChannel = pixelValueArray[0] < solarization ? 255 - pixelValueArray[0] : pixelValueArray[0];
		int GChannel = pixelValueArray[1] < solarization ? 255 - pixelValueArray[1] : pixelValueArray[1];
		int BChannel = pixelValueArray[2] < solarization ? 255 - pixelValueArray[2] : pixelValueArray[2];
		return new int[] { RChannel, GChannel, BChannel };
	}

	private int[] changeDesaturation(int pixelValueArray[], double desaturation) {
		double pixelRToGray = 0.333 * pixelValueArray[0];
		double pixelGToGray = 0.333 * pixelValueArray[1];
		double pixelBToGray = 0.333 * pixelValueArray[2];
		double pixelValueMean = pixelRToGray + pixelGToGray + pixelBToGray;
		int RChannel = (int) (pixelValueMean + desaturation * (pixelValueArray[0] - pixelValueMean));
		int GChannel = (int) (pixelValueMean + desaturation * (pixelValueArray[1] - pixelValueMean));
		int BChannel = (int) (pixelValueMean + desaturation * (pixelValueArray[2] - pixelValueMean));
		return new int[] { RChannel, GChannel, BChannel };
	}

	private int validatePixelValue(double value) {
		if (value < 0)
			return 0;
		if (value > 255)
			return 255;
		return (int) value;
	}

	enum Operation {
		BRIGHTNESS("brightness"), CONTRAST("contrast"), SOLARIZATION("solarization"), DESATURATION("desaturation");

		Operation(String operation) {
		}
	}

}
