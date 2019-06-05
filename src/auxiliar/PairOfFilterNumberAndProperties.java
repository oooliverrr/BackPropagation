package auxiliar;

import java.io.Serializable;

import filters.FilterProperties;

public class PairOfFilterNumberAndProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int numberOfFilters;
	private final FilterProperties filterProperties;

	public PairOfFilterNumberAndProperties(int numberOfFilters,
			FilterProperties filterProperties) {
		this.numberOfFilters = numberOfFilters;
		this.filterProperties = filterProperties;
	}
	
	@Override
	public String toString(){
		return "PairOfFilterNumberAndProperties = { "+numberOfFilters+" filters: ["+filterProperties.toString()+"] }";
	}

	public int getNumberOfFilters() {
		return numberOfFilters;
	}

	public FilterProperties getFilterProperties() {
		return filterProperties;
	}
}
