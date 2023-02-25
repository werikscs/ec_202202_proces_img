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

		// pegar as imagens que tenham no titulo a sequencia "splitted"
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
		int channel = 0;
		for (ImagePlus splitedImg : splitedImgsArray) {
			ImageProcessor splitedImgProcessor = splitedImg.getProcessor();
			
			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValueSplitedImg = splitedImgProcessor.getPixel(column, row, null)[0];
					int pixelValueMergedImg[] = mergedImg.getPixel(column, row);
					int RChannel = 0, GChannel = 0, BChannel = 0;

					if (channel == 0) {
						RChannel = pixelValueSplitedImg;
						GChannel = pixelValueMergedImg[1];
						BChannel = pixelValueMergedImg[2];
					}

					if (channel == 1) {
						RChannel = pixelValueMergedImg[0];
						GChannel = pixelValueSplitedImg;
						BChannel = pixelValueMergedImg[2];
					}

					if (channel == 2) {
						RChannel = pixelValueMergedImg[0];
						GChannel = pixelValueMergedImg[1];
						BChannel = pixelValueSplitedImg;
					}
					
					imgProcessor.putPixel(column, row, new int[]{ RChannel, GChannel, BChannel });
				}
			}
			splitedImg.close();
			channel++;
		}

		mergedImg.show();
	}

}
