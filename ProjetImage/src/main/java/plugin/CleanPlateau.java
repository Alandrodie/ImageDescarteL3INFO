package plugin;

import java.util.ArrayList;
import java.util.Optional;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;


@Plugin(type = Op.class, name = "cleanPlateau")
	public class CleanPlateau<T extends RealType<T>> extends AbstractOp {
		@Parameter
		ConvertService cs;

		@Parameter(persist = false)
		ImgPlus<UnsignedByteType> imgCC;
		@Parameter(persist = false)
		ImgPlus<UnsignedByteType> img;

		@Parameter(type = ItemIO.OUTPUT)
		ImgPlus<UnsignedByteType> imageConv;

		@Override
		public void run() {
			
			long[] dimensions = new long[img.numDimensions()];
			img.dimensions(dimensions);
			// Creation of the resulting image with the same size as the input image.
			imageConv = ImgPlus.wrap(ArrayImgs.unsignedBytes(dimensions));
			imageConv.setName(img.getName() + "_Mask");
		
			ArrayList<ComposanteConnexe> composantesVisitees = new ArrayList<ComposanteConnexe>();
			
			
			long[] position = new long[imgCC.numDimensions()];
			imgCC.dimensions(dimensions);
			RandomAccess<UnsignedByteType> cursorCC =  imgCC.randomAccess();
			RandomAccess<UnsignedByteType> cursorImg =  img.randomAccess();
			for (long i =0 ; i < dimensions[0]; i++) {
				position[0] = i;
				for (long j = 0; j < dimensions[1]; j++) {
					position[1] = j;
					cursorCC.setPosition(position);
					cursorImg.setPosition(position);
					int currentCC =(int) cursorCC.get().getRealFloat();
					// vérifie si c'est une ligne droite et que l'on connais la composante connexe
					
					ComposanteConnexe CC = null;
					Optional<ComposanteConnexe> optionnalCC = composantesVisitees.stream().filter(x-> x.getNumeroCC() == currentCC).findFirst();
					if (optionnalCC.isPresent())
						CC = optionnalCC.get();
					
					
					
					if (CC == null) 
					{
						composantesVisitees.add(new ComposanteConnexe(currentCC, (int)cursorImg.get().getRealFloat()));
						
					} else  {
						CC.addPixel();
					}
				}
				
				
			}
			int numGrille = composantesVisitees.stream().filter(x -> x.getCouleur() == 0).max((x1, x2) -> (int) (x1.getNbPixel() - x2.getNbPixel())).get().getNumeroCC();
			
			for (long i = 0; i < dimensions[0]; i++) {
				position[0] = i;
				for (long j = 0; j < dimensions[1]; j++) {
					position[1] = j;
					cursorCC.setPosition(position);
					cursorImg.setPosition(position);
					int currentCC =(int) cursorCC.get().getRealFloat();
					// vérifie si c'est une ligne droite et que l'on connais la composante connexe
					
					ComposanteConnexe CC = null;
					
					Optional<ComposanteConnexe> optionnalCC = composantesVisitees.stream().filter(x-> x.getNumeroCC() == currentCC).findFirst();
					if (optionnalCC.isPresent())
						CC = optionnalCC.get();
					if (currentCC == numGrille ||(CC != null && CC.getNbPixel() < 30)) {
						
						if( cursorImg.get().getRealFloat() == 255)
						{
							cursorImg.get().set(0);
						}else
							cursorImg.get().set(255);
					}
				}
			}
			System.out.println("fin nettoyage");
			imageConv = img;
			
			
		}
		
		
		
			
			
			
			
			
		}
	
