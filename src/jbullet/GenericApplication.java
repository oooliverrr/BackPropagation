package jbullet;

import static jbullet.IGL.GL_AMBIENT;
import static jbullet.IGL.GL_DEPTH_TEST;
import static jbullet.IGL.GL_DIFFUSE;
import static jbullet.IGL.GL_LESS;
import static jbullet.IGL.GL_LIGHT0;
import static jbullet.IGL.GL_LIGHT1;
import static jbullet.IGL.GL_LIGHTING;
import static jbullet.IGL.GL_POSITION;
import static jbullet.IGL.GL_SMOOTH;
import static jbullet.IGL.GL_SPECULAR;

import java.util.ArrayList;
import java.util.Collections;

import javax.vecmath.Color3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShapeZ;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConeShapeZ;
import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

public abstract class GenericApplication {

	// Mouse
	public static RigidBody pickedBody = null;
	private static float mousePickClamping = 3f;
	protected TypedConstraint pickConstraint = null;

	// Graphics
	protected IGL gl;

	// Shoot box, sphere
	private double boxX = 1.0;
	private double boxY = 1.0;
	private double boxZ = 1.0;
	private double boxR = Math.pow(3 * boxX * boxY * boxZ / (4 * Math.PI),
			1.0 / 3.0);
	float shootBoxMass = (float) (10.0);
	float shootBoxFriction = (float) (1.0);
	float shootBoxRestitution = (float) (0.1);
	protected float shootBoxInitialSpeed = 40f;
	protected float shootBoxForwardDistance = 10000f;

	// Clock
	protected Clock clock = new Clock();
	protected boolean isIdle = false;

	// DynamicsWorld, physics
	protected DynamicsWorld dynamicsWorld = null;
	protected Vector3f zeroInertia = new Vector3f(0, 0, 0);
	protected float impulseStrength = (float) (100.0);

	// Debug
	protected int debugMode = 0;

	// Camera
	protected final Vector3f cameraPosition = new Vector3f(0f, 0f, 0f);
	protected final Vector3f cameraTargetPosition = new Vector3f(0f, 0f, 0f);
	protected final Vector3f cameraNormal = new Vector3f(0f, 0f, 1f);

	// Screen
	protected int glutScreenWidth = 0;
	protected int glutScreenHeight = 0;
	private double maxAngleX, maxAngleY;

	// Render
	protected ArrayList<Vector3f> colors;
	private ArrayList<Vector3f> brightColors;

	public GenericApplication(IGL gl) {
		this.gl = gl;

		brightColors = new ArrayList<Vector3f>();
		brightColors.add(getColor(255,0,0)); // Red
		brightColors.add(getColor(0,255,0)); // Green
		brightColors.add(getColor(0,0,255)); // Blue
		brightColors.add(getColor(255,255,0)); // Yellow
		brightColors.add(getColor(255,0,255)); // Pink
		brightColors.add(getColor(0,255,255)); // Cyan
		brightColors.add(getColor(255,150,0)); // Orange
		brightColors.add(getColor(255,0,150)); // Salmon
		brightColors.add(getColor(150,255,0)); // Lemon green
		brightColors.add(getColor(0,255,150)); // Spring green
		brightColors.add(getColor(150,0,255)); // Purple
		brightColors.add(getColor(0,150,255)); // Light blue
		
		BulletStats.setProfileEnabled(true);
	}

	protected Vector3f getColor(double R, double G, double B) {
		return new Vector3f((float) (R / 255.0), (float) (G / 255.0),
				(float) (B / 255.0));
	}

	public abstract void initPhysics() throws Exception;

	public void myinit() {
		float[] light_ambient = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
		float[] light_diffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] light_specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		/* light_position is NOT default value */
		float[] light_position0 = new float[] { 1.0f, 10.0f, 1.0f, 0.0f };
		float[] light_position1 = new float[] { -1.0f, -10.0f, -1.0f, 0.0f };

		gl.glLight(GL_LIGHT0, GL_AMBIENT, light_ambient);
		gl.glLight(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
		gl.glLight(GL_LIGHT0, GL_SPECULAR, light_specular);
		gl.glLight(GL_LIGHT0, GL_POSITION, light_position0);

		gl.glLight(GL_LIGHT1, GL_AMBIENT, light_ambient);
		gl.glLight(GL_LIGHT1, GL_DIFFUSE, light_diffuse);
		gl.glLight(GL_LIGHT1, GL_SPECULAR, light_specular);
		gl.glLight(GL_LIGHT1, GL_POSITION, light_position1);

		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);
		gl.glEnable(GL_LIGHT1);

