package com.lianshang.sqlSessionFactoryBeanConfig;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 描述:
 *
 * @AUTHOR 孙龙云
 * @date 2019-06-07 10:43
 */
@Component
@Aspect
@Order(100)
public class DynamicDataSourceAspect {
	@Pointcut("execution(* com..service.impl..*(..))  "
		+ "|| execution(* com..generator.commons..*(..))"
		+ "|| execution(* *.*Impl(..))")
	private void beforeMethod() {

	}

	/**
	 * 前置通知
	 */
	@Before("beforeMethod()")
	public void beforeMethod(JoinPoint joinPoint) {
		Class<?> clazz = joinPoint.getTarget().getClass();
		try {
			//包名称
			Package pkg =  clazz.getPackage();
			if(null != pkg){
				String packageName =  pkg.getName();
				if(StringUtils.isNotEmpty(packageName)){
					DynamicDatasource.setDataSourceKey(packageName);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}

	}

}
