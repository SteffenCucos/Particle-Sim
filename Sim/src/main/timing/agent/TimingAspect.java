package main.timing.agent;

import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;

import main.simulator.Simulator.Time;

@Aspect
public class TimingAspect {
	
	private static final Logger LOGGER = Logger.getLogger(TimingAspect.class.getName());
	
	@Around("execution(@Time * *(..)) && @annotation(timeAnnotation)")
	public Object time(ProceedingJoinPoint proceedingJoinPoint, Time timeAnnotation) throws Throwable {
		long startTime = System.currentTimeMillis();
		Exception exception = null;
		try {
			return proceedingJoinPoint.proceed();
		} catch (Exception e) {
			exception = e;
			throw e; // Re-throw the exception, the caller needs to know about it.
		} finally {
			/* According to https://docs.oracle.com/javase/tutorial/essential/exceptions/finally.html
			 * finally blocks always run immediately after the try/catch blocks finish, so stopping the 
			 * stop watch here will be accurate.*/
			long endTime = System.currentTimeMillis();
			String timeTaken = (endTime - startTime) + " milliseconds";
			String outcome = exception == null
					? "took"
					: String.format("failed with exception: {%s} after", exception.getMessage());
			
			String msg = String.format("%s %s %s", timeAnnotation.value(), outcome, timeTaken);
			//System.out.println(msg);
			LOGGER.info(msg);
		}
	}
}