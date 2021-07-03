package main;

public class Point {
	public float x = 0;
	public float y = 0;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void Add(Point point) {
		this.x += point.x;
		this.y += point.y;
	}
}
