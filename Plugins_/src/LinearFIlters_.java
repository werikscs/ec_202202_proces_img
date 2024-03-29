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
		genericDialog.addRadioButtonGroup(null, filters, 3, 1, null);

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
			//  Média Aritmética
			double kernelMean = 1 / 9.0;
			double kernel[][] = {
					{ kernelMean, kernelMean, kernelMean },
					{ kernelMean, kernelMean, kernelMean },
					{ kernelMean, kernelMean, kernelMean }
			};
			updateImage(kernel);
		}

		if (chosenFilter.equals(Filters.PASSA_ALTA.name())) {
			double kernel[][] = {
					{ 0, -1, 0 },
					{ -1, 5, -1},
					{ 0, -1, 0 }
			};
			updateImage(kernel);
		}

		if (chosenFilter.equals(Filters.DE_BORDA.name())) {
			// norte
			double kernel[][] = {
					{ 1, 1, 1 },
					{ 1, -2, 1},
					{ -1, -1, -1 }
			};
			updateImage(kernel);
		}

		return true;
	}
	
	private void updateImage(double kernel[][]) {
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
				
				int newPixel = applyKernelToPixel(kernel, BACKUP_IMG, column, row);				
				imgProcessor.putPixel(column, row, newPixel);
			}
		}
		
		img.updateAndDraw();
	}

	private int applyKernelToPixel(double[][] kernel, ImagePlus img, int column, int row) {
		int coords[][] = {
				{column-1, row-1},{column, row-1},{column+1, row-1},
				{column-1, row},  {column, row},  {column+1, row},
				{column-1, row+1},{column, row+1},{column+1, row+1}
		};
		
		int newPixel = 0;
		
		for(int i = 0; i < coords.length; i++ ) {
			try {
		    int adjacentImgPixel = img.getPixel(coords[i][0], coords[i][1])[0];
		    double kernelToAdjancentPixel = kernel[i/3][i%3];
		    newPixel += kernelToAdjancentPixel * adjacentImgPixel;
	  	} catch (ArrayIndexOutOfBoundsException e) {}
		}
		
		return newPixel;		
	}

	enum Filters {
		PASSA_BAIXA("Passa-baixa"), PASSA_ALTA("Passa-alta"), DE_BORDA("De borda");

		Filters(String filter) {
		}
	}
}
