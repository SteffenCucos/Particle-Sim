package main;

import java.awt.*;


public class Particle {

	public Point location;
	public Point direction;
	public float mass;
	public Color color;
	
	public Particle(Point location, Point direction, float mass) {
		this.location = location;
		this.direction = direction;
		this.mass = mass;
	}
	
	public void paint(Graphics g) {
		g.setColor(color);
		//g.fillRect((int)this.location.x-1, (int)this.location.y-1, 2, 2);
		g.fillRect((int)this.location.x, (int)this.location.y, 1, 1);
	}
	
	public void updatePosition() {
		this.location.Add(this.direction);
	}
	
	public void drag() {
		this.direction.x*=0.99;
		this.direction.y*=0.99;
	}
	
	public void attract(Point centerOfMass, int interpolationStep) {
		Point d = this.location;
		//System.out.println(d.x+" "+d.y);
	    float dx = centerOfMass.x-d.x;
	    float dy = centerOfMass.y-d.y;
	    
	    double distance = Math.sqrt(dx*dx+dy*dy);
	    
	    if(distance == 0) {
	    	return;
	    }
	    
	    float newXdir = dx/(float)distance/1;
	    float newYdir = dy/(float)distance/1;
    	
	    this.drag();
	    this.direction.x += newXdir/interpolationStep;
	    this.direction.y += newYdir/interpolationStep;
	}
	
	public void bounce(Point bottomLeft, Point topRight) {
		if(this.location.x < bottomLeft.x || this.location.x > topRight.x) {
			this.direction.x *= -1;
		}
		if(this.location.y > bottomLeft.y || this.location.y < topRight.y) {
			this.direction.y *= -1;
		}
	}
	
    public float getDistanceFrom(Point B) {
    	float dx = this.location.x - B.x;
    	float dy = this.location.y - B.y;
    	
    	return (float)Math.sqrt(dx*dx+dy*dy);
    }
	
	public void updateColor(Point CenterOfMass) {
		float distance = this.getDistanceFrom(CenterOfMass);
		
		float a = 120;
		int r = (int)Math.abs(Math.sin(distance/a*Math.PI)*255);
		int g = (int)Math.abs(Math.cos(distance/a*Math.PI)*255);	
		int b = (int)Math.abs(Math.cos(distance/a*Math.PI-Math.PI/4f)*255);
	
		this.color = new Color(r, g, b);
	}
	
	
	
}
