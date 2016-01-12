package ground;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;

public class Ground implements PConstants{
	
	private PApplet parent;
	private PImage map;
	private PGraphics tex;
	private GPoint[] points;
	private GFace[] faces;
	private int cols;
	private int rows;
	SimpleOpenNI context;
	//doit avoir le même nom que la classe pour le constructeur
	public Ground(PApplet parent){ //constructeur
		
		this.parent = parent;
		this.map = null;
		points = null;
		faces = null;
		tex = null;
		cols = 0;
		rows = 0;
		System.out.println("nouveau ground" + parent);
		context = new SimpleOpenNI(parent);
		// context.setMirror(false);
		context.enableDepth();
		// context.enableRGB();
	}
	
	
	public void init(String path, int columns, int rows){
//		map = parent.loadImage(path); 
		map = context.depthImage();
		this.cols = columns;
		this.rows = rows;
		points = new GPoint[ (columns + 1) * (rows + 1)];
		faces = new GFace[ columns * rows *2 ];
		
		float cellwidth = (map.width - 1) * 1.f / columns;
		float cellheight = (map.height - 1) * 1.f / rows;
		
		map.loadPixels();
		int i = 0;
		int ip = 0;
		for (float y = 0; y < map.height; y += cellheight){ //permet de faire la colonne y suivante après la ligne x soit fini
			for (float x = 0; x < map.width; x += cellwidth ){
				i = (int) (x + PApplet.floor(y) * map.width);
				//System.out.println(x + " / " + y + " / " + ip + " / >>" + i);
				
				float px = x;
				float py = y;
				float pz = parent.red(map.pixels[i]);
				
				points[ip] = new GPoint( px, py, pz,
						px / map.width, py /map.height, pz / 255);
				ip++;
				
			}
			
		}
		//map.updatePixels();
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
	
	public void loadTexture (String path1, String path2) {
		PImage im1 = parent.loadImage(path1);
		PImage im2 = parent.loadImage(path2);
		
		PGraphics pg1 = parent.createGraphics(512,512);
		pg1.beginDraw();
		pg1.image(im1, 0, 0, 512, 512);
		pg1.endDraw();
		PGraphics pg2 = parent.createGraphics(512,512);
		pg2.beginDraw();
		pg2.image(im2, 0, 0, 512, 512);
		pg2.endDraw();
		
		float zswitch = 0.50f;
		
		tex = parent.createGraphics(512, 512, P3D);
		for ( int i= 0; i<points.length; i++){
			points[i].setUV(tex.width, tex.height);
		}
		tex.beginDraw();
		tex.background(255, 0, 0);
		tex.noStroke();
		tex.fill(255);
		tex.beginShape( TRIANGLES);
		tex.texture(pg1);
		for (int i =0; i < faces.length; ++i){
			GFace f = faces[ i ];
			if (f.normal.z > zswitch){
				tex.vertex( f.pt1.u, f.pt1.v, 0,f.pt1.u, f.pt1.v);
				tex.vertex( f.pt2.u, f.pt2.v, 0,f.pt2.u, f.pt2.v);
				tex.vertex( f.pt3.u, f.pt3.v, 0,f.pt3.u, f.pt3.v);
			}
			
		}
		tex.endShape();
		tex.beginShape( TRIANGLES);
		tex.texture(pg2);
		for (int i =0; i < faces.length; ++i){
			GFace f = faces[ i ];
			if (f.normal.z <= zswitch){
				tex.vertex( f.pt1.u, f.pt1.v, 0,f.pt1.u, f.pt1.v);
				tex.vertex( f.pt2.u, f.pt2.v, 0,f.pt2.u, f.pt2.v);
				tex.vertex( f.pt3.u, f.pt3.v, 0,f.pt3.u, f.pt3.v);
			}
			
		}
		tex.endShape();
		tex.endDraw();
	}
	public void draw() {
		context.update();
		map = context.depthImage();
		map.loadPixels();
		PVector[] mappoints = context.depthMapRealWorld();
		for ( int i = 0; i < points.length; ++i){
			int pId = (int)(points[i].u ) + (map.width)* (int)(points[i].v);
			if  (pId >= map.pixels.length){
				pId = map.pixels.length -1;
			}
//		points[i].z = ((map.pixels[pId] >> 16) & 0xFF)*2;
//		( ( imap.pixels[ i ] >> 16 ) & 0xFF ) / 255.f;
			points [i].z = -mappoints[pId].z *0.1f;
		}
		
		if (map != null) {
			parent.pushMatrix();
			parent.translate(-map.width * 0.5f, -map.height *0.5f, 0);
			parent.fill(255);
			parent.image(map, 0, 0);
		
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
			
//			parent.strokeWeight(3);
//			parent.stroke(255,0,0);
//			
//			for (int i =0; i< faces.length; ++i){
//				
//				GFace f = faces[i];
//				parent.point(f.center.x, f.center.y, f.center.z);
//			}
//			parent.beginShape( LINE);
//			for (int i = 0; i < faces.length; ++i) {
//				GFace f = faces [i];
//				parent.vertex(f.center.x, f.center.y, f.center.z);
//				PVector n = new PVector( f.normal);
//				n.mult(100);
//				parent.vertex(f.center.x + n.x, f.center.y + n.y, f.center.z + n.z);
//			}
//			parent.endShape();
			parent.noStroke();
			parent.fill(255);
//			parent.noFill();
//			parent.stroke(255,255,255);
			parent.beginShape( TRIANGLES);
			if (tex != null) parent.texture( tex );
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
		return map;
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
