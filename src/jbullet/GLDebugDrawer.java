package jbullet;

import static jbullet.IGL.*;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;

/**
 *
 * @author jezek2
 */
public class GLDebugDrawer extends IDebugDraw {

	// JAVA NOTE: added
	private static final boolean DEBUG_NORMALS = false;

	private IGL gl;
	private int debugMode;

	private final Vector3f tmpVec = new Vector3f();

	public GLDebugDrawer(IGL gl) {
		this.gl = gl;
	}

	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		if (debugMode > 0) {
			gl.glBegin(GL_LINES);
			gl.glColor3f(color.x, color.y, color.z);
			gl.glVertex3f(from.x, from.y, from.z);
			gl.glVertex3f(to.x, to.y, to.z);
			gl.glEnd();
		}
	}

	@Override
	public void setDebugMode(int debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public void draw3dText(Vector3f location, String textString) {
		// glRasterPos3f(location.x, location.y, location.z);
		// TODO: BMF_DrawString(BMF_GetFont(BMF_kHelvetica10),textString);
	}

	@Override
	public void reportErrorWarning(String warningString) {
		System.err.println(warningString);
	}

	@Override
	public void drawContactPoint(Vector3f pointOnB, Vector3f normalOnB,
			float distance, int lifeTime, Vector3f color) {
		if ((debugMode & DebugDrawModes.DRAW_CONTACT_POINTS) != 0) {
			Vector3f to = tmpVec;
			to.scaleAdd(distance * 100f, normalOnB, pointOnB);
			Vector3f from = pointOnB;

			// JAVA NOTE: added
			if (DEBUG_NORMALS) {
				to.normalize(normalOnB);
				to.scale(10f);
				to.add(pointOnB);
				gl.glLineWidth(3f);
				gl.glPointSize(6f);
				gl.glBegin(GL_POINTS);
				gl.glColor3f(color.x, color.y, color.z);
				gl.glVertex3f(from.x, from.y, from.z);
				gl.glEnd();
			}

			gl.glBegin(GL_LINES);
			gl.glColor3f(color.x, color.y, color.z);
			gl.glVertex3f(from.x, from.y, from.z);
			gl.glVertex3f(to.x, to.y, to.z);
			gl.glEnd();

			// JAVA NOTE: added
			if (DEBUG_NORMALS) {
				gl.glLineWidth(1f);
				gl.glPointSize(1f);
			}

			// glRasterPos3f(from.x, from.y, from.z);
			// char buf[12];
			// sprintf(buf," %d",lifeTime);
			// TODO: BMF_DrawString(BMF_GetFont(BMF_kHelvetica10),buf);
		}
	}

	@Override
	public int getDebugMode() {
		return debugMode;
	}

}