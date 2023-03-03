import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class MergeChannels_ implements PlugIn {

	@Override
	public void run(String arg) {

		String text = "splitted";
		ArrayList<ImagePlus> splitedImgsArray = Utils.getOpenedImagesByTextInTitle(text);
		ImagePlus referenceImg = splitedImgsArray.get(0);
		ImagePlus mergedImg = createRGBImage(referenceImg, splitedImgsArray);

		mergedImg.show();
	}

	private ImagePlus createRGBImage(ImagePlus referenceImg, ArrayList<ImagePlus> splitedImgsArray) {
		int imgWidth = referenceImg.getWidth();
		int imgHeight = referenceImg.getHeight();
		ImagePlus mergedImg = IJ.createImage("merged", "RGB black", imgWidth, imgHeight, 1);
		ImageProcessor imgProcessor = mergedImg.getProcessor();
		int channel = 0;

		for (ImagePlus splitedImg : splitedImgsArray) {
			ImageProcessor splitedImgProcessor = splitedImg.getProcessor();

			for (int row = 0; row < imgHeight; row++) {
				for (int column = 0; column < imgWidth; column++) {
					int pixelValueSplitedImg = splitedImgProcessor.getPixel(column, row);
					int pixelValueMergedImg[] = mergedImg.getPixel(column, row);

					int RGBPixelValue[] = createRGBPixel(channel, pixelValueSplitedImg, pixelValueMergedImg);

					imgProcessor.putPixel(column, row, RGBPixelValue);
				}
			}
			splitedImg.close();
			channel++;
		}

		return mergedImg;
	}

	private int[] createRGBPixel(int channel, int pixelValueSplitedImg, int[] pixelValueMergedImg) {
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
		
		return new int[] {RChannel, GChannel, BChannel};
	}

}
