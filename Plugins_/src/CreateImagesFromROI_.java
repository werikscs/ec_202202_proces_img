
import java.io.File;

import javax.swing.JFileChooser;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.*;

public class CreateImagesFromROI_ implements PlugIn {

	public void run(String arg) {
		try {
			String inputDir = this.chooseDir("Select Input Directory");
			String outputDir = this.chooseDir("Select Output Directory");

			MyFunction myFunction = new MyFunction() {

				@Override
				public <T> T call(T param) {
					ImagePlus img = createImagePlusFromFile((File) param);
					System.out.println(img.getTitle());
					return null;
				}
			};

			this.traverseFileDirectory(inputDir, myFunction);
		} catch (Exception e) {
		}
	}

	private void traverseFileDirectory(String dirPath, MyFunction myFunction) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				myFunction.call(file);
			}
		}
	}

	private ImagePlus createImagePlusFromFile(File file) {
		ImagePlus imgPlus = IJ.openImage(file.getAbsolutePath());
		return imgPlus;
	}

	private String chooseDir(String title) throws Exception {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int inputResult = chooser.showOpenDialog(null);
		if (inputResult != JFileChooser.APPROVE_OPTION) {
			throw new Exception("No directory selected");
		}
		String dir = chooser.getSelectedFile().getAbsolutePath();
		return dir;
	}

	public interface MyFunction {
		public <T> T call(T param);
	}
}
