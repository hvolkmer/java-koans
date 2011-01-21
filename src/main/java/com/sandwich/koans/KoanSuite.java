package com.sandwich.koans;

public abstract class KoanSuite {
	
	public static final String __ = "REPLACE ME";
	
	/*
	 * Forwarding methods, makes KoanResult.message readable for people
	 * completing the koans.
	 */
	public static void assertTrue(Object obj){
		org.testng.Assert.assertEquals(true, obj);
	}
	
	public static void assertFalse(Object obj){
		org.testng.Assert.assertEquals(false, obj);
	}
	
	public static void assertEquals(int first, Integer second) {
		org.testng.Assert.assertTrue(first == second);
	}
	
	public static void assertEquals(int first, int second) {
		org.testng.Assert.assertTrue(first == second);
	}
	
	public static void assertEquals(char first, char second) {
		org.testng.Assert.assertTrue(first == second);
	}
	
	public static void assertEquals(char first, Character second) {
		org.testng.Assert.assertTrue(first == second);
	}
	
	public static void assertEquals(Object first, Object second) {
		org.testng.Assert.assertEquals(first, second);
	}
}
