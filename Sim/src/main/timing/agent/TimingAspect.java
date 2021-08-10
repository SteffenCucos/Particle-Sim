package main.timing.agent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;

import main.simulator.Simulator.Time;

@Aspect
public class TimingAspect {

	@Around("execution(@Time * *(..)) && @annotation(timeAnnotation)")
	public Object time(ProceedingJoinPoint proceedingJoinPoint, Time timeAnnotation) throws Throwable {
		long startTime = System.currentTimeMillis();

		Object result = proceedingJoinPoint.proceed();

		long endTime = System.currentTimeMillis();
		System.out.println(timeAnnotation.value() + " took " + (endTime - startTime) + " milliseconds");

		return result;
	}
}