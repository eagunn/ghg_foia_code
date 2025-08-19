package gov.epa.ghg.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Aspect
public class LoggingAspect {
	
	// Pointcut definitions on a method signature basis
	// All public methods
	@Pointcut("execution(public * *(..))")
	private void publicMethod() {
	}
	
	// All public setters
	@Pointcut("execution(public * set*(..))")
	private void publicSetMethod() {
	}
	
	// All public getters
	@Pointcut("execution(public * get*(..))")
	private void publicGetMethod() {
	}
	
	@Pointcut("within(gov.epa.ghg.*)")
	public void inServiceLayer() {
	}
	
	@Pointcut("within(gov.epa.ghg.dao.*)")
	public void inDaoLayer() {
	}
	
	// The code advices
	
	@Before("inServiceLayer(),inDaoLayer()")
	public void beforePublic() {
		if (log.isDebugEnabled()) {
			log.debug("Before method call --------------------------------------------------------------------------->");
		}
	}
	
	@AfterReturning("inServiceLayer(),inDaoLayer")
	public void afterPublicReturning() {
		if (log.isDebugEnabled()) {
			log.debug("After returning<--------------------------------------------------------------------------- END");
		}
	}
	
	//@AfterThrowing(pointcut="inServiceLayer(),publicMethod()",throwing="exception")
	@AfterThrowing(pointcut = "inServiceLayer(),inDaoLayer()", throwing = "exception")
	public void afterPublicThrowing(Throwable exception) {
		if (log.isDebugEnabled()) {
			log.debug("After throwing");
			log.error("An Exception has been thrown" + exception);
		}
	}
	
	@Around("inServiceLayer(),inDaoLayer()")
	public Object aroundPublic(final ProceedingJoinPoint pjp) throws Throwable {
		if (log.isDebugEnabled()) {
			log.debug("Around: before");
			StringBuilder logString = new StringBuilder();
			logString.append("Entering >>>> ").append(getShortClassName(pjp)).append(".").append(pjp.getStaticPart().getSignature().toShortString()).append("(");
			Object[] args = pjp.getArgs();
			for (int i = 0; i < args.length; i++) {
				log.debug("with arg: " + args[i].toString());
			}
			logString.append(")");
			log.debug(logString.toString());
			
		}
		Object retVal = pjp.proceed();
		if (log.isDebugEnabled()) {
			log.debug("Around: after");
			log.debug("Calling " + pjp.getSignature().getName());
			StringBuilder logString = new StringBuilder();
			logString.append("Returning <<<< ").append(getShortClassName(pjp)).append(".").append(pjp.getStaticPart().getSignature().toShortString()).append(". Return value = ").append(pjp);
			log.debug(logString.toString());
		}
		return retVal;
	}
	
	public String getShortClassName(ProceedingJoinPoint proceedJoinPoint) {
		String className = proceedJoinPoint.getTarget().getClass().getCanonicalName();
		int nameIndex = className.lastIndexOf('.');
		return className.substring(nameIndex + 1);
	}
}
