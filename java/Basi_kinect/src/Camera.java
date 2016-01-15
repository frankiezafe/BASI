import java.io.File;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.data.XML;
import processing.opengl.PGraphicsOpenGL;

public class Camera implements PConstants {

	public float fov;
	// frustum
	public float near;
	public float far;
	public float xmin;
	public float xmax;
	public float ymin;
	public float ymax;
	public float aspect;

	// position
	public PVector eye; 			// request position
	public PVector eye_center; 		// with eye, defines the orientation of the camera
	public PVector real_eye; 		// position with FOV applied
	public PVector center_offset;	// frustum offset in pixel coordinates
	public PVector frustum_offset;	// rendered frustum offset
	public PVector up;				// UP axis of the camera
	
	public PVector offset;
	
	private PApplet parent;
	
	public Camera( PApplet parent ) {
		this.parent = parent;
		eye = 				new PVector();
		eye_center = 		new PVector();
	    center_offset = 	new PVector();
	    real_eye = 			new PVector();
	    frustum_offset =	new PVector();
	    up = 				new PVector();
	    offset =			new PVector();
	    reset();
	}
	
	public boolean load( String path ) {
		if ( ! new File( path ).exists() ) return false;
		XML xml;
		xml = parent.loadXML( path );
		XML xchild;
		xchild = xml.getChild( "eye" );
		if ( xchild != null ) {
			eye.x = xchild.getFloat( "x" );
			eye.y = xchild.getFloat( "y" );
			eye.z = xchild.getFloat( "z" );
			eye_center.x = xchild.getFloat( "centerx" );
			eye_center.y = xchild.getFloat( "centery" );
			eye_center.z = xchild.getFloat( "centerz" );
		}
		xchild = xml.getChild( "frustum" );
		if ( xchild != null ) {
			center_offset.x = xchild.getFloat( "x" );
			center_offset.y = xchild.getFloat( "y" );
			center_offset.z = xchild.getFloat( "z" );
		}
		xchild = xml.getChild( "up" );
		if ( xchild != null ) {
			up.x = xchild.getFloat( "x" );
			up.y = xchild.getFloat( "y" );
			up.z = xchild.getFloat( "z" );
		}
		xchild = xml.getChild( "fieldofview" );
		if ( xchild != null ) {
			fov = xchild.getFloat( "fov" );
			near = xchild.getFloat( "near" );
			far = xchild.getFloat( "far" );
		}
		xchild = xml.getChild( "offset" );
		if ( xchild != null ) {
			offset.x = xchild.getFloat( "x" );
			offset.y = xchild.getFloat( "y" );
			offset.z = xchild.getFloat( "z" );
		}
		return true;
	}
	
	public boolean save( String path ) {
		XML xml = new XML( "camera" );
		XML xchild;
		xchild = xml.addChild( "eye" );
		xchild.setFloat( "x" , eye.x );
		xchild.setFloat( "y" , eye.y );
		xchild.setFloat( "z" , eye.z );
		xchild.setFloat( "centerx" , eye_center.x );
		xchild.setFloat( "centery" , eye_center.y );
		xchild.setFloat( "centerz" , eye_center.z );
		xchild = xml.addChild( "frustum" );
		xchild.setFloat( "x" , center_offset.x );
		xchild.setFloat( "y" , center_offset.y );
		xchild.setFloat( "z" , center_offset.z );
		xchild = xml.addChild( "up" );
		xchild.setFloat( "x" , up.x );
		xchild.setFloat( "y" , up.y );
		xchild.setFloat( "z" , up.z );
		xchild = xml.addChild( "fieldofview" );
		xchild.setFloat( "fov" , fov );
		xchild.setFloat( "near" , near );
		xchild.setFloat( "far" , far );
		xchild = xml.addChild( "offset" );
		xchild.setFloat( "x" , offset.x );
		xchild.setFloat( "y" , offset.y );
		xchild.setFloat( "z" , offset.z );
		return xml.save( new File( path ) );
	}
	
	public void reset() {
		fov = 58 * DEG_TO_RAD;
	    aspect = (float) parent.width / (float) parent.height;
	    eye.set( 0, 0, 600 );
	    eye_center.set( 0, 0, 0 );
	    center_offset.set( 0, 0, 0 );
	    up.set( 0, 1, 0 ); //rotation de la caméra
	    near = 1;
	    far = 10000;
	    render();
	}
	
	public void render() {
		
		float t = (float) Math.tan( fov );
		float l = 0;
		
		// eye always point to 0,eye.y,0 -> physical view axis
		real_eye.set( eye );
		real_eye.x -= eye_center.x;
		real_eye.y -= eye_center.y;
		real_eye.z -= eye_center.z;
		l = real_eye.mag();
		real_eye.normalize();
		real_eye.mult( l / t );
		real_eye.add( eye_center );
		
		ymax = near * t * 0.5f;
	    ymin = -ymax;
	    xmin = ymin * aspect;
	    xmax = ymax * aspect;
	    
	    // center influence frustum!
	    float w = xmax - xmin;
	    float h = ymax - ymin;
	    float r = w / parent.width;
	    frustum_offset.set( center_offset );
	    frustum_offset.mult( r );
	    
	    xmin -= frustum_offset.x;
	    xmax -= frustum_offset.x;
	    ymin -= frustum_offset.y;
	    ymax -= frustum_offset.y;
	    
	}
	
	public void apply( PGraphicsOpenGL pg ) {
		pg.frustum( xmin, xmax, ymin, ymax, near, far );
		pg.camera(
			real_eye.x, real_eye.y, real_eye.z,
			eye_center.x, eye_center.y, eye_center.y,
			up.x, up.y, up.z
		);
		pg.updateProjmodelview();
	}
	
	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
	}

	public float getNear() {
		return near;
	}

	public void setNear(float near) {
		this.near = near;
	}

	public float getFar() {
		return far;
	}

	public void setFar(float far) {
		this.far = far;
	}

	public float getAspect() {
		return aspect;
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
	}

	public PVector getEye() {
		return eye;
	}

	public PVector getCenter() {
		return center_offset;
	}

	public PVector getRealeye() {
		return real_eye;
	}

	public PVector getRealcenter() {
		return frustum_offset;
	}
	
}
