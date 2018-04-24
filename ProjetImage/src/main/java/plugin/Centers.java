package plugin;

import java.util.List;

public class Centers {
	public static long[] computeCenter(List<CCData> component) {
		long accX = 0;
		long accY = 0;
		for (CCData data : component) {
			accX += data.getX();
			accY += data.getY();
		}
		return new long[] { accX / component.size(), accY / component.size() };
	}
}
