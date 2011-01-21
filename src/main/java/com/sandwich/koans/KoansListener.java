package com.sandwich.koans;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class KoansListener extends TestListenerAdapter {
	
	private static final String CLASS_NAME = KoansListener.class.getName();
	private static final int LOG_SEVERITY = 1;
	
	private boolean firstTime = true;
	
	@Override
	public void onStart(ITestContext testContext) {
		log("***********************");
		log("*   Java Koans 0.2    *");  
		log("***********************");
		log("Passing Koans:\n");
	}
	
	@Override
	public void onTestSuccess(ITestResult tr) {

		log(tr.getTestClass().getName() + '.' + tr.getMethod().getMethodName());
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		if (firstTime) {
			log("Failing Koan:\n");
			log(tr.getTestClass().getName() + '.' + tr.getMethod().getMethodName());
			firstTime = false;
		}
	}

	@Override
	public void onFinish(ITestContext testContext) {
		printChart();
		if (allTestsSuccessful()) {
			log("\nWay to go! You've completed all of the koans! Feel like writing any?");
		} else {
			
			ITestResult result = getFailingTest();
			String message = result.getThrowable().getMessage();
			log(message == null || message.length() == 0 ? 
					"" : '\n' + "What went wrong:\n" + message + '\n');
			printSuggestion(result);
			encourage();
			
		}
	}

	private void encourage() {
		int totalKoans = totalNumberOfTests();
		int numberPassing = this.getPassedTests().size();
		log("You have conquered " + numberPassing
				+ " out of " + totalKoans
				+ " koan" + (totalKoans != 1 ? 's' : "")
				+ "! Keep going, you will persevere!\n");		
	}

	private void printSuggestion(ITestResult result) {

		ITestNGMethod failedKoan = result.getMethod();
		
		Koan annotation = failedKoan.getMethod().getAnnotation(Koan.class);
		if(annotation != null){
			log("The Koan: " + annotation.value() + " failed.");
		}
		log("Ponder what's going wrong in the "
				+ result.getTestClass().getName() + " class's "
				+ result.getTestName() + " method.\n");
		
	}

	private ITestResult getFailingTest() {
		return this.getFailedTests().get(0);
	}

	private boolean allTestsSuccessful() {
		return (this.getFailedTests().size() > 0) ? false : true;
	}

	private void printChart() {
		StringBuilder sb = new StringBuilder("Progress:\n");
		sb.append('[');
		
		int numberPassing = this.getPassedTests().size();
		int totalKoans = totalNumberOfTests();
		double percentPassing = ((double) numberPassing) / ((double) totalKoans);
		int fifty = 50;
		int percentWeightedToFifty = (int) (percentPassing * fifty);
		for (int i = 0; i < fifty; i++) {
			if (i < percentWeightedToFifty) {
				sb.append('X');
			} else {
				sb.append('-');
			}
		}
		sb.append(']');
		sb.append(' ');
		sb.append(numberPassing + "/" + totalKoans);
		log(sb.toString());	
	}

	private int totalNumberOfTests() {
		return this.getPassedTests().size() + this.getSkippedTests().size() + this.getFailedTests().size();
	}

	private void log(String msg) {
		org.testng.internal.Utils.log(CLASS_NAME, LOG_SEVERITY, msg); 
		System.out.println(msg);
	}
}
