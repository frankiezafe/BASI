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
	private Slider ui_resize;
	private Slider ui_kinectx;
	private Slider ui_kinecty;
	private Slider ui_kinectz;
	private float resize = 2.55f;
	private float kinectx = 346.66f;
	private float kinecty = 283.33f;
	private float kinectz= 1060;
	
	private static final String campath = "camera.xml";
	private static final PVector ONE = 	new PVector( 1,1,1 );

	private PImage cubetex;

	private Camera cam;
	private PGraphicsOpenGL pg3d;
	
	public void settings() {
		size(1024, 768, P3D);
	}

	public void setup() {
		
		cam = new Camera( this );
		cam.load( campath );
		pg3d = (PGraphicsOpenGL) createGraphics( width , height, P3D );
		cubetex = loadImage("cube-tex.png");
		
		ground = new Ground(this);
		ground.init( 32*3, 24*3 );
		view_rot = new PVector();
//		ground.loadTexture("textures/grass.jpg", "textures/rock.jpg");
		pause = false;
		// ground.print();
		
		//partie slider
		int sliderw = 300;
		ui = new ControlP5 ( this);
		int uix = 10;
		int uiy = 30;
		ui.addLabel("distance d'affichage", uix, uiy);
		uiy += 15;
		ui_minz = ui.addSlider( "ground.minz", 0, 2000, uix, uiy, sliderw, 10).setValue(ground.minz);
		uiy += 15;
		ui_maxz = ui.addSlider( "ground.maxz", 2000, 4000, uix, uiy, sliderw, 10).setValue(ground.maxz);
		uiy += 15;
		ui_resize = ui.addSlider( "resize", 0, 5, uix, uiy, sliderw, 10).setValue(resize);
		uiy += 15;
		ui_kinectx = ui.addSlider( "kinectx", 0, 1000, uix, uiy, sliderw, 10).setValue(kinectx);
		uiy += 15;
		ui_kinecty = ui.addSlider( "kinecty", -500, 500, uix, uiy, sliderw, 10).setValue(kinecty);
		uiy += 15;
		ui_kinectz = ui.addSlider( "kinectz", 0, 2000, uix, uiy, sliderw, 10).setValue(kinectz);

	}
private void drawCubes( PGraphics pg, PVector scale ) {
		
		pg.pushMatrix();
			pg.scale( scale.x, scale.y, scale.z );
			pg.fill( 255, 255, 255 );
			//GAUCHE
			pg.pushMatrix();
			pg.translate((float) cam.offset.x,(float) cam.offset.y, (float) cam.offset.z );
			pg.fill( 255, 247, 42 );
			texturedCube( pg, cubetex, 110, 65, 50 );
			pg.popMatrix();
			pg.pushMatrix();
			pg.translate( (float) (55+30), (float) -51.3, (float) 19.6 );
			pg.fill( 255, 247, 42 );
			pg.popMatrix();
		pg.popMatrix();
		
	}
	
	private void draw3d() {

		// 3D view with camera
		pg3d.beginDraw();
		pg3d.background( 0 );
		cam.apply( pg3d );
		pg3d.noFill();
		pg3d.lights();
//		pg3d.hint(ENABLE_DEPTH_SORT);
//		pg3d.hint(ENABLE_DEPTH_TEST);
//		drawCubes( pg3d, ONE );
		pg3d.pushMatrix();
		pg3d.translate((float) cam.offset.x,(float) cam.offset.y, (float) cam.offset.z );
		pg3d.translate((float) kinectx,(float) kinecty, (float) kinectz );
		pg3d.rotateY(4.0699997f);
		pg3d.scale(resize);
		ground.draw(pg3d);
		pg3d.popMatrix();
		pg3d.endDraw();

		image( pg3d, 0, 0 );

	}
	public void draw() {
		
		cam.render();
		
		ground.minz = ui_minz.getValue();
		ground.maxz = ui_maxz.getValue();
		resize = ui_resize.getValue();
		kinectx = ui_kinectx.getValue();
		kinecty = ui_kinecty.getValue();
		kinectz = ui_kinectz.getValue();

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
		popMatrix();
		draw3d();
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
	public void texturedCube( PGraphics pg, PImage tex, float x, float y, float z ) {

		pg.noStroke();
		pg.textureMode(NORMAL);
		
		pg.pushMatrix();
		pg.scale( x * 0.5f, y * 0.5f, z * 0.5f );
		pg.beginShape(QUADS);
		pg.texture(tex);

		// +Z "front" face
		pg.vertex(-1, -1, 1, 0, 0);
		pg.vertex(1, -1, 1, 1, 0);
		pg.vertex(1, 1, 1, 1, 1);
		pg.vertex(-1, 1, 1, 0, 1);

		// -Z "back" face
		pg.vertex(1, -1, -1, 0, 0);
		pg.vertex(-1, -1, -1, 1, 0);
		pg.vertex(-1, 1, -1, 1, 1);
		pg.vertex(1, 1, -1, 0, 1);

		// +Y "bottom" face
		pg.vertex(-1, 1, 1, 0, 0);
		pg.vertex(1, 1, 1, 1, 0);
		pg.vertex(1, 1, -1, 1, 1);
		pg.vertex(-1, 1, -1, 0, 1);

		// -Y "top" face
		pg.vertex(-1, -1, -1, 0, 0);
		pg.vertex(1, -1, -1, 1, 0);
		pg.vertex(1, -1, 1, 1, 1);
		pg.vertex(-1, -1, 1, 0, 1);

		// +X "right" face
		pg.vertex(1, -1, 1, 0, 0);
		pg.vertex(1, -1, -1, 1, 0);
		pg.vertex(1, 1, -1, 1, 1);
		pg.vertex(1, 1, 1, 0, 1);

		// -X "left" face
		pg.vertex(-1, -1, -1, 0, 0);
		pg.vertex(-1, -1, 1, 1, 0);
		pg.vertex(-1, 1, 1, 1, 1);
		pg.vertex(-1, 1, -1, 0, 1);

		pg.endShape();
		pg.popMatrix();
		
	}
	public static void main(String args[]) {
//		PApplet.main(new String[] { "BasiKinect" });
		PApplet.main(new String[] { "--present", "BasiKinect" });

	}

}
