import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
	
	List<Particle> particles;
	Point bottomLeft;
	Point topRight;
	
	public Simulator(int width, int height) {
		this.particles = new ArrayList<Particle>();
		this.bottomLeft = new Point(0, height);
		this.topRight = new Point(width, 0);
	}
	
	public void paint(Graphics g) {
		for(Particle p: particles) {
			g.setColor(p.color);
			p.paint(g);
		}
	}
	
    public void updateParticles() {
    	Point CM = getCenterofMass();

    	for(Particle p: particles) {
    		p.update(CM, bottomLeft, topRight);
    	}
    }
	
	public Point getCenterofMass() {
		float x = 0;
		float y = 0;
		float mass = 0;
		
		for(Particle p: particles) {
			float cMass = p.mass;
			float cX = p.location.x;
			float cY = p.location.y;
		
			x += cX*cMass;
			y += cY*cMass;
			mass += cMass;
		}

		x = x/mass;
		y = y/mass;
		
		return new Point(x,y);
	}
	
	public void addParticle(int mass, Point location) {
		Point direction = new Point((float)Math.random(),(float)Math.random());
		direction = new Point(0,0);
		Particle g = new GasParticle(location,direction,mass);
		g.color = new Color(255,255,255);
		particles.add(g);
	}

	public Integer getNumParticles() {
		return this.particles.size();
	}
}