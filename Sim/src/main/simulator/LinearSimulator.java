package main.simulator;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Particle;

public class LinearSimulator extends BoundedGraphicsParticleSimulator {

	Graphics graphics;
	
	protected LinearSimulator(BufferedImage image, int width, int height, int interpolationStep) {
		super(image, width, height, interpolationStep);
		this.graphics = image.getGraphics();
	}
	
	@Override
	public void render() {
		for(Particle p: particles) {
			p.paint(graphics);
		}
	}
	
	@Override
    public void updateVelocities() {
    	for(Particle p: particles) {
    		p.attract(centerOfMass, interpolationStep);
    	}
    }
    
	@Override
    public void updatePositions() {
    	for(Particle p: particles) {
    		p.updateColor(centerOfMass);
    		p.updatePosition();
    		p.bounce(bottomLeft, topRight);
    	}
    }
	
	@Override
	public void updateCenterofMass() {
		centerOfMass = getCenterOfMass(this.particles).location;
	}
	
	@Override
	public void addElement(Particle particle) {
		this.particles.add(particle);
	}

	@Override
	public void reset() {
		this.particles.clear();
	}
}