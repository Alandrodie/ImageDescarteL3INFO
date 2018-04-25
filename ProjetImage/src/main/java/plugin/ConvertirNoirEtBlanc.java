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
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Op.class, name = "convertirNoirBlanc")
public class ConvertirNoirEtBlanc extends AbstractOp {

	@Parameter
	ConvertService conv;
	@Parameter
	Dataset inputImage;
	
	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> output;
	@Override
	public void run() {
		ImagePlus colorImagePlus = conv.convert(inputImage, ImagePlus.class);
		ImageConverter converter = new ImageConverter(colorImagePlus);
		converter.convertToGray8();
		output = new ImgPlus<UnsignedByteType>(ImagePlusAdapter.wrapByte(colorImagePlus), inputImage.getName() + "_gray");
	}
}