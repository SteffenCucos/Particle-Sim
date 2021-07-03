package main.simulator;
import java.awt.image.BufferedImage;

import main.Point;

public abstract class BoundedGraphicsParticleSimulator extends ParticleSimulator {
	Point bottomLeft;
	Point topRight;
	int width;
	int height;
	BufferedImage image;
	
	protected BoundedGraphicsParticleSimulator(BufferedImage image, int width, int height, int interpolationStep) {
		super(interpolationStep);
		
		this.image = image;
		this.width = width;
		this.height = height;
		
		this.bottomLeft = new Point(0, height);
		this.topRight = new Point(width, 0);
	}
}