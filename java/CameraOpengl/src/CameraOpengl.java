import processing.controlP5.ControlP5;
import processing.controlP5.Slider;
import processing.controlP5.Toggle;
import processing.core.*;

public class CameraOpengl extends PApplet {

	private float fov, cam_near, cam_far, cam_aspect;
	private PVector cam_eye;
	private PVector cam_center;
	private PVector cam_up;
	private boolean reset_view;
	private float orbit_theta;
	private float orbit_radius;
	private boolean orbit;
	
	private ControlP5 ui;
	private Slider ui_eyex;
	private Slider ui_eyey;
	private Slider ui_eyez;
	private Slider ui_centerx;
	private Slider ui_centery;
	private Slider ui_upx;
	private Slider ui_upy;
	private Slider ui_upz;
	private Slider ui_fov;
	private Slider ui_near;
	private Slider ui_far;
	private Slider ui_aspect;
	private Toggle ui_resetview;
	private Toggle ui_orbit;
	private Toggle ui_orthofront;
	private Toggle ui_ortholeft;
	private Toggle ui_orthotop;
	
	private PGraphics pg3d;
	private PGraphics pgortho;
	private PVector orthoview_trans;
	private PVector orthoview_rot;
	private boolean orthodrag_trans;
	private boolean orthodrag_rot;
	private boolean ortho_front;
	private boolean ortho_left;
	private boolean ortho_top;
	
	private PVector lastmouse;
	
	public void settings() {
	
		size( 800, 600, P3D );
		ui_eyex = null;
	
	}
	
	private void resetView() {

		fov = 58 * DEG_TO_RAD;
	    cam_aspect = (float) width / (float) height;
	    cam_eye.set( 0, 0, ( height / 2.0f ) / ( (float) Math.tan( fov / 2.0f) ) );
	    cam_center.set( 0, 100, 0 ); //coordonnées du centre;
	    cam_up.set( 0, 1, 0 ); //rotation de la caméra
	    cam_near = cam_eye.z / 10.0f;
	    cam_far = cam_eye.z * 10.0f;
	    
		reset_view = 	false;
	    orbit_theta = 	0;
	    orbit_radius = 	-1;
	    orbit = 		false;
	    
	    if ( ui_eyex == null ) return;
	    
	    ui_eyex.setValue( cam_eye.x );
	    ui_eyey.setValue( cam_eye.y );
	    ui_eyez.setValue( cam_eye.z );
	    ui_centerx.setValue( cam_center.x );
	    ui_centery.setValue( cam_center.y );
	    ui_upx.setValue( cam_up.x );
	    ui_upy.setValue( cam_up.y );
	    ui_upz.setValue( cam_up.z );
	    ui_fov.setValue( fov );
	    ui_near.setValue( cam_near );
	    ui_far.setValue( cam_far );
	    ui_aspect.setValue( cam_aspect );
	
	}
	
