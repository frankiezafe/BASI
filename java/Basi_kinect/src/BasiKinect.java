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
		ground.init( 100, 50 );
//		ground.loadTexture("textures/grass.jpg", "textures/rock.jpg");
		view_rot = new PVector();
		pause = false;
		// ground.print();
		

	}

	public void draw() {
		
		view_rot.x = mouseY * 0.1f;
		view_rot.y = mouseX * 0.1f;

		background( 100,100,100 );
		lights();
		// mon image doit tourner
		pushMatrix();
		translate(width * 0.5f, height * 0.5f, 0);
		// mettre scale apr�s translate sinon il ne le prend pas en compte
		rotateX(view_rot.x);
		rotateY(view_rot.y);
		rotateZ(view_rot.z);
		stroke( 255,0,0 );
		line( 0,0,0, 100,0,0 );
		stroke( 0,255,0 );
		line( 0,0,0, 0,100,0 );
		stroke( 0,0,255 );
		line( 0,0,0, 0,0,100 );
		scale(0.1f, 0.1f, 0.1f);
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
