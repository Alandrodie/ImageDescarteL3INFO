package plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import plugin.CCData.Color;

public class ConnectedCoponents {

	public static ArrayList<LinkedList<CCData>> getAllCC(RandomAccess<UnsignedByteType> cursorIn,
			RandomAccess<UnsignedByteType> cursorOut, long[] dimensions) {
		ArrayList<LinkedList<CCData>> CCList = new ArrayList<>();
		CCList.add(null);
		int nbCC = 0;
		for (long y = 0; y < dimensions[1]; y++)
			for (long x = 0; x < dimensions[0]; x++) {
				cursorOut.setPosition(new long[] { x, y, 0 });
				if (cursorOut.get().getRealFloat() == 0) {
					nbCC++;
					CCList.add(new LinkedList<>());
					ConnectedCoponents.getCC(x, y, nbCC, cursorIn, cursorOut, dimensions, CCList);
				}
			}
		return CCList;
	}

	private static void getCC(long x, long y, int nbCC, RandomAccess<UnsignedByteType> cursorIn,
			RandomAccess<UnsignedByteType> cursorOut, long[] dimensions, ArrayList<LinkedList<CCData>> CCList) {
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
			for (long[] pos : Neighbors.closeNeighbors(x, y)) {
				if (isInBounds(dimensions, pos[0], pos[1])) {
					cursorIn2.setPosition(new long[] { pos[0], pos[1], 0 });
					if (cursorIn.get().getRealFloat() == cursorIn2.get().getRealFloat()) {
						cursorOut.get().set(nbCC);
						float colorf = cursorIn.get().getRealFloat();
						Color colorc = colorf == 0 ? Color.black : Color.white;
						CCData ccdata = new CCData(pos[0], pos[1], colorc, nbCC);
						CCList.get(nbCC).add(ccdata);
						stack.push(new StackData(pos[0], pos[1]));
					}
				}
			}
		}
		// endregion
		while (!stack.isEmpty()) {
			StackData data = stack.pop();
			if (isInBounds(dimensions, data.x, data.y)) {
				cursorIn.setPosition(new long[] { data.x, data.y, 0 });
				cursorOut.setPosition(new long[] { data.x, data.y, 0 });
				if (cursorOut.get().getRealFloat() == 0) {
					for (long[] pos : Neighbors.closeNeighbors(data.x, data.y)) {
						if (isInBounds(dimensions, pos[0], pos[1])) {
							cursorIn2.setPosition(new long[] { pos[0], pos[1], 0 });
							if (cursorIn.get().getRealFloat() == cursorIn2.get().getRealFloat()) {
								cursorOut.get().set(nbCC);
								float colorf = cursorIn.get().getRealFloat();
								Color colorc = colorf == 0 ? Color.black : Color.white;
								CCData ccdata = new CCData(pos[0], pos[1], colorc, nbCC);
								CCList.get(nbCC).add(ccdata);
								stack.push(new StackData(pos[0], pos[1]));
							}
						}
					}
				}
			}
		}
	}

	public static boolean isInBounds(long[] dimensions, long x, long y) {
		return 0 <= x && x < dimensions[0] && 0 <= y && y < dimensions[1];
	}
}
