package plugin;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, menuPath = "Plugins>Projet > PlugindeBase (Not normalized)")
public class Plugindebase<T extends RealType<T>>  implements Command{

		@Parameter(persist = false)
		ImgPlus<T> img;

		@Parameter(type = ItemIO.OUTPUT)
		ImgPlus<UnsignedByteType> imageConv;

		@Override
		public void run() {
			
			long[] dimensions = new long[img.numDimensions()];
			img.dimensions(dimensions);
			// Creation of the resulting image with the same size as the input image.
			imageConv = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
			imageConv.setName(img.getName() + "_Mask");

			// Two random cursor to visit all pixels in the input and output images.
			RandomAccess<T> cursorIn = img.randomAccess();
			RandomAccess<UnsignedByteType> cursorOut = imageConv.randomAccess();
			appliquerSeuillage(127, cursorIn,cursorOut,dimensions);
		}
		private void appliquerSeuillage(int seuil, RandomAccess<T> cursorIn, RandomAccess<UnsignedByteType> cursorOut,
				long[] dimensions) {
			long[] position = new long[3];
			for (int i = 0; i < dimensions[0] ;i++) {
				position[0]=i;
				for (int j = 0; j < dimensions[1];j++) {
					position[1]=j;
					cursorIn.setPosition(position);
					cursorOut.setPosition(position);
					if(cursorIn.get().getRealDouble()<seuil)
						cursorOut.get().set(0);
					else
						cursorOut.get().set(255);
							
				}
			}
		}
	}


