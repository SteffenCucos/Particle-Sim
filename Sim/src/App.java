


import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public class App extends Applet implements KeyListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	
	Timer timer;
	Graphics offg;
	Image offscreen;
	Simulator simulator;
	
	int width = 900;
	int height = 900;
	int startTimer = 0;
	int frameInLastSecond = 0;
	int framesInCurrentSecond = 0;
	long nextSecond = System.currentTimeMillis() + 1000;

	public void init() {
		this.setSize(width,height);
		this.addKeyListener(this);
		this.simulator = new Simulator(width, height);
		timer = new Timer(20,this);

		offscreen = createImage(this.getWidth(),this.getHeight());
		offg = offscreen.getGraphics();		
	}
	
    public void update(Graphics g) {
        paint(g);
    }
	
	public void paint(Graphics g) {
		offg.setColor(Color.BLACK);
		offg.fillRect(0, 0, width,height);
		offg.setColor(Color.GREEN);
		
		//Draw the mouse
		Point p = getMousePoint();
		offg.fillOval((int)p.x, (int)p.y, 5,5);
		offg.drawString(simulator.getNumParticles().toString(), 20, 20);
		
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
	    simulator.paint(offg);
		
		//DOUBLE BUFFERING 
		g.drawImage(offscreen,0,0,this);
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {		
		this.simulator.updateParticles();
	}
    
	public Point getParameterizedPoint(int t) {
		float x = (float) (width/2*(1 + Math.cos(t/180f*Math.PI)));
		float y = (float) (height/2*(1 + Math.sin(t/180f*Math.PI)));
		
		return new Point(x, y);
	}
   
	public Point getMousePoint() {
		float mouseY = MouseInfo.getPointerInfo().getLocation().y;
        float mouseX = MouseInfo.getPointerInfo().getLocation().x; 
        return new Point((float)mouseX,(float)mouseY);
	}
	
	public void keyPressed(KeyEvent e) { 
		int key = e.getKeyCode();
		
		switch(key) { 
			case KeyEvent.VK_1:
				this.simulator.addParticle(1, getMousePoint());
				break;
			case KeyEvent.VK_2:
				this.simulator.addParticle(1000, getMousePoint());
				break;
			case KeyEvent.VK_3:
				//Add points in a sphere
				for(int t = 0; t < 360; t++) {
					this.simulator.addParticle(1, getParameterizedPoint(t));
				}
				break;
			case KeyEvent.VK_4:
				//Fill the screen with points
				for(int x = 0; x < width; x+=4) {
					for(int y = 0; y < height; y+=4) {
						this.simulator.addParticle(1, new Point(x,y));
					}
				}
				break;
			case KeyEvent.VK_5:
				this.simulator.particles = new ArrayList<Particle>();
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
}