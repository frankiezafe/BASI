import processing.controlP5.ControlP5;
import processing.core.*;

public class ThrowRatio extends PApplet {

	private float distance;
	private float throwratio;
	private float angle;
	
	private ControlP5 ui;
	
	public void settings() {
		size( 800, 600 );
	}
	
	public void setup() {
		
		distance = 100;
		throwratio = 0.63f;

// https://en.wikipedia.org/wiki/Triangle
//
//		   1
//		  /|
//	H    / | A
//		/  |
//	  3  B  2
//
// LOGIQUE:		
//
//		float sideA = 130;
//		float sideB = sideA / throwratio * 0.5f;
//		// a² + b² = c²
//		float sideH = sqrt( (sideA*sideA) + (sideB*sideB) );
//		
//		// angle = arcsin( cote oppose/hypothenuse )
//		// nous, on cherche 1
//		angle = asin( sideB / sideH );
		
		angle = 0;
		
		ui = new ControlP5( this );
		ui.addSlider( "distance", 10, 2000, 10, 50, 300, 10 );
		ui.addSlider( "throwratio", 0.1f, 2.5f, 10, 70, 300, 10 );
		
	}
	
	public void draw() {
		
		float sideA = distance;
		float sideB = sideA / throwratio * 0.5f;
		float sideH = sqrt( (sideA*sideA) + (sideB*sideB) );
		angle = asin( sideB / sideH );
		
		background( 5 );
		
		pushMatrix();
		translate( 50, 300 );
		
		stroke( 255 );
		strokeWeight( 1 );
		line( 0,0, cos( angle ) * 500, sin( angle ) * 500 );
		line( 0,0, 500, 0 );
		line( 0,0, cos( -angle ) * 500, sin( -angle ) * 500 );
		text( ( ( (int)( (angle * 2) * 100 ) ) * 0.01d ) + " rad", 5, -10 );
		
		stroke( 255,10,0 );
		strokeWeight( 2 );
		line( 0,0, sideA, 0 );
		line( sideA,-sideB, sideA, sideB );
		text( (sideB * 2) + "cm", sideA + 5, -10 );
		
		popMatrix();
		
		text( frameRate, 10,25 );
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "ThrowRatio" });
	}
	
}