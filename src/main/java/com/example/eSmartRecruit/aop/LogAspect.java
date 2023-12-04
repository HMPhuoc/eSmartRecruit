//package com.example.eSmartRecruit.aop;
//
//
//import lombok.extern.log4j.Log4j2;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//@Aspect
//@Configuration
//@Order
//@Component
//@Log4j2
//public class LogAspect {
//
////    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    /**
//     * Advice that logs methods throwing exceptions.
//     *
//     * @param joinPoint join point for advice
//     * @param e         exception
//     */
//    @AfterThrowing(pointcut = "com.example.eSmartRecruit.aop.AppPointcuts.applicationPackagePointcut() " +
//            "&& com.example.eSmartRecruit.aop.AppPointcuts.springBeanPointcut()", throwing = "e")
//    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
//        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
//                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
//    }
//
//    /**
//     * Advice that logs when a method is entered and exited.
//     *
//     * @param joinPoint join point for advice
//     * @return result
//     * @throws Throwable throws IllegalArgumentException
//     */
//    @Around("com.example.eSmartRecruit.aop.AppPointcuts.applicationPackagePointcut() " +
//            "&& com.example.eSmartRecruit.aop.AppPointcuts.springBeanPointcut()")
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        if (log.isDebugEnabled()) {
//            log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
//                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
//        }
//        try {
//            Object result = joinPoint.proceed();
//            if (log.isDebugEnabled()) {
//                log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
//                        joinPoint.getSignature().getName(), result);
//            }
//            return result;
//        } catch (IllegalArgumentException e) {
//            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
//                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
//            throw e;
//        }
//    }
//}
