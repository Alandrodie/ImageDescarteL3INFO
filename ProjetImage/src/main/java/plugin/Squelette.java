package plugin;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Op.class, name = "squelette")
public class Squelette extends AbstractOp {

	@Parameter
	ConvertService conv;

	@Parameter
	Dataset inputImage;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> output;

	@Override
	public void run() {
		ImagePlus imp = convertInputToImagePlus();
		output = skeletonize(imp);
	}
	
	private ImagePlus convertInputToImagePlus() {
		ImagePlus imp = conv.convert(inputImage, ImagePlus.class);
		ImageConverter c = new ImageConverter(imp);
		c.convertToGray8();
		imp = imp.duplicate();
		return imp;
	}

	private ImgPlus<UnsignedByteType> skeletonize(ImagePlus imp) {
		ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
		BinaryProcessor binPr = new BinaryProcessor(pr);
		binPr.skeletonize();
		binPr.dilate();
		
		return new ImgPlus<UnsignedByteType>(ImagePlusAdapter.wrapByte(imp), "Skeleton");
	}

}