import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class SplitChannels_ implements PlugIn {

	@Override
	public void run(String arg) {
		ImagePlus img = IJ.getImage();
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		String imgTitle = img.getShortTitle();

		splitImageToChannel(imgTitle + " - splitted - " + "R", 0, imgWidth, imgHeight, img);
		splitImageToChannel(imgTitle + " - splitted - " + "G", 1, imgWidth, imgHeight, img);
		splitImageToChannel(imgTitle + " - splitted - " + "B", 2, imgWidth, imgHeight, img);
		
		img.close();
	}

	private void splitImageToChannel(String title, int channel, int imgWidth, int imgHeight, ImagePlus originalImage) {
		ImagePlus splitedImg = IJ.createImage(title, "8-bit", imgWidth, imgHeight, 1);
		ImageProcessor imgProcessor = splitedImg.getProcessor();

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {
				int pixelValueArray[] = originalImage.getPixel(column, row);
				int pixelValue = pixelValueArray[channel];
				imgProcessor.putPixel(column, row, pixelValue);
			}
		}

		splitedImg.show();
	}

}
