// package com.eventorback.global.config;
//
// import org.aspectj.lang.annotation.After;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Before;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;
//
// @Aspect
// @Component
// public class DataSourceRoutingAspect {
//
// 	@Before("@annotation(transactional)")
// 	public void setDataSourceBasedOnTransaction(Transactional transactional) {
// 		if (transactional.readOnly()) {
// 			DataSourceContextHolder.setRoutingKey(DataSourceContextHolder.READ);
// 		} else {
// 			DataSourceContextHolder.setRoutingKey(DataSourceContextHolder.WRITE);
// 		}
// 	}
//
// 	@After("@annotation(transactional)")
// 	public void clearDataSourceContext(Transactional transactional) {
// 		DataSourceContextHolder.clear();
// 	}
// }
