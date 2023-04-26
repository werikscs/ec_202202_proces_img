
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JFileChooser;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.*;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

public class CreateImagesFromROI_ implements PlugIn {

	public void run(String arg) {
		try {
			String inputDir = this.chooseDir("Select Input Directory");
			String outputDir = this.chooseDir("Select Output Directory");

			MyFunction mainFunction = new MyFunction() {

				@Override
				public <T> T call(T param) {
					// Obter a imagem original
					ImagePlus img = createImagePlusFromFile((File) param);

					// Transformar a imagem para 8-Bits
					ImagePlus convertedImg = convertImgTo8bit(img);

					// Realizar o Threshold na imagem
					ImageProcessor convertedImgProcessor = convertedImg.getProcessor();
					convertedImgProcessor.threshold(240);

					// Executar o comando Analyze Particles para identificar automaticamente as ROIs
					// presentes na imagem, adicioná-los no RoiManager
					IJ.run(convertedImg, "Analyze Particles...", "size=500-Infinity circularity=0.00-0.5 show=Overlay add");
					RoiManager roiManager = RoiManager.getRoiManager();
					Roi[] rois = roiManager.getRoisAsArray();
					int index = 0;
					for (Roi roi : rois) {
						Rectangle roiBounds = roi.getBounds(); 
						img.setRoi(roiBounds);
						ImagePlus imgCroped = img.crop();
						
						// para salvar no Windows
						//IJ.save(imgCroped, outputDir+"\\"+img.getShortTitle()+"_"+index++);
						
						// para salvar no Linux
						IJ.save(imgCroped, outputDir+"/"+img.getShortTitle()+"_"+index++);
					}
					roiManager.close();
					return null;
				}
			};

			this.traverseFileDirectory(inputDir, mainFunction);
		} catch (Exception e) {
		}
	}

	private ImagePlus convertImgTo8bit(ImagePlus img) {
		int imgHeight = img.getHeight();
		int imgWidth = img.getWidth();

		ImagePlus convertedImg = IJ.createImage(img.getTitle(), "8-bit", imgWidth, imgHeight, 1);
		ImageProcessor newImageProcessor = convertedImg.getProcessor();

		for (int row = 0; row < imgHeight; row++) {
			for (int column = 0; column < imgWidth; column++) {
				int pixelValueArray[] = img.getPixel(column, row);
				int RChannel = (int) (pixelValueArray[0] * 0.333);
				int GChannel = (int) (pixelValueArray[1] * 0.333);
				int BChannel = (int) (pixelValueArray[2] * 0.333);
				int newPixelvalue = RChannel + GChannel + BChannel;
				newImageProcessor.putPixel(column, row, new int[] { newPixelvalue, newPixelvalue, newPixelvalue });
			}
		}
		return convertedImg;
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