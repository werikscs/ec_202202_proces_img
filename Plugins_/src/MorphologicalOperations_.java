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
		ImagePlus img = IJ.getImage();

		if (BACKUP_IMG == null) {
			BACKUP_IMG = img.duplicate();
			BACKUP_IMG.setTitle(img.getTitle());
		}
		
		ImagePlus imgBlack = IJ.createImage("result", "black", IJ.getImage().getWidth(),IJ.getImage().getHeight(), 1);
		img.setProcessor(imgBlack.getProcessor());

		String chosenFilter = gd.getNextRadioButton();

		int structuringElement[][] = {
				{ 1, 1, 1 },
				{ 1, 1, 1 },
				{ 1, 1, 1 }
		};

		if (chosenFilter.equals(Filters.DILATACAO.name())) {
			ImagePlus dilatedImg = updateImage(img, BACKUP_IMG, structuringElement, 0);
			dilatedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.EROSAO.name())) {
			ImagePlus erodedImg = updateImage(img, BACKUP_IMG, structuringElement, 1);
			erodedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.FECHAMENTO.name())) {
			ImagePlus dilatedImg = updateImage(img, BACKUP_IMG, structuringElement, 0);
			ImagePlus duplicatedDilatedImg = dilatedImg.duplicate();
			ImagePlus closedImg = updateImage(dilatedImg, duplicatedDilatedImg, structuringElement, 1);
			closedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.ABERTURA.name())) {
			ImagePlus erodedImg = updateImage(img, BACKUP_IMG, structuringElement, 1);
			ImagePlus duplicatedErodedImg = erodedImg.duplicate();
			ImagePlus openedImg = updateImage(duplicatedErodedImg, erodedImg, structuringElement, 0);
			openedImg.updateAndDraw();
		}

		if (chosenFilter.equals(Filters.BORDA_OUTLINE.name())) {
			ImagePlus erodedImg = updateImage(img, BACKUP_IMG, structuringElement, 1);
			ImagePlus subtractedImg = subtractImages(erodedImg, BACKUP_IMG);
			subtractedImg.updateAndDraw();
		}

		return true;
	}
	
	private ImagePlus updateImage(ImagePlus img, ImagePlus backupImg, int structuringElement[][], int operation) {
		ImageProcessor imgProcessor = img.getProcessor();
		ImageProcessor backupImgProcessor = backupImg.getProcessor();

		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {
				int currentPixel = backupImgProcessor.getPixel(column, row);
				if (currentPixel == 255) {
					applyOperation(structuringElement, backupImgProcessor, imgProcessor, column, row, operation);
				}
			}
		}

		return img;
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

	private void applyOperation(int[][] structuringElement, ImageProcessor backupImgProcessor,
			ImageProcessor imgProcessor, int column, int row, int operation) {

		int coords[][] = {
				{ column - 1, row - 1 }, { column, row - 1 }, { column + 1, row - 1 },
				{ column - 1, row     }, { column, row     }, { column + 1, row     },
				{ column - 1, row + 1 }, { column, row + 1 }, { column + 1, row + 1 }
		};

		if (operation == 0) {
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
						if (currentPixel == 0) {
							return;							
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) { return; }
			}
			
			imgProcessor.putPixel(coords[4][0], coords[4][1], 255);

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