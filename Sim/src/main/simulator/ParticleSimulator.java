package main.simulator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.GasParticle;
import main.Particle;
import main.Point;


public abstract class ParticleSimulator implements Simulator<Particle> {

	List<Particle> particles;
	
	int interpolationStep;
	int interpolationTime;
	
	Point centerOfMass;
	
	public ParticleSimulator(int interpolationStep) {
		this.particles = new ArrayList<Particle>();
		this.interpolationStep = interpolationStep;
		this.interpolationTime = interpolationStep;
		this.centerOfMass = new Point(0,0);
	}

	@Time("Increment state")
	public void incrementState() {
		if(interpolationTime >= interpolationStep) {
			updateCenterofMass();
			updateVelocities();
			
			interpolationTime = 1;
		} else {
			interpolationTime++;
		}
		
		updatePositions();
	}
	
	abstract void updateCenterofMass();
	
	abstract void updateVelocities();
	
	abstract void updatePositions();
		
	public Integer getNumParticles() {
		return this.particles.size();
	}
	
	public static Particle createParticle(int mass, Point location) {
		Point direction = new Point((float)Math.random(),(float)Math.random());
		direction = new Point(0,0);
		Particle g = new GasParticle(location, direction, mass);
		g.color = new Color(255,255,255);
		return g;
	}
	
	public static Particle getCenterOfMass(List<Particle> particles) {
		float x = 0;
		float y = 0;
		float mass = 0;
		
		for(Particle p: particles) {
			if(p != null && p.mass > 0) {
				float cMass = p.mass;
				float cX = p.location.x;
				float cY = p.location.y;
			
				x += cX*cMass;
				y += cY*cMass;
				mass += cMass;
			}
		}

		x = x/mass;
		y = y/mass;
		
		return new Particle(new Point(x,y), null, mass);
	}
}