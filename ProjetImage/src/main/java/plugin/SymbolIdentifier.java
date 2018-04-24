package plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import plugin.CCData.Color;

public class SymbolIdentifier {
	private List<List<CCData>> circles = new ArrayList<>();
	private List<List<CCData>> crosses = new ArrayList<>();

	public void classify(ArrayList<LinkedList<CCData>> CCList, int[] neighboring) {
		for (LinkedList<CCData> component : CCList) {
			if (component != null && !component.isEmpty()) {
				if (component.getFirst().getColor() == Color.black
						&& neighboring[component.getFirst().getNoCC()] == 1) {
					crosses.add(component);
				} else if (component.getFirst().getColor() == Color.black
						&& neighboring[component.getFirst().getNoCC()] == 2) {
					circles.add(component);
				}
			}
		}
	}

	public List<List<CCData>> getCircles() {
		return circles;
	}

	public List<List<CCData>> getCrosses() {
		return crosses;
	}
}
