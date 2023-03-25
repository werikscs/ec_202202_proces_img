import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class MorphologicalOperations_2_ implements PlugIn, DialogListener {
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
		ImagePlus img = IJ.getImage();
		
		if (BACKUP_IMG == null) {
			BACKUP_IMG = img.duplicate();
			BACKUP_IMG.setTitle(img.getTitle());
		}
		
		String chosenFilter = gd.getNextRadioButton();

		int structuringElement[][] = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };

		if (chosenFilter.equals(Filters.DILATACAO.name())) {
			// IJ.showMessage(chosenFilter);
			ImagePlus dilatedImg = updateImage(img, BACKUP_IMG, structuringElement, 255);
			dilatedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.EROSAO.name())) {
			// IJ.showMessage(chosenFilter);
			ImagePlus erodedImg = updateImage(img, BACKUP_IMG, structuringElement, 0);
			erodedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.FECHAMENTO.name())) {
			// IJ.showMessage(chosenFilter);
			ImagePlus erodedImg = updateImage(img, BACKUP_IMG, structuringElement, 255);
			ImagePlus duplicatedErodedImg = erodedImg.duplicate();
			ImagePlus openedImg = updateImage(erodedImg, duplicatedErodedImg, structuringElement, 0);
			openedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.ABERTURA.name())) {
			// IJ.showMessage(chosenFilter);
			ImagePlus erodedImg = updateImage(img, BACKUP_IMG, structuringElement, 0);
			ImagePlus duplicatedErodedImg = erodedImg.duplicate();
			ImagePlus openedImg = updateImage(erodedImg, duplicatedErodedImg, structuringElement, 255);
			openedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.BORDA_OUTLINE.name())) {
			// IJ.showMessage(chosenFilter);
			ImagePlus erodedImg = updateImage(img, BACKUP_IMG, structuringElement, 0);
			ImagePlus subtractedImg = subtractImages(erodedImg, BACKUP_IMG);
			subtractedImg.updateAndDraw();
		}

		return true;
	}

	private ImagePlus subtractImages(ImagePlus erodedImg, ImagePlus originalImage) {
		ImageProcessor erodedImgProcessor = erodedImg.getProcessor();
		
		for (int x = 0; x < erodedImg.getWidth(); x++) {
	    for (int y = 0; y < erodedImg.getHeight(); y++) {
        int pixelOriginal = originalImage.getPixel(x, y)[0];
        int pixelEroded = erodedImg.getPixel(x, y)[0];
        int result = Math.max(0, pixelOriginal - pixelEroded);
        erodedImgProcessor.putPixel(x, y, result);
	    }
		}
		
		return erodedImg;
	}

	private ImagePlus updateImage(ImagePlus img, ImagePlus backupImg, int structuringElement[][], int pixelOfInterestValue) {
		ImageProcessor imgProcessor = img.getProcessor();
		ImageProcessor backupImgProcessor = backupImg.getProcessor();
		
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();		

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {
				int currentPixel = backupImgProcessor.getPixel(column, row);
				if (currentPixel == pixelOfInterestValue) {
					applyOperation(structuringElement, imgProcessor, column, row, pixelOfInterestValue);
				}	
			}
		}

		return img;
	}

	private void applyOperation(int[][] structuringElement, ImageProcessor imgProcessor, int column,	int row, int value) {
		
		int coords[][] = { 
				{ column-1, row-1 }, { column, row-1 }, { column+1, row-1 }, 
				{ column-1, row   }, { column, row   }, { column+1, row   },
				{ column-1, row+1 }, { column, row+1 }, { column+1, row+1 }
		};
		
		for (int i = 0; i < coords.length; i++) {
			try {
				int structuringElementValue = structuringElement[i / 3][i % 3];
				if (structuringElementValue == 1) {
					imgProcessor.putPixel(coords[i][0], coords[i][1], value);
				}
			} catch (ArrayIndexOutOfBoundsException e) {}
		}
		return;
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
