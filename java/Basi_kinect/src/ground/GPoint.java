package ground;

import processing.core.PVector;

public class GPoint  extends PVector {
	
	public float u;
	public float v;
	private PVector initial;
	private PVector norm;
	private PVector normuv;
	
	public GPoint (float x, float y, float z,
			float normx, float normy, float normz
			){
		super ( x, y, z);
		initial = new PVector( x,y,z );
		norm = new PVector( normx, normy, normz);
		normuv = new PVector();
	}
	
	public void reset() {
		this.set( initial );
	}
	
	public void setNormUV( float u, float v ) {
		normuv.set( u, v );
	}
	
	public void renderUV(float w, float h) {
		u = normuv.x * w;
		v = normuv.y * h;
	}
	public void setZ ( float h){
//		z = norm.z *h;
		
	}
	public void setX ( float h){
//		x = norm.x *h;
	}
	
	public void print() {
		System.out.println(
				"UV: " +  normuv.x + ", " + normuv.y
				);
	}
}
