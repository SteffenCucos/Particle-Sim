


import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public class Game extends Applet implements KeyListener, ActionListener
{
	Image offscreen;
	Graphics offg;
	int width = 2560;
	int height = 1440;
	Timer timer;
	
	Point bottomLeft = new Point(0, height);
	Point topRight = new Point(width, 0);
	
	long nextSecond = System.currentTimeMillis() + 1000;
	int startTimer = 0;
	
	int frameInLastSecond = 0;
	int framesInCurrentSecond = 0;
	
	List<Particle> GasParticles;

	public void init() {
		this.setSize(width,height);
		this.addKeyListener(this);
		timer = new Timer(20,this);
		
		GasParticles = new ArrayList<Particle>();

		offscreen = createImage(this.getWidth(),this.getHeight());
		offg = offscreen.getGraphics();		
	}
	
	public void paint(Graphics g) {
		offg.setColor(Color.BLACK);
		offg.fillRect(0, 0, width,height);
		offg.setColor(Color.GREEN);
		Point p = getMousePoint();
		offg.fillOval((int)p.x, (int)p.y, 5,5);
		offg.drawString(Integer.toString(GasParticles.size()), 20, 20);
		
		//FPS Logic
		long currentTime = System.currentTimeMillis();
	    if (currentTime > nextSecond) {
	        nextSecond += 1000;
	        frameInLastSecond = framesInCurrentSecond;
	        framesInCurrentSecond = 0;
	        
	    }
	    framesInCurrentSecond++;

	    offg.drawString(frameInLastSecond + " fps", 20, 40);
		
		//Draw the particles
		for(int i = 0; i < GasParticles.size(); i++) {
			Particle P = GasParticles.get(i);
			
			offg.setColor(P.color);
			P.paint(offg);
		}
		
		//DOUBLE BUFFERING 
		g.drawImage(offscreen,0,0,this);
		
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {		
		this.UpdateParticles();
	}

    public void update(Graphics g) {
        paint(g);
    }
    
    public void UpdateParticles() {
    	Point CM = getCenterofMass(GasParticles);
    	
    	for(int i = 0; i < GasParticles.size();i++) {
			GasParticle g = (GasParticle)GasParticles.get(i);
			g.update(CM, bottomLeft, topRight);
		}
    }
    
	public Point getParameterizedPoint(int t) {
		float x = (float) (400*Math.cos(t/180f*Math.PI)+width/2);
		float y = (float) (400*Math.sin(t/180f*Math.PI)+height/2);
		
		return new Point(x, y);
	}
   
	public Point getMousePoint() {
		float mouseY = MouseInfo.getPointerInfo().getLocation().y;
        float mouseX = MouseInfo.getPointerInfo().getLocation().x; 
        return new Point((float)mouseX,(float)mouseY);
	}
	
	public Point getCenterofMass(List<Particle> particles) {
		float x = 0;
		float y = 0;
		float mass = 0;
		
		for(int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			float cMass = p.mass;
			float cX = (float) (p.location.x);
			float cY = (float) (p.location.y);
		
			x += cX*cMass;
			y += cY*cMass;
			mass += cMass;
		}
		
		x = x/mass;
		y = y/mass;
		
		return new Point(x,y);
	}
	
	public void keyPressed(KeyEvent e) { 
		int key = e.getKeyCode();
		
		switch(key) { 
			case KeyEvent.VK_1:
				addParticle(1, getMousePoint());
				break;
			case KeyEvent.VK_2:
				addParticle(1000, getMousePoint());
				break;
			case KeyEvent.VK_3:
				for(int t = 0; t < 360; t++) {
					for(int i = 0; i < 20; i++) {
						addParticle(1, getParameterizedPoint(t));
					}
				}
				break;
			default:
				break;
		}
	} 
	 
	 
	public void keyReleased(KeyEvent e) { } 
	 
	public void keyTyped(KeyEvent e) { }
	
	public void start() {
		timer.start();
	}
	
	public void stop() {
		timer.stop();
	}
	
	public void addParticle(int mass, Point location) {
		Point direction = new Point((float)Math.random(),(float)Math.random());
		direction = new Point(0,0);
		GasParticle g = new GasParticle(location,direction,mass);
		g.color = new Color(255,255,255);
		GasParticles.add(g);
	}
}
	
	
	
	

