import java.awt.Color;


public class GasParticle extends Particle {
	
	public GasParticle(Point location, Point direction, float mass) {
		super(location, direction, mass);
	}

	public void update(Point CenterOfMass, Point bottomLeft, Point topRight) {
		float distance = this.getDistanceFrom(CenterOfMass);
		
		float a = 720;
		float r = (float)Math.abs(Math.sin(distance/a*Math.PI)*255);
		float g = (float)Math.abs(Math.cos(distance/a*Math.PI)*255);	
		float b = (float)Math.abs(Math.cos(distance/a*Math.PI-Math.PI/4f)*255);
	
		this.color = new Color((int)r,(int)g,(int)b);		
		super.update(CenterOfMass, bottomLeft, topRight);	
	}
}
