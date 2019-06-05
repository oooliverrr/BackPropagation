package jbullet;

public class FastFormat {

	private static final char[] DIGITS = "0123456789".toCharArray();

	public static void append(StringBuilder b, int i) {
		if (i < 0) {
			b.append('-');
			i = Math.abs(i);
		}

		int digit = 1000000000;
		boolean first = true;
		while (digit >= 1) {
			int v = (i / digit);
			if (v != 0 || !first) {
				b.append(DIGITS[v]);
				first = false;
			}
			i -= v * digit;
			digit /= 10;
		}

		if (first)
			b.append('0');
	}

	public static void append(StringBuilder b, float f) {
		append(b, f, 2);
	}

	public static void append(StringBuilder b, float f, int fracDigits) {
		int mult = 10 * fracDigits;
		int val = Math.round(f * mult);
		append(b, val / mult);
		b.append('.');
		append(b, Math.abs(val % mult));
	}

}