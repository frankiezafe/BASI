import processing.controlP5.ControlP5;
import processing.controlP5.Slider;
import processing.controlP5.Toggle;
import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

public class ProjectorCalibrator extends PApplet {

	private static final String campath = 		"camera.xml";
	private static final float SLIDER_RANGE =	1500.f;
	private static final PVector ONE = 			new PVector( 1,1,1 );
	
	private boolean reset_view;
	private boolean save_cam;
	
	private ControlP5 ui;
	private Slider ui_eyex;
	private Slider ui_eyey;
	private Slider ui_eyez;
	private Slider ui_centery;
	private Slider ui_upx;
	private Slider ui_upy;
	private Slider ui_upz;
	private Slider ui_fov;
	private Slider ui_near;
	private Slider ui_far;
	private Slider ui_aspect;
	private Toggle ui_resetview;
	private Toggle ui_savecam;
	private Slider ui_position;
	private Slider ui_translatex;
	private Slider ui_translatey;
	private Slider ui_translatez;
	
	private PVector orthoview_trans;
	private PVector orthoview_rot;
	private boolean orthodrag_trans;
	private boolean orthodrag_rot;
	private boolean ortho_front;
	private boolean ortho_left;
	private boolean ortho_top;
	
	private PVector lastmouse;
	
	private PImage cubetex;

	private Camera cam;
	private PGraphicsOpenGL pg3d; 	// main render
	private View[] views;			// working views
	

	
	public void settings() {
		size( 1024,768, P3D );
		ui_eyex = null;
	}
	
	public void setup() {
		
		cam = new Camera( this );
		cam.load( campath );
		
		views = new View[ 3 ];
		String name = "";
		int type = 0;
		for ( int i = 0; i < 3; ++i ) {
			switch( i ) {
				case 0:
					name = "free";
					type = View.VIEW_FREE;
					break;
				case 1:
					name = "top";
					type = View.VIEW_TOP;
					break;
				case 2:
					name = "left";
					type = View.VIEW_LEFT;
					break;
			}
			views[ i ] = new View( 
					this, name, 
					width - ( width / 4 ) - 10,
					height - ( ( ( height / 4 ) + 10 ) * ( i + 1 ) ), 
					width / 4, height / 4 );
			views[ i ].setType( type );
			views[ i ].setCamera( cam );
		}
		
		pg3d = (PGraphicsOpenGL) createGraphics( width , height, P3D );
	    
	    orthoview_trans = new PVector( 0,0,0 );
	    orthoview_rot = new PVector( 0,0,0 );
	    orthodrag_trans = false;
	    orthodrag_rot = false;
	    lastmouse = new PVector();
	    ortho_front = false;
	    ortho_left = false;
	    ortho_top = false;
	    
	    cubetex = loadImage("cube-tex.png");
	    
	    int sliderw = 300;
	    
		ui = new ControlP5( this );
		int uix = 10;
		int uiy = 10;
		ui.addLabel( "EYE", uix, uiy ); 
		uiy += 15;
		ui_eyex = ui.addSlider( cam.eye, "eye.x", "x", -SLIDER_RANGE, SLIDER_RANGE, uix, uiy, sliderw, 10 ).setValue( cam.eye.x );
		uiy += 15;
		ui_eyey = ui.addSlider( cam.eye, "eye.y", "y", -SLIDER_RANGE, SLIDER_RANGE, uix, uiy, sliderw, 10 ).setValue( cam.eye.y );
		uiy += 15;
		ui_eyez = ui.addSlider( cam.eye, "eye.z", "z", -SLIDER_RANGE, SLIDER_RANGE, uix, uiy, sliderw, 10 ).setValue( cam.eye.z );
		uiy += 35;
		ui.addLabel( "CENTER", uix, uiy );
		uiy += 15;
		ui_centery = ui.addSlider( cam.center_offset, "center.y", "y", -SLIDER_RANGE, SLIDER_RANGE, uix, uiy, sliderw, 10 ).setValue( cam.center_offset.y );
		uiy += 25;
		ui.addLabel( "UP", uix, uiy );
		uiy += 15;
		ui_upx = ui.addSlider( cam.up, "cam.up.x", "x", -1, 1, uix, uiy, sliderw, 10 ).setValue( cam.up.x );
		uiy += 15;
		ui_upy = ui.addSlider( cam.up, "cam.up.y", "y", -1, 1, uix, uiy, sliderw, 10 ).setValue( cam.up.y );
		uiy += 15;
		ui_upz = ui.addSlider( cam.up, "cam.up.z", "z", -1, 1, uix, uiy, sliderw, 10 ).setValue( cam.up.z );
		uiy += 25;
		ui.addLabel( "PERSPECTIVE", uix, uiy );
		uiy += 15;
		ui_fov = ui.addSlider( cam.fov, "fov", "fov", 0.1f, HALF_PI, uix, uiy, sliderw, 10 ).setValue( cam.fov );
		uiy += 15;
		ui_aspect = ui.addSlider( "cam.aspect", 0, 50, uix, uiy, sliderw, 10 ).setValue( cam.aspect );
		uiy += 15;
		ui_near = ui.addSlider( "cam.near", 0.1f, 1000, uix, uiy, sliderw, 10 ).setValue( cam.near );
		uiy += 15;
		ui_far = ui.addSlider( "cam.far", 0, 10000, uix, uiy, sliderw, 10 ).setValue( cam.far );
		uiy += 15;
		ui_resetview = ui.addToggle( "reset_view", uix, uiy, 10, 10 );
		ui_savecam = ui.addToggle( "save_cam", uix + 60, uiy, 10, 10 );
		uiy += 15;
		ui_translatex = ui.addSlider( "cam.offset.x", 0, -100, uix, uiy, sliderw, 10 ).setValue( cam.offset.x );
		uiy += 15;
		ui_translatey = ui.addSlider( "cam.offset.y", 0, -100, uix, uiy, sliderw, 10 ).setValue( cam.offset.y );
		uiy += 15;
		ui_translatez = ui.addSlider( "cam.offset.z", 0, -100, uix, uiy, sliderw, 10 ).setValue( cam.offset.z );
	}
	
