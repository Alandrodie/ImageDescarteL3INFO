package plugin;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;

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
			
		}

	}