	public void setup() {
		
		pg3d = createGraphics( width , height, P3D );
		pgortho = createGraphics( width / 4, height / 4, P3D );
		
	    cam_eye = 		new PVector();
	    cam_center = 	new PVector();
	    cam_up = 		new PVector();
		resetView();
	    
	    orthoview_trans = new PVector( 0,0,0 );
	    orthoview_rot = new PVector( 0,0,0 );
	    orthodrag_trans = false;
	    orthodrag_rot = false;
	    lastmouse = new PVector();
	    ortho_front = false;
	    ortho_left = false;
	    ortho_top = false;
	    
		ui = new ControlP5( this );
		int uix = 10;
		int uiy = 10;
		ui.addLabel( "EYE", uix, uiy ); 
		uiy += 15;
		ui_eyex = ui.addSlider( cam_eye, "eye.x", "x", -1000, 1000, uix, uiy, 100, 10 ).setValue( cam_eye.x );
		uiy += 15;
		ui_eyey = ui.addSlider( cam_eye, "eye.y", "y", -1000, 1000, uix, uiy, 100, 10 ).setValue( cam_eye.y );
		uiy += 15;
		ui_eyez = ui.addSlider( cam_eye, "eye.z", "z", -1000, 10000, uix, uiy, 100, 10 ).setValue( cam_eye.z );
		uiy += 15;
		ui_orbit = ui.addToggle( "orbit", uix, uiy, 10, 10 );
		uiy += 35;
		ui.addLabel( "CENTER", uix, uiy );
		uiy += 15;
		ui_centerx = ui.addSlider( cam_center, "center.x", "x", -width, width, uix, uiy, 100, 10 ).setValue( cam_center.x );
		uiy += 15;
		ui_centery = ui.addSlider( cam_center, "center.y", "y", -height, height, uix, uiy, 100, 10 ).setValue( cam_center.y );
		uiy += 25;
		ui.addLabel( "UP", uix, uiy );
		uiy += 15;
		ui_upx = ui.addSlider( cam_up, "cam_up.x", "x", -1, 1, uix, uiy, 100, 10 ).setValue( cam_up.x );
		uiy += 15;
		ui_upy = ui.addSlider( cam_up, "cam_up.y", "y", -1, 1, uix, uiy, 100, 10 ).setValue( cam_up.y );
		uiy += 15;
		ui_upz = ui.addSlider( cam_up, "cam_up.z", "z", -1, 1, uix, uiy, 100, 10 ).setValue( cam_up.z );
		uiy += 25;
		ui.addLabel( "PERSPECTIVE", uix, uiy );
		uiy += 15;
		ui_fov = ui.addSlider( "fov", 0, PI, uix, uiy, 100, 10 ).setValue( fov );
		uiy += 15;
		ui_aspect = ui.addSlider( "cam_aspect", 0, 10, uix, uiy, 100, 10 ).setValue( cam_aspect );
		uiy += 15;
		ui_near = ui.addSlider( "cam_near", 0.1f, 500, uix, uiy, 100, 10 ).setValue( cam_near );
		uiy += 15;
		ui_far = ui.addSlider( "cam_far", 0, 10000, uix, uiy, 100, 10 ).setValue( cam_far );
		uiy += 15;
		ui_resetview = ui.addToggle( "reset_view", uix, uiy, 10, 10 );
		
		ui_orthofront = ui.addToggle( "ortho_front", width - pgortho.width, height - ( pgortho.height + 25 ), 10, 10 );
		ui_ortholeft = ui.addToggle( "ortho_left", width - ( pgortho.width - 60 ), height - ( pgortho.height + 25 ), 10, 10 );
		ui_orthotop = ui.addToggle( "ortho_top", width - ( pgortho.width - 120 ), height - ( pgortho.height + 25 ), 10, 10 );
		
	}
	
	public void drawAxis( PGraphics pg, float scale ) {
		
		pg.strokeWeight( 3 );
		pg.stroke( 255, 0, 0 );
		pg.line( 0, 0, 0, 100 * scale, 0, 0 );
		pg.stroke( 0, 255, 0 );
		pg.line( 0, 0, 0, 0, 100 * scale, 0 );
		pg.stroke( 0, 0, 255 );
		pg.line( 0, 0, 0, 0, 0, 100 * scale );
		pg.strokeWeight( 1 );
		
	}
	