	private void adaptView() {
		if ( reset_view ) {
			cam.reset();
			ui_resetview.setValue( false );
		}	
		if ( save_cam ) {
			cam.save( campath );
			ui_savecam.setValue( false );
		}
	}
	
	private void syncUI() {
		ui_centery.setValue( cam.center_offset.y );
		ui_eyex.setValue( cam.eye.x );
		ui_eyey.setValue( cam.eye.y );
		ui_eyez.setValue( cam.eye.z );
		ui_upx.setValue( cam.up.x );
		ui_upy.setValue( cam.up.y );
		ui_upz.setValue( cam.up.z );
		ui_near.setValue( cam.near );
		ui_far.setValue( cam.far );
		ui_fov.setValue( cam.fov );
		ui_translatex.setValue( cam.offset.x );
		ui_translatey.setValue( cam.offset.y );
		ui_translatez.setValue( cam.offset.z );
	}
	
	private void drawAxis( PGraphics pg, float scale ) {
		
		pg.strokeWeight( 3 );
		pg.stroke( 255, 0, 0 );
		pg.line( 0, 0, 0, 100 * scale, 0, 0 );
		pg.stroke( 0, 255, 0 );
		pg.line( 0, 0, 0, 0, 100 * scale, 0 );
		pg.stroke( 0, 0, 255 );
		pg.line( 0, 0, 0, 0, 0, 100 * scale );
		pg.strokeWeight( 1 );
		
	}
	
	private void drawCubes( PGraphics pg, PVector scale ) {
		
		pg.pushMatrix();
			pg.scale( scale.x, scale.y, scale.z );
			pg.fill( 255, 255, 255 );
			pg.box( 10, 10, 10 );
//			pg.box( 10, pg3d.height, 10 );
//			pg.box( pg3d.width, 10, 10 );
//			pg.noFill();
			//GAUCHE
			pg.pushMatrix();
			pg.translate((float) cam.offset.x,(float) cam.offset.y, (float) cam.offset.z );
//			pg.translate((float) (-55-2), cam.translatey, (float) -25 );
			//-17.5 -55,    -57  -37.5	-25
			//pg.texture( cubetex );
			pg.fill( 255, 247, 42 );
			texturedCube( pg, cubetex, 110, 65, 50 );
			pg.popMatrix();
			pg.pushMatrix();
			pg.translate( (float) (55+30), (float) -51.3, (float) 19.6 );
			//17.5+37.7
			pg.fill( 255, 247, 42 );
//			texturedCube( pg, cubetex, (float) 37.7, (float) 102.6, (float) 39.2 );
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
		drawAxis( pg3d, 1 );
		drawCubes( pg3d, ONE );
		pg3d.endDraw();

		image( pg3d, 0, 0 );

	}
	
