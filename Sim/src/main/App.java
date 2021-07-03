package main;



import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import main.simulator.ParticleSimulator;
import main.simulator.Simulator;
import main.simulator.SimulatorFactory;

public class App extends Applet implements KeyListener, ActionListener
{
	private static final long serialVersionUID = 1L;

	Timer timer;
	Graphics offg;
	BufferedImage offscreen;
	Simulator<Particle> simulator;

	int width = 900;
	int height = 900;
	int startTimer = 0;
	int frameInLastSecond = 0;
	int framesInCurrentSecond = 0;
	long nextSecond = System.currentTimeMillis() + 1000;

	public void init() {
		this.setSize(width,height);
		this.addKeyListener(this);
		timer = new Timer(2,this);

		offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		offg = offscreen.getGraphics();

		this.simulator = new SimulatorFactory().getThreadedSimulator(offscreen, width, height, 8, 128);
	}

    public void update(Graphics g) {
        paint(g);
    }

	public void paint(Graphics g) {
		offg.setColor(Color.BLACK);
		offg.fillRect(0, 0, width, height);
		offg.setColor(Color.GREEN);

		//Draw the mouse
		Point p = getMousePoint();
		offg.fillOval((int)p.x, (int)p.y, 5,5);

		//FPS Logic
		long currentTime = System.currentTimeMillis();
	    if (currentTime > nextSecond) {
	        nextSecond += 1000;
	        frameInLastSecond = framesInCurrentSecond;
	        framesInCurrentSecond = 0;
	    }
	    framesInCurrentSecond++;

	    //Draw the particles
	    simulator.render();

	    // Draw the FPS
	    offg.setColor(Color.PINK);
	    Frame title = (Frame)this.getParent().getParent();
	    title.setTitle(frameInLastSecond + " fps");

	    // Center of mass
	    //offg.setColor(Color.PINK);
	    //offg.fillOval((int)simulator.cm.x - 5, (int)simulator.cm.y - 5, 10,10);

		//DOUBLE BUFFERING
		g.drawImage(offscreen,0,0,this);
		repaint();
	}

	public void actionPerformed(ActionEvent e) {
		this.simulator.incrementState();
	}

	public Point getParameterizedPoint(int t) {
		float x = (float) (width/2*(1 + Math.cos(t/180f*Math.PI)));
		float y = (float) (height/2*(1 + Math.sin(t/180f*Math.PI)));

		return new Point(x, y);
	}

	public Point getMousePoint() {
		java.awt.Point offset = this.getLocationOnScreen();
		float mouseY = MouseInfo.getPointerInfo().getLocation().y - offset.y;
        float mouseX = MouseInfo.getPointerInfo().getLocation().x - offset.x;
        return new Point((float)mouseX,(float)mouseY);
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		switch(key) {
			case KeyEvent.VK_1:
				this.simulator.addElement(ParticleSimulator.createParticle(1, getMousePoint()));
				break;
			case KeyEvent.VK_2:
				this.simulator.addElement(ParticleSimulator.createParticle(1000, getMousePoint()));
				break;
			case KeyEvent.VK_3:
				//Add points in a sphere
				for(int t = 0; t < 360; t++) {
					this.simulator.addElement(ParticleSimulator.createParticle(1, getParameterizedPoint(t)));
				}
				break;
			case KeyEvent.VK_4:
				//Fill the screen with points
				for(int x = 0; x < width; x+=1) {
					for(int y = 0; y < height; y+=1) {
						this.simulator.addElement(ParticleSimulator.createParticle(1, new Point(x,y)));
					}
				}
				break;
			case KeyEvent.VK_5:
				this.simulator.reset();
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