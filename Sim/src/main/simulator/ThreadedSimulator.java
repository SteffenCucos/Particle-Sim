package main.simulator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import main.Particle;

public class ThreadedSimulator extends BoundedGraphicsParticleSimulator {

	interface ThreadableOperation {
		Runnable getRunnable(int index);
	}

	public ThreadableOperation getCenterOfMassOperation() {
		return index -> () -> {
			Particle newCM = getCenterOfMass(particlesList.get(index));
			partialCenters.get(index).location = newCM.location;
			partialCenters.get(index).mass = newCM.mass;
		};
	}

	public ThreadableOperation getUpdateOperation() {
		return index -> () -> {
			for (Particle p : particlesList.get(index)) {
				p.updateColor(centerOfMass);
				p.updatePosition();
				p.bounce(bottomLeft, topRight);
			}
		};
	}

	public ThreadableOperation getAttractOperation() {
		return index -> () -> {
			for (Particle p : particlesList.get(index)) {
				p.attract(centerOfMass, interpolationStep);
			}
		};
	}

	public ThreadableOperation getPaintOperation() {
		@SuppressWarnings("unused")
		final Color[] threadColors = new Color[] { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
		return index -> () -> {
			for (Particle p : particlesList.get(index)) {
				// p.setColor(threadColors[t % 4]);
				p.paint(threadsGraphics.get(index));
			}
		};
	}

	int currBucket = 0;
	int parallelism;
	ExecutorService executorService;
	List<Particle> partialCenters = new ArrayList<>();
	List<List<Particle>> particlesList = new ArrayList<>();
	List<Graphics2D> threadsGraphics = new ArrayList<>();
	List<BufferedImage> images = new ArrayList<>();

	final Object particlesLock = new Object();

	public ThreadedSimulator(BufferedImage image, int width, int height, int interpolationStep, int parallelism) {
		super(image, width, height, interpolationStep);

		this.parallelism = parallelism;
		this.executorService = Executors.newFixedThreadPool(parallelism);

		for (int t = 0; t < parallelism; t++) {
			Particle partialCenter = new Particle(null, null, -1);
			List<Particle> particles = new ArrayList<>();

			// BufferedImage image = new BufferedImage(width, height,
			// BufferedImage.TYPE_INT_ARGB);
			Graphics2D threadGraphics = (Graphics2D) image.getGraphics();
			threadsGraphics.add(threadGraphics);
			images.add(image);

			particlesList.add(particles);
			partialCenters.add(partialCenter);
		}
	}

	public void threadTask(ThreadableOperation operation) {
		List<Future<?>> tasks = new ArrayList<>();
		for (int t = 0; t < parallelism; t++) {
			Future<?> task = executorService.submit(operation.getRunnable(t));
			tasks.add(task);
		}

		awaitTasks(tasks);
	}

	public void awaitTasks(List<Future<?>> tasks) {
		for (Future<?> task : tasks) {
			try {
				task.get();
			} catch (InterruptedException | ExecutionException e) {
			}
		}
	}

	@Time("Render")
	@Override
	public void render() {
		synchronized (particlesLock) {
			threadTask(getPaintOperation());
		}
	}

	@Override
	public void updateCenterofMass() {
		synchronized (particlesLock) {
			threadTask(getCenterOfMassOperation());
			centerOfMass = getCenterOfMass(partialCenters).location;
		}
	}

	@Override
	public void updateVelocities() {
		synchronized (particlesLock) {
			threadTask(getAttractOperation());
		}
	}

	@Override
	public void updatePositions() {
		synchronized (particlesLock) {
			threadTask(getUpdateOperation());
		}
	}

	@Override
	public void addElement(Particle particle) {
		synchronized (particlesLock) {
			particlesList.get(currBucket).add(particle);
			currBucket = (currBucket + 1) % parallelism;
			particles.add(particle);
		}
	}

	@Override
	public void reset() {
		synchronized (particlesLock) {
			this.particles.clear();
			for (List<Particle> particles : particlesList) {
				particles.clear();
			}
		}
	}
}