	public void draw() {

		for ( int i = 0; i < views.length; ++i ) { 
			views[ i ].renderBegin();
			drawCubes( views[ i ].getPg(), views[ i ].getScale() );
			views[ i ].renderEnd();
		}

		cam.near = ui_near.getValue();
		cam.far = ui_far.getValue();
		cam.fov = ui_fov.getValue();
		cam.offset.x = ui_translatex.getValue();
		cam.offset.y = ui_translatey.getValue();
		cam.offset.z = ui_translatez.getValue();
		cam.render();
		
		adaptView();

		//actual display
		background( 5 );
		draw3d();
		stroke( 255, 180 );
		line( width * 0.5f, 0, width * 0.5f, height );
		line( 0, height * 0.5f, width, height * 0.5f );
		fill( 255 );
		text( frameRate, 10, height - 15 );
		
		String smat = 
			cam.xmin + " <> " + cam.xmax + "\n"+
			cam.ymin + " <> " + cam.ymax + "\n"
			;
		text( smat, 10, height - 100 );

		for ( int i = 0; i < views.length; ++i ) views[ i ].draw();
		
	}

	public void mouseMoved() {
		for ( int i = 0; i < views.length; ++i ) views[ i ].mouseMoved( mouseX, mouseY );
	}
	
	public void mouseReleased() {
		for ( int i = 0; i < views.length; ++i ) views[ i ].mouseReleased( mouseX, mouseY, mouseButton );
		orthodrag_trans = false;
	    orthodrag_rot = false;
	}
	
	public void mousePressed() {
		for ( int i = 0; i < views.length; ++i ) views[ i ].mousePressed( mouseX, mouseY, mouseButton );
	}
	
	public void mouseDragged() {
		
		boolean camUpdated = false;
		for ( int i = 0; i < views.length; ++i ) {
			views[ i ].mouseDragged( mouseX, mouseY, mouseButton );
			if ( views[ i ].isCamUpdated() ) camUpdated = true;
		}
		// thanks to UI...
		if ( camUpdated ) syncUI();
		
		if ( orthodrag_rot ) {
			orthoview_rot.x += -( mouseY - lastmouse.y ) * 0.03f;
			orthoview_rot.y += ( mouseX - lastmouse.x ) * 0.03f;
			lastmouse.set( mouseX, mouseY );
		} else if ( orthodrag_trans ) {
			orthoview_trans.x += mouseX - lastmouse.x;
			orthoview_trans.y += mouseY - lastmouse.y;
			lastmouse.set( mouseX, mouseY );
		}
	}
	
	// from https://processing.org/examples/texturecube.html
	public void texturedCube( PGraphics pg, PImage tex, float x, float y, float z ) {

		pg.noStroke();
		pg.textureMode(NORMAL);
		
		pg.pushMatrix();
		pg.scale( x * 0.5f, y * 0.5f, z * 0.5f );
		pg.beginShape(QUADS);
		pg.texture(tex);

		// Given one texture and six faces, we can easily set up the uv
		// coordinates
		// such that four of the faces tile "perfectly" along either u or v, but
		// the other
		// two faces cannot be so aligned. This code tiles "along" u, "around"
		// the X/Z faces
		// and fudges the Y faces - the Y faces are arbitrarily aligned such
		// that a
		// rotation along the X axis will put the "top" of either texture at the
		// "top"
		// of the screen, but is not otherwised aligned with the X/Z faces.
		// (This
		// just affects what type of symmetry is required if you need seamless
		// tiling all the way around the cube)

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
		PApplet.main(new String[] { "--present", "ProjectorCalibrator" });
//		PApplet.main(new String[] { "ProjectorCalibrator" });
	}
	
}