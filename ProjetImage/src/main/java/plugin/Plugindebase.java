package plugin;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

@Plugin(type = Command.class, menuPath = "Plugins>Projet > PlugindeBase (Not normalized)")
public class Plugindebase implements Command{
		@Parameter
		OpService ops;
		@Parameter
		DatasetService dss;


		@Parameter
		Dataset img;

		@Parameter(type = ItemIO.OUTPUT)
		ImgPlus<RealType<?>> outImgP;

		@Override
		public void run() {
			// Take img dataset as random accessible interval
			@SuppressWarnings("rawtypes")
			RandomAccessibleInterval image = img;
			
			RandomAccessibleInterval<RealType<?>> result = img;
			long[] dimension = new long[2];
			long[] position = new long[2];
			image.dimensions(dimension);
			for (int i = 0; i < dimension[0] ;i++) {
				position[0]=i;
				for (int j = 0; j < dimension[1];j++) {
					position[1]=j;

				}
			}
			Img<RealType<?>> outImg = dss.create(result);


			outImgP = new ImgPlus<RealType<?>>(outImg, "img");
				
		}

	}


