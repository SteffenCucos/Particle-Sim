
public class Point {
	float x = 0;
	float y = 0;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void Add(Point point) {
		this.x += point.x;
		this.y += point.y;
	}
}
