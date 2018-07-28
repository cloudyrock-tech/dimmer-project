package com.github.cloudyrock.dimmer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This aspect intercepts all the calls to methods annotated with (@{@link DimmerFeature})
 *
 * @author Antonio Perez Dieppa
 * @see DimmerFeature
 * @since 11/06/2018
 */
@Aspect
public class DimmerAspect {

    private FeatureExecutor featureExecutor;

    void setFeatureExecutor(FeatureExecutor featureExecutor) {
        this.featureExecutor = featureExecutor;
    }

    @Pointcut("@annotation(dimmerFeatureAnnotation) && execution(* *(..))")
    public void dimmerFeaturePointCutDef(DimmerFeature dimmerFeatureAnnotation) {
    }

    @Around("dimmerFeaturePointCutDef(dimmerFeatureAnn)")
    public Object dimmerFeatureAdvice(ProceedingJoinPoint joinPoint,
                                      DimmerFeature dimmerFeatureAnn) throws Throwable {
        return featureExecutor.executeDimmerFeature(
                dimmerFeatureAnn.value(),
                generateFeatureInvocation(dimmerFeatureAnn.value(), joinPoint),
                createCallerInstance(joinPoint)
        );
    }

    private MethodCaller createCallerInstance(ProceedingJoinPoint joinPoint) {
        //for some reasons doesn't work when using lambda
        return new MethodCaller() {
            @Override
            public Object call() throws Throwable {
                return joinPoint.proceed();
            }
        };
    }

    private FeatureInvocation generateFeatureInvocation(String feature,
                                                        ProceedingJoinPoint joinPoint) {
        final MethodSignature p = (MethodSignature) joinPoint.getSignature();
        return new FeatureInvocation(
                feature,
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getArgs(),
                p.getReturnType()
        );
    }

}