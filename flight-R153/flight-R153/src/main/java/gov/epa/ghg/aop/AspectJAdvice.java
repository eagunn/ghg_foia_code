package gov.epa.ghg.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
public class AspectJAdvice {
	
	/**
	 * Applied around a any public method.
	 */
	@Around("execution(public * *(..))")
	public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("Around advice before joinpoint");
		log.debug("Entering >>>> " + getShortClassName(pjp) + "." + pjp.getStaticPart().getSignature().toShortString());
		log.debug("Around advice before joinpoint");
		Object obj = pjp.proceed();
		log.debug("Around advice after running");
		return obj;
	}
	
	/**
	 * Applied before the execution of any method which takes a String argument.
	 */
	@Before("execution(* *(..)) &&" + "args(prefix)")
	public void beforeAdvice(String prefix) {
		// System.out.println("Before advice : " + prefix);
		log.debug("Before advice : " + prefix);
	}
	
	/**
	 * Applied after returning from the pointcut defined by anyPublicMethod
	 */
	@AfterReturning("anyPublicMethod()")
	public void afterAdvice() {
		// System.out.println("After Returning advice");
		log.debug("After Returning advice");
	}
	
	/**
	 * Defines a pointcut that matches any public method.
	 */
	@Pointcut("execution(public * *(..))")
	private void anyPublicMethod() {
		// System.out.println("After anyPublicMethod");
		log.debug("After anyPublicMethod");
	}
	
	public String getShortClassName(JoinPoint aJoinPoint) {
		String className = aJoinPoint.getTarget().getClass().getCanonicalName();
		int nameIndex = className.lastIndexOf('.');
		return className.substring(nameIndex + 1);
	}
	
}
