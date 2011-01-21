package com.sandwich.koans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;

public class KoansOrderer implements IMethodInterceptor {

	public List<IMethodInstance> intercept(List<IMethodInstance> methods,
			ITestContext context) {
		
		final List<Class<?>> testClassesList = createTestClassOrder(context);
		
		
		Comparator<IMethodInstance> comparator = new Comparator<IMethodInstance>() {
			
			private int getClassOrder(IMethodInstance mi) {
				Method method = mi.getMethod().getMethod();
				Class<?> cls = method.getDeclaringClass();
				int classIdx = testClassesList.indexOf(cls);
				System.out.println(cls.getName() + ' ' + classIdx);
				return ( classIdx < 0) ? 10000 : classIdx; 
			}
			private int getMethodOrder(IMethodInstance mi) {
				int result = 10000;
				
				Method method = mi.getMethod().getMethod();
				Order a1 = method.getAnnotation(Order.class);
				if (a1 != null) {
					result = a1.value();
				} 
				return result;
			}

			public int compare(IMethodInstance m1, IMethodInstance m2) {
				int retVal = getClassOrder(m1) - getClassOrder(m2);
				if (retVal == 0) {
					retVal = getMethodOrder(m1) - getMethodOrder(m2);
				}
				return retVal;
			}

		};

		Collections.sort(methods, comparator);
		return methods;

	}

	public List<Class<?>> createTestClassOrder(ITestContext context) {
		List<Class<?>> retVal = new ArrayList<Class<?>>();
		
		XmlTest xmlTest = context.getSuite().getXmlSuite().getTests().get(0);
		// For Koans, there should only be one test, containing all Koans classes.

		for (XmlClass xmlClass : xmlTest.getXmlClasses()) {
			retVal.add(xmlClass.getClass());
		}
		return retVal;
	}
}