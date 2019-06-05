package auxiliar;

public class Pair<A, B> {

	private final A a;
	private final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getFirstElement() {
		return a;
	}

	public B getSecondElement() {
		return b;
	}
}
