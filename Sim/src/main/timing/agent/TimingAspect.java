package main.timing.agent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
		Throwable thrown = null;
		try {
			return proceedingJoinPoint.proceed();
		} catch (Throwable t) {
			thrown = t;
			throw t; // Re-throw the exception, the caller needs to know about it.
		} finally {
			/* According to https://docs.oracle.com/javase/tutorial/essential/exceptions/finally.html
			 * finally blocks always run immediately after the try/catch blocks finish, so stopping the 
			 * stop watch here will be accurate.*/
			long endTime = System.currentTimeMillis();
			String timeTaken = getScaledTime(endTime - startTime);
			String outcome = thrown == null
					? "took"
					: String.format("failed with exception: {%s} after", thrown.getMessage());
			
			String msg = String.format("%s %s %s", timeAnnotation.value(), outcome, timeTaken);
			LOGGER.info(msg);
		}
	}
	
	private static class TimeScale {
		
		static final TimeScale DAY = TimeScale.of("Day", 24*60*60*1000);
		static final TimeScale HOUR = TimeScale.of("Hour", 60*60*1000);
		static final TimeScale MINUTE =TimeScale.of("Minute", 60*1000);
		static final TimeScale SECOND = TimeScale.of("Second", 1000);
		static final TimeScale MILLISECOND = TimeScale.of("Millisecond", 1);
		
		static TimeScale of(String timeScale, final long millisecondsInTimeScale) {
			return new TimeScale(timeScale, millisecondsInTimeScale);
		}
		
		final String timeScale;
		final long millisecondsInTimeScale;
		
		private TimeScale(String timeScale, final long millisecondsInTimeScale) {
			this.timeScale = timeScale;
			this.millisecondsInTimeScale = millisecondsInTimeScale;
		}
		
		public long timeScalesContained(long milliseconds) {
			return milliseconds / millisecondsInTimeScale;
		}
		
		public long millisecondsLeft(long milliseconds) {
			return milliseconds % millisecondsInTimeScale;
		}
		
		public String getTimeScaleString(long timeScaleTaken) {
			if (timeScaleTaken == 0) {
				return "";
			}
			
			String postFix = timeScaleTaken == 1 ? "" : "s";
			
			return String.format("%s %s%s", timeScaleTaken, timeScale, postFix);
		}
	}
	
	public static String getScaledTime(long milliseconds) {
		if (milliseconds == 0) {
			return "0 Milliseconds";
		}
		
		AtomicLong atomicMilliseconds = new AtomicLong(milliseconds);
		
		List<TimeScale> timeScales = Arrays.asList(
			TimeScale.DAY,
			TimeScale.HOUR,
			TimeScale.MINUTE,
			TimeScale.SECOND,
			TimeScale.MILLISECOND
		);
		
		Map<TimeScale, Long> timeScalesMap= timeScales.stream()
			.collect(Collectors.toMap(
				ts -> ts, 
				ts -> {
					Long timeScalesTaken = ts.timeScalesContained(atomicMilliseconds.get());
					atomicMilliseconds.set(ts.millisecondsLeft(milliseconds));
					return timeScalesTaken;
				}));

		return String.join(", ", 
				timeScales.stream()
					.filter(ts -> timeScalesMap.get(ts) > 0)
					.map(ts -> ts.getTimeScaleString(timeScalesMap.get(ts)))
					.collect(Collectors.toList()));
	}
}