package auxiliar;

import java.io.Serializable;

public class TripletOfWeightValueIDOLD implements Serializable {

	private static final long serialVersionUID = 1L;

	private final double w, z, wz;
	private final int ID;

	public TripletOfWeightValueIDOLD(double w, double z, int id) {
		this.w = w;
		this.z = z;
		this.wz = w * z;
		this.ID = id;
	}

	@Override
	public String toString() {
		return "TripletOfWeightValueID = {[w = " + w + "], [z = " + z
				+ "], [ID = " + ID + "]}";
	}

	public TripletOfWeightValueIDOLD(double w, double z) {
		this.w = w;
		this.z = z;
		this.wz = w * z;
		this.ID = -1;
	}

	public int getID() {
		return ID;
	}

	public double getW() {
		return w;
	}

	public double getZ() {
		return z;
	}

	public double getWZ() {
		return wz;
	}
}
