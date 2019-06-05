package jbullet;

import java.awt.event.KeyEvent;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class LWJGL {

	private static boolean redisplay = false;
	private static LwjglGL gl = new LwjglGL();

	public static void postRedisplay() {
		redisplay = true;
	}

	public static IGL getGL() {
		return gl;
	}

	public static int main(String[] args, int width, int height, String title,
			GenericApplication demoApp) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setTitle(title);
		Display.create(new PixelFormat(0, 24, 0));

		Keyboard.create();
		Keyboard.enableRepeatEvents(true);
		Mouse.create();

		gl.init();

		demoApp.myinit();
		demoApp.reshape(width, height);

		boolean quit = false;

		long lastTime = System.currentTimeMillis();
		int frames = 0;

		while (!Display.isCloseRequested() && !quit) {
			demoApp.moveAndDisplay();
			Display.update();

			int modifiers = 0;
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
					|| Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
				modifiers |= KeyEvent.SHIFT_DOWN_MASK;
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
					|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
				modifiers |= KeyEvent.CTRL_DOWN_MASK;
			if (Keyboard.isKeyDown(Keyboard.KEY_LMETA)
					|| Keyboard.isKeyDown(Keyboard.KEY_RMETA))
				modifiers |= KeyEvent.ALT_DOWN_MASK;

			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					demoApp.keyPressed(Keyboard.getEventKey(), Mouse.getX(),
							Mouse.getY(), modifiers);
				} else {
					demoApp.keyReleased(Keyboard.getEventKey(), Mouse.getX(),
							Mouse.getY(), modifiers);
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					quit = true;
				}
			}

			while (Mouse.next()) {
				int wheel = Mouse.getDWheel();
				if (wheel > 0) {
					demoApp.mouseWheel(true);
				}
				if (wheel < 0) {
					demoApp.mouseWheel(false);
				}
				if (Mouse.getEventButton() != -1) {
					int btn = Mouse.getEventButton();
					if (btn == 1) {
						btn = 2;
					} else if (btn == 2) {
						btn = 1;
					}
					demoApp.mouseClicked(btn, Mouse.getEventButtonState() ? 0
							: 1, Mouse.getEventX(),  Mouse.getEventY());
				}
				demoApp.mouseMotionFunc(Mouse.getEventX(),  Mouse.getEventY());
			}

			long time = System.currentTimeMillis();
			if (time - lastTime < 1000) {
				frames++;
			} else {
				Display.setTitle(title + " | FPS: " + frames);
				lastTime = time;
				frames = 0;
			}
		}

		System.exit(0);
		return 0;
	}

}