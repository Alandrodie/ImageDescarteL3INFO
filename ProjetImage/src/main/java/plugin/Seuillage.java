package plugin;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 8>Seuillage")
public class Seuillage implements Command {

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
		long[] dimensions = new long[img.numDimensions()];
		img.dimensions(dimensions);
		output = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		output.setName(img.getName() + "_Mask");
		RandomAccess<UnsignedByteType> cursorIn = img.randomAccess();
		RandomAccess<UnsignedByteType> cursorOut = output.randomAccess();
		long[] position = new long[3];
		for (long i = 0; i < dimensions[0]; i++) {
			position[0] = i;
			for (long j = 0; j < dimensions[1]; j++) {
				position[1] = j;
				cursorIn.setPosition(position);
				cursorOut.setPosition(position);
				if (cursorIn.get().getRealDouble() < seuil)
					cursorOut.get().set(0);
				else
					cursorOut.get().set(255);

			}
		}
	}
}