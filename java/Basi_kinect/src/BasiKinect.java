import SimpleOpenNI.SimpleOpenNI;
import ground.Ground;
import processing.core.*;
import SimpleOpenNI.*;
import processing.controlP5.ControlP5;
import processing.controlP5.Slider;
import processing.controlP5.Toggle;
import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

public class BasiKinect extends PApplet {

	private Ground ground;
	private PVector view_rot;
	private boolean pause;
	private ControlP5 ui;
	
	private Slider ui_minz;
	private Slider ui_maxz;

	public void settings() {
		size(1280, 720, P3D);
	}

	public void setup() {

		ground = new Ground(this);
		ground.init( 32*3, 24*3 );
//		ground.loadTexture("textures/grass.jpg", "textures/rock.jpg");
		view_rot = new PVector();
		pause = false;
		// ground.print();
		
		//partie slider
		int sliderw = 300;
		ui = new ControlP5 ( this);
		int uix = 10;
		int uiy = 10;
		ui.addLabel("distance d'affichage", uix, uiy);
		uiy += 15;
		ui_minz = ui.addSlider( "ground.minz", 0, 2000, uix, uiy, sliderw, 10).setValue(ground.minz);
		uiy += 15;
		ui_maxz = ui.addSlider( "ground.maxz", 2000, 4000, uix, uiy, sliderw, 10).setValue(ground.maxz);

	}

	public void draw() {
		ground.minz = ui_minz.getValue();
		ground.maxz = ui_maxz.getValue();
//		view_rot.x = mouseY * 0.1f;
//		view_rot.y = mouseX * 0.1f;
		view_rot.y = PI;
//		view_rot.x = -0.2f;
//		view_rot.y = PI - 0.6f;
//		view_rot.z = 0;
		background( 100,100,100 );
//		pointLight(255,255,255,0,0,-2000);
		// mon image doit tourner
		pushMatrix();
		translate(width * 0.5f, height * 0.5f, 0);
		// mettre scale après translate sinon il ne le prend pas en compte
		rotateX(view_rot.x);
		rotateY(view_rot.y);
		rotateZ(view_rot.z);
		stroke( 255,0,0 );
		line( 0,0,0, 100,0,0 );
		stroke( 0,255,0 );
		line( 0,0,0, 0,100,0 );
		stroke( 0,0,255 );
		line( 0,0,0, 0,0,100 );
//		scale(0.1f, 0.1f, 0.1f);
		ground.draw();
		popMatrix();

		// //doit rester au bon endroit
		fill(255);
		text(frameRate, 10, 25);
		//image(context.depthImage(), 0, 0, 640, 480);
		
	}

	public void keyPressed() {
//		pause = !pause;
//		if ( keyCode == 88 ) ground.swapx();
//		else if ( keyCode == 89 ) ground.swapy();
//		else 
		System.out.println( keyCode );
		
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "BasiKinect" });
	}

}
