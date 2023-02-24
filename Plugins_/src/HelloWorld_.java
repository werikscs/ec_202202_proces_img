import ij.IJ;
import ij.plugin.PlugIn;
 
public class HelloWorld_  implements PlugIn {
	public void run(String arg) {
		IJ.error("Hello world!");
	}
}