package ground;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;

public class Ground implements PConstants{
	
	public static final float kinectfovH = 58.500004f / 180 * PI; 	//radian
	public static final float kinectfovV = 45.6f / 180 * PI;		//radian
	
	private PApplet parent;
	private PImage controleimg;
//	private PGraphics tex;
	private GPoint[] points;
	private GFace[] faces;
	private int cols;
	private int rows;
	SimpleOpenNI context;
	//doit avoir le m�me nom que la classe pour le constructeur
	
	public Ground(PApplet parent){ //constructeur
		
		this.parent = parent;
		this.controleimg = null;
		points = null;
		faces = null;
//		tex = null;
		cols = 0;
		rows = 0;
		System.out.println("nouveau ground" + parent);
		context = new SimpleOpenNI(parent);
		// context.setMirror(false);
		context.enableDepth();
		// context.enableRGB();
	}
	
	
	public void init( int columns, int rows ){
		
		controleimg = context.depthImage();
		this.cols = columns;
		this.rows = rows;
		points = new GPoint[ (columns + 1) * (rows + 1)];
		faces = new GFace[ columns * rows *2 ];
		
		float cellwidth = (controleimg.width - 1) * 1.f / columns;
		float cellheight = (controleimg.height - 1) * 1.f / rows;
		
		controleimg.loadPixels();
		int i = 0;
		int ip = 0;
		for (float y = 0; y < controleimg.height; y += cellheight){ //permet de faire la colonne y suivante apr�s la ligne x soit fini
			for (float x = 0; x < controleimg.width; x += cellwidth ){
				i = (int) (x + PApplet.floor(y) * controleimg.width);
				//System.out.println(x + " / " + y + " / " + ip + " / >>" + i);
				
				float px = x;
				float py = y;
				float pz = parent.red(controleimg.pixels[i]);
				
				// on met tous les points à 5m
				// à 5m, il faut que je trouve la position x et y correspondante
				// h 58.500004°
				// v 45.6°
				
				// x et y en relatif par rapport au centre de l'image [ -0.5, 0.5 ]
				float relx = ( x - controleimg.width * 0.5f ) / controleimg.width;
				float rely = ( y - controleimg.height * 0.5f ) / controleimg.height;
				// angle relatif
				float anglh = relx * kinectfovH;
				float anglv = rely * kinectfovV;
				
				// on veut Z à 5000
				float worldx = (float) Math.sin( anglh ) * 5000;
				float worldy = -(float) Math.sin( anglv ) * 5000;
				float worldz = 5000;
				PVector worldp = new PVector( worldx, worldy, worldz );
				worldp.normalize();
				
				points[ip] = new GPoint( 
					worldx, worldy, worldz, 
					worldp.x, worldp.y, worldp.z
				);
				points[ip].setNormUV( 1 - 0.5f + relx, 0.5f + rely );
				points[ip].renderUV( controleimg.width * 1.f, controleimg.height * 1.f );
				
				ip++;
				
			}
			
		}
		int f = 0;
		int pcols = (columns + 1);
		for( int r = 0; r < rows; ++r){
			for( int c = 0; c < columns; ++c){
				GPoint topleft = points[c+ r *pcols];
				GPoint topright = points[(c +1)+ r *pcols];
				GPoint bottomright = points[(c + 1)+ (r + 1) *pcols];
				GPoint bottomleft = points[c + (r + 1) *pcols];
				faces[ f ] = new GFace(topleft,topright, bottomleft );
				f++;
				faces[ f ] = new GFace(topright, bottomright,bottomleft );
				f++;
			}
		}
		
	}
	
	public void draw() {
		
		context.update();
		controleimg = context.depthImage();
//		controleimg.loadPixels();
		PVector[] cloud = context.depthMapRealWorld();
		
		for ( int i = 0; i < points.length; ++i) {
			int pid = (int)( points[i].u ) + (controleimg.width)* (int)( points[i].v );
			PVector realp = cloud[ pid ];
			if ( realp.z != 0 ) {
				points[i].set( realp );
			} else {
				points[i].reset();
			}
		}
		
		if (controleimg != null) {
			parent.pushMatrix();
			parent.translate(-controleimg.width * 0.5f, -controleimg.height *0.5f, 0);
			parent.fill(255);
//			parent.image(controleimg, 0, 0);
		
		if (points != null){
//			parent.strokeWeight(3);
//			parent.stroke( 0, 255, 0);
			parent.noStroke();
			for ( int i = 0; i < points.length; ++i){
				if (points[i] == null) { 
					break;
					}
				parent.point( points[ i ].x, points[ i ].y, points[ i ].z);
			}
			
		}
		if ( faces != null){
			

			parent.noStroke();
			parent.fill(255);
//			parent.noFill();
//			parent.stroke(255,255,255);
			parent.beginShape( TRIANGLES);
			if ( controleimg != null ) parent.texture( controleimg );
			for (int i =0; i < faces.length; ++i){
				GFace f = faces[ i ];
//				parent.fill( parent.abs(f.normal.x) *255, parent.abs(f.normal.y) *255, parent.abs(f.normal.z) *255);
				parent.vertex( f.pt1.x, f.pt1.y, f.pt1.z, f.pt1.u, f.pt1.v);
				parent.vertex( f.pt2.x, f.pt2.y, f.pt2.z, f.pt2.u, f.pt2.v);
				parent.vertex( f.pt3.x, f.pt3.y, f.pt3.z, f.pt3.u, f.pt3.v);
			}
			parent.endShape();
			
		}
		parent.popMatrix();
	}
	}
	
	
	public PImage getMap() {
		return controleimg;
	}


	public void print(){
		
		System.out.println(this);
		System.out.println(this.parent);
		
	}

	public void setHeight( float h) {
		
		for (int i=0; i< points.length; ++i){
			points [i].setZ(h);
		}
	}
	public void setWidth( float h) {
		
		for (int i=0; i< points.length; ++i){
			points [i].setX(h);
		}
	}
	
	private void updateFaces(){
		
		for (int i = 0; i < faces.length ; ++i){
			faces[ i ].update();
		}
	}
}
