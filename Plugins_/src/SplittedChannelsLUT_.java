import java.util.ArrayList;

import ij.plugin.PlugIn;
import ij.process.LUT;
import ij.ImagePlus;

public class SplittedChannelsLUT_ implements PlugIn {

	@Override
	public void run(String arg) {
		String text = "splitted";
		ArrayList<ImagePlus> images = Utils.getOpenedImagesByTextInTitle(text);

		for (ImagePlus img : images) {
			ImagePlus imgCopy = img.duplicate();
			byte[] reds = new byte[256];
			byte[] greens = new byte[256];
			byte[] blues = new byte[256];
			byte[] lutChannel = null;
			
			if (img.getTitle().contains("R")) {
				imgCopy.setTitle("LUT - R");
				lutChannel = reds;
			}
			
			if (img.getTitle().contains("G")) {
				imgCopy.setTitle("LUT - G");
				lutChannel = greens;
			}
			
			if (img.getTitle().contains("B")) {
				imgCopy.setTitle("LUT - B");
				lutChannel = blues;
			}
			
			for (int i = 1; i < 256; i++) {
				lutChannel[i] = (byte) i;
			}
			
			imgCopy.setLut(new LUT(reds, greens, blues));
			imgCopy.show();
		}
	}
}
