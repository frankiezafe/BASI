package ground;

import processing.core.PVector;

public class GPoint  extends PVector{
	
	public float u;
	public float v;
	private PVector norm;
	
	public GPoint (float x, float y, float z,
			float normx, float normy, float normz
			){
		super ( x, y, z);
		norm = new PVector( normx, normy, normz);
		u = normx;
		v = normy;
	}
	public void setUV(float w, float h){
		u = norm.x*w;
		v =norm.y*h;
	}
	public void setZ ( float h){
		z = norm.z *h;
		
	}
	public void setX ( float h){
		x = norm.x *h;
		
	}
}
