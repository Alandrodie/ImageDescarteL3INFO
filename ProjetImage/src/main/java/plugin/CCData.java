package plugin;

public class CCData {
	private long x;
	private long y;

	public static enum Color {
		black , white
	};
	
	

	private Color color;

	private int noCC;
	private long nbPixels;
	

	public CCData(long x, long y, plugin.CCData.Color color, int noCC) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.noCC = noCC;
	}
	
	public CCData(int noCC, Color couleur) {
		this.noCC = noCC;
		
		color = couleur;
		nbPixels = 1;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public int getNoCC() {
		return noCC;
	}

	public void addPixel() {
		nbPixels++;
	}
	
	public long getNbPixel() {
		return nbPixels;
	}
}
