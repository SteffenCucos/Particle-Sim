package main.simulator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Simulator<T> {
	
	public void render();
	
	public void incrementState();
	
	public void addElement(T t);
	
	public void reset();
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Time {
		public String value() default "";
	}
}