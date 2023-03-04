//Criar um plugin para alterar os valores de brilho e contraste de uma imagem,
//bem como aplicar as técnicas de solarização e dessaturação na  mesma.
//
//Utilizar uma interface gráfica com quatro barras do tipo slider,
//uma para cada técnica,  um botão "ok" e um botão "cancel".
//As barras de slider alterarão as características da imagem quando movimentadas.
//
//Quando pressionado o botão "ok" as características da imagem serão alteradas de forma definitiva,
//caso seja pressionado o botão "cancel" a imagem voltará para as suas caraterísticas originais.

import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class PontoAPontoOperations_ implements PlugIn, DialogListener {
	ImagePlus BACKUP_IMG = null;
	int SLIDERS_VALUES[] = { 0, 128, 128, 128 };

	@Override
	public void run(String arg) {
		GenericDialog gui = createGUI();
		checkGUIOutput(gui);
	}

	private GenericDialog createGUI() {
		GenericDialog gui = new GenericDialog("Operações Ponto a Ponto");
		gui.addDialogListener(this);

		gui.addSlider("Brilho", -255, 255, SLIDERS_VALUES[0], 1);
		gui.addSlider("Contraste", 0, 255, SLIDERS_VALUES[1], 1);
		gui.addSlider("Solarização", 0, 255, SLIDERS_VALUES[2], 1);
		gui.addSlider("Dessaturação", 0, 255, SLIDERS_VALUES[3], 1);

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
		int brightness = (int) gui.getNextNumber();
		int contrast = (int) gui.getNextNumber();
		int solarization = (int) gui.getNextNumber();
		int desaturation = (int) gui.getNextNumber();

		if (brightness != SLIDERS_VALUES[0]) {
  		changeBrightness(brightness);  			
			SLIDERS_VALUES[0] = brightness;
		}

		if (contrast != SLIDERS_VALUES[1]) {
			System.out.println("contrast");
			SLIDERS_VALUES[1] = contrast;
		}

		if (solarization != SLIDERS_VALUES[2]) {
			System.out.println("solarization");
			SLIDERS_VALUES[2] = solarization;
		}

		if (desaturation != SLIDERS_VALUES[3]) {
			System.out.println("desaturation");
			SLIDERS_VALUES[3] = desaturation;
		}

		return true;
	}

	private void changeBrightness(int brightness) {
		ImagePlus img = IJ.getImage();
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		ImageProcessor imgProcessor = img.getProcessor();
		
		if(BACKUP_IMG == null) {
			BACKUP_IMG = img.duplicate();
			BACKUP_IMG.setTitle(img.getTitle());
		}

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {
				int pixelValueArray[] = BACKUP_IMG.getPixel(column, row);
				int RChannel = validatePixelValue((pixelValueArray[0] + brightness));
				int GChannel = validatePixelValue((pixelValueArray[1] + brightness));
				int BChannel = validatePixelValue((pixelValueArray[2] + brightness));
				imgProcessor.putPixel(column, row, new int[] { RChannel, GChannel, BChannel });
			}
		}
		
		img.updateAndDraw();
	}

	private int validatePixelValue(int value) {
		if(value < 0) return 0;
		if(value > 255) return 255;
		return value;
	}

}
