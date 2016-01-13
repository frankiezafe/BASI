import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public class View implements PConstants {

	public static final float SCREEN_SIZE = 200.f;
	public static final int VIEW_FREE = 	0;
	public static final int VIEW_LEFT = 	1;
	public static final int VIEW_TOP = 		2;
	
	private static final PVector default_view_free_translation = new PVector( 0.5f,0.5f );
	private static final PVector default_view_free_rotation = new PVector( -HALF_PI * 0.18f, HALF_PI * 0.4f, 0 );
	private static final PVector default_view_left_translation = new PVector( 0.4f,0.5f );
	private static final PVector default_view_left_rotation = new PVector( 0, HALF_PI, 0 );
	private static final PVector default_view_top_translation = new PVector( 0.5f,0.4f );
	private static final PVector default_view_top_rotation = new PVector( -HALF_PI, 0, 0 );
	
	public static final int LEFT_CLICK = 	37;
	public static final int RIGHT_CLICK =	39;
	
	private PApplet parent;
	private PVector scale;
	private PGraphicsOpenGL pg;
	private String name;
	private PVector offset;
	private boolean translation_enabled;
	private boolean translation_dragged;
	private PVector translation;
	private boolean rotation_enabled;
	private boolean rotation_dragged;
	private PVector rotation;
	private PVector prev_relmouse;
	private PVector relmouse;
	private boolean over;
	private int lastrelease; // double-click
	
	private Camera cam;
	private float orthoratio;
	private int view_type;
	
	private Handler handler_dragged;
	private Handler[] handlers;
	private boolean camUpdated;
	
	public View( PApplet parent, String name, int x, int y, int width, int height ) {
		this.parent = parent;
		this.name = name;
		pg = (PGraphicsOpenGL) parent.createGraphics( width, height, P3D );
		offset = new PVector( x, y );
		orthoratio = SCREEN_SIZE * 0.25f / pg.width;
		translation_enabled = true;
		translation_dragged = false;
		translation = new PVector( width * 0.5f, height * 0.5f, 0 );
		scale = new PVector( width * 1.f / parent.width, height * 1.f / parent.height, width * 1.f / parent.width );
		rotation_enabled = true;
		rotation_dragged = false;
		rotation = new PVector( 0, 0, 0 );
		prev_relmouse = new PVector( 0, 0 );
		relmouse = new PVector( 0, 0 );
		over = false;
		camUpdated = false;
		lastrelease = -1;
		cam = null;
		handler_dragged = null;
		handlers = null;
		view_type = VIEW_FREE;
		reset();
		render();
	}
	
	public void setType( int vt) {
		switch( vt ) {
			case VIEW_FREE:
				rotation_enabled = true;
				translation_enabled = true;
				rotation.set( default_view_free_rotation );
				translation.set( default_view_free_translation );
				translation.mult( new PVector( pg.width, pg.height ) );
				view_type = vt;
				break;
			case VIEW_LEFT:
				rotation_enabled = false;
				translation_enabled = true;
				rotation.set( default_view_left_rotation );
				translation.set( default_view_left_translation );
				translation.mult( new PVector( pg.width, pg.height ) );
				view_type = vt;
				handlers = new Handler[ 3 ];
				handlers[ 0 ] = new Handler( new PVector( 0, 0 ), Handler.HANDLER_CAM_FRUSTUM_CENTER );
				handlers[ 1 ] = new Handler( new PVector( 0, 0 ), Handler.HANDLER_CAM_EYE_CENTER );
				handlers[ 2 ] = new Handler( new PVector( 0, 0 ), Handler.HANDLER_CAM_EYE );
				break;
			case VIEW_TOP:
				rotation_enabled = false;
				translation_enabled = true;
				rotation.set( default_view_top_rotation );
				translation.set( default_view_top_translation );
				translation.mult( new PVector( pg.width, pg.height ) );
				handlers = new Handler[ 1 ];
				handlers[ 0 ] = new Handler( new PVector( 30, 40 ), Handler.HANDLER_CAM_EYE );
				view_type = vt;
				break;
			default:
				System.err.println( "Unhandled view type!" );
				break;
		
		}
		render();
	}
	
	public void reset() {
		setType( view_type );
	}
	
	public void setCamera( Camera cam ) {
		this.cam = cam;
		// SCREEN_SIZE represent the whole window
		// to get the correct scale,  
		scale = new PVector( 
				SCREEN_SIZE / parent.width, 
				SCREEN_SIZE / parent.width, 
				SCREEN_SIZE / parent.width );
		handlers2cam();
	}
	
	private void renderAxis( float scale ) {	
		pg.strokeWeight( 2 );
		pg.stroke( 255, 0, 0 );
		pg.line( 0, 0, 0, 1 * scale, 0, 0 );
		pg.stroke( 0, 255, 0 );
		pg.line( 0, 0, 0, 0, 1 * scale, 0 );
		pg.stroke( 0, 0, 255 );
		pg.line( 0, 0, 0, 0, 0, 1 * scale );
		pg.strokeWeight( 1 );
	}
	
	public void renderBegin() {
		handlers2cam();
		pg.beginDraw();
		if (over) pg.background( 100 );
		else pg.background( 190, 120 );
		pg.ortho();
		pg.pushMatrix();
		pg.translate( translation.x, translation.y, translation.z );
		pg.rotateX( rotation.x );
		pg.rotateY( rotation.y );
		pg.rotateZ( rotation.z );
		pg.hint( ENABLE_DEPTH_TEST );
	}
	
	public void renderEnd() {
		if ( cam != null ) drawCam();
		pg.hint( DISABLE_DEPTH_TEST );
		renderAxis( 30 );
		pg.popMatrix();
		if ( handlers != null ) {
			for ( int i = 0; i < handlers.length; ++i ) handlers[ i ].draw( pg );
		}
		pg.fill( 255 );
		pg.text( name , 5, pg.height - 5 );
		pg.endDraw();
	}
	
	public void render() {
		renderBegin();
		renderEnd();
	}
	
	public void mouseMoved( float x, float y ) {
		if ( x > offset.x && y > offset.y && x <= offset.x + pg.width && y <= offset.y + pg.height ) {
			relmouse.set( x - offset.x, y - offset.y );
			over = true;
			if ( handlers != null ) {
				for ( int i = 0; i < handlers.length; ++i ) { 
					if ( handlers[ i ].hover( relmouse ) ) break;
				}
			}
		} else {
			over = false;
		}
	}
	
	public void mousePressed( float x, float y, int button ) {
		if ( !over ) return;
		boolean keepon = true;
		if ( handlers != null ) {
			for ( int i = 0; i < handlers.length; ++i ) { 
				if ( handlers[ i ].click( relmouse ) ) {
					handler_dragged = handlers[ i ];
					keepon = false;
					break;
				}
			}
		}
		if ( !keepon ) return;
		if ( button == LEFT_CLICK && parent.millis() - lastrelease < 200 ) reset();
		if ( button == LEFT_CLICK && rotation_enabled ) {
			prev_relmouse.set( x - offset.x, y - offset.y );
			rotation_dragged = true;
		} else if ( button == RIGHT_CLICK && translation_enabled ) {
			prev_relmouse.set( x - offset.x, y - offset.y );
			translation_dragged = true;
		}
	}
	
	public void mouseReleased( float x, float y, int button ) {
		if ( handler_dragged != null ) { 
			handler_dragged.release();
			handler_dragged = null;
			camUpdated = false;
		}
		if ( translation_dragged ) translation_dragged = false;
		if ( rotation_dragged ) rotation_dragged = false;
		if ( over && button == LEFT_CLICK ) {
			lastrelease = parent.millis();
		}
	}
	
	public void mouseDragged( float x, float y, int button ) {
		if ( !over ) return;
		if ( handler_dragged != null ) {
			relmouse.set( x - offset.x, y - offset.y );
			if ( handler_dragged.getType() == Handler.HANDLER_CAM_FRUSTUM_CENTER ) handler_dragged.moveY( relmouse.y );
			else if ( handler_dragged.getType() == Handler.HANDLER_CAM_EYE_CENTER ) handler_dragged.moveY( relmouse.y );
			else handler_dragged.move( relmouse );
			prev_relmouse.set( relmouse );
			cam2handler();
		}
		if ( rotation_dragged ) {
			relmouse.set( x - offset.x, y - offset.y );
			rotation.set(
					rotation.x - ( relmouse.y - prev_relmouse.y ) * 0.03f, 
					rotation.y + ( relmouse.x - prev_relmouse.x ) * 0.03f, 
					rotation.z
			);
			prev_relmouse.set( relmouse );
		}
		if ( translation_dragged ) {
			relmouse.set( x - offset.x, y - offset.y );
			translation.set( translation.x + relmouse.x - prev_relmouse.x, translation.y + relmouse.y - prev_relmouse.y );
			prev_relmouse.set( relmouse );
		}
	}
	
	// HANDLERS PROCESSING
	private void cam2handler() {
		if ( handler_dragged == null || cam == null ) return;
		float r = parent.width / SCREEN_SIZE;
		switch( handler_dragged.getType() ) {
			case Handler.HANDLER_CAM_EYE_CENTER:
			{
				switch( view_type ) {
					case VIEW_LEFT:
						cam.eye_center.y = ( handler_dragged.y - translation.y ) * r;
						camUpdated = true;
						cam.render();
						break;
				}
			}
			break;
			case Handler.HANDLER_CAM_FRUSTUM_CENTER:
				{
					switch( view_type ) {
						case VIEW_LEFT:
							cam.center_offset.y = ( handler_dragged.y - translation.y ) * r;
							camUpdated = true;
							cam.render();
							break;
					}
				}
				break;
			case Handler.HANDLER_CAM_EYE:
			{
				switch( view_type ) {
					case VIEW_TOP:
						cam.eye.x = ( handler_dragged.x - translation.x ) * r;
						cam.eye.z = ( handler_dragged.y - translation.y ) * r;
						camUpdated = true;
						cam.render();
						break;
					case VIEW_LEFT:
						cam.eye.y = ( handler_dragged.y - translation.y ) * r;
						cam.eye.z = ( handler_dragged.x - translation.x ) * r;
						camUpdated = true;
						cam.render();
						break;
				}
			}
			break;
		}
	}
	
	private void handlers2cam() {
		if ( handlers == null || cam == null ) return;
		float r = SCREEN_SIZE / parent.width;
		for ( int i = 0; i < handlers.length; ++i ) {
			Handler h = handlers[ i ];
			if ( h == handler_dragged ) continue;
			// switch²
			switch( h.getType() ) {
				case Handler.HANDLER_CAM_EYE_CENTER:
				{
					switch( view_type ) {
						case VIEW_LEFT:
							h.x = translation.x + cam.eye_center.z * r;
							h.y = translation.y + cam.eye_center.y * r;
							break;
					}
				}
				break;
				case Handler.HANDLER_CAM_FRUSTUM_CENTER:
					{
						switch( view_type ) {
							case VIEW_LEFT:
								h.x = translation.x + cam.center_offset.z * r;
								h.y = translation.y + cam.center_offset.y * r;
								break;
						}
					}
					break;
				case Handler.HANDLER_CAM_EYE:
				{
					switch( view_type ) {
						case VIEW_TOP:
							h.x = translation.x + cam.eye.x * r;
							h.y = translation.y + cam.eye.z * r;
							break;
						case VIEW_LEFT:
							h.x = translation.x + cam.eye.z * r;
							h.y = translation.y + cam.eye.y * r;
							break;
					}
				}
				break;
			}
		}
	}
	
	public void drawCam() {
		
		PVector _eye = new PVector( cam.eye );
		PVector _eyer = new PVector( cam.real_eye );
		PVector _eyec = new PVector( cam.eye_center );
		PVector _frustc = new PVector( cam.center_offset );
		_eye.mult( orthoratio );
		_eyer.mult( orthoratio );
		_eyec.mult( orthoratio );
		_frustc.mult( orthoratio );

		// lines between center & eyes
		pg.stroke( 255, 50 );
		pg.line( _eyer, _eyec );
		pg.stroke( 0, 255, 255 );
		pg.line( _eye, _eyer );
		
		// axis of the screen in camera
		_eyec.sub( _eye );
		_eyec.normalize();
		PVector _up = new PVector( cam.up );
		_up.normalize();
		PVector _camX = _eyec.cross( _up );
		_camX.normalize();
		PVector _camY = _camX.cross( _eyec );
		_camY.normalize();

		// reset eye center
		_eyec.set( cam.eye_center );
		_eyec.mult( orthoratio );
		_eyec.add( PVector.mult( _camY, _frustc.y ) );
		
		// displayable axis
		PVector axisx = new PVector( _camX );
		axisx.mult( SCREEN_SIZE * 0.5f );
		PVector axisy = new PVector( _camY );
		axisy.mult( SCREEN_SIZE / cam.aspect * 0.5f );
		
		// screen borders
		PVector[] borders = new PVector[ 4 ];
		borders[ 0 ] = PVector.add( PVector.mult( axisx, -1 ), PVector.mult( axisy, -1 ) );
		borders[ 1 ] = PVector.add( PVector.mult( axisx, 1 ), PVector.mult( axisy, -1 ) );
		borders[ 2 ] = PVector.add( PVector.mult( axisx, 1 ), PVector.mult( axisy, 1 ) );
		borders[ 3 ] = PVector.add( PVector.mult( axisx, -1 ), PVector.mult( axisy, 1 ) );
		PVector diffeer = new PVector( _eyer );
		diffeer.sub( _eye );
		diffeer.add( _eyec );
		for ( int i = 0; i < 4; ++i ) { 
			borders[ i ].add( diffeer );
		}
		
		pg.stroke( 255, 50 );
		pg.line( borders[ 0 ], borders[ 1 ] );
		pg.line( borders[ 1 ], borders[ 2 ] );
		pg.line( borders[ 2 ], borders[ 3 ] );
		pg.line( borders[ 3 ], borders[ 0 ] );
		pg.line( borders[ 0 ], _eyer );
		pg.line( borders[ 1 ], _eyer );
		pg.line( borders[ 2 ], _eyer );
		pg.line( borders[ 3 ], _eyer );
		
		axisx.add( diffeer );
		pg.stroke( 255, 0, 0 );
		pg.line( diffeer, axisx );
		axisy.add( diffeer );
		pg.stroke( 0, 255, 0 );
		pg.line( diffeer, axisy );
		
		
//		drawCam( cam.real_eye, 	true );
//		drawCam( cam.eye, 		false );
	}
	
	private void drawCam( PVector eye, boolean real ) {
		
		// projection plane
		float roty = parent.atan2( eye.x, eye.z );
		PVector _eye = new PVector( eye );
		PVector _eyec = new PVector( cam.eye_center );
		PVector _frustc = new PVector( cam.center_offset );
		_eye.mult( orthoratio );
		_eyec.mult( orthoratio );
		_frustc.mult( orthoratio );
		
		if ( !real ) {
			pg.stroke( 255, 50 );
			pg.line( _eye, _eyec );
		} else {
			PVector _reye = new PVector( cam.eye );
			_reye.mult( orthoratio );
			pg.stroke( 0, 255, 255 );
			pg.line( _eye, _reye );
		}
		
		// axis of the screen in camera
		_eyec.sub( _eye );
		_eyec.normalize();
		PVector _up = new PVector( cam.up );
		_up.normalize();
		PVector _camX = _eyec.cross( _up );
		_camX.normalize();
		PVector _camY = _camX.cross( _eyec );
		_camY.normalize();

		// reset eye center
		_eyec.set( cam.eye_center );
		_eyec.mult( orthoratio );
		_eyec.add( PVector.mult( _camY, _frustc.y ) );
		

		PVector axisx = new PVector( _camX );
		axisx.mult( SCREEN_SIZE * 0.5f );
		PVector axisy = new PVector( _camY );
		axisy.mult( SCREEN_SIZE / cam.aspect * 0.5f );
		
		// screen borders
		PVector[] borders = new PVector[ 4 ];
		borders[ 0 ] = PVector.add( PVector.mult( axisx, -1 ), PVector.mult( axisy, -1 ) );
		borders[ 1 ] = PVector.add( PVector.mult( axisx, 1 ), PVector.mult( axisy, -1 ) );
		borders[ 2 ] = PVector.add( PVector.mult( axisx, 1 ), PVector.mult( axisy, 1 ) );
		borders[ 3 ] = PVector.add( PVector.mult( axisx, -1 ), PVector.mult( axisy, 1 ) );
		for ( int i = 0; i < 4; ++i ) borders[ i ].add( _eyec );
		
		if ( !real ) {
			pg.stroke( 255, 50 );
			pg.line( borders[ 0 ], borders[ 1 ] );
			pg.line( borders[ 1 ], borders[ 2 ] );
			pg.line( borders[ 2 ], borders[ 3 ] );
			pg.line( borders[ 3 ], borders[ 0 ] );
			pg.line( borders[ 0 ], _eye );
			pg.line( borders[ 1 ], _eye );
			pg.line( borders[ 2 ], _eye );
			pg.line( borders[ 3 ], _eye );
		} else { 
			pg.stroke( 255, 50 );
			pg.line( borders[ 0 ], borders[ 1 ] );
			pg.line( borders[ 1 ], borders[ 2 ] );
			pg.line( borders[ 2 ], borders[ 3 ] );
			pg.line( borders[ 3 ], borders[ 0 ] );
			pg.line( borders[ 0 ], _eye );
			pg.line( borders[ 1 ], _eye );
			pg.line( borders[ 2 ], _eye );
			pg.line( borders[ 3 ], _eye );
			for ( int i = 0; i < 4; ++i ) { 
				borders[ i ].sub( _eye );
				borders[ i ].mult( 2 );
				borders[ i ].add( _eye );
			}
			pg.beginShape( LINES );
			pg.stroke( 0, 255, 255 );
			pg.vertex( _eye );
			pg.stroke( 0, 255, 255, 0 );
			pg.vertex( borders[ 0 ] );
			pg.stroke( 0, 255, 255 );
			pg.vertex( _eye );
			pg.stroke( 0, 255, 255, 0 );
			pg.vertex( borders[ 1 ] );
			pg.stroke( 0, 255, 255 );
			pg.vertex( _eye );
			pg.stroke( 0, 255, 255, 0 );
			pg.vertex( borders[ 2 ] );
			pg.stroke( 0, 255, 255 );
			pg.vertex( _eye );
			pg.stroke( 0, 255, 255, 0 );
			pg.vertex( borders[ 3 ] );
			pg.endShape();
			pg.stroke( 255, 50 );
			pg.line( _eyec, _eye );
		}
		
		// display axis
		if ( !real ) {
			axisx.add( _eyec );
			pg.stroke( 255, 0, 0 );
			pg.line( _eyec, axisx );
			axisy.add( _eyec );
			pg.stroke( 0, 255, 0 );
			pg.line( _eyec, axisy );
		}
		
	}
	
	public void draw() {
		parent.fill( 255 );
		parent.image( pg, offset.x, offset.y );
		if ( over ) {
			parent.noFill();
			parent.stroke( 255 );
			parent.rect( offset.x, offset.y, pg.width, pg.height );
		}
	}

	public boolean isCamUpdated() {
		return camUpdated;
	}

	public PGraphicsOpenGL getPg() {
		return pg;
	}
	
	public PVector getScale() {
		return scale;
	}
	
}
