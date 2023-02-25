import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class MergeChannels_ implements PlugIn {

	@Override
	public void run(String arg) {
		// pegar os titulos das imagens abertas
		String imgTitles[] = WindowManager.getImageTitles();

		// pegar as imagens que tenham no titulo a identificação "splitted"
		String SEQUENCE = "splitted";
		ArrayList<ImagePlus> splitedImgsArray = new ArrayList<ImagePlus>();
		ImagePlus referenceImg = null;

		for (String title : imgTitles) {
			if (title.contains(SEQUENCE)) {
				ImagePlus tempImg = WindowManager.getImage(title);
				splitedImgsArray.add(tempImg);
				referenceImg = tempImg;
			}
		}

		// criar a imagem que será um merge das splitted
		int imgWidth = referenceImg.getWidth();
		int imgHeight = referenceImg.getHeight();
		ImagePlus mergedImg = IJ.createImage("merged", "RGB black", imgWidth, imgHeight, 1);
		ImageProcessor imgProcessor = mergedImg.getProcessor();

		// inserir os pixels das imagens na imagens final
		int count = 0;
		for (ImagePlus splitedImg : splitedImgsArray) {
			ImageProcessor splitedImgProcessor = splitedImg.getProcessor();
			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValueSplitedImg[] = splitedImgProcessor.getPixel(column, row, null);
					int pixelValueMergedImg[] = mergedImg.getPixel(column, row);
					
					if(count == 0) {
						int a[] = {pixelValueSplitedImg[0],pixelValueMergedImg[1],pixelValueMergedImg[2]};
						imgProcessor.putPixel(column, row, a);
					}
					
					if(count == 1) {
						int a[] = {pixelValueMergedImg[0],pixelValueSplitedImg[0],pixelValueMergedImg[2]};
						imgProcessor.putPixel(column, row, a);
					}
					
					if(count == 2) {
						int a[] = {pixelValueMergedImg[0],pixelValueMergedImg[1],pixelValueSplitedImg[0]};
						imgProcessor.putPixel(column, row, a);
					}
					
					
				}
			}
			count++;
		}

		mergedImg.show();
	}

}
