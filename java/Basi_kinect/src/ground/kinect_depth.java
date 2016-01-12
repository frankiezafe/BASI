package ground;

import SimpleOpenNI.*;
import processing.core.*;

public class kinect_depth extends PApplet {

    SimpleOpenNI context;
    public void settings() {
        
        size(640, 480, P3D);
    }

    public void setup() {
        context = new SimpleOpenNI(this);
        //context.setMirror(false);
        context.enableDepth();
        //context.enableRGB();
    }

    public void draw() {

        context.update();
        background( 5 );

        pushMatrix();
        //translate(width / 2, height / 2, 0);
        //rotateX(rotX);
        //rotateY(rotY);
        //scale(zoomF);
        //int[] depthMap = context.depthMap();
        //int steps = 3; // to speed up the drawing, draw every third point
        //int index;
        //PVector realWorldPoint;
        //translate(0, 0, -1000);
        //stroke(255);
        //PVector[] realWorldMap = context.depthMapRealWorld();
        //beginShape(POINTS);
        
        /*
        for (int y = 0; y < context.depthHeight(); y += steps) {
            for (int x = 0; x < context.depthWidth(); x += steps) {
                index = x + y * context.depthWidth();
                if (depthMap[index] > 0) {
                    // draw the projected point
                    realWorldPoint = realWorldMap[index];
                    vertex(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
                }
            }
        }
        */
        
        endShape();
        context.drawCamFrustum();
        
        popMatrix();
        
        //image(context.depthImage(), 0,0, 160,120 );
        image(context.depthImage(), 0,0, 640, 480 );
        //image(context.rgbImage(), 165,0, 160,120 );
        
        fill( 255 );
        text( frameRate, 10,140 );

    }

    public void exit() {
        // ! important, kinect must be stopped before closing the application
        context.dispose();
        super.exit();
    }

    public static void main(String args[]) {
        PApplet.main(new String[] { "SimpleKinect" });
    }

}
