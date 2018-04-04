package plugin;

import java.util.HashSet;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class Neighbors {

	public static long[][] closeNeighbors(long x, long y) {
		long[][] out = new long[8][2];
		// order is top,corner,right,corner,bottom,corner,left,corner
		out[0] = new long[] { x, y - 1 };
		out[1] = new long[] { x + 1, y - 1 };
		out[2] = new long[] { x + 1, y };
		out[3] = new long[] { x + 1, y + 1 };
		out[4] = new long[] { x, y + 1 };
		out[5] = new long[] { x - 1, y + 1 };
		out[6] = new long[] { x - 1, y };
		out[7] = new long[] { x - 1, y - 1 };
		return out;
	}

	public static int[] getNumNeighbors(RandomAccess<UnsignedByteType> cursorIn, long[] dimensions, int nbCC) {
		return numNeighbors(areNeighbors(cursorIn, dimensions), nbCC);
	}

	private static HashSet<Couple<Integer>> areNeighbors(RandomAccess<UnsignedByteType> cursorIn, long[] dimensions) {
		HashSet<Couple<Integer>> out = new HashSet<>();
		RandomAccess<UnsignedByteType> cursorIn2 = cursorIn.copyRandomAccess();
		for (long y = 0; y < dimensions[1]; y++)
			for (long x = 0; x < dimensions[0]; x++) {
				cursorIn.setPosition(new long[] { x, y, 0 });
				for (long[] pos : Neighbors.closeNeighbors(x, y)) {
					long nx = pos[0];
					long ny = pos[1];
					if (ConnectedCoponents.isInBounds(dimensions, nx, ny)) {
						cursorIn2.setPosition(new long[] { nx, ny, 0 });
						if (cursorIn.get().getRealFloat() != cursorIn2.get().getRealFloat()) {
							out.add(new Couple<Integer>(new Integer((int) cursorIn.get().getRealFloat()),
									new Integer((int) cursorIn2.get().getRealFloat())));
						}
					}
				}
			}
		return out;
	}

	private static int[] numNeighbors(HashSet<Couple<Integer>> neighborsMap, int nbCC) {
		int[] out = new int[nbCC + 1];
		for (Couple<Integer> c : neighborsMap) {
			out[c.getA()]++;
		}
		return out;
	}

}
