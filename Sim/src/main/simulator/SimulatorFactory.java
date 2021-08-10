package main.simulator;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import main.Particle;

public class SimulatorFactory {

	public ParticleSimulator getLinearSimulator(BufferedImage image, int width, int height, int interpolationStep) {
		ParticleSimulator impl = new LinearSimulator(image, width, height, interpolationStep);
		return impl; // getProxiedSimulator(impl);
	}

	public ParticleSimulator getThreadedSimulator(BufferedImage image, int width, int height, int interpolationStep,
			int parallelism) {
		ParticleSimulator impl = new ThreadedSimulator(image, width, height, interpolationStep, parallelism);

		return impl; // getProxiedSimulator(impl);
	}

	@SuppressWarnings("unchecked")
	public Simulator<Particle> getProxiedSimulator(Simulator<Particle> impl) {
		return (Simulator<Particle>) Proxy.newProxyInstance(ParticleSimulator.class.getClassLoader(),
				new Class[] { Simulator.class }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (!method.isAnnotationPresent(Simulator.Time.class)) {
							return method.invoke(impl, args);
						}

						long startTime = System.currentTimeMillis();
						Object result = method.invoke(impl, args);
						long endTime = System.currentTimeMillis();
						String description = method.getAnnotation(Simulator.Time.class).value();
						System.out.println(description + " took " + (endTime - startTime) + " milliseconds");

						return result;
					}
				});
	}

}