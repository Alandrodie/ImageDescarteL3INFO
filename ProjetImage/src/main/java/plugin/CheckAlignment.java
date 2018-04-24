package plugin;

public class CheckAlignment {
	public static double getAngle(long[] b, long[] a, long[] c) {
		long[] s1 = new long[] { a[0] - b[0], a[1] - b[1] };
		long[] s2 = new long[] { c[0] - a[0], c[1] - a[1] };
		double a1 = Math.atan2(s1[1], s1[0]);
		double a2 = Math.atan2(s2[1], s2[0]);
		return Math.toDegrees(Math.abs(a2 - a1));
	}

	public static double[] angulier(long[][] list) {
		double[] out = new double[list.length * list.length * list.length];
		int i = 0;
		for (int b = 0; b < list.length; b++)
			for (int a = 0; a < list.length; a++)
				for (int c = 0; c < list.length; c++) {
					if (!isEqual(list[a], list[b]) && !isEqual(list[b], list[c]) && !isEqual(list[a], list[c]))
						out[i++] = getAngle(list[b], list[a], list[c]);
					else
						out[i++] = Double.MAX_VALUE;
				}
		return out;
	}

	private static boolean isEqual(long[] a, long[] b) {
		return a[0] == b[0] && a[1] == b[1];
	}

}
