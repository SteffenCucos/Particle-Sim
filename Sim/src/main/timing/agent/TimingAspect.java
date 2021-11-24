package main.timing.agent;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

import com.google.common.collect.Maps;

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
		
		private final String timeScale;
		private final long millisecondsInTimeScale;
		
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
		
		return String.join(", ", 
			Arrays.asList(
				TimeScale.DAY,
				TimeScale.HOUR,
				TimeScale.MINUTE,
				TimeScale.SECOND,
				TimeScale.MILLISECOND
			).stream()
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
		
		Iterable<Entry<K, V>> entryIterator = () -> new Iterator<Entry<K, V>>() {
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
		
		return StreamSupport.stream(entryIterator.spliterator(), false);
	}
}