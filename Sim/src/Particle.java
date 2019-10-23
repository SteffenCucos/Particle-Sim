
import java.awt.*;


public class Particle {

	protected Point location;
	protected Point direction;
	protected float mass;
	protected Color color;
	
	int interpolationTime = 1;
	int interpolationStep = 4;
	
	public Particle(Point location, Point direction, float mass) {
		this.location = location;
		this.direction = direction;
		this.mass = mass;
	}
	
	public void paint(Graphics g) {
		g.fillRect((int)this.location.x-1, (int)this.location.y-1, 2, 2);
	}
	
	public void update(Point CenterOfMass, Point bottomLeft, Point topRight) {	
		this.location.Add(this.direction);
		//this.drag();
		this.bounce(bottomLeft, topRight);
		this.attract(CenterOfMass);
	}
	
	public void drag() {
		this.direction.x*=0.99;
		this.direction.y*=0.99;
	}
	
	public void attract(Point Mass) {
		Point d = this.location;
		//System.out.println(d.x+" "+d.y);
	    float dx = Mass.x-d.x;
	    float dy = Mass.y-d.y;
	    
	    double distance = Math.sqrt(dx*dx+dy*dy);
	    
	    if(distance == 0) {
	    	return;
	    }
	    
	    float newXdir = dx/(float)distance/1;
	    float newYdir = dy/(float)distance/1;
	    
	    if(false || interpolationTime >= interpolationStep) {
		    this.drag();
		    this.direction.x += newXdir/interpolationStep;
		    this.direction.y += newYdir/interpolationStep;
		    interpolationTime = 1;
	    } else {
		    interpolationTime++;
	    }

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
	
	
	
	
	
}
