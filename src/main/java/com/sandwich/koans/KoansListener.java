package com.sandwich.koans;

import java.util.Collection;

import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.internal.Utils;

public class KoansListener extends TestListenerAdapter {

	private static final String CLASS_NAME = KoansListener.class.getName();
	private static final int LOG_SEVERITY = 2;
	
	@Override
	public void onStart(ITestContext testContext) {
		log("***********************");
		log("*   Java Koans 0.2    *");  
		log("***********************");
	}
	
	@Override
	public void onFinish(ITestContext testContext) {
		printPassingFailing(testContext);
		printChart(testContext);
		if (allTestsSuccessful(testContext)) {
			log("\nWay to go! You've completed all of the koans! Feel like writing any?");
		} else {
			ITestResult result = getFailingTest(testContext);
			String message = result.getThrowable().getMessage();
			log(message == null || message.length() == 0 ? 
					"" : '\n' + "What went wrong:\n" + message + '\n');
			printSuggestion(result);
			encourage(testContext);
			
		}
	}

	private void encourage(ITestContext testContext) {
		int totalKoans = totalNumberOfTests(testContext);
		int numberPassing = testContext.getPassedTests().size();
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

	private ITestResult getFailingTest(ITestContext testContext) {
		ITestResult retVal = null;
		
		for (ITestResult result: testContext.getFailedTests().getAllResults()) {
			retVal = result;
			break;
		}
		return retVal;
	}

	private boolean allTestsSuccessful(ITestContext testContext) {
		return (testContext.getFailedTests().size() > 0) ? false : true;
	}

	private void printChart(ITestContext testContext) {
		StringBuilder sb = new StringBuilder("Progress:\n");
		sb.append('[');
		
		int numberPassing = testContext.getPassedTests().size();
		int totalKoans = totalNumberOfTests(testContext);
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
		System.out.println(sb.toString());	}

	private int totalNumberOfTests(ITestContext testContext) {
		int totalKoans = testContext.getAllTestMethods().length + testContext.getSkippedTests().size();
		return totalKoans;
	}

	private void printPassingFailing(ITestContext testContext) {
		StringBuilder testsLog = new StringBuilder("Passing Koans\n");
		listMethods(testContext.getPassedTests().getAllMethods(), testsLog);
		testsLog.append('\n');
		testsLog.append("Failing Koans\n");
		listMethods(testContext.getFailedTests().getAllMethods(), testsLog);
		testsLog.append('\n');
		log(testsLog);
	}

	private void listMethods(Collection<ITestNGMethod> methods, StringBuilder passing) {
		for (ITestNGMethod method : methods) {
			passing.append(method.getMethodName());
			passing.append('\n');
		}
	}

	private void log(String msg) {
		Utils.log(CLASS_NAME, LOG_SEVERITY, msg);
	}
	
	private void log(StringBuilder msg) {
		log(msg.toString());
	}
}
