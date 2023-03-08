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
			byte[] RChannel = new byte[256];
			byte[] GChannel = new byte[256];
			byte[] BChannel = new byte[256];
			byte[] lutChannel = null;
			
			if (img.getTitle().contains("R")) {
				imgCopy.setTitle("LUT - R");
				lutChannel = RChannel;
			}
			
			if (img.getTitle().contains("G")) {
				imgCopy.setTitle("LUT - G");
				lutChannel = GChannel;
			}
			
			if (img.getTitle().contains("B")) {
				imgCopy.setTitle("LUT - B");
				lutChannel = BChannel;
			}
			
			for (int i = 1; i < 256; i++) {
				lutChannel[i] = (byte) i;
			}
			
			imgCopy.setLut(new LUT(RChannel, GChannel, BChannel));
			imgCopy.show();
		}
	}
}
