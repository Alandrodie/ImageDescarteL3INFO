package plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageConverter;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccess;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, menuPath = "Plugins>Projet > PlugindeBase (Not normalized)")
public class Plugindebase<T extends RealType<T>> implements Command {
	@Parameter
	ConvertService cs;

	@Parameter
	private OpService ops;

	@Parameter(persist = false)
	ImgPlus img;
	@Parameter
	Dataset colorImage;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> imageConv;

	@Parameter
	double maxAngle;

	@Override
	public void run() {

		// ImgPlus<UnsignedByteType> img = (ImgPlus<UnsignedByteType>)
		// ops.run("squelette", colorImage);

		long[] dimensions = new long[img.numDimensions()];
		img.dimensions(dimensions);
		// Creation of the resulting image with the same size as the input image.
		imageConv = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		imageConv.setName(img.getName() + "_Mask");

		// processing chain
		// ImgPlus<UnsignedByteType> noirEtBlanc = convertirNoiretBlanc(colorImage);
		RandomAccess<UnsignedByteType> cursorIn = img.randomAccess();

		ImgPlus<UnsignedByteType> imageSeuillée = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));

		imageSeuillée = (ImgPlus<UnsignedByteType>) ops.run("seuillage", img, 220);
		RandomAccess<UnsignedByteType> cursorSeuil = imageSeuillée.randomAccess();
		// appliquerSeuillage(220, cursorIn, cursorSeuil, dimensions);

		RandomAccess<UnsignedByteType> cursorOut = imageConv.randomAccess();
		ImgPlus<UnsignedByteType> imageCC = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		RandomAccess<UnsignedByteType> cursorCC = imageCC.randomAccess();

		ArrayList<LinkedList<CCData>> CCList = ConnectedCoponents.getAllCC(cursorSeuil, cursorCC, dimensions);
		System.out.println("nbCC = " + CCList.size());

		ImgPlus<UnsignedByteType> imageClean = (ImgPlus<UnsignedByteType>) ops.run("cleanPlateau", imageCC,
				imageSeuillée);
		// imageConv = (ImgPlus<UnsignedByteType>) ops.run("cleanPlateau", imageCC,
		// imageSeuillée);

		RandomAccess<UnsignedByteType> cursorClean = imageClean.randomAccess();

		ImgPlus<UnsignedByteType> imageCC2 = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
		RandomAccess<UnsignedByteType> cursorCC2 = imageCC2.randomAccess();

		ArrayList<LinkedList<CCData>> CCList2 = ConnectedCoponents.getAllCC(cursorClean, cursorCC2, dimensions);
		System.out.println("nbCC = " + CCList2.size());
		int[] neighboring = Neighbors.getNumNeighbors(cursorCC2, dimensions, CCList2.size());

		SymbolIdentifier si = new SymbolIdentifier();
		si.classify(CCList2, neighboring);

		// affichage
		for (LinkedList<CCData> l : CCList2) {
			if (l != null && !l.isEmpty()) {
				if (neighboring[l.getFirst().getNoCC()] == 1 && l.getFirst().getColor() == CCData.Color.black) {
					for (CCData c : l) {
						cursorOut.setPosition(new long[] { c.getX(), c.getY(), 0 });
						cursorOut.get().set(255);
					}
				}
			}
		}
		List<List<CCData>> circles = si.getCircles();
		System.out.println("nb cercles =" + si.getCircles().size());
		long[][] centersCircles = new long[circles.size()][2];
		int i = 0;
		for (List<CCData> component : circles) {
			centersCircles[i++] = Centers.computeCenter(component);
		}
		double[] angulierCircles = CheckAlignment.angulier(centersCircles);
		Arrays.sort(angulierCircles);
		List<List<CCData>> crosses = si.getCrosses();
		long[][] centerCrosses = new long[crosses.size()][2];
		i = 0;
		for (List<CCData> component : crosses) {
			centerCrosses[i++] = Centers.computeCenter(component);
		}
		double[] angulierCrosses = CheckAlignment.angulier(centerCrosses);
		Arrays.sort(angulierCrosses);
		System.out.println("nb crosses = " + si.getCrosses().size());
		System.out.println("min angle circles = " + angulierCircles[0]);
		System.out.println("min angle crosses = " + angulierCrosses[0]);
		if (angulierCircles[0] < maxAngle) {
			System.out.println("Les cercles ont gagné !");
		}
		if (angulierCrosses[0] < maxAngle) {
			System.out.println("Les croix ont gangé !");
		}
	}

	private ImgPlus<UnsignedByteType> convertirNoiretBlanc(Dataset dataset) {
		ImagePlus colorImagePlus = cs.convert(dataset, ImagePlus.class);
		ImageConverter converter = new ImageConverter(colorImagePlus);
		converter.convertToGray8();
		return new ImgPlus<>(ImagePlusAdapter.wrapByte(colorImagePlus), dataset.getName() + "_gray");
	}

	private void appliquerSeuillage(int seuil, RandomAccess<UnsignedByteType> cursorIn,
			RandomAccess<UnsignedByteType> cursorOut, long[] dimensions) {
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