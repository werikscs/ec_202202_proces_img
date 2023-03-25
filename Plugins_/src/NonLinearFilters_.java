//Desenvolver um plugin para aplicar o filtro não linear de Sobel na vertical e na horizontal em uma imagem corrente.  Apresentar os resultados em duas novas imagens.
//
//Em seguida deverá ser criada e apresentada uma terceira imagem com a junção dos dois resultados. (Fórmula presente no último slide da aula)

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class NonLinearFilters_ implements PlugIn {
	ImagePlus BACKUP_IMG = null;

@Override
public void run(String arg) {
	ImagePlus img = IJ.getImage();
	BACKUP_IMG = img;
	
	ImagePlus sobelVertImg = img.duplicate();
	ImagePlus sobelHorizImg = img.duplicate();
	ImagePlus mergedImg = img.duplicate();
	sobelVertImg.setTitle("SobelVert");
	sobelHorizImg.setTitle("SobelHoriz");
	mergedImg.setTitle("MergedSobels");
	
	double maskSobelVert[][] = {
			{ -1, 0, 1 },
			{ -2, 0, 2 },
			{ -1, 0, 1 }
	};
	
	double maskSobelHoriz[][] = {
			{ 1, 2, 1 },
			{ 0, 0, 0 },
			{ -1, -2, -1 }
	};
	
	updateImage(maskSobelVert, sobelVertImg);
	updateImage(maskSobelHoriz, sobelHorizImg);
	mergeSobels(sobelVertImg, sobelHorizImg, mergedImg);
	
	sobelVertImg.show();
	sobelHorizImg.show();
	mergedImg.show();
}

private void mergeSobels(ImagePlus sobelVertImg, ImagePlus sobelHorizImg, ImagePlus mergedImg) {
	int imgWidth = mergedImg.getWidth();
	int imgHeight = mergedImg.getHeight();
	ImageProcessor imgProcessor = mergedImg.getProcessor();
	
	for (int row = 0; row < imgHeight; row++) {
		for (int column = 0; column < imgWidth; column++) {
			int sobelVertPixel = sobelVertImg.getPixel(column, row)[0];
			int sobelHorizPixel = sobelHorizImg.getPixel(column, row)[0];
			
			int newPixel = (int) Math.sqrt(Math.pow(sobelVertPixel, 2) + Math.pow(sobelHorizPixel, 2));
			imgProcessor.putPixel(column, row, newPixel);
		}
	}	
}

private void updateImage(double kernel[][], ImagePlus img) {
	int imgWidth = img.getWidth();
	int imgHeight = img.getHeight();
	ImageProcessor imgProcessor = img.getProcessor();
	
	for (int row = 0; row < imgHeight; row++) {
		for (int column = 0; column < imgWidth; column++) {
			
			int newPixel = applyKernelToPixel(kernel, BACKUP_IMG, column, row);				
			imgProcessor.putPixel(column, row, newPixel);
		}
	}
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
	SOBEL_VERT("Passa-baixa"), SOBEL_HORIZ("Passa-alta");

	Filters(String filter) {
	}
}
}

