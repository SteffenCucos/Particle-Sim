import java.awt.Color;

public class GasParticle extends Particle {
	
	public GasParticle(Point location, Point direction, float mass) {
		super(location, direction, mass);
	}

	public void update(Point CenterOfMass, Point bottomLeft, Point topRight) {
		float distance = this.getDistanceFrom(CenterOfMass);
		
		float a = 120;
		int r = (int)Math.abs(Math.sin(distance/a*Math.PI)*255);
		int g = (int)Math.abs(Math.cos(distance/a*Math.PI)*255);	
		int b = (int)Math.abs(Math.cos(distance/a*Math.PI-Math.PI/4f)*255);
	
		this.color = new Color(r, g, b);		
		super.update(CenterOfMass, bottomLeft, topRight);	
	}
}