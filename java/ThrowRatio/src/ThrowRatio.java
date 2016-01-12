import processing.controlP5.ControlP5;
import processing.controlP5.Toggle;
import processing.core.*;
import java.lang.Enum;

public class ThrowRatio extends PApplet {
	
	public static final int RATIO_4_3 = 0;
	public static final int RATIO_16_9 = 1;
	public static final int RATIO_16_10 = 2;
	
	private float distance;
	private float throwratio;
	private float angle;
	private int imageratio;
	
	private ControlP5 ui;
	private Toggle b4_3;
	private Toggle b16_9;
	private Toggle b16_10;
	
	public void settings() {
		size( 800, 600, P3D );
	}
	
	public void setup() {
		
		distance = 100;
		throwratio = 0.63f;
		angle = 0;
		imageratio = RATIO_16_9;

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
		
		ui = new ControlP5( this );
		ui.addSlider( "distance", 10, 2000, 10, 50, 300, 10 );
		ui.addSlider( "throwratio", 0.1f, 2.5f, 10, 70, 300, 10 );
		b4_3 = ui.addToggle("4:3", 10, 90, 10, 10);
		b16_9 = ui.addToggle("16:9", 40, 90, 10, 10);
		b16_10 = ui.addToggle("16:10", 70, 90, 10, 10);
		
		b16_9.setValue( true );
		
	}
	
	public void draw() {
		
		ortho();
		
		if ( b4_3.getBooleanValue() && imageratio != RATIO_4_3 ) {
			imageratio = RATIO_4_3;
			b16_9.setValue( false );
			b16_10.setValue( false );
		}
		if ( b16_9.getBooleanValue() && imageratio != RATIO_16_9 ) {
			imageratio = RATIO_16_9;
			b4_3.setValue( false );
			b16_10.setValue( false );
		}
		if ( b16_10.getBooleanValue() && imageratio != RATIO_16_10 ) {
			imageratio = RATIO_16_10;
			b4_3.setValue( false );
			b16_9.setValue( false );
		}
		
		float sideA = distance;
		float sideB = sideA / throwratio * 0.5f;
		float sideC = 0;
		switch( imageratio ) {
			case RATIO_4_3:
				sideC = sideB / 4 * 3;
				break;
			case RATIO_16_9:
				sideC = sideB / 16 * 9;
				break;
			case RATIO_16_10:
				sideC = sideB / 16 * 10;
				break;
			default:
				break;
		}
		float sideH = sqrt( (sideA*sideA) + (sideB*sideB) );
		angle = asin( sideB / sideH );
		
		background( 5 );
		
		pushMatrix();
		translate( 150, 200 );
		
		pushMatrix();
		rotateY( PI * -0.03f );
		rotateX( PI * 0.25f );
		rotateZ( PI * 0.2f );
		
		noFill();
		stroke( 255, 90 );
		strokeWeight( 2 );
		line( 0,0,0, cos( angle ) * 500, sin( angle ) * 500, 0 );
		line( 0,0,0, 500, 0, 0 );
		line( 0,0,0, cos( -angle ) * 500, sin( -angle ) * 500, 0 );
		
		pushMatrix();
		translate( sideA, 0, 0 );
		rotateY( PI * 0.5f );
		rect( -sideC,-sideB, sideC * 2, sideB * 2 );
		popMatrix();
		
		stroke( 255,10,0 );
		strokeWeight( 3 );
		line( 0,0, 0, sideA, 0, 0 );
		line( sideA,-sideB,0, sideA,sideB,0 ); // largeur
		line( sideA,0,-sideC, sideA,0,sideC ); // hauteur
		
		popMatrix();
		
		popMatrix();
		
		text( frameRate, 10,25 );
		text( ( ( (int)( (angle * 2) * 100 ) ) * 0.01d ) + " rad", 10, 140 );
		text( "largeur: " + (sideB * 2) + "cm", 10, 155 );
		text( "hauteur: " + (sideC * 2) + "cm", 10, 170 );
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "ThrowRatio" });
	}
	
}