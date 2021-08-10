package main;

public class Point {
	public double x = 0;
	public double y = 0;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void Add(Point point) {
		this.x += point.x;
		this.y += point.y;
	}
}
