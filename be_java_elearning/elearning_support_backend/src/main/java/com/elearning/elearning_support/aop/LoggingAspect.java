package com.elearning.elearning_support.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j(topic = "AOP-LoggingAspect")
public class LoggingAspect {

    @Around(value = "@annotation(com.elearning.elearning_support.annotations.log.ExecutionTimeLog)")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final String methodName = proceedingJoinPoint.getSignature().getName();
        final String classSimpleName = proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName();
        // log
        long startLoggedTimeInMillis = System.currentTimeMillis();
        log.info("===== STARTED METHOD [{}] IN [{}] =====", methodName, classSimpleName);
        Object proceed = proceedingJoinPoint.proceed();
        log.info("===== ENDED METHOD [{}] IN [{}] AFTER {} ms =====", methodName, classSimpleName, System.currentTimeMillis() - startLoggedTimeInMillis);
        return proceed;
    }
}
