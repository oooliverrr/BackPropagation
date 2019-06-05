package filters;

import java.io.Serializable;

public class FilterProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int filterWidth, filterHeight, borderWidth, borderHeight,
			poolWidth, poolHeight;

	public FilterProperties(int filterWidth, int filterHeight, int borderWidth,
			int borderHeight, int poolWidth, int poolHeight) {
		this.filterWidth = filterWidth;
		this.filterHeight = filterHeight;
		this.borderWidth = borderWidth;
		this.borderHeight = borderHeight;
		this.poolWidth = poolWidth;
		this.poolHeight = poolHeight;
	}

	@Override
	public String toString() {
		return "{Filter [" + filterWidth + " x " + filterHeight + "], Border["
				+ borderWidth + " x " + borderHeight + "], Pool [" + poolWidth
				+ " x " + poolHeight + "]}";
	}

	public int getFilterWidth() {
		return filterWidth;
	}

	public int getFilterHeight() {
		return filterHeight;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public int getBorderHeight() {
		return borderHeight;
	}

	public int getPoolWidth() {
		return poolWidth;
	}

	public int getPoolHeight() {
		return poolHeight;
	}
}
