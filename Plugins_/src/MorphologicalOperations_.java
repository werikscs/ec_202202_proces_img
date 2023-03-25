import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class MorphologicalOperations_ implements PlugIn, DialogListener {
	ImagePlus BACKUP_IMG = null;

	@Override
	public void run(String arg) {
		GenericDialog genericDialog = createDialog();
		checkDialogOutput(genericDialog);
	}

	private GenericDialog createDialog() {
		GenericDialog genericDialog = new GenericDialog("Operações Morfológicas");
		genericDialog.addDialogListener(this);

		genericDialog.addMessage("Escolha a operação:");
		String filters[] = { Filters.DILATACAO.name(), Filters.EROSAO.name(), Filters.FECHAMENTO.name(),
				Filters.ABERTURA.name(), Filters.BORDA_OUTLINE.name(), };
		genericDialog.addRadioButtonGroup(null, filters, 5, 1, null);

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

		int structuringElement[][] = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };

		if (chosenFilter.equals(Filters.DILATACAO.name())) {
//  		IJ.showMessage(chosenFilter);
			updateImage(structuringElement, 0);
		}

		if (chosenFilter.equals(Filters.EROSAO.name())) {
//			IJ.showMessage(chosenFilter);
			updateImage(structuringElement, 1);
		}

		if (chosenFilter.equals(Filters.FECHAMENTO.name())) {
//			IJ.showMessage(chosenFilter);
		}

		if (chosenFilter.equals(Filters.ABERTURA.name())) {
//			IJ.showMessage(chosenFilter);
			updateImage(structuringElement, 0);
			updateImage(structuringElement, 1);
		}

		if (chosenFilter.equals(Filters.BORDA_OUTLINE.name())) {
			IJ.showMessage(chosenFilter);
		}

		return true;
	}

	private void updateImage(int structuringElement[][], int operation) {
		ImagePlus img = IJ.getImage();
		ImageProcessor imgProcessor = img.getProcessor();
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();

		if (BACKUP_IMG == null) {
			BACKUP_IMG = img.duplicate();
			BACKUP_IMG.setTitle(img.getTitle());
		}
		
		ImageProcessor backupImgProcessor = BACKUP_IMG.getProcessor();

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {

				int currentPixel = backupImgProcessor.getPixel(column, row);

					if (currentPixel == 255) {
						applyOperation(structuringElement, backupImgProcessor, imgProcessor, column, row, operation);
					}				
			}
		}

		img.updateAndDraw();
	}

	private void applyOperation(int[][] structuringElement, ImageProcessor backupImgProcessor, ImageProcessor imgProcessor, int column,	int row, int operation) {
		
		int coords[][] = { 
				{ column-1, row-1 }, { column, row-1 }, { column+1, row-1 }, 
				{ column-1, row   }, { column, row   }, { column+1, row   },
				{ column-1, row+1 }, { column, row+1 }, { column+1, row+1 }
		};
		
		if(operation == 0) {
			for (int i = 0; i < coords.length; i++) {
				try {
					int structuringElementValue = structuringElement[i / 3][i % 3];
					if (structuringElementValue == 1) {
						imgProcessor.putPixel(coords[i][0], coords[i][1], 255);
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
			return;
		}
		
		if (operation == 1) {
			for (int i = 0; i < coords.length; i++) {
				try {
					int structuringElementValue = structuringElement[i / 3][i % 3];
					if (structuringElementValue == 1) {
						int currentPixel = backupImgProcessor.getPixel(coords[i][0], coords[i][1]);
						if(currentPixel == 0) return;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					return;
				}
			}
			
//			IJ.log("----- " + "row: " + row + ", column: " + column + " -----");
			
			for (int i = 0; i < coords.length; i++) {
				if(i == 4) continue;
				int structuringElementValue = structuringElement[i / 3][i % 3];
				if (structuringElementValue == 1) {
//					int currentPixel = backupImgProcessor.getPixel(coords[i][0], coords[i][1]);
//					IJ.log("EE - row: " + coords[i][1] + ", column: " + coords[i][0] + ", currentPixel: " + currentPixel);
					imgProcessor.putPixel(coords[i][0], coords[i][1], 0);
				}
			}
			
			return;
		}

	}

	enum Filters {
		DILATACAO("dilatação"),
		EROSAO("erosao"),
		FECHAMENTO("fechamento"),
		ABERTURA("abertura"),
		BORDA_OUTLINE("borda-outline");

		Filters(String filter) {}
	}
}
