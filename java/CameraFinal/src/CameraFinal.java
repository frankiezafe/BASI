import processing.controlP5.ControlP5;
import processing.controlP5.Slider;
import processing.controlP5.Toggle;
import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

public class CameraFinal extends PApplet {

	private static final String campath = 		"camera.xml";
	private static final PVector ONE = 			new PVector( 1,1,1 );

	private PImage cubetex;

	private Camera cam;
	private PGraphicsOpenGL pg3d; 	// main render
	

	
	public void settings() {
		size( 1024,768, P3D );
	}
	
	public void setup() {
		
		cam = new Camera( this );
		cam.load( campath );
		
		pg3d = (PGraphicsOpenGL) createGraphics( width , height, P3D );
		
		cubetex = loadImage("cube-tex.png");
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
		pg3d.background( 5 );
		cam.apply( pg3d );
		pg3d.noFill();
		pg3d.lights();
		pg3d.hint(ENABLE_DEPTH_SORT);
		pg3d.hint(ENABLE_DEPTH_TEST);
		drawCubes( pg3d, ONE );
		pg3d.endDraw();

		image( pg3d, 0, 0 );

	}
	
	public void draw() {
		cam.render();
		
		//actual display
		background( 5 );
		draw3d();
	}
	
	
	// from https://processing.org/examples/texturecube.html
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
		PApplet.main(new String[] { "--present", "CameraFinal" });
//		PApplet.main(new String[] { "ProjectorCalibrator" });
	}
	
}