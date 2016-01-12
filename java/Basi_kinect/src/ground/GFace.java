package ground;

import processing.core.PVector;

public class GFace {
	
	public GPoint pt1;
	public GPoint pt2;
	public GPoint pt3;
	public PVector center;
	public PVector normal;
	
	public GFace (GPoint pt1, GPoint pt2, GPoint pt3){
		this.pt1 = pt1;
		this.pt2 = pt2;
		this.pt3 = pt3;
		center = new PVector();
		normal = new PVector();
		update();
	}
	
	public void update(){
		center.x = (pt1.x + pt2.x + pt3.x) /3;
		center.y = (pt1.y + pt2.y + pt3.y) /3;
		center.z = (pt1.z + pt2.z + pt3.z) /3;
		
		PVector dir12 = new PVector (pt2.x - pt1.x, pt2.y - pt1.y, pt2.z - pt1.z);
		PVector dir13 = new PVector (pt3.x - pt1.x, pt3.y - pt1.y, pt3.z - pt1.z);
		dir12.normalize();
		dir13.normalize();
		normal = dir12.cross(dir13);
		normal.normalize();
	}
	
}
