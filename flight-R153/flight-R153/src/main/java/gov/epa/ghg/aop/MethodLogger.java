package gov.epa.ghg.aop;

import java.lang.reflect.Method;
import java.util.Calendar;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Aspect
public class MethodLogger implements ThrowsAdvice {
	
	public MethodLogger() {
	}
	
	//@Autowired
	//@Qualifier("emailService")
	// private EmailService emailService;
	/**
	 * next ticket number.
	 */
	private String nextTicketNumber;
	
	/**
	 * static for zero.
	 */
	private static final String ZERO = "0";
	
	@Pointcut("execution(* gov.epa.ghg.service.*.*(..))")
	public void serviceMethods() {
	}
	
	@Pointcut("execution(* gov.epa.ghg.presentation.controller.*.*(..))")
	public void presentationMethods() {
	}
	
	@Pointcut("execution(* gov.epa.ghg.*.*.*(..))")
	public void inAllApplication() {
	}
	
	@Pointcut("execution(* gov.epa.ghg.aop.*.*(..))")
	public void inAopPackage() {
	}
	
	@Pointcut("execution(* gov.epa.ghg.dao.*.*(..))")
	public void inDaoPackage() {
	}
	
	//@Around("presentationMethods() || cdxRegistrationMethods() || cdxAuthenticationMethods() || serviceMethods()")
	public void before(Method arg0, Object[] arg1, Object arg2) throws Throwable {
		log.debug("Beginning method: " + arg0.getName());
	}
	
	//@Around("presentationMethods() || cdxRegistrationMethods() || cdxAuthenticationMethods() || serviceMethods()")
	public void afterReturning(Object arg0, Method arg1, Object[] arg2, Object arg3) throws Throwable {
		log.debug("Ending method: " + arg1.getName());
	}
	
	//@AfterThrowing(pointcut = "execution(* gov.epa.ghg.serviceTest.UserServiceTest.*(..))", throwing = "e")
	@AfterThrowing(pointcut = "execution(* gov.epa.ghg.*.*.*(..))", throwing = "e")
	//@AfterThrowing(pointcut = "inAllApplication() && !inAopPackage()", throwing = "exception")  HttpSession session,
	public void advice(JoinPoint joinPoint, Throwable e) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String tickeNumber = getNextTicketNumber();
		
		// User user = (User) session.getAttribute("user");
		
		log.debug("MethodLogger.advice(). Return type is " + signature.getReturnType());
		log.error("Ticket Number: " + tickeNumber + " : ");
	        /*if(user != null){
	        	log.debug("USER_Name:" +user.getUsername());
	        }*/
		log.error(e.getMessage(), e);
	        /*emailService.sendEmail(emailService.getHelpdeskEmail(), emailService.getHelpdeskEmail(),
					"Exception in eGGRT."+"Ticket Number: "+tickeNumber,
					"Ticket Number: "+tickeNumber+" : "+  e.fillInStackTrace());*/
	}
	
	public void afterThrowing(IllegalArgumentException ie) throws Throwable {
		String tickeNumber = getNextTicketNumber();
		log.error("Ticket Number: " + tickeNumber + " : ");
		log.error(ie.getMessage(), ie);
		log.error("HijackThrowException : Throw exception hijacked!");
		System.out.println("HijackThrowException : Throw exception hijacked!");
	}
	
	public void afterThrowing(NullPointerException ne) throws Throwable {
		String tickeNumber = getNextTicketNumber();
		log.error("Ticket Number: " + tickeNumber + " : ");
		log.error(ne.getMessage(), ne);
		log.error("HijackThrowException : Throw exception hijacked!");
		System.out.println("HijackThrowException : Throw exception hijacked!");
	}
	
	/*	@AfterThrowing(pointcut="execution(* gov.epa.ghg.*.*(..))",throwing="ex")
	//public void afterThrowing(Method m, Throwable ex) 
	public void afterThrowing(Exception ex)
	{ 
		Logger log = null;
		log = Logger.getLogger(this.getClass());
		//log.info("Exception in method: "+ m.getName()+" Exception is: "+ex.getMessage());	
		log.info("Exception in method: "+ " Exception is: "+ex.getMessage());
		emailInterface.setFromEmail("porankis@saic.com");
		emailInterface.sendEmail("porankis@saic.com", "Ticket Number: "+getNextTicketNumber()+" : "+ ex.getMessage());
		
	}*/
	
	//@Around("presentationMethods() || serviceMethods()")
	public Object timeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object retVal = null;
		Monitor mon = MonitorFactory.start(joinPoint.getSignature().getName());
		try {
			retVal = joinPoint.proceed();
			stopWatch.stop();
			StringBuffer logMessageStringBuffer = new StringBuffer();
			logMessageStringBuffer.append(joinPoint.getTarget().getClass().getName());
			logMessageStringBuffer.append(".");
			logMessageStringBuffer.append(joinPoint.getSignature().getName());
			logMessageStringBuffer.append("(");
			logMessageStringBuffer.append(joinPoint.getArgs());
			logMessageStringBuffer.append(")");
			logMessageStringBuffer.append(" execution time: ");
			logMessageStringBuffer.append(stopWatch.getTotalTimeMillis());
			logMessageStringBuffer.append(" ms");
			log.info(logMessageStringBuffer.toString());
		} finally {
			mon.stop();
			// System.out.println(mon);
			log.info("timeMethod: {}", mon);
		}
		return retVal;
	}
	
	//@Around("presentationMethods() || cdxRegistrationMethods() || cdxAuthenticationMethods() || cdxAuthenticationMethod() || serviceMethods() || cdxCromErrMethods() || frsWebServiceMethod() || inDaoPackage()")
	//@Around("cdxAuthenticationMethod()")|| frsWebServiceMethod()
	public Object profileMethod(ProceedingJoinPoint pjp) throws Throwable {
		Monitor mon = MonitorFactory.start(pjp.getSignature().getName());
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
				log.debug(logPrefix + " START Time : " + start);
			}
			Object retVal = pjp.proceed();
			long end = System.currentTimeMillis();
			long differenceMs = end - start;
			if (DEBUG) {
				log.debug(logPrefix + " End Time : " + end);
				log.debug(logPrefix + " EXECUTION RETURNED in : " + differenceMs + " ms");
			}
			return retVal;
		} catch (Throwable t) {
			log.error("Interceptor caught error: " + t, t);
			throw t;
		} finally {
			mon.stop();
			// System.out.println(mon);
			log.info("profileMethod: {}", mon);
		}
	}
	
	/**
	 * Returns the next available ticket number Generated from the current date,
	 * plus a static int that counts until the server is restarted.
	 *
	 * @return the next available ticket number
	 */
	public final String getNextTicketNumber() {
		Calendar calendar = Calendar.getInstance();
		StringBuffer buffer = new StringBuffer();
		buffer.append(calendar.get(Calendar.MONTH) + 1);
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			buffer.append(ZERO);
		}
		buffer.append(calendar.get(Calendar.DAY_OF_MONTH));
		buffer.append(calendar.get(Calendar.YEAR));
		buffer.append(System.currentTimeMillis() + "");
		nextTicketNumber = buffer.toString();
		return nextTicketNumber;
	}
	
}
