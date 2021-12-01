package main.timing.agent;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import main.simulator.Simulator.Time;

public class TimeProcessor extends AbstractProcessor {
	
	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv){ 
	    super.init(processingEnv);
	    typeUtils = processingEnv.getTypeUtils();
	    elementUtils = processingEnv.getElementUtils();
	    filer = processingEnv.getFiler();
	    messager = processingEnv.getMessager();
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		
		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Time.class)) {
			// Make sure we are only processing methods
			if (annotatedElement.getKind() != ElementKind.METHOD) {
				error(annotatedElement, "Only methods can be annotated with @Time");
				return false;
			}
			
			Time timeAnnotation = annotatedElement.getAnnotation(Time.class);
			
			if (!validateAnnotationParameters(timeAnnotation)) {
				error(annotatedElement, "The minimum granularity cannot be greater than maximum granularity");
				return false;
			}
			
			// Make sure we don't process illegal methods
//			Set<Modifier> illegalModifiers = new HashSet<>(Arrays.asList(Modifier.ABSTRACT));
//			for (Modifier modifier : method.getModifiers()) {
//				if (illegalModifiers.contains(modifier)) {
//					error(annotatedElement, "%s methods cannot be timed", modifier);
//					return false;
//				}
//			}
		}
		
		return false;
	}
	
	public boolean validateAnnotationParameters(Time timeAnnotation) {
		// The minimum granularity cannot be greater than maximum granularity
		return timeAnnotation.minimumTimeScale().ordinal() <= timeAnnotation.maximumTimeScale().ordinal();
	}
	
	private void warn(Element e, String msg, Object...args) {
		messager.printMessage(
			Diagnostic.Kind.WARNING,
			String.format(msg, args),
			e);
	}
	
	private void error(Element e, String msg, Object... args) {
		messager.printMessage(
			Diagnostic.Kind.ERROR,
			String.format(msg, args),
			e);
	}
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
	    return Collections.singleton(Time.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
		
	}
}
