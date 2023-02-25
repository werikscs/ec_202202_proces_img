import java.util.ArrayList;

import ij.plugin.PlugIn;
import ij.process.LUT;
import ij.ImagePlus;

public class SplittedChannelsLUT_ implements PlugIn {

	@Override
	public void run(String arg) {
		String text = "splitted";
		ArrayList<ImagePlus> images = Utils.getOpenedImagesByTextInTitle(text);

		byte[] reds = new byte[256];
		byte[] greens = new byte[256];
		byte[] blues = new byte[256];

		for (ImagePlus img : images) {
			if (img.getTitle().contains("R")) {
				for (int i = 0; i < 256; i++) {
					reds[i] = (byte) i;
					greens[i] = (byte) 0;
					blues[i] = (byte) 0;
				}

				LUT lut = new LUT(reds, greens, blues);
				ImagePlus imgCopy = img.duplicate();
				imgCopy.setTitle("LUT - R");
				imgCopy.setLut(lut);
				imgCopy.show();
			}
			if (img.getTitle().contains("G")) {
				for (int i = 0; i < 256; i++) {
					reds[i] = (byte) 0;
					greens[i] = (byte) i;
					blues[i] = (byte) 0;
				}

				LUT lut = new LUT(reds, greens, blues);
				ImagePlus imgCopy = img.duplicate();
				imgCopy.setTitle("LUT - G");
				imgCopy.setLut(lut);
				imgCopy.show();
			}
			if (img.getTitle().contains("B")) {
				for (int i = 0; i < 256; i++) {
					reds[i] = (byte) 0;
					greens[i] = (byte) 0;
					blues[i] = (byte) i;
				}

				LUT lut = new LUT(reds, greens, blues);
				ImagePlus imgCopy = img.duplicate();
				imgCopy.setTitle("LUT - B");
				imgCopy.setLut(lut);
				imgCopy.show();
			}
		}

	}

}
