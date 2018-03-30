package plugin;

public class Couple<T> {
	private T a;
	private T b;

	public Couple(T a, T b) {
		this.a = a;
		this.b = b;
	}

	public T getA() {
		return a;
	}

	public T getB() {
		return b;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Couple<?>)) {
			return false;
		}
		Couple<?> t = (Couple<?>) o;
		if ((this.getA().equals(t.getA()) && this.getB().equals(t.getB()))
				|| (this.getA().equals(t.getB()) && this.getB().equals(t.getA())))
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return a.hashCode() + b.hashCode();
	}

	@Override
	public String toString() {
		return "[" + a.toString() + ", " + b.toString() + "]";
	}
}
