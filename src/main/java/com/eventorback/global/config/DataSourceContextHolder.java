// package com.eventorback.global.config;
//
// public class DataSourceContextHolder {
// 	public static final String READ = "READ";
// 	public static final String WRITE = "WRITE";
//
// 	private DataSourceContextHolder() {
// 	}
//
// 	private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
//
// 	public static void setRoutingKey(String key) {
// 		contextHolder.set(key);
// 	}
//
// 	public static String getRoutingKey() {
// 		return contextHolder.get();
// 	}
//
// 	public static void clear() {
// 		contextHolder.remove();
// 	}
// }