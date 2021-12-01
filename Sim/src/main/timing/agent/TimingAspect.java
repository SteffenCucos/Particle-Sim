package main.timing.agent;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
			throw t; // Re-throw the throwable, the caller needs to know about it.
		} finally {
			/* According to https://docs.oracle.com/javase/tutorial/essential/exceptions/finally.html
			 * finally blocks always run immediately after the try/catch blocks finish, so stopping the 
			 * stop watch here will be accurate.*/
			long endTime = System.currentTimeMillis();
			String timeTaken = getScaledTime(endTime - startTime, timeAnnotation.minimumTimeScale(), timeAnnotation.maximumTimeScale());
			String outcome = thrown == null
					? "took"
					: String.format("failed with exception: {%s} after", thrown.getMessage());
			
			String msg = String.format("%s %s %s", timeAnnotation.value(), outcome, timeTaken);
			LOGGER.info(msg);
		}
	}
	
	public enum TimeScale {
		
		MILLISECOND("Millisecond", 1),
		SECOND("Second", 1000 * MILLISECOND.millisecondsInTimeScale),
		MINUTE("Minute", 60 * SECOND.millisecondsInTimeScale),
		HOUR("Hour", 60 * MINUTE.millisecondsInTimeScale),
		DAY("Day", 24 * HOUR.millisecondsInTimeScale),
		WEEK("Week", 7 * DAY.millisecondsInTimeScale),
		MONTH("Month", 4 * WEEK.millisecondsInTimeScale),
		YEAR("Year", 12 * MONTH.millisecondsInTimeScale);
		
		public final String timeScale;
		public final long millisecondsInTimeScale;
		
		private TimeScale(String timeScale, long millisecondsInTimeScale) {
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
			// 0 Seconds, 1 Second, 2 Seconds ...
			String postFix = timeScaleTaken == 1 ? "" : "s";
			
			return String.format("%s %s%s", timeScaleTaken, timeScale, postFix);
		}
		
		public static List<TimeScale> getTimeScalesDecreasing() {
			List<TimeScale> timeScales = getTimeScalesIncreasing();
			Collections.reverse(timeScales);
			return timeScales;
		}
		
		public static List<TimeScale> getTimeScalesIncreasing() {
			return Arrays.asList(TimeScale.values());
		}
	}
	
	public static String getScaledTime(long milliseconds, TimeScale minimumTimeScale, TimeScale maximumTimeScale) {
		if (milliseconds < minimumTimeScale.millisecondsInTimeScale) {
			return minimumTimeScale.getTimeScaleString(0);
		}
		
		AtomicLong atomicMilliseconds = new AtomicLong(milliseconds);
		
		return String.join(", ", 
			TimeScale.getTimeScalesDecreasing()
			.stream()
			// Only consider TimeScales that fit between the minimum & maximum timescales requested
			.filter(ts -> ts.ordinal() >= minimumTimeScale.ordinal() && ts.ordinal() <= maximumTimeScale.ordinal())
			.map(ts -> {
				Long timeScalesTaken = ts.timeScalesContained(atomicMilliseconds.get());
				atomicMilliseconds.set(ts.millisecondsLeft(milliseconds));
				return new AbstractMap.SimpleEntry<>(ts, timeScalesTaken);
			})
			.filter(pair -> pair.getValue() > 0)
			.map(pair -> pair.getKey().getTimeScaleString(pair.getValue()))
			.collect(Collectors.toList()));
	}
	
	public static <K, V> Stream<Entry<K, V>> orderedMapStream(Map<K, V> map, List<K> orderedKeys) {
		assert(map.keySet().size() == orderedKeys.size());
		
		Iterator<Entry<K, V>> entryIterator = new Iterator<Entry<K, V>>() {
			Iterator<K> sourceIterator = orderedKeys.iterator();
			
			@Override
			public boolean hasNext() {
				return sourceIterator.hasNext();
			}

			@Override
			public Entry<K, V> next() {
				K key = sourceIterator.next();
				V value = map.get(key);
				return new AbstractMap.SimpleEntry<>(key, value);
			}
		};
		
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(entryIterator, Spliterator.ORDERED),
				false);
	}
}