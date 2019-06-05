package jbullet;

import javax.vecmath.Vector3f;

import org.lwjgl.Sys;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class Joint {

	private final RigidBody rbA, rbB;
	private final Vector3f fA, fB, zeroVector;

	public Joint(RigidBody rbA, RigidBody rbB, Vector3f jointLocationA,
			Vector3f jointLocationB, Vector3f relativeJointAxisA,
			Vector3f relativeJointAxisB) {
		this.rbA = rbA;
		this.rbB = rbB;

		// Joints
		Vector jLA = new Vector((double) (jointLocationA.x),
				(double) (jointLocationA.y), (double) (jointLocationA.z));
		Vector jLB = new Vector((double) (jointLocationB.x),
				(double) (jointLocationB.y), (double) (jointLocationB.z));
		jLA.normalize();
		jLB.normalize();

		// Relative Axis
		Vector rJAA = new Vector((double) (relativeJointAxisA.x),
				(double) (relativeJointAxisA.y),
				(double) (relativeJointAxisA.z));
		Vector rJAB = new Vector((double) (relativeJointAxisB.x),
				(double) (relativeJointAxisB.y),
				(double) (relativeJointAxisB.z));
		rJAA.normalize();
		rJAB.normalize();

		// vA and vB define the force vectors of bodies A and B
		Vector vA = jLA.cross(rJAA);
		Vector vB = rJAB.cross(jLB);
		vA.normalize();
		vB.normalize();

		fA = new Vector3f((float) (vA.x()), (float) (vA.y()), (float) (vA.z()));
		fB = new Vector3f((float) (vB.x()), (float) (vB.y()), (float) (vB.z()));
		zeroVector = new Vector3f(0.0f, 0.0f, 0.0f);
	}

	public void applyForce(double force) {

		// Scale forces
		fA.normalize();
		fA.scale((float) (force));
		fB.normalize();
		fB.scale((float) (force));

		// Apply forces
		rbA.applyImpulse(fA, zeroVector);
		rbA.applyImpulse(fB, zeroVector);
	}

	private class Vector {

		private double x, y, z;

		public Vector(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public String toString() {
			return "[" + x + "," + y + "," + z + "]";
		}

		public double x() {
			return x;
		}

		public double y() {
			return y;
		}

		public double z() {
			return z;
		}

		public Vector cross(Vector v) {
			return new Vector(y * v.z() - z * v.y(), z * v.x() - x * v.z(), x
					* v.y() - y * v.x());
		}

		public Vector normalize() {
			return setlength(1.0);
		}

		public Vector setlength(double length) {
			double R = Math.sqrt(x * x + y * y + z * z);

			if (R > 0) {
				double lengthSQRT = 1.0;
				if (length >= 0) {
					lengthSQRT = Math.sqrt(length);
					x = x * lengthSQRT / R;
					y = y * lengthSQRT / R;
					z = z * lengthSQRT / R;
				} else {
					lengthSQRT = Math.sqrt(-length);
					x = -x * lengthSQRT / R;
					y = -y * lengthSQRT / R;
					z = -z * lengthSQRT / R;
				}
			}
			return this;
		}
	}
}
