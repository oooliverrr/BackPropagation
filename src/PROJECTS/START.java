package PROJECTS;

import org.lwjgl.LWJGLException;

import jbullet.GLDebugDrawer;
import jbullet.LWJGL;

public class START {

	public static void main(String[] args) {

		// Example 1
		// example1();

		// Example 2
		// example2();

		// Example 3
		// example3();

		// Test, average of two numbers between -100 and 100
		// exampleAverageOfTwoNumbers();

		// Image recognition
//		 imageRecognition();

		// Example dinosaur
		exampleDinosaur(args);
	}

	// Example 1
	private static void example1() {
		new Example1();
	}

	// Example 2
	private static void example2() {
		new Example2();
	}

	// Example 3
	private static void example3() {
		new Example3();
	}

	// Test, average of two numbers between -100 and 100
	private static void exampleAverageOfTwoNumbers() {
		new AverageOfTwoNumbers();
	}

	// Image recognition
	private static void imageRecognition() {
		new ImageRecognition();
	}

	// Example dinosaur
	private static void exampleDinosaur(String[] args) {
		ExampleDinosaur exDino = new ExampleDinosaur(LWJGL.getGL());
		exDino.initPhysics();
		exDino.getDynamicsWorld().setDebugDrawer(
				new GLDebugDrawer(LWJGL.getGL()));

		try {
			LWJGL.main(args, 800, 600, "Dinosaur Demo", exDino);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
}