	public void drawCubes( PGraphics pg, float scale ) {
		
		pg.pushMatrix();
			//pg.stroke( 0, 255, 255 );
			pg.fill( 255 );
			pg.box( 50 * scale );
			//pg.rotateX( frameCount * 0.001f );
			//pg.rotateZ( frameCount * 0.0015f );
			pg.noFill();
			pg.box( 100 * scale );
			pg.fill( 255 );
			/*pg.pushMatrix();
				pg.translate( 150 * scale, 0, 0 );
				pg.rotateX( frameCount * -0.004f );
				pg.box( 100 * scale );
			pg.popMatrix();
			pg.pushMatrix();
				pg.translate( -150 * scale, 0, 0 );
				pg.rotateX( frameCount * 0.004f );
				pg.box( 100 * scale );
			pg.popMatrix();
			pg.pushMatrix();
				pg.translate( 0, 150 * scale, 0 );
				pg.rotateY( frameCount * -0.004f );
				pg.box( 100 * scale );
			pg.popMatrix();
			pg.pushMatrix();
				pg.translate( 0, -150 * scale, 0 );
				pg.rotateY( frameCount * 0.004f );
				pg.box( 100 * scale );
			pg.popMatrix();*/
		pg.popMatrix();
		
	}
	
	public void draw() {
		
		if ( ortho_front ) {
			orthoview_trans.set( 0,0,0 );
			orthoview_rot.set( 0,0,0 );
			ortho_front = false;
			ui_orthofront.setValue( false );
		} else if ( ortho_left ) {
			orthoview_trans.set( -pgortho.width * 0.2f,0,0 );
			orthoview_rot.set( 0,HALF_PI,0 );
			ortho_left = false;
			ui_ortholeft.setValue( false );
		} else if ( ortho_top ) {
			orthoview_trans.set( 0,-pgortho.height * 0.2f,0 );
			orthoview_rot.set( -HALF_PI,0,0 );
			ortho_top = false;
			ui_orthotop.setValue( false );
		}
		
		if ( reset_view ) {
			resetView();
			ui_resetview.setValue( false );
		}
		
		if ( orbit ) {
			if ( orbit_radius == -1 ) {
				orbit_radius = dist( cam_eye.x, cam_eye.y, cam_eye.z, cam_center.x, cam_center.y, cam_center.z );
				orbit_theta = atan2( cam_eye.z - cam_center.z, cam_eye.x - cam_center.x );
			}
			orbit_theta += 0.01f;
			cam_eye.x = cos( orbit_theta ) * orbit_radius;
			cam_eye.z = sin( orbit_theta ) * orbit_radius;
			ui_eyex.setValue( cam_eye.x );
			ui_eyez.setValue( cam_eye.z );
		} else if ( orbit_radius != -1 ) {
			orbit_radius = -1;
		}
		
		// 3D view with camera
		pg3d.beginDraw();
		pg3d.background( 5 );
		pg3d.camera( 
				cam_eye.x, 		cam_eye.y, 		cam_eye.z, 
				cam_center.x, 	cam_center.y, 	cam_center.z, 
				cam_up.x, 		cam_up.y, 		cam_up.z
		);
		pg3d.perspective( fov, cam_aspect, cam_near, cam_far );
		pg3d.noFill();
		pg3d.lights();
		drawAxis( pg3d, 1 );
		drawCubes( pg3d, 1 );
		pg3d.endDraw();

		// scene display
		float orthoratio = 100.f / pg3d.width;
		PVector workv1 = new PVector();
		PVector workv2 = new PVector();
		PVector camx = new PVector();
		PVector camy = new PVector();
		pgortho.beginDraw();
		pgortho.background( 190, 200 );
		pgortho.ortho();
		pgortho.pushMatrix();
		
			pgortho.translate( pgortho.width * 0.5f + orthoview_trans.x, pgortho.height * 0.5f + orthoview_trans.y, 0 );
			pgortho.rotateX( orthoview_rot.x );
			pgortho.rotateY( orthoview_rot.y );
			pgortho.rotateZ( orthoview_rot.z );
			
			drawAxis( pgortho, orthoratio );
			
			// viewport
			pgortho.pushMatrix();
				pgortho.noFill();
				pgortho.stroke( 255 );
				pgortho.translate( -50, -50 / cam_aspect, 0 );
				pgortho.rect( 0, 0, 100, 100 / cam_aspect );
				pgortho.translate( 50, 50 / cam_aspect, 0 );
				drawCubes( pgortho, orthoratio );
			pgortho.popMatrix();
			
			// eye - center axis
			workv1.set( cam_eye );
			workv1.mult( orthoratio );
			workv2.set( cam_center );
			workv2.mult( orthoratio * -1 );
			workv2.set( cam_center );
			workv2.mult( orthoratio );
			pgortho.line( workv1.x, workv1.y, workv1.z, workv2.x, workv2.z, workv2.z );
			
			// camera axis, X & Y in world space
			workv2.set( cam_center.x - cam_eye.x, cam_center.y - cam_eye.y, cam_center.z - cam_eye.z );
			workv2.mult( orthoratio );
			
			float m = 50;
			
			cam_up.cross( workv2, camx );
			camx.normalize();
			camx.mult( m );
			camy.set( cam_up );
			camx.cross( workv2, camy );
			camy.normalize();
			camy.mult( m / cam_aspect );
			
			// rendering the 4 points
			workv2.set( camx );
			workv2.mult( -1 );
			PVector pt1 = new PVector( camx );
			pt1.add( camy );
			PVector pt2 = new PVector( camy );
			pt2.mult( -1 );
			pt2.add( camx );
			PVector pt3 = new PVector( camy );
			pt3.mult( -1 );
			pt3.add( workv2 );
			PVector pt4 = new PVector( camy );
			pt4.add( workv2 );
			
			workv2.set( cam_center );
			workv2.mult( orthoratio );
			pt1.add( workv2 );
			pt2.add( workv2 );
			pt3.add( workv2 );
			pt4.add( workv2 );
			
			// drawing the camera plane
			pgortho.stroke( 255, 80 );
			pgortho.line( pt1.x, pt1.y, pt1.z, pt2.x, pt2.y, pt2.z );
			pgortho.line( pt3.x, pt3.y, pt3.z, pt2.x, pt2.y, pt2.z );
			pgortho.line( pt3.x, pt3.y, pt3.z, pt4.x, pt4.y, pt4.z );
			pgortho.line( pt1.x, pt1.y, pt1.z, pt4.x, pt4.y, pt4.z );
			pgortho.line( workv1.x, workv1.y, workv1.z, pt1.x, pt1.y, pt1.z );
			pgortho.line( workv1.x, workv1.y, workv1.z, pt2.x, pt2.y, pt2.z );
			pgortho.line( workv1.x, workv1.y, workv1.z, pt3.x, pt3.y, pt3.z );
			pgortho.line( workv1.x, workv1.y, workv1.z, pt4.x, pt4.y, pt4.z );

			pgortho.stroke( 255, 0, 0 );
			pgortho.line( workv1.x, workv1.y, workv1.z, workv1.x + 10 * cam_up.x, workv1.y, workv1.z );
			pgortho.stroke( 0, 255, 0 );
			pgortho.line( workv1.x, workv1.y, workv1.z, workv1.x, workv1.y + 10 * cam_up.y, workv1.z );
			pgortho.stroke( 0, 0, 255 );
			pgortho.line( workv1.x, workv1.y, workv1.z, workv1.x, workv1.y, workv1.z + 10 * cam_up.z );
			
		pgortho.popMatrix();
		pgortho.endDraw();
		
		//actual display
		background( 5 );
		image( pg3d, 0, 0 );
		image( pgortho, width - pgortho.width, height - pgortho.height );
		fill( 255 );
		text( frameRate, 10, height - 15 );
		
	}

	public void mouseReleased() {
	    orthodrag_trans = false;
	    orthodrag_rot = false;
	}
	
	public void mousePressed() {
		if ( mouseX >= width - pgortho.width && mouseY >= height - pgortho.height ) {
			if ( mouseButton == LEFT ) orthodrag_rot = true;
			else if ( mouseButton == RIGHT ) orthodrag_trans = true;
			lastmouse.set( mouseX, mouseY );
		}
	}
	
	public void mouseDragged() {
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
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "CameraOpengl" });
	}
	
}