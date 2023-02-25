import java.util.ArrayList;

import ij.ImagePlus;
import ij.WindowManager;

public class Utils {
	public static ArrayList<ImagePlus> getOpenedImagesByTextInTitle(String text) {
		String imgTitles[] = WindowManager.getImageTitles();
		ArrayList<ImagePlus> splitedImgsArray = new ArrayList<ImagePlus>();

		for (String title : imgTitles) {
			if (title.contains(text)) {
				ImagePlus tempImg = WindowManager.getImage(title);
				splitedImgsArray.add(tempImg);
			}
		}
		return splitedImgsArray;
	}
}
