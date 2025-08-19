package gov.epa.ghg.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.log4j.Log4j2;

@Log4j2
//@Component
//@Aspect
public class MethodProfiler {
	
	@Pointcut("@annotation(gov.epa.ghg.aop.Profile)")
	public void profiledMethods() {
	}
	
	/**
	 * Intercepts methods that declare gov.epa.ghg.aop.Profile and prints out the time it takes to complete
	 *
	 * @param pjp proceeding join point
	 *
	 * @return the intercepted method returned object
	 *
	 * @throws Throwable in case something goes wrong in the actual method call
	 */
	@Around("profiledMethods()")
	public Object profileMethod(ProceedingJoinPoint pjp) throws Throwable {
		try {
			final boolean DEBUG = log.isDebugEnabled();
			long start = System.currentTimeMillis();
			// Parse out the first arg
			String arg1 = "";
			Object[] pjpArgs = pjp.getArgs();
			if ((pjpArgs != null) && (pjpArgs.length > 0) && (pjpArgs[0] != null)) {
				arg1 = pjpArgs[0].toString();
			}
			String logPrefix = null;
			if (DEBUG) {
				logPrefix = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName() + " " + arg1;
				log.debug(logPrefix + " START");
			}
			Object retVal = pjp.proceed();
			long end = System.currentTimeMillis();
			long differenceMs = end - start;
			if (DEBUG) {
				log.debug(logPrefix + " RETURN in " + differenceMs + " ms");
			}
			return retVal;
		} catch (Throwable t) {
			log.error("Interceptor caught error: " + t, t);
			throw t;
		}
	}
}
