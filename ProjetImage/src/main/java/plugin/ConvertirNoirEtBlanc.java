package plugin;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageConverter;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 8>Seuillage")
public class ConvertirNoirEtBlanc implements Command {

	@Parameter
	ConvertService conv;
	@Parameter(persist = false)
	ImgPlus<UnsignedByteType> img;
	@Parameter
	Dataset inputImage;
	@Parameter(required = false)
	int seuil = 127;
	
	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> output;
	@Override
	public void run() {
		ImagePlus colorImagePlus = conv.convert(img, ImagePlus.class);
		ImageConverter converter = new ImageConverter(colorImagePlus);
		converter.convertToGray8();
		output = new ImgPlus<UnsignedByteType>(ImagePlusAdapter.wrapByte(colorImagePlus), inputImage.getName() + "_gray");
	}
}