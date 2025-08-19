package gov.epa.ghg.aop;

/**
 * Marker for methods that will be profiled, By placing this annotation on a method spring will proxy the service and
 * call the interceptor that provides advice to the real method call
 */
public @interface Profile {
}