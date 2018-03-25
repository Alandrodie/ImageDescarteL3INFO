package plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import org.python.antlr.PythonParser.return_stmt_return;
import org.renjin.gnur.Sort;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageConverter;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, menuPath = "Plugins>Projet > PlugindeBase (Not normalized)")
public class Plugindebase<T extends RealType<T>> implements Command {
	@Parameter
	ConvertService cs;

	@Parameter(persist = false)
	ImgPlus<T> img;
	@Parameter
	Dataset colorImage;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> imageConv;

	@Parameter
	int nocc;

	@Override
	public void run() {

		long[] dimensions = new long[img.numDimensions()];
		img.dimensions(dimensions);
		// Creation of the resulting image with the same size as the input image.
		imageConv = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		imageConv.setName(img.getName() + "_Mask");

		ImgPlus<UnsignedByteType> noirEtBlanc = convertirNoiretBlanc(colorImage);

		// Two random cursor to visit all pixels in the input and output images.
		RandomAccess<UnsignedByteType> cursorIn = noirEtBlanc.randomAccess();
		ImgPlus<UnsignedByteType> imageSeuillée = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		RandomAccess<UnsignedByteType> cursorSeuil = imageSeuillée.randomAccess();
		appliquerSeuillage(220, cursorIn, cursorSeuil, dimensions);
		RandomAccess<UnsignedByteType> cursorOut = imageConv.randomAccess();
		ImgPlus<UnsignedByteType> imageCC = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		RandomAccess<UnsignedByteType> cursorCC = imageCC.randomAccess();

		int nbCC = 0;
		for (long y = 0; y < dimensions[1]; y++)
			for (long x = 0; x < dimensions[0]; x++) {
				cursorCC.setPosition(new long[] { x, y, 0 });
				if (cursorCC.get().getRealFloat() == 0) {
					nbCC++;
					getCC(x, y, nbCC, cursorSeuil, cursorCC, dimensions);
				}
			}
		System.out.println("nbcc=" + nbCC);

		int[][] count = count(cursorCC, nbCC, dimensions);
		Arrays.sort(count, new Comparator<int[]>() {

			@Override
			public int compare(int[] o1, int[] o2) {
				if (o1[1] < o2[1])
					return 1;
				if (o1[1] > o2[1])
					return -1;
				if (o1[1] == o2[1])
					return 0;
				return 0;
			}
		});
		// affichage
		for (int y = 0; y < dimensions[1]; y++)
			for (int x = 0; x < dimensions[0]; x++) {
				long[] position = new long[3];
				position[0] = x;
				position[1] = y;
				cursorCC.setPosition(position);
				cursorOut.setPosition(position);
				if (cursorCC.get().getRealFloat() == count[nocc][0])
					cursorOut.get().set(0);
				else {
					cursorOut.get().set(255);
				}
			}
	}

	private ImgPlus<UnsignedByteType> convertirNoiretBlanc(Dataset dataset) {
		ImagePlus colorImagePlus = cs.convert(dataset, ImagePlus.class);
		ImageConverter converter = new ImageConverter(colorImagePlus);
		converter.convertToGray8();
		return new ImgPlus<UnsignedByteType>(ImagePlusAdapter.wrapByte(colorImagePlus), dataset.getName() + "_gray");
	}

	private void appliquerSeuillage(int seuil, RandomAccess<UnsignedByteType> cursorIn,RandomAccess<UnsignedByteType> cursorOut, long[] dimensions) {
		long[] position = new long[3];
		for (int i = 0; i < dimensions[0]; i++) {
			position[0] = i;
			for (int j = 0; j < dimensions[1]; j++) {
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

	private boolean isInBounds(long[] dimensions, long x, long y) {
		return 0 <= x && x < dimensions[0] && 0 <= y && y < dimensions[1];
	}

	private long[][] closeNeighbors(long x, long y) {
		long[][] out = new long[4][2];
		// order is top,right,bottom,left
		out[0] = new long[] { x, y - 1 };
		out[1] = new long[] { x + 1, y };
		out[2] = new long[] { x, y + 1 };
		out[3] = new long[] { x - 1, y };
		return out;
	}

	private void getCC(long x, long y, int nbCC, RandomAccess<UnsignedByteType> cursorIn,
			RandomAccess<UnsignedByteType> cursorOut, long[] dimensions) {
		class StackData {
			long x;
			long y;

			public StackData(long x, long y) {
				this.x = x;
				this.y = y;
			}

		}
		Stack<StackData> stack = new Stack<>();
		RandomAccess<UnsignedByteType> cursorIn2 = cursorIn.copyRandomAccess();
		// region first loop
		cursorIn.setPosition(new long[] { x, y, 0 });
		cursorOut.setPosition(new long[] { x, y, 0 });
		if (cursorOut.get().getRealFloat() == 0) {
			for (long[] pos : closeNeighbors(x, y)) {
				if (isInBounds(dimensions, pos[0], pos[1])) {
					cursorIn2.setPosition(new long[] { pos[0], pos[1], 0 });
					if (cursorIn.get().getRealFloat() == cursorIn2.get().getRealFloat()) {
						cursorOut.get().set(nbCC);
						stack.push(new StackData(pos[0], pos[1]));
					}
				}
			}
		}
		// System.out.println(stack.size());
		// endregion
		while (!stack.isEmpty()) {
			StackData data = stack.pop();
			if (isInBounds(dimensions, data.x, data.y)) {
				cursorIn.setPosition(new long[] { data.x, data.y, 0 });
				cursorOut.setPosition(new long[] { data.x, data.y, 0 });
				if (cursorOut.get().getRealFloat() == 0) {
					for (long[] pos : closeNeighbors(data.x, data.y)) {
						if (isInBounds(dimensions, pos[0], pos[1])) {
							cursorIn2.setPosition(new long[] { pos[0], pos[1], 0 });
							if (cursorIn.get().getRealFloat() == cursorIn2.get().getRealFloat()) {
								cursorOut.get().set(nbCC);
								stack.push(new StackData(pos[0], pos[1]));
							}
						}
					}
				}
			}
		}
	}
	private int[][] count(RandomAccess<UnsignedByteType> cursorIn, int lenght,long[] dimensions) {
		int[][] out=new int[lenght+1][2];
		for(int i=0;i<out.length;i++)
			out[i][1]=i;
		
		for(long y=0;y<dimensions[1];y++)
			for(long x=0;x<dimensions[0];x++) {
				cursorIn.setPosition(new long[] {x,y,0});
				out[(int)(cursorIn.get().getRealFloat())][0]++;
			}
		return out;
	}
}
