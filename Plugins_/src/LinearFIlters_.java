//Desenvolver um plugin para aplicar o filtro passa-baixas de média,
//um dos filtros passa-altas
//e um dos filtros de borda apresentados nos slides da aula.
//
//Deverá ser apresentada uma interface gráfica
//com as descrições dos filtros e botões de rádio
//para viabilizar a seleção do filtro a ser aplicado.
//
//Utilizar um kernels de dimensões 3 x 3.

import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class LinearFIlters_ implements PlugIn, DialogListener {
	ImagePlus BACKUP_IMG = null;

	@Override
	public void run(String arg) {
		GenericDialog genericDialog = createDialog();
		checkDialogOutput(genericDialog);
	}

	private GenericDialog createDialog() {
		GenericDialog genericDialog = new GenericDialog("Filtros Lineares");
		genericDialog.addDialogListener(this);

		genericDialog.addMessage("Escolha o filtro:");
		String filters[] = { Filters.PASSA_BAIXA.name(), Filters.PASSA_ALTA.name(), Filters.DE_BORDA.name() };
		genericDialog.addRadioButtonGroup(null, filters, 3, 1, Filters.PASSA_BAIXA.name());

		genericDialog.showDialog();
		return genericDialog;
	}

	private void checkDialogOutput(GenericDialog genericDialog) {
		ImagePlus img = IJ.getImage();

		if (genericDialog.wasCanceled()) {
			img.setImage(BACKUP_IMG);
			img.updateAndDraw();
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		String chosenFilter = gd.getNextRadioButton();

		if (chosenFilter.equals(Filters.PASSA_BAIXA.name())) {
			double kernelMean = 1 / 9.0;
			double kernel[][] = {
					{ kernelMean, kernelMean, kernelMean },
					{ kernelMean, kernelMean, kernelMean },
					{ kernelMean, kernelMean, kernelMean } };
		}

		if (chosenFilter.equals(Filters.PASSA_ALTA.name())) {
			IJ.showMessage(chosenFilter);
		}

		if (chosenFilter.equals(Filters.DE_BORDA.name())) {
			IJ.showMessage(chosenFilter);
		}

		return true;
	}
	
	private void updateImage() {
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

				
//				imgProcessor.putPixel(column, row, new int[] { pixelValue, pixelValue, pixelValue });
			}
		}
	}

	enum Filters {
		PASSA_BAIXA("Passa-baixa de média x"), PASSA_ALTA("Passa-alta de x"), DE_BORDA("De borda x");

		Filters(String filter) {
		}
	}
}
