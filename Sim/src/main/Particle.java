package main;

import java.awt.*;

public class Particle {

	public Point location;
	public Point direction;
	public double mass;
	public Color color;

	public Particle(Point location, Point direction, double mass) {
		this.location = location;
		this.direction = direction;
		this.mass = mass;
	}

	public void paint(Graphics g) {
		g.setColor(color);
		// g.fillRect((int)this.location.x-1, (int)this.location.y-1, 2, 2);
		g.fillRect((int) this.location.x, (int) this.location.y, 1, 1);
	}

	public void updatePosition() {
		this.location.Add(this.direction);
	}

	public void drag() {
		this.direction.x *= 0.99;
		this.direction.y *= 0.99;
	}

	public void attract(Point centerOfMass, int interpolationStep) {
		Point d = this.location;
		// System.out.println(d.x+" "+d.y);
		double dx = centerOfMass.x - d.x;
		double dy = centerOfMass.y - d.y;

		if (dx == 0 && dy == 0) {
			return;
		}

		double distance = Math.sqrt(dx * dx + dy * dy);

		double newXdir = dx / (double) distance / 1;
		double newYdir = dy / (double) distance / 1;

		this.drag();
		this.direction.x += newXdir / interpolationStep;
		this.direction.y += newYdir / interpolationStep;
	}

	public void bounce(Point bottomLeft, Point topRight) {
		if (this.location.x < bottomLeft.x || this.location.x > topRight.x) {
			this.direction.x *= -1;
		}
		if (this.location.y > bottomLeft.y || this.location.y < topRight.y) {
			this.direction.y *= -1;
		}
	}

	public double getDistanceFrom(Point B) {
		double dx = this.location.x - B.x;
		double dy = this.location.y - B.y;

		return Math.sqrt(dx * dx + dy * dy);
	}

	public void updateColor(Point CenterOfMass) {
		double distance = this.getDistanceFrom(CenterOfMass);

		float a = 120;
		int r = (int) Math.abs(Math.sin(distance / a * Math.PI) * 255);
		int g = (int) Math.abs(Math.cos(distance / a * Math.PI) * 255);
		int b = (int) Math.abs(Math.cos(distance / a * Math.PI - Math.PI / 4f) * 255);

		this.color = new Color(r, g, b);
	}

}
