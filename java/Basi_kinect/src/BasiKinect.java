import SimpleOpenNI.SimpleOpenNI;
import ground.Ground;
import processing.core.*;
import SimpleOpenNI.*;

public class BasiKinect extends PApplet {

	private Ground ground;
	private PVector view_rot;
	private boolean pause;
	

	public void settings() {
		size(1280, 720, P3D);
	}

	public void setup() {

		ground = new Ground(this);
		ground.init("greymap-smooth.png", 100, 50);
		ground.loadTexture("textures/grass.jpg", "textures/rock.jpg");
		view_rot = new PVector();
		pause = false;
		// ground.print();
		

	}

	public void draw() {
		
		if (!pause) {
			view_rot.x += 0.001f;
			view_rot.y += 0.001f;
			view_rot.z += 0.001f;

		}
		background(0, 0, 0);
		lights();
		// mon image doit tourner
		pushMatrix();
		translate(width * 0.5f, height * 0.5f, 0);
		// mettre scale après translate sinon il ne le prend pas en compte
		scale(0.3f, 0.3f, 0.3f);
		rotateX(view_rot.x);
		rotateY(view_rot.y);
		rotateZ(view_rot.z);

		ground.draw();
		popMatrix();

		// //doit rester au bon endroit
		fill(255);
		text(frameRate, 10, 25);
		//image(context.depthImage(), 0, 0, 640, 480);

	}

	public void keyPressed() {
		pause = !pause;
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "BasiKinect" });
	}

}
