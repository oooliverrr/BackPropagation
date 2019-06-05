package PROJECTS;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import jbullet.GLShapeDrawer;
import jbullet.GenericApplication;
import jbullet.IGL;
import jbullet.Joint;

import org.lwjgl.input.Keyboard;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class ExampleDinosaur extends GenericApplication {

	// Engine stuff
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver constraintSolver;
	private DefaultCollisionConfiguration collisionConfiguration;

	// Dinosaur, ground
	private RigidBody mainBone;
	private ArrayList<Joint> joints;
	private float mainBoneLX = (float) (6.0);
	private float mainBoneLY = (float) (6.0);
	private float mainBoneLZ = (float) (2.0);

	// Physics stuff
	private float gravity = 9.8f;
	private Vector3f zeroLinearVelocity = new Vector3f(0f, 0f, 0f);
	private Vector3f zeroAngularVelocity = new Vector3f(0f, 0f, 0f);
	private float groundFriction = (float) (1.0);
	private float groundRestitution = (float) (1.0);
	private float bodyFriction = (float) (0.05f);
	private float bodyRestitution = (float) (0.1f);

	// Camera
	private double minRadius = 1.0;
	private double radiusStep = 1.0;
	private double thetaStep = 1.0 * Math.PI / 180.0;
	private double phiStep = 1.0 * Math.PI / 180.0;
	private double cameraRadius, cameraTheta, cameraPhi;
	private Vector3f cameraDeltaLocation;

	public ExampleDinosaur(IGL gl) {
		super(gl);
		mainBone = null;
		joints = new ArrayList<Joint>();

		colors = new ArrayList<Vector3f>();

		cameraRadius = 25.0;
		cameraTheta = 55.0 * Math.PI / 180.0;
		cameraPhi = -45.0 * Math.PI / 180.0;

		updateCameraDeltaLocation();
		cameraPosition.set(cameraDeltaLocation);
	}

	private void updateCameraDeltaLocation() {

		if (cameraPhi < 0) {
			cameraPhi = cameraPhi + 2 * Math.PI;
		}
		if (cameraPhi > 2 * Math.PI) {
			cameraPhi = cameraPhi - 2 * Math.PI;
		}
		if (cameraTheta < 0) {
			cameraTheta = cameraTheta + 2 * Math.PI;
		}
		if (cameraTheta > 2 * Math.PI) {
			cameraTheta = cameraTheta - 2 * Math.PI;
		}
		if (cameraRadius < minRadius) {
			cameraRadius = minRadius;
		}

		float cameraDX = (float) (cameraRadius * Math.sin(cameraTheta) * Math.cos(cameraPhi));
		float cameraDY = (float) (cameraRadius * Math.sin(cameraTheta) * Math.sin(cameraPhi));
		float cameraDZ = (float) (cameraRadius * Math.cos(cameraTheta));

		cameraDeltaLocation = new Vector3f(cameraDX, cameraDY, cameraDZ);
		updateCamera();
	}

	private void createTable(double xo, double zo, double xf, double zf, double width, Vector3f color) {

		double tableSize = Math.sqrt((xf - xo) * (xf - xo) + (zf - zo) * (zf - zo));
		float cmX = (float) (0.5 * (xf + xo));
		float cmZ = (float) (0.5 * (zf + zo));

		float tableHeight = (float) (0.1);

		double theta = Math.atan2(zf - zo, xf - xo);
		float groundYaw = (float) (-theta * 180.0 / Math.PI);

		float groundPitch = (float) (0.0);
		float groundRoll = (float) (0.0);
		RigidBody tableRigidBody = createBox(0.0f, (float) (tableSize), (float) (width), tableHeight, cmX, 0.0f, cmZ, zeroLinearVelocity, zeroAngularVelocity, groundFriction, groundRestitution,
				groundYaw, groundPitch, groundRoll);

		dynamicsWorld.addRigidBody(tableRigidBody);
		colors.add(color);
	}

	// Create world
	public void initPhysics() {

		// Physics stuff
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		broadphase = new DbvtBroadphase();
		constraintSolver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfiguration);

		// Generate gravity
		dynamicsWorld.setGravity(new Vector3f(0, 0, -gravity));

		// Generate ground (X-direction)
		double xo = -20.0;
		double zo = 0.0;
		double xf = 20.0;
		double zf = 0.0;
		double width = 10.0;
		Vector3f color = getColor(255, 255, 0);
		createTable(xo, zo, xf, zf, width, color);

		// Generate sub ground
		Vector3f groundNormal = new Vector3f((float) (0.0), (float) (0.0), (float) (1.0));
		DefaultMotionState zeroMotionState = new DefaultMotionState();
		float subgroundDistance = (float) (-10.0);
		CollisionShape groundShape = new StaticPlaneShape(groundNormal, subgroundDistance);
		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, zeroMotionState, groundShape, zeroInertia);
		RigidBody groundRigidBodyBelow = new RigidBody(groundRigidBodyCI);

		// Create dinosaur main bone (followed by cam)
		float mainBoneMass = (float) (20);
		double mainBonePosX = 0.0;
		double mainBonePosY = 0.0;
		double mainBonePosZ = 10.0;
		float mainBoneYaw = (float) (0.0);
		float mainBonePitch = (float) (0.0);
		float mainBoneRoll = (float) (0.0);
		mainBone = createBox(mainBoneMass, mainBoneLX, mainBoneLY, mainBoneLZ, mainBonePosX, mainBonePosY, mainBonePosZ, zeroLinearVelocity, zeroAngularVelocity, bodyFriction, bodyRestitution,
				mainBoneYaw, mainBonePitch, mainBoneRoll);

		// Create dinosaur head
		float headMass = (float) (10);
		double headRadius = 2.0;
		double headPosX = 0.0;
		double headPosY = 0.0;
		double headPosZ = 10.0;
		RigidBody head = createSphere(headMass, headRadius, headPosX, headPosY, headPosZ, zeroLinearVelocity, zeroAngularVelocity, bodyFriction, bodyRestitution);

		// Create dinosaur legs
		float legMass = (float) (10);
		double legRadius = 0.5;
		double legHeight = 5.0;
		// 1
		double leg1PosX = mainBonePosX + 0.5 * mainBoneLX + legRadius + 0.5 * legHeight;
		double leg1PosY = mainBonePosY + 0.5 * mainBoneLY;
		double leg1PosZ = mainBonePosZ - 0.5 * mainBoneLZ;
		float leg1Yaw = (float) (90.0);
		float leg1Pitch = (float) (0.0);
		float leg1Roll = (float) (0.0);
		RigidBody leg1 = createCapsule(legMass, legRadius, legHeight, leg1PosX, leg1PosY, leg1PosZ, zeroLinearVelocity, zeroAngularVelocity, bodyFriction, bodyRestitution, leg1Yaw, leg1Pitch,
				leg1Roll);
		// 2
		double leg2PosX = mainBonePosX + 0.5 * mainBoneLX + legRadius + 0.5 * legHeight;
		double leg2PosY = mainBonePosY - 0.5 * mainBoneLY;
		double leg2PosZ = mainBonePosZ - 0.5 * mainBoneLZ;
		float leg2Yaw = (float) (90.0);
		float leg2Pitch = (float) (0.0);
		float leg2Roll = (float) (0.0);
		RigidBody leg2 = createCapsule(legMass, legRadius, legHeight, leg2PosX, leg2PosY, leg2PosZ, zeroLinearVelocity, zeroAngularVelocity, bodyFriction, bodyRestitution, leg2Yaw, leg2Pitch,
				leg2Roll);
		// 3
		double leg3PosX = mainBonePosX - 0.5 * mainBoneLX - legRadius - 0.5 * legHeight;
		double leg3PosY = mainBonePosY + 0.5 * mainBoneLY;
		double leg3PosZ = mainBonePosZ - 0.5 * mainBoneLZ;
		float leg3Yaw = (float) (-90.0);
		float leg3Pitch = (float) (0.0);
		float leg3Roll = (float) (0.0);
		RigidBody leg3 = createCapsule(legMass, legRadius, legHeight, leg3PosX, leg3PosY, leg3PosZ, zeroLinearVelocity, zeroAngularVelocity, bodyFriction, bodyRestitution, leg3Yaw, leg3Pitch,
				leg3Roll);
		// 4
		double leg4PosX = mainBonePosX - 0.5 * mainBoneLX - legRadius - 0.5 * legHeight;
		double leg4PosY = mainBonePosY - 0.5 * mainBoneLY;
		double leg4PosZ = mainBonePosZ - 0.5 * mainBoneLZ;
		float leg4Yaw = (float) (-90.0);
		float leg4Pitch = (float) (0.0);
		float leg4Roll = (float) (0.0);
		RigidBody leg4 = createCapsule(legMass, legRadius, legHeight, leg4PosX, leg4PosY, leg4PosZ, zeroLinearVelocity, zeroAngularVelocity, bodyFriction, bodyRestitution, leg4Yaw, leg4Pitch,
				leg4Roll);

		// Create Joints
		createJoint(mainBone, leg1, new Vector3f((float) (0.5 * mainBoneLX), (float) (0.5 * mainBoneLY), (float) (-0.5 * mainBoneLZ)),
				new Vector3f(0.0f, 0.0f, (float) (-legRadius - 0.5 * legHeight)), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		createJoint(mainBone, leg2, new Vector3f((float) (0.5 * mainBoneLX), (float) (-0.5 * mainBoneLY), (float) (-0.5 * mainBoneLZ)),
				new Vector3f(0.0f, 0.0f, (float) (-legRadius - 0.5 * legHeight)), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		createJoint(mainBone, leg3, new Vector3f((float) (-0.5 * mainBoneLX), (float) (0.5 * mainBoneLY), (float) (-0.5 * mainBoneLZ)),
				new Vector3f(0.0f, 0.0f, (float) (-legRadius - 0.5 * legHeight)), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		createJoint(mainBone, leg4, new Vector3f((float) (-0.5 * mainBoneLX), (float) (-0.5 * mainBoneLY), (float) (-0.5 * mainBoneLZ)), new Vector3f(0.0f, 0.0f,
				(float) (-legRadius - 0.5 * legHeight)), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));

		// Add objects
		dynamicsWorld.addRigidBody(groundRigidBodyBelow);
		colors.add(getColor(255, 0, 0));
		dynamicsWorld.addRigidBody(mainBone);
		colors.add(getColor(0, 255, 0));
		// dynamicsWorld.addRigidBody(head);
		// colors.add(getColor(0, 0, 255));
		dynamicsWorld.addRigidBody(leg1);
		colors.add(getColor(0, 0, 255));
		dynamicsWorld.addRigidBody(leg2);
		colors.add(getColor(0, 0, 255));
		dynamicsWorld.addRigidBody(leg3);
		colors.add(getColor(0, 0, 255));
		dynamicsWorld.addRigidBody(leg4);
		colors.add(getColor(0, 0, 255));
	}

	private void createJoint(RigidBody bodyA, RigidBody bodyB, Vector3f relativePositionA, Vector3f relativePositionB, Vector3f relativeJointAxisA, Vector3f relativeJointAxisB) {

		// Create HingeConstraint and Joint
		HingeConstraint hc = new HingeConstraint(bodyA, bodyB, relativePositionA, relativePositionB, relativeJointAxisA, relativeJointAxisB);
		joints.add(new Joint(bodyA, bodyB, relativePositionA, relativePositionB, relativeJointAxisA, relativeJointAxisB));

		// Add constraints
		dynamicsWorld.addConstraint(hc);
	}

	// Render the world
	@Override
	public void renderme() {
		if (dynamicsWorld != null) {
			// Collision objects
			int numObjects = dynamicsWorld.getNumCollisionObjects();
			for (int i = 0; i < numObjects; i++) {
				CollisionObject collisionObject = dynamicsWorld.getCollisionObjectArray().getQuick(i);
				RigidBody rigidBody = RigidBody.upcast(collisionObject);
				Transform rigidBodyTransform = new Transform();

				if (rigidBody != null && rigidBody.getMotionState() != null) {
					// DefaultMotionState motionState = (DefaultMotionState)
					// rigidBody
					// .getMotionState();
					// rigidBodyTransform.set(motionState.graphicsWorldTrans);

					rigidBody.getCenterOfMassTransform(rigidBodyTransform);
				} else {
					collisionObject.getWorldTransform(rigidBodyTransform);
				}

				GLShapeDrawer.drawOpenGL(gl, rigidBodyTransform, collisionObject.getCollisionShape(), colors.get(i), getDebugMode());
			}
		}

		updateCamera();
	}

	// Display the world
	@Override
	public void clientMoveAndDisplay() {
		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

		if (dynamicsWorld != null) {
			float dt = getDeltaTimeMicroseconds() * 0.000001f;
			int maxSimSubSteps = 1;

			// Vector3f impulseDir = new Vector3f(0.001f,0.0f,0.0f);
			// Vector3f impulseRelativeLocation = new Vector3f(1.0f,1.0f,2.0f);
			// mainBone.applyImpulse(impulseDir, impulseLocation);

			dynamicsWorld.stepSimulation(dt, maxSimSubSteps);
		}

		renderme();
	}

	// Display the world
	@Override
	public void clientDisplayOnly() {
		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

		renderme();
	}

	@Override
	public void keyReleased(int key, int x, int y, int modifiers) {
		switch (key) {
		case Keyboard.KEY_UP: {
			// UP
			break;
		}
		case Keyboard.KEY_DOWN: {
			// DOWN
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void keyPressed(int key, int x, int y, int modifiers) {

		switch (key) {

		case Keyboard.KEY_S: {
			cameraTheta = cameraTheta + thetaStep;
			updateCameraDeltaLocation();
			break;
		}
		case Keyboard.KEY_W: {
			cameraTheta = cameraTheta - thetaStep;
			updateCameraDeltaLocation();
			break;
		}

		case Keyboard.KEY_A: {
			cameraPhi = cameraPhi - phiStep;
			updateCameraDeltaLocation();
			break;
		}
		case Keyboard.KEY_D: {
			cameraPhi = cameraPhi + phiStep;
			updateCameraDeltaLocation();
			break;
		}

		case Keyboard.KEY_Q: { // Shoot box
			Vector3f rayVector = new Vector3f(getRayVector(x, y));
			shootBox(rayVector);
			break;
		}
		case Keyboard.KEY_E: {// Shoot sphere
			Vector3f rayVector = new Vector3f(getRayVector(x, y));
			shootSphere(rayVector);
			break;
		}

		case Keyboard.KEY_T: {// joints
			double forceVal = 40.0;
			Joint j = joints.get(0);
			j.applyForce(forceVal);
			j = joints.get(1);
			j.applyForce(forceVal);
			j = joints.get(2);
			j.applyForce(-forceVal);
			j = joints.get(3);
			j.applyForce(-forceVal);
			break;
		}

		case Keyboard.KEY_F: {// Change debug mode
			if (debugMode == 0) {
				debugMode = DebugDrawModes.DRAW_WIREFRAME;
			} else {
				debugMode = 0;
			}
			break;
		}

		// Remove last object of dynamicsWorld
		case Keyboard.KEY_DELETE: {
			int numObj = dynamicsWorld.getNumCollisionObjects();
			if (numObj != 0) {
				CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(numObj - 1);

				dynamicsWorld.removeCollisionObject(obj);
				colors.remove(numObj - 1);
				RigidBody body = RigidBody.upcast(obj);
				if (body != null && body.getMotionState() != null) {
					// delete body->getMotionState();
				}
				// delete obj;
			}
			break;
		}
		// idle
		case Keyboard.KEY_SPACE: {
			toggleIdle();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void mouseWheel(boolean up) {
		if (up) {
			cameraRadius = cameraRadius - radiusStep;
		} else {
			cameraRadius = cameraRadius + radiusStep;
		}
		updateCameraDeltaLocation();
	}

	@Override
	public void updateCamera() {
		try {
			gl.glMatrixMode(gl.GL_PROJECTION);
			gl.glLoadIdentity();

			// Look at the mainBone
			Transform mainBoneTransform = new Transform();
			mainBone.getMotionState().getWorldTransform(mainBoneTransform);
			Vector3f mainBonePosition = new Vector3f(mainBoneTransform.origin.x - (float) (mainBoneLX * 0.5), mainBoneTransform.origin.y - (float) (mainBoneLY * 0.5), mainBoneTransform.origin.z
					- (float) (mainBoneLZ * 0.5));
			// Vector3f mainBonePosition = new
			// Vector3f(mainBoneTransform.origin.x
			// , mainBoneTransform.origin.y
			// , mainBoneTransform.origin.z);
			cameraTargetPosition.set(mainBonePosition);

			// Find camera position
			mainBonePosition.add(cameraDeltaLocation);
			cameraPosition.set(mainBonePosition);

			// Point the camera to the target
			Vector3f camToObject = new Vector3f();
			camToObject.sub(cameraTargetPosition, cameraPosition);

			// update OpenGL camera settings
			gl.glFrustum(-1.0, 1.0, -1.0, 1.0, 1.0, 10000.0);
			gl.glMatrixMode(IGL.GL_MODELVIEW);
			gl.glLoadIdentity();

			// Look at!
			gl.gluLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z, cameraTargetPosition.x, cameraTargetPosition.y, cameraTargetPosition.z, cameraNormal.x, cameraNormal.y, cameraNormal.z);
		} catch (Exception e) {
		}
	}
}
