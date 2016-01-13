import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public class Handler extends PVector {

	public static final int HANDLER_CAM_FRUSTUM_CENTER = 	0;
	public static final int HANDLER_CAM_EYE = 				1;
	public static final int HANDLER_CAM_EYE_CENTER = 		2;
	
	private static final float HANDLER_RADIUS = 	7.f;
	
	private float radius;
	private int type;
	private boolean hover;
	private boolean dragged;
	private PVector click_offset;
	
	public Handler( PVector pos, int type ) {
		set( pos );
		this.type = type;
		hover = false;
		dragged = false;
		radius = HANDLER_RADIUS;
		click_offset = new PVector();
	}
	
	private boolean inside( PVector m ) {
		return dist( m ) <= radius;
	}
	
	public boolean hover( PVector m ) {
		if ( inside( m )  ) {
			hover = true;
		} else {
			hover = false;
		}
		dragged = false;
		return hover;
	}
	
	public boolean click( PVector m ) {
		if ( inside( m )  ) {
			click_offset.set( x - m.x, y - m.y );
			dragged = true;
		} else {
			dragged = false;
		}
		return dragged;
	}
	
	public void release() {
		hover = false;
		dragged = false;
	}
	
	public void move( PVector m ) {
		this.x = m.x + click_offset.x;
		this.y = m.y + click_offset.y;
	}
	
	public void moveX( float x ) {
		this.x = x + click_offset.x;
	}
	
	public void moveY( float y ) {
		this.y = y + click_offset.y;
	}
	
	public void draw( PGraphicsOpenGL pg ) {
		pg.fill( 0, 180 );
		pg.noStroke();
		pg.ellipse( x, y, radius + 3, radius + 3 );
		if ( dragged ) {
			pg.noStroke();
			pg.fill( 255,0,0 );
		} else if ( hover ) {
			pg.strokeWeight( 2 );
			pg.noFill();
			pg.stroke( 255 );
		} else {
			pg.noFill();
			pg.stroke( 255, 190 );
		}
		pg.ellipse( x, y, radius, radius );
		pg.strokeWeight( 1 );
	}

	public float getRadius() {
		return radius;
	}

	public int getType() {
		return type;
	}

	public boolean isHover() {
		return hover;
	}

	public boolean isDragged() {
		return dragged;
	}
	
}