		gl.glShadeModel(GL_SMOOTH);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LESS);

		gl.glClearColor(0.7f, 0.7f, 0.7f, 0f);
	}

	public void toggleIdle() {
		isIdle = !isIdle;
	}

	public void updateCamera() {
		// // This method should be overriden
	}

	public void reshape(int w, int h) {
		glutScreenWidth = w;
		glutScreenHeight = h;

		maxAngleY = 45.0 * Math.PI / 180;
		maxAngleX = maxAngleY * glutScreenWidth / glutScreenHeight;

		gl.glViewport(0, 0, w, h);
		updateCamera();
	}

	public void keyReleased(int key, int x, int y, int modifiers) {
		// Key released!
		// Method to be overridden
	}

	public void keyPressed(int key, int x, int y, int modifiers) {
		// Key pressed!
		// Method to be overridden
	}

	public void moveAndDisplay() {
		if (isIdle) {
			clientDisplayOnly();
		} else {
			clientMoveAndDisplay();
		}
	}

	public void shootBox(Vector3f direction) {
		if (dynamicsWorld != null) {

			Vector3f linearVelocity = new Vector3f(direction.x, direction.y,
					direction.z);
			linearVelocity.normalize();
			linearVelocity.scale(shootBoxInitialSpeed);
			Vector3f angularVelocity = new Vector3f(0f, 0f, 0f);

			RigidBody shootBox = createBox(shootBoxMass, boxX, boxY, boxZ,
					cameraPosition.x, cameraPosition.y, cameraPosition.z,
					linearVelocity, angularVelocity, shootBoxFriction,
					shootBoxRestitution, 0.0f, 0.0f, 0.0f);

			dynamicsWorld.addRigidBody(shootBox);
			Collections.shuffle(brightColors);
			colors.add(brightColors.get(0));
		}
	}

	public void shootSphere(Vector3f direction) {
		if (dynamicsWorld != null) {

			Vector3f linearVelocity = new Vector3f(direction.x
					- cameraPosition.x, direction.y - cameraPosition.y,
					direction.z - cameraPosition.z);
			linearVelocity.normalize();
			linearVelocity.scale(shootBoxInitialSpeed);
			Vector3f angularVelocity = new Vector3f(0f, 0f, 0f);

			RigidBody shootSphere = createSphere(shootBoxMass, boxR,
					cameraPosition.x, cameraPosition.y, cameraPosition.z,
					linearVelocity, angularVelocity, shootBoxFriction,
					shootBoxRestitution);

			dynamicsWorld.addRigidBody(shootSphere);
			Collections.shuffle(brightColors);
			colors.add(brightColors.get(0));
		}
	}

	public Vector3f getRayVector(int x, int y) {

		Vector3f rayForward = new Vector3f();
		rayForward.sub(cameraTargetPosition, cameraPosition);
		rayForward.normalize();

		Vector3f rayUp = new Vector3f(cameraNormal);
		rayUp.normalize();

		Vector3f rayRight = new Vector3f();
		rayRight.cross(rayForward, rayUp);
		rayRight.normalize();

		rayUp.cross(rayRight, rayForward);
		rayUp.normalize();

		// Normalize x and y
		double trueX = 2.0 * (x - 0.5 * glutScreenWidth) / glutScreenWidth;
		double trueY = 2.0 * (y - 0.5 * glutScreenHeight) / glutScreenHeight;
		double trueAngleX = trueX * maxAngleX;
		double trueAngleY = trueY * maxAngleY;

		// Rotate
		rayForward = rotate(rayForward, rayUp, -trueAngleX);
		rayForward.normalize();
		rayForward = rotate(rayForward, rayRight, trueAngleY);
		rayForward.normalize();

		// Compute rayTo
		Vector3f rayVector = new Vector3f();
		rayVector.add(rayForward);
		rayVector.normalize();
		rayVector.scale(shootBoxForwardDistance);

		return rayVector;
	}

	private Vector3f rotate(Vector3f originalPoint, Vector3f axis, double angle) {

		double sin = Math.sin(angle);
		double cos = Math.cos(angle);

		double xo = originalPoint.x;
		double yo = originalPoint.y;
		double zo = originalPoint.z;

		double ux = axis.x;
		double uy = axis.y;
		double uz = axis.z;

		double R11 = cos + ux * ux * (1 - cos);
		double R12 = ux * uy * (1 - cos) - uz * sin;
		double R13 = ux * uz * (1 - cos) + uy * sin;
		double R21 = ux * uy * (1 - cos) + uz * sin;
		double R22 = cos + uy * uy * (1 - cos);
		double R23 = uy * uz * (1 - cos) - ux * sin;
		double R31 = ux * uz * (1 - cos) - uy * sin;
		double R32 = uy * uz * (1 - cos) + ux * sin;
		double R33 = cos + uz * uz * (1 - cos);

		return new Vector3f((float) (R11 * xo + R12 * yo + R13 * zo),
				(float) (R21 * xo + R22 * yo + R23 * zo), (float) (R31 * xo
						+ R32 * yo + R33 * zo));
	}

	public void mouseWheel(boolean up) {
		// Method to be overridden
	}

	public void mouseClicked(int button, int state, int x, int y) {

		Vector3f rayVector = new Vector3f(getRayVector(x, y));

		switch (button) {
		case 2: { // RIGHT CLICK
			if (state == 0) {
				// Do something when right-clicking
			}
			break;
		}
		case 1: { // WHEEL pressed, apply an impulse
			if (state == 0) {
				if (dynamicsWorld != null) {
					CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(
							cameraPosition, rayVector);
					dynamicsWorld.rayTest(cameraPosition, rayVector,
							rayCallback);
					if (rayCallback.hasHit()) {
						RigidBody body = RigidBody
								.upcast(rayCallback.collisionObject);
						if (body != null) {
							body.setActivationState(CollisionObject.ACTIVE_TAG);
							Vector3f impulse = new Vector3f(rayVector);
							impulse.normalize();
							impulse.scale(impulseStrength);
							Vector3f relPos = new Vector3f();
							relPos.sub(rayCallback.hitPointWorld, body
									.getCenterOfMassPosition(new Vector3f()));
							body.applyImpulse(impulse, relPos);
						}
					}
				}
			} else {
			}
			break;
		}
		case 0: { // LEFT CLICK, grab something
			if (state == 0) {
				// add a point to point constraint for picking
				if (dynamicsWorld != null) {
					CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(
							cameraPosition, rayVector);
					dynamicsWorld.rayTest(cameraPosition, rayVector,
							rayCallback);
					if (rayCallback.hasHit()) {
						RigidBody body = RigidBody
								.upcast(rayCallback.collisionObject);
						if (body != null) {
							// other exclusions?
							if (!(body.isStaticObject() || body
									.isKinematicObject())) {
								pickedBody = body;
								pickedBody
										.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

								Vector3f pickPos = new Vector3f(
										rayCallback.hitPointWorld);

								Transform tmpTrans = body
										.getCenterOfMassTransform(new Transform());
								tmpTrans.inverse();
								Vector3f localPivot = new Vector3f(pickPos);
								tmpTrans.transform(localPivot);

								Point2PointConstraint p2p = new Point2PointConstraint(
										body, localPivot);
								p2p.setting.impulseClamp = mousePickClamping;

								dynamicsWorld.addConstraint(p2p);
								pickConstraint = p2p;
								// save mouse position for dragging
								BulletStats.gOldPickingPos.set(rayVector);
								Vector3f eyePos = new Vector3f(cameraPosition);
								Vector3f tmp = new Vector3f();
								tmp.sub(pickPos, eyePos);
								BulletStats.gOldPickingDist = tmp.length();
								// very weak constraint for picking
								p2p.setting.tau = 0.1f;
							}
						}
					}
				}
			} else { // release grabbed thing
				if (pickConstraint != null && dynamicsWorld != null) {
					dynamicsWorld.removeConstraint(pickConstraint);
					pickConstraint = null;
					pickedBody.forceActivationState(CollisionObject.ACTIVE_TAG);
					pickedBody.setDeactivationTime(0f);
					pickedBody = null;
				}
			}
			break;
		}
		default: {
		}
		}
	}

	public void mouseMotionFunc(int x, int y) {
		if (pickConstraint != null) {
			// move the constraint pivot
			Point2PointConstraint p2p = (Point2PointConstraint) pickConstraint;
			if (p2p != null) {
				// keep it at the same picking distance

				Vector3f newRayVector = new Vector3f(getRayVector(x, y));
				Vector3f eyePos = new Vector3f(cameraPosition);
				Vector3f dir = new Vector3f();
				dir.sub(newRayVector, eyePos);
				dir.normalize();
				dir.scale(BulletStats.gOldPickingDist);

				Vector3f newPos = new Vector3f();
				newPos.add(eyePos, dir);
				p2p.setPivotB(newPos);
			}
		}
	}

	protected RigidBody createBox(float mass, double lX, double lY, double lZ,
			double x, double y, double z, Vector3f linearVelocity,
			Vector3f angularVelocity, float bodyFriction,
			float bodyRestitution, float yaw, float pitch, float roll) {

		CollisionShape collisionShape = new BoxShape(new Vector3f(
				(float) (0.5 * lX), (float) (0.5 * lY), (float) (0.5 * lZ)));

		return locateRigidBody(mass, collisionShape, x, y, z, linearVelocity,
				angularVelocity, bodyFriction, bodyRestitution, yaw, pitch,
				roll);
	}

	protected RigidBody createCapsule(float mass, double radius, double height,
			double x, double y, double z, Vector3f linearVelocity,
			Vector3f angularVelocity, float bodyFriction,
			float bodyRestitution, float yaw, float pitch, float roll) {

		CollisionShape collisionShape = new CapsuleShapeZ((float) (radius),
				(float) (height));

		return locateRigidBody(mass, collisionShape, x, y, z, linearVelocity,
				angularVelocity, bodyFriction, bodyRestitution, yaw, pitch,
				roll);
	}

	protected RigidBody createCone(float mass, double radius, double height,
			double x, double y, double z, Vector3f linearVelocity,
			Vector3f angularVelocity, float bodyFriction,
			float bodyRestitution, float yaw, float pitch, float roll) {

		CollisionShape collisionShape = new ConeShapeZ((float) (radius),
				(float) (height));

		return locateRigidBody(mass, collisionShape, x, y, z, linearVelocity,
				angularVelocity, bodyFriction, bodyRestitution, yaw, pitch,
				roll);
	}

	protected RigidBody createCylinder(float mass, double radius,
			double height, double x, double y, double z,
			Vector3f linearVelocity, Vector3f angularVelocity,
			float bodyFriction, float bodyRestitution, float yaw, float pitch,
			float roll) {

		float halfX = (float) (radius);
		float halfY = (float) (radius);
		float halfZ = (float) (0.5 * height);

		CollisionShape collisionShape = new CylinderShapeZ(new Vector3f(halfX,
				halfY, halfZ));

		return locateRigidBody(mass, collisionShape, x, y, z, linearVelocity,
				angularVelocity, bodyFriction, bodyRestitution, yaw, pitch,
				roll);
	}

	protected RigidBody createSphere(float mass, double radius, double x,
			double y, double z, Vector3f linearVelocity,
			Vector3f angularVelocity, float bodyFriction, float bodyRestitution) {

		CollisionShape collisionShape = new SphereShape((float) (radius));

		return locateRigidBody(mass, collisionShape, x, y, z, linearVelocity,
				angularVelocity, bodyFriction, bodyRestitution, 0.0f, 0.0f,
				0.0f);
	}

	private RigidBody locateRigidBody(float mass,
			CollisionShape collisionShape, double x, double y, double z,
			Vector3f linearVelocity, Vector3f angularVelocity,
			float bodyFriction, float bodyRestitution, float yaw, float pitch,
			float roll) {

		// Yaw: rotation around Y
		// Pitch: rotation around X
		// Roll: rotation around Z
		yaw = (float) (yaw * Math.PI / 180.0);
		pitch = (float) (pitch * Math.PI / 180.0);
		roll = (float) (roll * Math.PI / 180.0);

		Transform startingTransform = new Transform();
		startingTransform.setIdentity();
		Vector3f truePosition = new Vector3f((float) (x), (float) (y),
				(float) (z));
		startingTransform.origin.set(truePosition);

		float fakeMass = (float)(1.0);
		RigidBody rigidBody = createRigidBody(fakeMass, collisionShape,
				startingTransform, linearVelocity, angularVelocity,
				bodyFriction, bodyRestitution);

		Transform centerOfMassTransform = new Transform();
		centerOfMassTransform.setIdentity();
		Vector3f centerOfMassPosition = new Vector3f((float) (x), (float) (y),
				(float) (z));
		centerOfMassTransform.origin.set(centerOfMassPosition);

		Quat4f rotationQuaternion = new Quat4f();
		QuaternionUtil.setEuler(rotationQuaternion, yaw, pitch, roll);
		centerOfMassTransform.setRotation(rotationQuaternion);

		rigidBody.setCenterOfMassTransform(centerOfMassTransform);
		
		Vector3f rigidBodyInertia = new Vector3f();
		rigidBody.getCollisionShape().calculateLocalInertia(fakeMass, rigidBodyInertia);
		rigidBody.setMassProps(mass, rigidBodyInertia);

		return rigidBody;
	}

	protected RigidBody createRigidBody(float mass,
			CollisionShape collisionShape, Transform startingTransform,
			Vector3f linearVelocity, Vector3f angularVelocity,
			float bodyFriction, float bodyRestitution) {

		if (mass != 0f) {
			collisionShape.calculateLocalInertia(mass, zeroInertia);
		}

		DefaultMotionState motionState = new DefaultMotionState(
				startingTransform);

		RigidBodyConstructionInfo rigidBodyCI = new RigidBodyConstructionInfo(
				mass, motionState, collisionShape, zeroInertia);
		RigidBody rigidBody = new RigidBody(rigidBodyCI);

		rigidBody.setFriction(bodyFriction);
		rigidBody.setRestitution(bodyRestitution);
		rigidBody.setLinearVelocity(linearVelocity);

		return rigidBody;
	}

	public abstract void renderme();

	public void clientResetScene() {
		BulletStats.gNumDeepPenetrationChecks = 0;
		BulletStats.gNumGjkChecks = 0;

		int numObjects = 0;
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(1f / 60f, 0);
			numObjects = dynamicsWorld.getNumCollisionObjects();
		}

		for (int i = 0; i < numObjects; i++) {
			CollisionObject collisionObject = dynamicsWorld
					.getCollisionObjectArray().getQuick(i);
			RigidBody rigidBody = RigidBody.upcast(collisionObject);

			if (rigidBody != null) {
				if (rigidBody.getMotionState() != null) {
					DefaultMotionState rigidBodyMotionState = (DefaultMotionState) rigidBody
							.getMotionState();
					rigidBodyMotionState.graphicsWorldTrans
							.set(rigidBodyMotionState.startWorldTrans);
					collisionObject
							.setWorldTransform(rigidBodyMotionState.graphicsWorldTrans);
					collisionObject
							.setInterpolationWorldTransform(rigidBodyMotionState.startWorldTrans);
					collisionObject.activate();
				}
				// removed cached contact points
				dynamicsWorld
						.getBroadphase()
						.getOverlappingPairCache()
						.cleanProxyFromPairs(
								collisionObject.getBroadphaseHandle(),
								dynamicsWorld.getDispatcher());

				rigidBody = RigidBody.upcast(collisionObject);
				if (rigidBody != null && !rigidBody.isStaticObject()) {
					RigidBody.upcast(collisionObject).setLinearVelocity(
							new Vector3f(0f, 0f, 0f));
					RigidBody.upcast(collisionObject).setAngularVelocity(
							new Vector3f(0f, 0f, 0f));
				}
			}
		}
	}

	public float getDeltaTimeMicroseconds() {
		float dt = clock.getTimeMicroseconds();
		clock.reset();
		return dt;
	}

	public abstract void clientMoveAndDisplay();

	public abstract void clientDisplayOnly();

	public DynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}

	public void setIdle(boolean idle) {
		this.isIdle = idle;
	}

	public void drawString(CharSequence s, int x, int y, Color3f color) {
		gl.drawString(s, x, y, color.x, color.y, color.z);
	}

	public void setDebugMode(int mode) {
		debugMode = mode;
		if (dynamicsWorld != null && dynamicsWorld.getDebugDrawer() != null) {
			dynamicsWorld.getDebugDrawer().setDebugMode(mode);
		}
	}

	public int getDebugMode() {
		return debugMode;
	}
}