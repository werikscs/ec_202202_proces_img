import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

public class Histogram_ implements PlugIn, DialogListener {
	ImagePlus BACKUP_IMG = null;

	@Override
	public void run(String arg) {
		GenericDialog gui = createGUI();
		checkGUIOutput(gui);
	}

	private GenericDialog createGUI() {
		GenericDialog gui = new GenericDialog("Técnicas de Histograma");
		gui.addDialogListener(this);

		gui.addMessage("Escolha a técnica:");
		String techniques[] = { Techniques.EXPANSAO.name(), Techniques.EQUALIZACAO.name() };
		gui.addRadioButtonGroup(null, techniques, 2, 1, Techniques.EXPANSAO.name());

		gui.showDialog();
		return gui;
	}

	private void checkGUIOutput(GenericDialog gui) {
		ImagePlus img = IJ.getImage();

		if (gui.wasCanceled()) {
			img.setImage(BACKUP_IMG);
			img.updateAndDraw();
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gui, AWTEvent event) {
		String chosenTechnique = gui.getNextRadioButton();
		return true;
	}

	enum Techniques {
		EXPANSAO("Expansão"), EQUALIZACAO("Equalização");

		Techniques(String technique) {
		}
	}

}
