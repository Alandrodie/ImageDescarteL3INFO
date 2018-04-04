package plugin;

public class CCData {
	private long x;
	private long y;

	public static enum color {
		black, white
	};

	private color color;

	private int noCC;

	public CCData(long x, long y, plugin.CCData.color color, int noCC) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.noCC = noCC;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public color getColor() {
		return color;
	}

	public int getNoCC() {
		return noCC;
	}

}
